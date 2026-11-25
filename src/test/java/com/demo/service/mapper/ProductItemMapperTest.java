package com.demo.service.mapper;

import static com.demo.domain.ProductItemAsserts.*;
import static com.demo.domain.ProductItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductItemMapperTest {

    private ProductItemMapper productItemMapper;

    @BeforeEach
    void setUp() {
        productItemMapper = new ProductItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProductItemSample1();
        var actual = productItemMapper.toEntity(productItemMapper.toDto(expected));
        assertProductItemAllPropertiesEquals(expected, actual);
    }
}
