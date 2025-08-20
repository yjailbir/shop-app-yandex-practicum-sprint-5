package ru.yjailbir.shopappyandexpracticumsprint5.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.ProductDto;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.CartElementEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.OrderEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.OrderItemEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.ProductEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.*;

import java.util.List;
import java.util.Objects;

@Service
public class ProductService {
    private final ProductCrudRepository productCrudRepository;
    private final CartElementCrudRepository cartElementCrudRepository;
    private final CustomRepository customRepository;
    private final OrderCrudRepository orderCrudRepository;
    private final OrderItemsCrudRepository orderItemsCrudRepository;

    @Autowired
    public ProductService(
            ProductCrudRepository productCrudRepository,
            CartElementCrudRepository cartElementCrudRepository,
            CustomRepository customRepository,
            OrderCrudRepository orderCrudRepository,
            OrderItemsCrudRepository orderItemsCrudRepository
    ) {
        this.productCrudRepository = productCrudRepository;
        this.cartElementCrudRepository = cartElementCrudRepository;
        this.customRepository = customRepository;
        this.orderCrudRepository = orderCrudRepository;
        this.orderItemsCrudRepository = orderItemsCrudRepository;
    }

    @CacheEvict(value = "products", key = "#productDto.name")
    public Mono<Void> save(ProductDto productDto) {
        return productCrudRepository.findByName(productDto.getName())
                .defaultIfEmpty(new ProductEntity())
                .map(productEntity -> {
                    productEntity.setName(productDto.getName());
                    productEntity.setDescription(productDto.getDescription());
                    productEntity.setPrice(productDto.getPrice());
                    productEntity.setImgName(productDto.getImgName());
                    return productEntity;
                })
                .flatMap(productCrudRepository::save)
                .then();
    }

    public Mono<Long> getProductsCount(int onePageProductsCount) {
        return productCrudRepository
                .count()
                .map(count -> (count % onePageProductsCount == 0 ? count / onePageProductsCount : count / onePageProductsCount + 1));
    }

    public Flux<ProductDto> getProducts(ShopUserDetails userDetails, int pageSize, int pageNumber, String search, String sort) {
        int offset = pageNumber * pageSize;
        String sortField = switch (sort) {
            case "ALPHA" -> "name";
            case "PRICE" -> "price";
            default -> null;
        };

        Mono<List<ProductEntity>> productsMono;

        if (search != null && !search.isEmpty()) {
            if ("NO".equals(sort)) {
                productsMono = customRepository.findByNameContainingIgnoreCasePaged(search, offset, pageSize);
            } else {
                productsMono = customRepository.findByNameContainingIgnoreCasePagedSorted(search, offset, pageSize, sortField);
            }
        } else {
            if ("NO".equals(sort)) {
                productsMono = customRepository.findPaged(offset, pageSize);
            } else {
                productsMono = customRepository.findPagedSorted(offset, pageSize, sortField);
            }
        }

        if (userDetails != null) {
            Long userId = userDetails.getId();
            return productsMono.flatMapMany(Flux::fromIterable)
                    .flatMap(product ->
                            cartElementCrudRepository.findByProductId(product.getId())
                                    .defaultIfEmpty(new CartElementEntity(userId, product.getId(), product, 0))
                                    .map(cartElement -> mapEntityToDto(product, cartElement.getQuantity()))
                    );
        } else {
            return productsMono.flatMapMany(Flux::fromIterable)
                    .map(product -> mapEntityToDto(product, 0));
        }
    }

    @Cacheable(value = "products", key = "#id")
    public Mono<ProductDto> getProductById(Long id) {
        return productCrudRepository.findById(id).mapNotNull(productEntity -> mapEntityToDto(productEntity, null));
    }

    public Mono<Void> changeCountInCart(ShopUserDetails userDetails, Long productId, String action) {
        Long userId = userDetails.getId();
        Mono<CartElementEntity> existingCartElementMono = cartElementCrudRepository.findByProductIdAndUserId(productId, userId);

        Mono<CartElementEntity> cartElementMono = existingCartElementMono.switchIfEmpty(
                productCrudRepository.findById(productId)
                        .map(product -> new CartElementEntity(userId, product.getId(), product, 0))
        );

        return cartElementMono.flatMap(cartElement -> {
            switch (action) {
                case "plus" -> {
                    cartElement.incrementQuantity();
                    return cartElementCrudRepository.save(cartElement).then();
                }
                case "minus" -> {
                    cartElement.decrementQuantity();
                    return cartElementCrudRepository.save(cartElement).then();
                }
                case "delete" -> {
                    return cartElementCrudRepository.delete(cartElement);
                }
                default -> {
                    return Mono.error(new IllegalArgumentException("Unknown action: " + action));
                }
            }
        });
    }

    public Flux<ProductDto> getCart(ShopUserDetails userDetails) {
        return cartElementCrudRepository.findAllByUserId(userDetails.getId())
                .publishOn(Schedulers.boundedElastic())
                .map(cartElement -> {
                    ProductEntity product = productCrudRepository.findById(cartElement.getProductId()).block();
                    assert product != null;
                    int quantity = Objects.requireNonNull(
                            cartElementCrudRepository.findByProductId(product.getId()).block()
                    ).getQuantity();
                    return mapEntityToDto(product, quantity);
                });
    }

    public Mono<Integer> getSumFromItemsList(List<ProductDto> products) {
        return Mono.just(products.stream().mapToInt(x -> (x.getPrice() * x.getCount())).sum());
    }

    public Flux<OrderEntity> getAllUserOrders(ShopUserDetails userDetails) {
        return orderCrudRepository.findByUserId(userDetails.getId());
    }

    public Mono<Long> getMaxOrderId() {
        return customRepository.getMaxOrderId();
    }

    public Mono<Long> makeOrder(ShopUserDetails userDetails) {
        Long userId = userDetails.getId();
        OrderEntity orderEntity = new OrderEntity(userId);
        return orderCrudRepository.save(orderEntity)
                .then(getMaxOrderId())
                .flatMap(id ->
                        getCart(userDetails)
                                .flatMap(productDto ->
                                        productCrudRepository.findByName(productDto.getName())
                                                .map(productEntity -> new OrderItemEntity(userId, id, productEntity.getId(), productDto.getCount()))
                                )
                                .collectList()
                                .flatMap(orderItems ->
                                        orderItemsCrudRepository.saveAll(orderItems).then()
                                                .then(cartElementCrudRepository.deleteAllByUserId(userId))
                                                .thenReturn(id)
                                )
                );


    }

    public Flux<ProductDto> getOrderById(Long orderId) {
        return orderItemsCrudRepository.findByOrderId(orderId)
                .flatMap(item ->
                        productCrudRepository.findById(item.getProductId())
                                .map(productEntity -> mapEntityToDto(productEntity, item.getQuantity()))
                );

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
