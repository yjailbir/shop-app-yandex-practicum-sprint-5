package ru.yjailbir.shopappyandexpracticumsprint5;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yjailbir.shopappyandexpracticumsprint5.controller.ShopController;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.ProductDto;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.OrderEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.service.ProductService;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@WebFluxTest(controllers = ShopController.class)
class ShopControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProductService productService;

    @Test
    void testHomePage() {
        ProductDto product = new ProductDto(1L, "TestProduct", "Description", 100, "image.png", 1);

        when(productService.getProductsCount(5)).thenReturn(Mono.just(1L));
        when(productService.getProducts(5, 0, "", "NO")).thenReturn(Flux.just(product));

        webTestClient.get()
                .uri("/shop?pageSize=5&pageNumber=0&search=&sort=NO")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("TestProduct"));
                });
    }

    @Test
    void testGetItem() {
        ProductDto product = new ProductDto(2L, "Item", "Details", 200, "item.png", 2);
        when(productService.getProductById(2L)).thenReturn(Mono.just(product));

        webTestClient.get()
                .uri("/shop/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(resp -> {
                    assert Objects.requireNonNull(resp.getResponseBody()).contains("Item");
                });
    }

    @Test
    void testAddItemToCart_redirectMain() {
        when(productService.changeCountInCart(3L, "plus")).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/shop/change/3")
                .contentType(APPLICATION_FORM_URLENCODED)
                .bodyValue("action=plus&redirect=main")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/shop");
    }

    @Test
    void testAddItemToCart_redirectCart() {
        when(productService.changeCountInCart(4L, "plus")).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/shop/change/4")
                .contentType(APPLICATION_FORM_URLENCODED)
                .bodyValue("action=plus&redirect=cart")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/shop/cart");
    }

    @Test
    void testCartPage() {
        ProductDto product = new ProductDto(5L, "CartProduct", "desc", 50, "cart.png", 2);
        List<ProductDto> list = List.of(product);

        when(productService.getCart()).thenReturn(Flux.fromIterable(list));
        when(productService.getSumFromItemsList(list)).thenReturn(Mono.just(100));

        webTestClient.get()
                .uri("/shop/cart")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String body = response.getResponseBody();
                    assert Objects.requireNonNull(body).contains("CartProduct");
                    assert body.contains("100");
                });
    }

    @Test
    void testMakeOrder() {
        when(productService.makeOrder()).thenReturn(Mono.just(42L));

        webTestClient.post()
                .uri("/shop/order")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/shop/orders/42");
    }

    @Test
    void testOrderById() {
        ProductDto product = new ProductDto(6L, "OrderedProduct", "details", 20, "img.png", 3);
        List<ProductDto> list = List.of(product);

        when(productService.getOrderById(123L)).thenReturn(Flux.fromIterable(list));
        when(productService.getSumFromItemsList(list)).thenReturn(Mono.just(60));

        webTestClient.get()
                .uri("/shop/orders/123")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(resp -> {
                    String body = resp.getResponseBody();
                    assert Objects.requireNonNull(body).contains("OrderedProduct");
                    assert body.contains("123");
                    assert body.contains("60");
                });
    }

    @Test
    void testOrdersList() {
        OrderEntity o1 = new OrderEntity();
        OrderEntity o2 = new OrderEntity();
        when(productService.getAllOrders()).thenReturn(Flux.just(o1, o2));

        webTestClient.get()
                .uri("/shop/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(resp -> {
                    String body = resp.getResponseBody();
                    assert Objects.requireNonNull(body).contains("orders"); // шаблон должен отрисовать список
                });
    }
}

