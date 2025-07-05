package ru.yjailbir.shopappyandexpracticumsprint5;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.ProductDto;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.CartElementEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.OrderEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.OrderItemEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.ProductEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.CartElementRepository;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.OrderRepository;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.ProductRepository;
import ru.yjailbir.shopappyandexpracticumsprint5.service.ProductService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productRepository;
    private CartElementRepository cartElementRepository;
    private OrderRepository orderRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        cartElementRepository = mock(CartElementRepository.class);
        orderRepository = mock(OrderRepository.class);
        productService = new ProductService(productRepository, cartElementRepository, orderRepository);
    }

    @Test
    void save_shouldCreateOrUpdateProduct() {
        ProductDto dto = new ProductDto(null, "Name", "Desc", 100, "img.png", null);
        when(productRepository.findByName("Name")).thenReturn(Optional.empty());

        productService.save(dto);

        verify(productRepository).save(any(ProductEntity.class));
    }

    @Test
    void getProductsCount_shouldReturnCorrectPageCount() {
        when(productRepository.count()).thenReturn(11L);
        assertEquals(3, productService.getProductsCount(5));
    }

    @Test
    void getProducts_shouldReturnProductDtosWithCartQuantity() {
        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName("Name");
        entity.setDescription("Desc");
        entity.setPrice(100);
        entity.setImgName("img.png");

        Page<ProductEntity> page = new PageImpl<>(List.of(entity));
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(cartElementRepository.findByProductEntity_Id(1L)).thenReturn(Optional.of(new CartElementEntity(entity, 2)));

        List<ProductDto> products = productService.getProducts(5, 0, "", "NO");

        assertEquals(1, products.size());
        assertEquals(2, products.getFirst().getCount());
    }

    @Test
    void getProductById_shouldReturnDtoWhenFound() {
        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName("Test");

        when(productRepository.findById(1L)).thenReturn(Optional.of(entity));

        ProductDto dto = productService.getProductById(1L);
        assertNotNull(dto);
        assertEquals("Test", dto.getName());
    }


    @Test
    void getCart_shouldReturnAllCartItems() {
        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        CartElementEntity element = new CartElementEntity(entity, 3);

        when(cartElementRepository.findAll()).thenReturn(List.of(element));
        when(cartElementRepository.findByProductEntity_Id(1L)).thenReturn(Optional.of(element));

        List<ProductDto> cart = productService.getCart();
        assertEquals(1, cart.size());
        assertEquals(3, cart.getFirst().getCount());
    }

    @Test
    void getSumFromItemsList_shouldReturnTotalSum() {
        List<ProductDto> products = List.of(
                new ProductDto(1L, "A", "", 100, "", 2),
                new ProductDto(2L, "B", "", 50, "", 1)
        );

        assertEquals(250, productService.getSumFromItemsList(products));
    }

    @Test
    void getAllOrders_shouldReturnAll() {
        List<OrderEntity> orders = List.of(new OrderEntity(), new OrderEntity());
        when(orderRepository.findAll()).thenReturn(orders);

        assertEquals(2, productService.getAllOrders().size());
    }

    @Test
    void getMaxOrderId_shouldReturnOrZero() {
        when(orderRepository.getMaxOrderId()).thenReturn(Optional.of(10L));
        assertEquals(10L, productService.getMaxOrderId());

        when(orderRepository.getMaxOrderId()).thenReturn(Optional.empty());
        assertEquals(0L, productService.getMaxOrderId());
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

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        List<ProductDto> result = productService.getOrderById(1L);
        assertEquals(1, result.size());
        assertEquals("Test", result.getFirst().getName());
        assertEquals(3, result.getFirst().getCount());
    }
}

