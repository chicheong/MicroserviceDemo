package com.demo.web.rest;

import com.demo.repository.ProductItemRepository;
import com.demo.service.ProductItemService;
import com.demo.service.dto.ProductItemDTO;
import com.demo.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.demo.domain.ProductItem}.
 */
@RestController
@RequestMapping("/api/product-items")
public class ProductItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductItemResource.class);

    private static final String ENTITY_NAME = "microserviceDemoProductItem";

    @Value("${jhipster.clientApp.name:microserviceDemo}")
    private String applicationName;

    private final ProductItemService productItemService;

    private final ProductItemRepository productItemRepository;

    public ProductItemResource(ProductItemService productItemService, ProductItemRepository productItemRepository) {
        this.productItemService = productItemService;
        this.productItemRepository = productItemRepository;
    }

    /**
     * {@code POST  /product-items} : Create a new productItem.
     *
     * @param productItemDTO the productItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productItemDTO, or with status {@code 400 (Bad Request)} if the productItem already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ProductItemDTO>> createProductItem(@RequestBody ProductItemDTO productItemDTO) throws URISyntaxException {
        LOG.debug("REST request to save ProductItem : {}", productItemDTO);
        if (productItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new productItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return productItemService.save(productItemDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/product-items/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /product-items/:id} : Updates an existing productItem.
     *
     * @param id the id of the productItemDTO to save.
     * @param productItemDTO the productItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productItemDTO,
     * or with status {@code 400 (Bad Request)} if the productItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProductItemDTO>> updateProductItem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductItemDTO productItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProductItem : {}, {}", id, productItemDTO);
        if (productItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productItemRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return productItemService
                .update(productItemDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /product-items/:id} : Partial updates given fields of an existing productItem, field will ignore if it is null
     *
     * @param id the id of the productItemDTO to save.
     * @param productItemDTO the productItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productItemDTO,
     * or with status {@code 400 (Bad Request)} if the productItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProductItemDTO>> partialUpdateProductItem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductItemDTO productItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update ProductItem : {}, {}", id, productItemDTO);
        if (productItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productItemRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<ProductItemDTO> result = productItemService.partialUpdate(productItemDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /product-items} : get all the Product Items.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Product Items in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ProductItemDTO>>> getAllProductItems(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of ProductItems");
        return productItemService
            .countAll()
            .zipWith(productItemService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /product-items/:id} : get the "id" productItem.
     *
     * @param id the id of the productItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductItemDTO>> getProductItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProductItem : {}", id);
        Mono<ProductItemDTO> productItemDTO = productItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productItemDTO);
    }

    /**
     * {@code DELETE  /product-items/:id} : delete the "id" productItem.
     *
     * @param id the id of the productItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProductItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProductItem : {}", id);
        return productItemService
            .delete(id)

            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code SEARCH  /product-items/_search?query=:query} : search for the productItem corresponding
     * to the query.
     *
     * @param query the query of the productItem search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<ProductItemDTO>>> searchProductItems(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to search for a page of ProductItems for query {}", query);
        return productItemService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page ->
                PaginationUtil.generatePaginationHttpHeaders(
                    ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                    page
                )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(productItemService.search(query, pageable)));
    }
}
