package ru.yjailbir.shopappyandexpracticumsprint5;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.ProductDto;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.CartElementEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.OrderEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.OrderItemEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.ProductEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.CartElementCrudRepository;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.CustomRepository;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.OrderCrudRepository;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.OrderItemsCrudRepository;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.ProductCrudRepository;
import ru.yjailbir.shopappyandexpracticumsprint5.service.ProductService;

public class ProductServiceUnitTest {

    @Mock
    private ProductCrudRepository productCrudRepository;

    @Mock
    private CartElementCrudRepository cartElementCrudRepository;

    @Mock
    private CustomRepository customRepository;

    @Mock
    private OrderCrudRepository orderCrudRepository;

    @Mock
    private OrderItemsCrudRepository orderItemsCrudRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSave_existingProduct() {
        ProductDto dto = new ProductDto(null, "Product1", "Desc", 100, "img.png", null);
        ProductEntity existing = new ProductEntity();
        when(productCrudRepository.findByName("Product1")).thenReturn(Mono.just(existing));
        when(productCrudRepository.save(any(ProductEntity.class))).thenReturn(Mono.just(existing));

        StepVerifier.create(productService.save(dto))
                .verifyComplete();

        verify(productCrudRepository).save(existing);
    }

    @Test
    public void testSave_newProduct() {
        ProductDto dto = new ProductDto(null, "Product2", "Description", 200, "img2.png", null);
        when(productCrudRepository.findByName("Product2")).thenReturn(Mono.empty());
        when(productCrudRepository.save(any(ProductEntity.class))).thenReturn(Mono.just(new ProductEntity()));

        StepVerifier.create(productService.save(dto))
                .verifyComplete();

        verify(productCrudRepository).save(any(ProductEntity.class));
    }

    @Test
    public void testGetProductsCount_exactDivision() {
        // При 10 продуктах и 5 на странице должно получиться 2 страницы
        when(productCrudRepository.count()).thenReturn(Mono.just(10L));
        StepVerifier.create(productService.getProductsCount(5))
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    public void testGetProductsCount_nonExactDivision() {
        // При 10 продуктах и 3 на странице должно получиться 4 страницы (3+3+3+1)
        when(productCrudRepository.count()).thenReturn(Mono.just(10L));
        StepVerifier.create(productService.getProductsCount(3))
                .expectNext(4L)
                .verifyComplete();
    }

    @Test
    public void testGetProducts_searchNonEmpty_noSort() {
        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setName("Alpha");
        product.setDescription("Desc");
        product.setPrice(100);
        product.setImgName("img.png");

        CartElementEntity cartElement = new CartElementEntity(1L, product, 2);
        List<ProductEntity> productList = List.of(product);

        when(customRepository.findByNameContainingIgnoreCasePaged("Al", 0, 10))
                .thenReturn(Mono.just(productList));
        when(cartElementCrudRepository.findByProductId(1L))
                .thenReturn(Mono.just(cartElement));

        Flux<ProductDto> result = productService.getProducts(10, 0, "Al", "NO");
        StepVerifier.create(result)
                .assertNext(dto -> {
                    assert dto.getId().equals(1L);
                    assert dto.getName().equals("Alpha");
                    assert dto.getCount() == 2;
                })
                .verifyComplete();
    }

    @Test
    public void testGetProducts_searchNonEmpty_sorted() {
        ProductEntity product = new ProductEntity();
        product.setId(2L);
        product.setName("Beta");
        product.setDescription("Desc");
        product.setPrice(150);
        product.setImgName("img2.png");

        CartElementEntity cartElement = new CartElementEntity(2L, product, 3);
        List<ProductEntity> productList = List.of(product);

        when(customRepository.findByNameContainingIgnoreCasePagedSorted("Be", 10, 5, "name"))
                .thenReturn(Mono.just(productList));
        when(cartElementCrudRepository.findByProductId(2L))
                .thenReturn(Mono.just(cartElement));

        Flux<ProductDto> result = productService.getProducts(5, 2, "Be", "ALPHA");
        StepVerifier.create(result)
                .assertNext(dto -> {
                    assert dto.getId().equals(2L);
                    assert dto.getName().equals("Beta");
                    assert dto.getCount() == 3;
                })
                .verifyComplete();
    }

    @Test
    public void testGetProducts_emptySearch_noSort() {
        ProductEntity product = new ProductEntity();
        product.setId(3L);
        product.setName("Gamma");
        product.setDescription("Desc");
        product.setPrice(250);
        product.setImgName("img3.png");

        CartElementEntity cartElement = new CartElementEntity(3L, product, 1);
        List<ProductEntity> productList = List.of(product);

        when(customRepository.findPaged(20, 10))
                .thenReturn(Mono.just(productList));
        when(cartElementCrudRepository.findByProductId(3L))
                .thenReturn(Mono.just(cartElement));

        Flux<ProductDto> result = productService.getProducts(10, 2, "", "NO");
        StepVerifier.create(result)
                .assertNext(dto -> {
                    assert dto.getId().equals(3L);
                    assert dto.getName().equals("Gamma");
                    assert dto.getCount() == 1;
                })
                .verifyComplete();
    }

    @Test
    public void testGetProducts_emptySearch_sorted() {
        ProductEntity product = new ProductEntity();
        product.setId(4L);
        product.setName("Delta");
        product.setDescription("Desc");
        product.setPrice(300);
        product.setImgName("img4.png");

        CartElementEntity cartElement = new CartElementEntity(4L, product, 4);
        List<ProductEntity> productList = List.of(product);

        when(customRepository.findPagedSorted(30, 15, "price"))
                .thenReturn(Mono.just(productList));
        when(cartElementCrudRepository.findByProductId(4L))
                .thenReturn(Mono.just(cartElement));

        Flux<ProductDto> result = productService.getProducts(15, 2, "", "PRICE");
        StepVerifier.create(result)
                .assertNext(dto -> {
                    assert dto.getId().equals(4L);
                    assert dto.getName().equals("Delta");
                    assert dto.getCount() == 4;
                })
                .verifyComplete();
    }

    @Test
    public void testGetProductById_found() {
        ProductEntity product = new ProductEntity();
        product.setId(5L);
        product.setName("Epsilon");
        product.setDescription("Desc");
        product.setPrice(500);
        product.setImgName("img5.png");

        when(productCrudRepository.findById(5L)).thenReturn(Mono.just(product));
        StepVerifier.create(productService.getProductById(5L))
                .assertNext(dto -> {
                    assert dto.getId().equals(5L);
                    assert dto.getName().equals("Epsilon");
                    assert dto.getCount() == null;
                })
                .verifyComplete();
    }

    @Test
    public void testGetProductById_notFound() {
        when(productCrudRepository.findById(6L)).thenReturn(Mono.empty());
        StepVerifier.create(productService.getProductById(6L))
                .verifyComplete();
    }

    @Test
    public void testChangeCountInCart_plus_noExistingCart() {
        ProductEntity product = new ProductEntity();
        product.setId(8L);
        product.setName("Eta");
        product.setDescription("Desc");
        product.setPrice(800);
        product.setImgName("img8.png");

        when(cartElementCrudRepository.findByProductId(8L)).thenReturn(Mono.empty());
        when(productCrudRepository.findById(8L)).thenReturn(Mono.just(product));
        when(cartElementCrudRepository.save(any(CartElementEntity.class)))
                .thenAnswer(invocation -> {
                    CartElementEntity ce = invocation.getArgument(0);
                    return Mono.just(ce);
                });

        StepVerifier.create(productService.changeCountInCart(8L, "plus"))
                .verifyComplete();
    }

    @Test
    public void testGetCart() {
        ProductEntity product = new ProductEntity();
        product.setId(12L);
        product.setName("Kappa");
        product.setDescription("Desc");
        product.setPrice(1200);
        product.setImgName("img12.png");

        CartElementEntity cartElement = new CartElementEntity(12L, product, 3);
        when(cartElementCrudRepository.findAll()).thenReturn(Flux.just(cartElement));
        when(productCrudRepository.findById(12L)).thenReturn(Mono.just(product));
        when(cartElementCrudRepository.findByProductId(12L)).thenReturn(Mono.just(cartElement));

        StepVerifier.create(productService.getCart())
                .assertNext(dto -> {
                    assert dto.getId().equals(12L);
                    assert dto.getName().equals("Kappa");
                    assert dto.getCount() == 3;
                })
                .verifyComplete();
    }

    @Test
    public void testGetSumFromItemsList() {
        ProductDto p1 = new ProductDto(13L, "Lambda", "Desc", 100, "img", 2);
        ProductDto p2 = new ProductDto(14L, "Mu", "Desc", 150, "img", 3);
        List<ProductDto> list = List.of(p1, p2);

        StepVerifier.create(productService.getSumFromItemsList(list))
                .expectNext(100 * 2 + 150 * 3)
                .verifyComplete();
    }

    @Test
    public void testGetAllOrders() {
        OrderEntity order1 = new OrderEntity();
        OrderEntity order2 = new OrderEntity();
        when(orderCrudRepository.findAll()).thenReturn(Flux.just(order1, order2));

        StepVerifier.create(productService.getAllOrders())
                .expectNext(order1, order2)
                .verifyComplete();
    }

    @Test
    public void testGetMaxOrderId() {
        when(customRepository.getMaxOrderId()).thenReturn(Mono.just(100L));
        StepVerifier.create(productService.getMaxOrderId())
                .expectNext(100L)
                .verifyComplete();
    }

    @Test
    public void testMakeOrder() {
        OrderEntity order = new OrderEntity();
        when(orderCrudRepository.save(any(OrderEntity.class))).thenReturn(Mono.just(order));
        when(customRepository.getMaxOrderId()).thenReturn(Mono.just(200L));

        ProductEntity product = new ProductEntity();
        product.setId(15L);
        product.setName("Nu");
        product.setDescription("Desc");
        product.setPrice(500);
        product.setImgName("img15.png");

        CartElementEntity cartElement = new CartElementEntity(15L, product, 2);
        when(cartElementCrudRepository.findAll()).thenReturn(Flux.just(cartElement));
        when(productCrudRepository.findById(15L)).thenReturn(Mono.just(product));
        when(cartElementCrudRepository.findByProductId(15L)).thenReturn(Mono.just(cartElement));

        // Внутри makeOrder вызывается productCrudRepository.findByName, возвращаем продукт
        when(productCrudRepository.findByName("Nu")).thenReturn(Mono.just(product));

        // Сохранение заказа и очистка корзины
        when(orderItemsCrudRepository.saveAll(any(List.class))).thenReturn(Flux.empty());
        when(cartElementCrudRepository.deleteAll()).thenReturn(Mono.empty());

        StepVerifier.create(productService.makeOrder())
                .expectNext(200L)
                .verifyComplete();
    }

    @Test
    public void testGetOrderById() {
        OrderItemEntity orderItem = new OrderItemEntity(300L, 16L, 4);
        when(orderItemsCrudRepository.findByOrderId(300L)).thenReturn(Flux.just(orderItem));

        ProductEntity product = new ProductEntity();
        product.setId(16L);
        product.setName("Xi");
        product.setDescription("Desc");
        product.setPrice(400);
        product.setImgName("img16.png");
        when(productCrudRepository.findById(16L)).thenReturn(Mono.just(product));

        Flux<ProductDto> result = productService.getOrderById(300L);
        StepVerifier.create(result)
                .assertNext(dto -> {
                    assert dto.getId().equals(16L);
                    assert dto.getName().equals("Xi");
                    assert dto.getCount() == 4;
                })
                .verifyComplete();
    }
}
