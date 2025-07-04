package ru.yjailbir.shopappyandexpracticumsprint5.dto;

public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Integer price;
    private String imgName;
    private Integer count;

    public ProductDto(Long id, String name, String description, Integer price, String imgName, Integer count) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgName = imgName;
        this.count = count;
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
