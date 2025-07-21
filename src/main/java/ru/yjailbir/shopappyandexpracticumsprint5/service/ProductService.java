package ru.yjailbir.shopappyandexpracticumsprint5.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.ProductDto;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.CartElementEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.OrderEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.OrderItemEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.ProductEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.CartElementCrudRepository;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.OrderCrudRepository;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.ProductCrudRepository;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.CustomRepository;

import java.util.List;
import java.util.Objects;

@Service
public class ProductService {
    private final ProductCrudRepository productCrudRepository;
    private final CartElementCrudRepository cartElementCrudRepository;
    private final CustomRepository customRepository;
    private final OrderCrudRepository orderCrudRepository;

    @Autowired
    public ProductService(
            ProductCrudRepository productCrudRepository,
            CartElementCrudRepository cartElementCrudRepository,
            CustomRepository customRepository,
            OrderCrudRepository orderCrudRepository
    ) {
        this.productCrudRepository = productCrudRepository;
        this.cartElementCrudRepository = cartElementCrudRepository;
        this.customRepository = customRepository;
        this.orderCrudRepository = orderCrudRepository;
    }

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

    public Flux<ProductDto> getProducts(int pageSize, int pageNumber, String search, String sort) {
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

        return productsMono.flatMapMany(Flux::fromIterable)
                .flatMap(product ->
                        cartElementCrudRepository.findByProductEntity_Id(product.getId())
                                .defaultIfEmpty(new CartElementEntity(product, 0))
                                .map(cartElement -> mapEntityToDto(product, cartElement.getQuantity()))
                );
    }

    public Mono<ProductDto> getProductById(Long id) {
        return productCrudRepository.findById(id).mapNotNull(productEntity -> mapEntityToDto(productEntity, null));
    }

    public Mono<Void> changeCountInCart(Long productId, String action) {

        Mono<CartElementEntity> existingCartElementMono = cartElementCrudRepository.findByProductEntity_Id(productId);

        Mono<CartElementEntity> cartElementMono = existingCartElementMono.switchIfEmpty(
                productCrudRepository.findById(productId)
                        .map(product -> new CartElementEntity(product, 0))
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

    public Flux<ProductDto> getCart() {
        return cartElementCrudRepository.findAll()
                .publishOn(Schedulers.boundedElastic())
                .map(cartElement -> {
                    ProductEntity product = cartElement.getProductEntity();
                    int quantity = Objects.requireNonNull(
                            cartElementCrudRepository.findByProductEntity_Id(product.getId()).block()
                    ).getQuantity();
                    return mapEntityToDto(product, quantity);
                });
    }

    public Mono<Integer> getSumFromItemsList(List<ProductDto> products) {
        return Mono.just(products.stream().mapToInt(x -> (x.getPrice() * x.getCount())).sum());
    }

    public Flux<OrderEntity> getAllOrders() {
        return orderCrudRepository.findAll();
    }

    public Mono<Long> getMaxOrderId() {
        return customRepository.getMaxOrderId();
    }

    public Mono<Long> makeOrder() {
        OrderEntity orderEntity = new OrderEntity();

        return getCart()
                .flatMap(productDto ->
                        productCrudRepository.findByName(productDto.getName())
                                .map(productEntity -> new OrderItemEntity(orderEntity, productEntity, productDto.getCount()))
                )
                .collectList()
                .flatMap(orderItems -> {
                    orderEntity.setItems(orderItems);
                    return orderCrudRepository.save(orderEntity);
                })
                .then(cartElementCrudRepository.deleteAll())
                .then(getMaxOrderId());
    }

    public Flux<ProductDto> getOrderById(Long orderId) {
        return orderCrudRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new RuntimeException("Order not found: " + orderId)))
                .flatMapMany(orderEntity -> Flux.fromIterable(orderEntity.getItems()))
                .map(item -> mapEntityToDto(item.getProduct(), item.getQuantity()));
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
