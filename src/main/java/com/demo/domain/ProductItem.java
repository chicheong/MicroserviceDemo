package com.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ProductItem.
 */
@Table("product_item")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "productitem")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Long)
    private Long id;

    @Column("code")
    @org.springframework.data.elasticsearch.annotations.MultiField(
        mainField = @org.springframework.data.elasticsearch.annotations.Field(
            type = org.springframework.data.elasticsearch.annotations.FieldType.Text
        ),
        otherFields = {
            @org.springframework.data.elasticsearch.annotations.InnerField(
                suffix = "keyword",
                type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword,
                ignoreAbove = 256
            ),
        }
    )
    private String code;

    @Column("is_default")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isDefault;

    @Column("quantity")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer quantity;

    @Column("currency")
    @org.springframework.data.elasticsearch.annotations.MultiField(
        mainField = @org.springframework.data.elasticsearch.annotations.Field(
            type = org.springframework.data.elasticsearch.annotations.FieldType.Text
        ),
        otherFields = {
            @org.springframework.data.elasticsearch.annotations.InnerField(
                suffix = "keyword",
                type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword,
                ignoreAbove = 256
            ),
        }
    )
    private String currency;

    @Column("price")
    private BigDecimal price;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "items" }, allowSetters = true)
    private Product product;

    @Column("product_id")
    private Long productId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public ProductItem code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getIsDefault() {
        return this.isDefault;
    }

    public ProductItem isDefault(Boolean isDefault) {
        this.setIsDefault(isDefault);
        return this;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public ProductItem quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCurrency() {
        return this.currency;
    }

    public ProductItem currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public ProductItem price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product != null ? product.getId() : null;
    }

    public ProductItem product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Long getProductId() {
        return this.productId;
    }

    public void setProductId(Long product) {
        this.productId = product;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductItem)) {
            return false;
        }
        return getId() != null && getId().equals(((ProductItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductItem{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", isDefault='" + getIsDefault() + "'" +
            ", quantity=" + getQuantity() +
            ", currency='" + getCurrency() + "'" +
            ", price=" + getPrice() +
            "}";
    }
}
