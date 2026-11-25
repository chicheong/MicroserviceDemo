package com.demo.repository;

import com.demo.domain.Product;
import com.demo.domain.ProductItem;
import java.util.List;
import java.util.Map;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the ProductItem entity.
 */
@SuppressWarnings("unused")
class ProductItemRepositoryInternalImpl extends AbstractR2dbcRepository<ProductItem, Long> implements ProductItemRepositoryInternal {

    private static final String SELECT_WITH_RELATIONS_SQL = "SELECT e.* FROM product_item e";

    // The clients sort by the related entity display field, with the entity id as secondary sort.
    private static final Map<String, String> SORT_COLUMNS = Map.ofEntries(Map.entry("id", "e.id"), Map.entry("product.id", "e.product_id"));

    public ProductItemRepositoryInternalImpl(R2dbcEntityTemplate template, R2dbcConverter converter, R2dbcDialect dialect) {
        super(ProductItem.class, template, converter, dialect);
    }

    @Override
    protected String selectWithRelationsSql() {
        return SELECT_WITH_RELATIONS_SQL;
    }

    @Override
    protected Map<String, String> sortColumns() {
        return SORT_COLUMNS;
    }

    @Override
    protected Mono<List<ProductItem>> populateRelationships(List<ProductItem> entities) {
        reference(entities, ProductItem::getProductId, Product::new, Product::setId, ProductItem::setProduct);
        return Mono.just(entities);
    }
}
