package ru.yjailbir.shopappyandexpracticumsprint5.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "cart")
public class CartElementEntity {
    @Id
    @Column("id")
    private Long id;

    @Column("product_id")
    private Long productId;
    private ProductEntity productEntity;

    @Column("quantity")
    private Integer quantity;

    public CartElementEntity() {
    }

    public CartElementEntity(ProductEntity productEntity, Integer quantity) {
        this.productEntity = productEntity;
        this.quantity = quantity;
    }

    public void incrementQuantity() {
        this.quantity++;
    }

    public void decrementQuantity() {
        this.quantity--;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductEntity getProductEntity() {
        return productEntity;
    }

    public void setProductEntity(ProductEntity productEntity) {
        this.productEntity = productEntity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long geProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
