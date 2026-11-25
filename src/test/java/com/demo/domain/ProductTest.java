package com.demo.domain;

import static com.demo.domain.ProductItemTestSamples.*;
import static com.demo.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.demo.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = getProductSample1();
        Product product2 = new Product();
        assertThat(product1).isNotEqualTo(product2);

        product2.setId(product1.getId());
        assertThat(product1).isEqualTo(product2);

        product2 = getProductSample2();
        assertThat(product1).isNotEqualTo(product2);
    }

    @Test
    void itemTest() {
        Product product = getProductRandomSampleGenerator();
        ProductItem productItemBack = getProductItemRandomSampleGenerator();

        product.addItem(productItemBack);
        assertThat(product.getItems()).containsOnly(productItemBack);
        assertThat(productItemBack.getProduct()).isEqualTo(product);

        product.removeItem(productItemBack);
        assertThat(product.getItems()).doesNotContain(productItemBack);
        assertThat(productItemBack.getProduct()).isNull();

        product.items(new HashSet<>(Set.of(productItemBack)));
        assertThat(product.getItems()).containsOnly(productItemBack);
        assertThat(productItemBack.getProduct()).isEqualTo(product);

        product.setItems(new HashSet<>());
        assertThat(product.getItems()).doesNotContain(productItemBack);
        assertThat(productItemBack.getProduct()).isNull();
    }
}
