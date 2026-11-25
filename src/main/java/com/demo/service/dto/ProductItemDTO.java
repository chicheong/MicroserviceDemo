package com.demo.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.demo.domain.ProductItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductItemDTO implements Serializable {

    private Long id;

    private String code;

    private Boolean isDefault;

    private Integer quantity;

    private String currency;

    private BigDecimal price;

    private ProductDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductItemDTO)) {
            return false;
        }

        ProductItemDTO productItemDTO = (ProductItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductItemDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", isDefault='" + getIsDefault() + "'" +
            ", quantity=" + getQuantity() +
            ", currency='" + getCurrency() + "'" +
            ", price=" + getPrice() +
            ", product=" + getProduct() +
            "}";
    }
}
