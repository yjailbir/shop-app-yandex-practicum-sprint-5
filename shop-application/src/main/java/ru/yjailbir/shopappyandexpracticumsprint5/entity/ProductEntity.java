package ru.yjailbir.shopappyandexpracticumsprint5.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("products")
public class ProductEntity {
    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("price")
    private Integer price;

    @Column("img_name")
    private String imgName;

    public ProductEntity() {
    }

    public ProductEntity(Long id, String name, String description, Integer price, String imgName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgName = imgName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgUrl) {
        this.imgName = imgUrl;
    }
}
