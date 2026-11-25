package com.demo.repository;

import com.demo.domain.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data R2DBC repository for the Product entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductRepository extends R2dbcRepository<Product, Long> {
    Flux<Product> findAllBy(Pageable pageable);
}
