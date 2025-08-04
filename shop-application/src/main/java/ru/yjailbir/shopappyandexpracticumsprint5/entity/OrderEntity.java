package ru.yjailbir.shopappyandexpracticumsprint5.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@Table("orders")
public class OrderEntity {
    @Id
    @Column("id")
    private Long id;

    @Transient
    private List<OrderItemEntity> items = new ArrayList<>();

    public OrderEntity() {
    }

    public Integer getSum() {
       return items.stream().mapToInt(OrderItemEntity::getSum).sum();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<OrderItemEntity> getItems() {
        return items;
    }

    public void setItems(List<OrderItemEntity> items) {
        this.items = items;
    }
}
