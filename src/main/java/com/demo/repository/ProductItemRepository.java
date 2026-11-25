package com.demo.repository;

import com.demo.domain.ProductItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ProductItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductItemRepository extends ReactiveCrudRepository<ProductItem, Long>, ProductItemRepositoryInternal {
    Flux<ProductItem> findAllBy(Pageable pageable);

    @Query("SELECT * FROM product_item entity WHERE entity.product_id = :id")
    Flux<ProductItem> findByProduct(Long id);

    @Query("SELECT * FROM product_item entity WHERE entity.product_id IS NULL")
    Flux<ProductItem> findAllWhereProductIsNull();

    @Override
    <S extends ProductItem> Mono<S> save(S entity);

    @Override
    Flux<ProductItem> findAll();

    @Override
    Mono<ProductItem> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProductItemRepositoryInternal {
    Flux<ProductItem> findAllBy(Pageable pageable);

    Flux<ProductItem> findAll();

    Mono<ProductItem> findById(Long id);
}
