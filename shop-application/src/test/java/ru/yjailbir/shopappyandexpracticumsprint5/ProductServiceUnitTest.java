package ru.yjailbir.shopappyandexpracticumsprint5;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.ProductDto;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.*;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.*;
import ru.yjailbir.shopappyandexpracticumsprint5.service.ProductService;
import ru.yjailbir.shopappyandexpracticumsprint5.service.ShopUserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

    @Mock private ProductCrudRepository productCrudRepository;
    @Mock private CartElementCrudRepository cartElementCrudRepository;
    @Mock private CustomRepository customRepository;
    @Mock private OrderCrudRepository orderCrudRepository;
    @Mock private OrderItemsCrudRepository orderItemsCrudRepository;

    @InjectMocks private ProductService productService;

    private ShopUserDetails user;

    @BeforeEach
    void setUp() {
        UserEntity userEntity = new UserEntity("user", "password1");
        userEntity.setId(1L);
        user = new ShopUserDetails(userEntity);
    }

    @Test
    void testSave_existingProduct() {
        ProductDto dto = new ProductDto(null, "Product1", "Desc", 100, "img.png", null);

        ProductEntity existing = new ProductEntity();
        existing.setId(10L);
        existing.setName("Product1");
        existing.setDescription("Old");
        existing.setPrice(1);
        existing.setImgName("old.png");

        when(productCrudRepository.findByName("Product1")).thenReturn(Mono.just(existing));
        when(productCrudRepository.save(any(ProductEntity.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(productService.save(dto)).verifyComplete();

        ArgumentCaptor<ProductEntity> captor = ArgumentCaptor.forClass(ProductEntity.class);
        verify(productCrudRepository).save(captor.capture());
        ProductEntity saved = captor.getValue();
        assertEquals("Product1", saved.getName());
        assertEquals("Desc", saved.getDescription());
        assertEquals(100, saved.getPrice());
        assertEquals("img.png", saved.getImgName());
    }

    @Test
    void testSave_newProduct() {
        ProductDto dto = new ProductDto(null, "Product2", "Description", 200, "img2.png", null);
        when(productCrudRepository.findByName("Product2")).thenReturn(Mono.empty());
        when(productCrudRepository.save(any(ProductEntity.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(productService.save(dto)).verifyComplete();

        verify(productCrudRepository).save(any(ProductEntity.class));
    }


    @Test
    void testGetProductsCount_exactDivision() {
        when(productCrudRepository.count()).thenReturn(Mono.just(10L));
        StepVerifier.create(productService.getProductsCount(5))
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    void testGetProductsCount_nonExactDivision() {
        when(productCrudRepository.count()).thenReturn(Mono.just(10L));
        StepVerifier.create(productService.getProductsCount(3))
                .expectNext(4L)
                .verifyComplete();
    }

    @Test
    void testGetProducts_searchNonEmpty_noSort_withUser() {
        ProductEntity product = makeProduct(1L, "Alpha", 100, "img.png");
        List<ProductEntity> list = List.of(product);

        when(customRepository.findByNameContainingIgnoreCasePaged("Al", 0, 10)).thenReturn(Mono.just(list));
        CartElementEntity ce = new CartElementEntity(user.getId(), product.getId(), product, 2);
        when(cartElementCrudRepository.findByProductId(1L)).thenReturn(Mono.just(ce));

        StepVerifier.create(productService.getProducts(user, 10, 0, "Al", "NO"))
                .assertNext(dto -> {
                    assertEquals(1L, dto.getId());
                    assertEquals("Alpha", dto.getName());
                    assertEquals(2, dto.getCount());
                })
                .verifyComplete();

        verify(customRepository).findByNameContainingIgnoreCasePaged("Al", 0, 10);
    }

    @Test
    void testGetProducts_searchNonEmpty_sorted_ALPHA_withUser() {
        ProductEntity product = makeProduct(2L, "Beta", 150, "img2.png");
        List<ProductEntity> list = List.of(product);

        when(customRepository.findByNameContainingIgnoreCasePagedSorted("Be", 10, 5, "name"))
                .thenReturn(Mono.just(list));
        when(cartElementCrudRepository.findByProductId(2L))
                .thenReturn(Mono.just(new CartElementEntity(user.getId(), 2L, product, 3)));

        StepVerifier.create(productService.getProducts(user, 5, 2, "Be", "ALPHA"))
                .assertNext(dto -> {
                    assertEquals(2L, dto.getId());
                    assertEquals("Beta", dto.getName());
                    assertEquals(3, dto.getCount());
                })
                .verifyComplete();

        verify(customRepository).findByNameContainingIgnoreCasePagedSorted("Be", 10, 5, "name");
    }

    @Test
    void testGetProducts_emptySearch_noSort_withUser() {
        ProductEntity product = makeProduct(3L, "Gamma", 250, "img3.png");
        when(customRepository.findPaged(20, 10)).thenReturn(Mono.just(List.of(product)));
        when(cartElementCrudRepository.findByProductId(3L))
                .thenReturn(Mono.just(new CartElementEntity(user.getId(), 3L, product, 1)));

        StepVerifier.create(productService.getProducts(user, 10, 2, "", "NO"))
                .assertNext(dto -> {
                    assertEquals(3L, dto.getId());
                    assertEquals("Gamma", dto.getName());
                    assertEquals(1, dto.getCount());
                })
                .verifyComplete();

        verify(customRepository).findPaged(20, 10);
    }

    @Test
    void testGetProducts_emptySearch_sorted_PRICE_withUser() {
        ProductEntity product = makeProduct(4L, "Delta", 300, "img4.png");
        when(customRepository.findPagedSorted(30, 15, "price")).thenReturn(Mono.just(List.of(product)));
        when(cartElementCrudRepository.findByProductId(4L))
                .thenReturn(Mono.just(new CartElementEntity(user.getId(), 4L, product, 4)));

        StepVerifier.create(productService.getProducts(user, 15, 2, "", "PRICE"))
                .assertNext(dto -> {
                    assertEquals(4L, dto.getId());
                    assertEquals("Delta", dto.getName());
                    assertEquals(4, dto.getCount());
                })
                .verifyComplete();

        verify(customRepository).findPagedSorted(30, 15, "price");
    }

    @Test
    void testGetProducts_withoutUser_countsZero_noCartLookups() {
        ProductEntity product = makeProduct(5L, "NoUser", 10, "img");
        when(customRepository.findPaged(0, 5)).thenReturn(Mono.just(List.of(product)));

        StepVerifier.create(productService.getProducts(null, 5, 0, "", "NO"))
                .assertNext(dto -> {
                    assertEquals(5L, dto.getId());
                    assertEquals(0, dto.getCount());
                })
                .verifyComplete();

        verify(cartElementCrudRepository, never()).findByProductId(anyLong());
        verify(cartElementCrudRepository, never()).findByProductIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void testGetProductById_found() {
        ProductEntity product = makeProduct(6L, "Epsilon", 500, "img5.png");
        when(productCrudRepository.findById(6L)).thenReturn(Mono.just(product));

        StepVerifier.create(productService.getProductById(6L))
                .assertNext(dto -> {
                    assertEquals(6L, dto.getId());
                    assertEquals("Epsilon", dto.getName());
                    assertNull(dto.getCount()); // как и в старых тестах
                })
                .verifyComplete();
    }

    @Test
    void testGetProductById_notFound() {
        when(productCrudRepository.findById(7L)).thenReturn(Mono.empty());
        StepVerifier.create(productService.getProductById(7L)).verifyComplete();
    }


    @Test
    void testChangeCountInCart_plus_noExistingCart_createsAndSaves() {
        ProductEntity product = makeProduct(8L, "Eta", 800, "img8.png");

        when(cartElementCrudRepository.findByProductIdAndUserId(8L, user.getId())).thenReturn(Mono.empty());
        when(productCrudRepository.findById(8L)).thenReturn(Mono.just(product));
        when(cartElementCrudRepository.save(any(CartElementEntity.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(productService.changeCountInCart(user, 8L, "plus")).verifyComplete();

        verify(cartElementCrudRepository).save(any(CartElementEntity.class));
    }

    @Test
    void testGetCart_userScoped() {
        ProductEntity product = makeProduct(12L, "Kappa", 1200, "img12.png");
        CartElementEntity ce = new CartElementEntity(user.getId(), 12L, product, 3);

        when(cartElementCrudRepository.findAllByUserId(user.getId())).thenReturn(Flux.just(ce));

        when(productCrudRepository.findById(12L)).thenReturn(Mono.just(product));
        when(cartElementCrudRepository.findByProductId(12L)).thenReturn(Mono.just(ce));

        StepVerifier.create(productService.getCart(user))
                .assertNext(dto -> {
                    assertEquals(12L, dto.getId());
                    assertEquals("Kappa", dto.getName());
                    assertEquals(3, dto.getCount());
                })
                .verifyComplete();
    }

    @Test
    void testGetSumFromItemsList() {
        ProductDto p1 = new ProductDto(13L, "Lambda", "Desc", 100, "img", 2);
        ProductDto p2 = new ProductDto(14L, "Mu", "Desc", 150, "img", 3);
        List<ProductDto> list = List.of(p1, p2);

        StepVerifier.create(productService.getSumFromItemsList(list))
                .expectNext(100 * 2 + 150 * 3)
                .verifyComplete();
    }

    @Test
    void testGetAllUserOrders() {
        OrderEntity o1 = new OrderEntity(user.getId());
        OrderEntity o2 = new OrderEntity(user.getId());
        when(orderCrudRepository.findByUserId(user.getId())).thenReturn(Flux.just(o1, o2));

        StepVerifier.create(productService.getAllUserOrders(user))
                .expectNext(o1, o2)
                .verifyComplete();
    }

    @Test
    void testGetMaxOrderId() {
        when(customRepository.getMaxOrderId()).thenReturn(Mono.just(100L));
        StepVerifier.create(productService.getMaxOrderId())
                .expectNext(100L)
                .verifyComplete();
    }

    @Test
    void testGetOrderById() {
        OrderItemEntity item = new OrderItemEntity(user.getId(), 300L, 16L, 4);

        when(orderItemsCrudRepository.findByOrderId(300L)).thenReturn(Flux.just(item));

        ProductEntity product = makeProduct(16L, "Xi", 400, "img16.png");
        when(productCrudRepository.findById(16L)).thenReturn(Mono.just(product));

        StepVerifier.create(productService.getOrderById(300L))
                .assertNext(dto -> {
                    assertEquals(16L, dto.getId());
                    assertEquals("Xi", dto.getName());
                    assertEquals(4, dto.getCount());
                })
                .verifyComplete();
    }


    private ProductEntity makeProduct(Long id, String name, int price, String img) {
        ProductEntity p = new ProductEntity();
        p.setId(id);
        p.setName(name);
        p.setDescription("Desc");
        p.setPrice(price);
        p.setImgName(img);
        return p;
    }
}
