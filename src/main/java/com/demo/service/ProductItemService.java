package com.demo.service;

import com.demo.repository.ProductItemRepository;
import com.demo.repository.search.ProductItemSearchRepository;
import com.demo.service.dto.ProductItemDTO;
import com.demo.service.mapper.ProductItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.demo.domain.ProductItem}.
 */
@Service
@Transactional
public class ProductItemService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductItemService.class);

    private final ProductItemRepository productItemRepository;

    private final ProductItemMapper productItemMapper;

    private final ProductItemSearchRepository productItemSearchRepository;

    public ProductItemService(
        ProductItemRepository productItemRepository,
        ProductItemMapper productItemMapper,
        ProductItemSearchRepository productItemSearchRepository
    ) {
        this.productItemRepository = productItemRepository;
        this.productItemMapper = productItemMapper;
        this.productItemSearchRepository = productItemSearchRepository;
    }

    /**
     * Save a productItem.
     *
     * @param productItemDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductItemDTO> save(ProductItemDTO productItemDTO) {
        LOG.debug("Request to save ProductItem : {}", productItemDTO);
        return productItemRepository
            .save(productItemMapper.toEntity(productItemDTO))

            .flatMap(productItemSearchRepository::save)
            .map(productItemMapper::toDto);
    }

    /**
     * Update a productItem.
     *
     * @param productItemDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductItemDTO> update(ProductItemDTO productItemDTO) {
        LOG.debug("Request to update ProductItem : {}", productItemDTO);
        return productItemRepository
            .save(productItemMapper.toEntity(productItemDTO))

            .flatMap(productItemSearchRepository::save)
            .map(productItemMapper::toDto);
    }

    /**
     * Partially update a productItem.
     *
     * @param productItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProductItemDTO> partialUpdate(ProductItemDTO productItemDTO) {
        LOG.debug("Request to partially update ProductItem : {}", productItemDTO);

        return productItemRepository
            .findById(productItemDTO.getId())
            .map(existingProductItem -> {
                productItemMapper.partialUpdate(existingProductItem, productItemDTO);

                return existingProductItem;
            })
            .flatMap(productItemRepository::save)
            .flatMap(savedProductItem -> {
                productItemSearchRepository.save(savedProductItem);
                return Mono.just(savedProductItem);
            })
            .map(productItemMapper::toDto);
    }

    /**
     * Get all the productItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductItemDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ProductItems");
        return productItemRepository.findAllBy(pageable).map(productItemMapper::toDto);
    }

    /**
     * Returns the number of productItems available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return productItemRepository.count();
    }

    /**
     * Returns the number of productItems available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return productItemSearchRepository.count();
    }

    /**
     * Get one productItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProductItemDTO> findOne(Long id) {
        LOG.debug("Request to get ProductItem : {}", id);
        return productItemRepository.findById(id).map(productItemMapper::toDto);
    }

    /**
     * Delete the productItem by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ProductItem : {}", id);
        return productItemRepository
            .deleteById(id)

            .then(productItemSearchRepository.deleteById(id));
    }

    /**
     * Search for the productItem corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductItemDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of ProductItems for query {}", query);
        return productItemSearchRepository.search(query, pageable).map(productItemMapper::toDto);
    }
}
