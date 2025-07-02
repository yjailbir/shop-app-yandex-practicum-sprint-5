package ru.yjailbir.shopappyandexpracticumsprint5.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cart")
public class CartElementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private ProductEntity productEntity;

    @Column(name = "quantity")
    private Integer quantity;

    public CartElementEntity() {
    }

    public CartElementEntity(Long id, ProductEntity productEntity, Integer quantity) {
        this.id = id;
        this.productEntity = productEntity;
        this.quantity = quantity;
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
}
