package ru.yjailbir.shopappyandexpracticumsprint5.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("order_items")
public class OrderItemEntity {
    @Id
    private Long id;

    @Column("order_id")
    private Long orderId;
    private OrderEntity order;

    @Column("product_id")
    private Long productId;
    private ProductEntity product;

    @Column("quantity")
    private Integer quantity;

    public OrderItemEntity() {
    }

    public OrderItemEntity(OrderEntity order, ProductEntity product, Integer quantity) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
    }

    public Integer getSum() {
        return quantity * product.getPrice();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
