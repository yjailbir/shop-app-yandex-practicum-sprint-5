package ru.yjailbir.shopappyandexpracticumsprint5;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.ProductDto;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.*;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.*;
import ru.yjailbir.shopappyandexpracticumsprint5.service.ProductService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductCrudRepository productCrudRepository;
    private CartElementCrudRepository cartElementCrudRepository;
    private CustomRepository customRepository;
    private OrderCrudRepository orderCrudRepository;
    private OrderItemsCrudRepository orderItemsCrudRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productCrudRepository = mock(ProductCrudRepository.class);
        cartElementCrudRepository = mock(CartElementCrudRepository.class);
        customRepository = mock(CustomRepository.class);
        orderCrudRepository = mock(OrderCrudRepository.class);
        orderItemsCrudRepository = mock(OrderItemsCrudRepository.class);
        productService = new ProductService(productCrudRepository, cartElementCrudRepository, customRepository, orderCrudRepository, orderItemsCrudRepository);
    }

    @Test
    void save_shouldCreateOrUpdateProduct() {
        ProductDto dto = new ProductDto(null, "Name", "Desc", 100, "img.png", null);
        ProductEntity savedEntity = new ProductEntity();

        when(productCrudRepository.findByName("Name")).thenReturn(Mono.empty());
        when(productCrudRepository.save(any(ProductEntity.class))).thenReturn(Mono.just(savedEntity));

        StepVerifier.create(productService.save(dto)).verifyComplete();

        verify(productCrudRepository).save(any(ProductEntity.class));
    }

    @Test
    void getProductsCount_shouldReturnCorrectPageCount() {
        when(productCrudRepository.count()).thenReturn(Mono.just(11L));

        StepVerifier.create(productService.getProductsCount(5))
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void getProductById_shouldReturnDtoWhenFound() {
        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName("Test");

        when(productCrudRepository.findById(1L)).thenReturn(Mono.just(entity));

        StepVerifier.create(productService.getProductById(1L))
                .expectNextMatches(dto -> dto.getName().equals("Test"))
                .verifyComplete();
    }

    @Test
    void getAllOrders_shouldReturnAllOrders() {
        OrderEntity order1 = new OrderEntity();
        OrderEntity order2 = new OrderEntity();

        when(orderCrudRepository.findAll()).thenReturn(Flux.just(order1, order2));

        StepVerifier.create(productService.getAllOrders())
                .expectNext(order1)
                .expectNext(order2)
                .verifyComplete();
    }

    @Test
    void getMaxOrderId_shouldReturnCorrectValue() {
        when(customRepository.getMaxOrderId()).thenReturn(Mono.just(10L));

        StepVerifier.create(productService.getMaxOrderId())
                .expectNext(10L)
                .verifyComplete();
    }

    @Test
    void getOrderById_shouldReturnProductDtos() {
        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setName("Test");
        product.setDescription("Desc");
        product.setPrice(100);
        product.setImgName("img.png");

        OrderItemEntity item = new OrderItemEntity();
        item.setProduct(product);
        item.setQuantity(3);

        OrderEntity order = new OrderEntity();
        order.setItems(List.of(item));

        when(orderCrudRepository.findById(1L)).thenReturn(Mono.just(order));

        StepVerifier.create(productService.getOrderById(1L))
                .expectNextMatches(dto -> dto.getName().equals("Test") && dto.getCount() == 3)
                .verifyComplete();
    }
}


