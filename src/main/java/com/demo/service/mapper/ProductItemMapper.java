package com.demo.service.mapper;

import com.demo.domain.Product;
import com.demo.domain.ProductItem;
import com.demo.service.dto.ProductDTO;
import com.demo.service.dto.ProductItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductItem} and its DTO {@link ProductItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductItemMapper extends EntityMapper<ProductItemDTO, ProductItem> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    ProductItemDTO toDto(ProductItem s);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);
}
