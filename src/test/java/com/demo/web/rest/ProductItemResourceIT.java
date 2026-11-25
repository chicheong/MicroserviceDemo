package com.demo.web.rest;

import static com.demo.domain.ProductItemAsserts.*;
import static com.demo.web.rest.TestUtil.createUpdateProxyForBean;
import static com.demo.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.demo.IntegrationTest;
import com.demo.domain.ProductItem;
import com.demo.repository.ProductItemRepository;
import com.demo.repository.search.ProductItemSearchRepository;
import com.demo.service.dto.ProductItemDTO;
import com.demo.service.mapper.ProductItemMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link ProductItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProductItemResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_DEFAULT = false;
    private static final Boolean UPDATED_IS_DEFAULT = true;

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/product-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/product-items/_search";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductItemRepository productItemRepository;

    @Autowired
    private ProductItemMapper productItemMapper;

    @Autowired
    private ProductItemSearchRepository productItemSearchRepository;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private WebTestClient webTestClient;

    private ProductItem productItem;

    private ProductItem insertedProductItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductItem createEntity() {
        return new ProductItem()
            .code(DEFAULT_CODE)
            .isDefault(DEFAULT_IS_DEFAULT)
            .quantity(DEFAULT_QUANTITY)
            .currency(DEFAULT_CURRENCY)
            .price(DEFAULT_PRICE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductItem createUpdatedEntity() {
        return new ProductItem()
            .code(UPDATED_CODE)
            .isDefault(UPDATED_IS_DEFAULT)
            .quantity(UPDATED_QUANTITY)
            .currency(UPDATED_CURRENCY)
            .price(UPDATED_PRICE);
    }

    public static void deleteEntities(R2dbcEntityTemplate template) {
        try {
            template.delete(ProductItem.class).all().block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    void initTest() {
        productItem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProductItem != null) {
            productItemRepository.delete(insertedProductItem).block();
            productItemSearchRepository.delete(insertedProductItem).block();
            insertedProductItem = null;
        }
        deleteEntities(template);
    }

    @Test
    void createProductItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
        // Create the ProductItem
        ProductItemDTO productItemDTO = productItemMapper.toDto(productItem);
        var returnedProductItemDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productItemDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ProductItemDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the ProductItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProductItem = productItemMapper.toEntity(returnedProductItemDTO);
        assertProductItemUpdatableFieldsEquals(returnedProductItem, getPersistedProductItem(returnedProductItem));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedProductItem = returnedProductItem;
    }

    @Test
    void createProductItemWithExistingId() throws Exception {
        // Create the ProductItem with an existing ID
        productItem.setId(1L);
        ProductItemDTO productItemDTO = productItemMapper.toDto(productItem);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllProductItems() {
        // Initialize the database
        insertedProductItem = productItemRepository.save(productItem).block();

        // Get all the productItemList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(productItem.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].isDefault")
            .value(hasItem(DEFAULT_IS_DEFAULT))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].currency")
            .value(hasItem(DEFAULT_CURRENCY))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)));
    }

    @Test
    void getProductItem() {
        // Initialize the database
        insertedProductItem = productItemRepository.save(productItem).block();

        // Get the productItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, productItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(productItem.getId().intValue()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.isDefault")
            .value(is(DEFAULT_IS_DEFAULT))
            .jsonPath("$.quantity")
            .value(is(DEFAULT_QUANTITY))
            .jsonPath("$.currency")
            .value(is(DEFAULT_CURRENCY))
            .jsonPath("$.price")
            .value(is(sameNumber(DEFAULT_PRICE)));
    }

    @Test
    void getNonExistingProductItem() {
        // Get the productItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProductItem() throws Exception {
        // Initialize the database
        insertedProductItem = productItemRepository.save(productItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        productItemSearchRepository.save(productItem).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());

        // Update the productItem
        ProductItem updatedProductItem = productItemRepository.findById(productItem.getId()).block();
        updatedProductItem
            .code(UPDATED_CODE)
            .isDefault(UPDATED_IS_DEFAULT)
            .quantity(UPDATED_QUANTITY)
            .currency(UPDATED_CURRENCY)
            .price(UPDATED_PRICE);
        ProductItemDTO productItemDTO = productItemMapper.toDto(updatedProductItem);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productItemDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productItemDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductItemToMatchAllProperties(updatedProductItem);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ProductItem> productItemSearchList = Streamable.of(
                    productItemSearchRepository.findAll().collectList().block()
                ).toList();
                ProductItem testProductItemSearch = productItemSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertProductItemAllPropertiesEquals(testProductItemSearch, updatedProductItem);
                assertProductItemUpdatableFieldsEquals(testProductItemSearch, updatedProductItem);
            });
    }

    @Test
    void putNonExistingProductItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
        productItem.setId(longCount.incrementAndGet());

        // Create the ProductItem
        ProductItemDTO productItemDTO = productItemMapper.toDto(productItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productItemDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchProductItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
        productItem.setId(longCount.incrementAndGet());

        // Create the ProductItem
        ProductItemDTO productItemDTO = productItemMapper.toDto(productItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamProductItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
        productItem.setId(longCount.incrementAndGet());

        // Create the ProductItem
        ProductItemDTO productItemDTO = productItemMapper.toDto(productItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productItemDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateProductItemWithPatch() throws Exception {
        // Initialize the database
        insertedProductItem = productItemRepository.save(productItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productItem using partial update
        ProductItem partialUpdatedProductItem = new ProductItem();
        partialUpdatedProductItem.setId(productItem.getId());

        partialUpdatedProductItem.price(UPDATED_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProductItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductItem, productItem),
            getPersistedProductItem(productItem)
        );
    }

    @Test
    void fullUpdateProductItemWithPatch() throws Exception {
        // Initialize the database
        insertedProductItem = productItemRepository.save(productItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productItem using partial update
        ProductItem partialUpdatedProductItem = new ProductItem();
        partialUpdatedProductItem.setId(productItem.getId());

        partialUpdatedProductItem
            .code(UPDATED_CODE)
            .isDefault(UPDATED_IS_DEFAULT)
            .quantity(UPDATED_QUANTITY)
            .currency(UPDATED_CURRENCY)
            .price(UPDATED_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProductItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductItemUpdatableFieldsEquals(partialUpdatedProductItem, getPersistedProductItem(partialUpdatedProductItem));
    }

    @Test
    void patchNonExistingProductItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
        productItem.setId(longCount.incrementAndGet());

        // Create the ProductItem
        ProductItemDTO productItemDTO = productItemMapper.toDto(productItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, productItemDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(productItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchProductItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
        productItem.setId(longCount.incrementAndGet());

        // Create the ProductItem
        ProductItemDTO productItemDTO = productItemMapper.toDto(productItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(productItemDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamProductItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
        productItem.setId(longCount.incrementAndGet());

        // Create the ProductItem
        ProductItemDTO productItemDTO = productItemMapper.toDto(productItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(productItemDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteProductItem() {
        // Initialize the database
        insertedProductItem = productItemRepository.save(productItem).block();
        productItemRepository.save(productItem).block();
        productItemSearchRepository.save(productItem).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the productItem
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, productItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(productItemSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchProductItem() {
        // Initialize the database
        insertedProductItem = productItemRepository.save(productItem).block();
        productItemSearchRepository.save(productItem).block();

        // Search the productItem
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + productItem.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(productItem.getId().intValue()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].isDefault")
            .value(hasItem(DEFAULT_IS_DEFAULT))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].currency")
            .value(hasItem(DEFAULT_CURRENCY))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)));
    }

    protected long getRepositoryCount() {
        return productItemRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected ProductItem getPersistedProductItem(ProductItem productItem) {
        return productItemRepository.findById(productItem.getId()).block();
    }

    protected void assertPersistedProductItemToMatchAllProperties(ProductItem expectedProductItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProductItemAllPropertiesEquals(expectedProductItem, getPersistedProductItem(expectedProductItem));
        assertProductItemUpdatableFieldsEquals(expectedProductItem, getPersistedProductItem(expectedProductItem));
    }

    protected void assertPersistedProductItemToMatchUpdatableProperties(ProductItem expectedProductItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProductItemAllUpdatablePropertiesEquals(expectedProductItem, getPersistedProductItem(expectedProductItem));
        assertProductItemUpdatableFieldsEquals(expectedProductItem, getPersistedProductItem(expectedProductItem));
    }
}
