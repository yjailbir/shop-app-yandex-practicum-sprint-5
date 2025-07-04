package ru.yjailbir.shopappyandexpracticumsprint5.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.ProductDto;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.CartElementEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.OrderEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.OrderItemEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.ProductEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.CartElementRepository;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.ProductRepository;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartElementRepository cartElementRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public ProductService(
            ProductRepository productRepository,
            CartElementRepository cartElementRepository,
            OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.cartElementRepository = cartElementRepository;
        this.orderRepository = orderRepository;
    }

    public void save(ProductDto productDto) {
        ProductEntity productEntity = productRepository.findByName(productDto.getName()).orElse(new ProductEntity());
        productEntity.setName(productDto.getName());
        productEntity.setDescription(productDto.getDescription());
        productEntity.setPrice(productDto.getPrice());
        productEntity.setImgName(productDto.getImgName());

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
                case "NO" ->
                        entities = productRepository.findAllByName(search, PageRequest.of(offset, onePageProductsCount));
                case "ALPHA" -> entities = productRepository.findAllByName(
                        search, PageRequest.of(offset, onePageProductsCount, Sort.by("name").ascending())
                );
                case "PRICE" -> entities = productRepository.findAllByName(
                        search, PageRequest.of(offset, onePageProductsCount, Sort.by("price").ascending())
                );
            }
        } else {
            switch (sort) {
                case "NO" ->
                        entities = productRepository.findAll(PageRequest.of(offset, onePageProductsCount)).getContent();
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
            } else if (elemNumber == onePageProductsCount) {
                elemNumber = 0;
                rowIndex++;
                continue;
            }
            CartElementEntity cartElement = cartElementRepository
                    .findByProductEntity_Id(productEntity.getId()).orElse(null);

            result.get(rowIndex).add(
                    mapEntityToDto(
                            productEntity,
                            cartElement == null ? 0 : cartElement.getQuantity()
                    )
            );
            elemNumber++;
        }

        return result;
    }

    public ProductDto getProductById(Long id) {
        ProductEntity productEntity = productRepository.findById(id).orElse(null);

        if (productEntity != null) {
            return mapEntityToDto(productEntity, null);
        } else {
            return null;
        }
    }

    public void changeCountInCart(Long productId, String action) {
        CartElementEntity cartElement = cartElementRepository.findByProductEntity_Id(productId)
                .orElse(new CartElementEntity(
                        productRepository.findById(productId).orElseThrow(),
                        0
                ));

        switch (action) {
            case "plus" -> {
                cartElement.incrementQuantity();
                cartElementRepository.save(cartElement);
            }
            case "minus" -> {
                cartElement.decrementQuantity();
                cartElementRepository.save(cartElement);
            }
            case "delete" -> cartElementRepository.delete(cartElement);
        }
    }

    public List<ProductDto> getCart() {
        List<ProductEntity> entities = cartElementRepository.findAll().stream().map(CartElementEntity::getProductEntity).toList();
        List<ProductDto> result = new ArrayList<>();

        entities.forEach(productEntity -> {
           Integer count = cartElementRepository.findByProductEntity_Id(productEntity.getId()).orElseThrow().getQuantity();
            result.add(mapEntityToDto(productEntity, count));
        });

        return result;
    }

    public Integer getSumFromItemsList(List<ProductDto> products) {
        return products.stream().mapToInt(x -> (x.getPrice() * x.getCount())).sum();
    }

    public Long getMaxOrderId() {
        return orderRepository.getMaxOrderId().orElse(0L);
    }

    public Long makeOrder() {
        List<ProductDto> products = getCart();
        OrderEntity orderEntity = new OrderEntity();
        List<OrderItemEntity> orderItems = new ArrayList<>();

        products.forEach(
                productDto -> orderItems.add(
                        new OrderItemEntity(
                                orderEntity,
                                productRepository.findByName(productDto.getName()).orElseThrow(),
                                productDto.getCount()
                        )
                )
        );

        orderEntity.setItems(orderItems);
        orderRepository.save(orderEntity);
        cartElementRepository.deleteAll();

        return getMaxOrderId();
    }

    public List<ProductDto> getOrderById(Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow();
        List<ProductDto> result = new ArrayList<>();

        orderEntity.getItems().forEach(item -> result.add(mapEntityToDto(item.getProduct(), item.getQuantity())));

        return result;
    }

    private ProductDto mapEntityToDto(ProductEntity productEntity, Integer count) {

        return new ProductDto(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getDescription(),
                productEntity.getPrice(),
                productEntity.getImgName(),
                count
        );
    }
}
