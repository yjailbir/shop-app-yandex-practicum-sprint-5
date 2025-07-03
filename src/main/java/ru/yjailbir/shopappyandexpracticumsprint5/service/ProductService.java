package ru.yjailbir.shopappyandexpracticumsprint5.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.ProductDto;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.ProductEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void save(ProductDto productDto) {
        ProductEntity productEntity = productRepository.findByName(productDto.getName()).orElse(new ProductEntity());
        productEntity.setName(productDto.getName());
        productEntity.setDescription(productDto.getDescription());
        productEntity.setPrice(productDto.getPrice());

        productRepository.save(productEntity);
    }

    public long getProductsCount(int onePageProductsCount) {
        long count = productRepository.count();

        return count % onePageProductsCount == 0 ? count / onePageProductsCount : count / onePageProductsCount + 1;
    }

    public List<List<ProductDto>> getProducts(int onePageProductsCount, int offset, String search, String sort) {
        List<ProductEntity> entities = new ArrayList<>();
        List<List<ProductDto>> result = new ArrayList<>();

        if (search != null && !search.isEmpty()) {
            switch (sort) {
                case "NO" -> entities = productRepository.findAllByName(search, PageRequest.of(offset, onePageProductsCount));
                case "ALPHA" -> entities = productRepository.findAllByName(
                        search, PageRequest.of(offset, onePageProductsCount, Sort.by("name").ascending())
                );
                case "PRICE" -> entities = productRepository.findAllByName(
                        search, PageRequest.of(offset, onePageProductsCount, Sort.by("price").ascending())
                );
            }
        }
        else {
            switch (sort) {
                case "NO" -> entities = productRepository.findAll(PageRequest.of(offset, onePageProductsCount)).getContent();
                case "ALPHA" -> entities = productRepository.findAll(
                        PageRequest.of(offset, onePageProductsCount, Sort.by("name").ascending())
                ).getContent();
                case "PRICE" -> entities = productRepository.findAll(
                        PageRequest.of(offset, onePageProductsCount, Sort.by("price").ascending())
                ).getContent();
            }
        }

        int rowIndex = 0;
        int elemNumber = 0;
        for (ProductEntity productEntity : entities) {
            if (elemNumber == 0) {
                result.add(new ArrayList<>());
            }
            else if (elemNumber == onePageProductsCount) {
                elemNumber = 0;
                rowIndex++;
                continue;
            }
            result.get(rowIndex).add(productEntity.toDto());
            elemNumber++;
        }

        return result;
    }
}
