package ru.yjailbir.shopappyandexpracticumsprint5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.ChangeCountDto;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.PaymentsApiBalanceResponseDto;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.PaymentsApiPayRequestDto;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.ProductDto;
import ru.yjailbir.shopappyandexpracticumsprint5.service.ProductService;
import ru.yjailbir.shopappyandexpracticumsprint5.service.ShopUserDetails;

import java.util.List;

@Controller
@RequestMapping("/shop")
public class ShopController {
    private final ProductService productService;
    private final ReactiveOAuth2AuthorizedClientManager manager;
    private final WebClient webClient;

    @Autowired
    public ShopController(ProductService productService, ReactiveOAuth2AuthorizedClientManager auth2AuthorizedClientManager) {
        this.productService = productService;
        this.manager = auth2AuthorizedClientManager;
        this.webClient = WebClient.create("http://payment-service:8082");
    }

    @GetMapping
    public Mono<String> home(
            @RequestParam(name = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "sort", defaultValue = "NO") String sort,
            Model model,
            @AuthenticationPrincipal ShopUserDetails user
    ) {
        Mono<Long> totalPagesMono = productService.getProductsCount(pageSize);
        Flux<ProductDto> productsFlux = productService.getProducts(user, pageSize, pageNumber, search, sort);

        return totalPagesMono.zipWith(productsFlux.collectList())
                .map(tuple -> {
                    Long totalPages = tuple.getT1();
                    List<ProductDto> products = tuple.getT2();
                    model.addAttribute("name", search);
                    model.addAttribute("pageSize", pageSize);
                    model.addAttribute("pageNumber", pageNumber);
                    model.addAttribute("sort", sort);
                    model.addAttribute("totalPages", totalPages);
                    model.addAttribute("products", products);

                    return "main";
                });
    }

    @GetMapping("/products/{id}")
    public Mono<String> getItem(@PathVariable("id") Long id, Model model) {
        return productService.getProductById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "item";
                });
    }

    @PostMapping("/change/{id}")
    public Mono<String> addItem(
            @PathVariable("id") Long id,
            @ModelAttribute ChangeCountDto formData,
            @AuthenticationPrincipal ShopUserDetails user
    ) {
        return productService.changeCountInCart(user, id, formData.getAction())
                .then(Mono.fromSupplier(() -> {
                    if ("main".equals(formData.getRedirect())) {
                        return "redirect:/shop";
                    } else if ("cart".equals(formData.getRedirect())) {
                        return "redirect:/shop/cart";
                    } else if (formData.getRedirect().equals(id.toString())) {
                        return "redirect:/shop/" + formData.getRedirect();
                    }
                    return "redirect:/shop";
                }));
    }

    @GetMapping("/cart")
    public Mono<String> cart(Model model, @AuthenticationPrincipal ShopUserDetails user) {
        return productService.getCart(user)
                .collectList()
                .flatMap(products -> {
                    model.addAttribute("products", products);

                    Mono<Integer> sumMono = productService.getSumFromItemsList(products);
                    Mono<Long> balanceMono = manager.authorize(OAuth2AuthorizeRequest
                                    .withClientRegistrationId("shop-client")
                                    .principal(user.getUsername())
                                    .build())
                            .map(OAuth2AuthorizedClient::getAccessToken)
                            .map(OAuth2AccessToken::getTokenValue)
                            .flatMap(accessToken -> webClient.get()
                                    .uri("/payments/balance/1")
                                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                    .retrieve()
                                    .bodyToMono(PaymentsApiBalanceResponseDto.class)
                                    .map(PaymentsApiBalanceResponseDto::getBalance)
                            );

                    return Mono.zip(sumMono, balanceMono)
                            .map(tuple -> {
                                Integer sum = tuple.getT1();
                                Long balance = tuple.getT2();

                                model.addAttribute("sum", sum);
                                model.addAttribute("balance", balance);
                                return "cart";
                            });
                });
    }


    @PostMapping("/order")
    public Mono<String> makeOrder(@AuthenticationPrincipal ShopUserDetails user) {
        return productService.getCart(user)
                .collectList()
                .flatMap(items ->
                        productService.getSumFromItemsList(items)
                                .map(sum -> new PaymentsApiPayRequestDto("1", Long.valueOf(sum)))
                )
                .flatMap(paymentRequest ->
                        manager.authorize(OAuth2AuthorizeRequest
                                        .withClientRegistrationId("shop-client")
                                        .principal(user.getUsername())
                                        .build())
                                .map(OAuth2AuthorizedClient::getAccessToken)
                                .map(OAuth2AccessToken::getTokenValue)
                                .flatMap(accessToken -> webClient.post()
                                        .uri("/payments/pay")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                        .bodyValue(paymentRequest)
                                        .retrieve()
                                        .bodyToMono(Void.class)
                                )
                )
                .then(productService.makeOrder(user)
                        .map(orderId -> "redirect:/shop/orders/" + orderId)
                );
    }

    @GetMapping("/orders/{id}")
    public Mono<String> order(@PathVariable("id") Long id, Model model) {
        return productService.getOrderById(id)
                .collectList()
                .flatMap(products -> {
                    model.addAttribute("orderId", id);
                    model.addAttribute("products", products);
                    return productService.getSumFromItemsList(products)
                            .map(sum -> {
                                model.addAttribute("sum", sum);
                                return "order";
                            });
                });
    }


    @GetMapping("/orders")
    public Mono<String> orders(Model model, @AuthenticationPrincipal ShopUserDetails user) {
        return productService.getAllUserOrders(user)
                .collectList()
                .map(orders -> {
                    model.addAttribute("orders", orders);
                    return "orders";
                });
    }
}
