package ru.yjailbir.shopappyandexpracticumsprint5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.ChangeCountDto;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.ProductDto;
import ru.yjailbir.shopappyandexpracticumsprint5.service.ProductService;

import java.util.List;

@Controller
@RequestMapping("/shop")
public class ShopController {
    private final ProductService productService;


    @Autowired
    public ShopController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Mono<String> home(
            @RequestParam(name = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "sort", defaultValue = "NO") String sort,
            Model model
    ) {
        Mono<Long> totalPagesMono = productService.getProductsCount(pageSize);
        Flux<ProductDto> productsFlux = productService.getProducts(pageSize, pageNumber, search, sort);

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

    @GetMapping("/{id}")
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
            @ModelAttribute ChangeCountDto formData
    ) {
        return productService.changeCountInCart(id, formData.getAction())
                .then(Mono.fromSupplier(() -> {
                    if ("main".equals(formData.getRedirect())) {
                        return "redirect:/shop";
                    } else if ("cart".equals(formData.getRedirect())) {
                        return "redirect:/shop/cart";
                    }
                    else if (formData.getRedirect().equals(id.toString())) {
                        return "redirect:/shop/" + formData.getRedirect();
                    }
                    return "redirect:/shop";
                }));
    }

    @GetMapping("/cart")
    public Mono<String> cart(Model model) {
        return productService.getCart()
                .collectList()
                .flatMap(products -> {
                    model.addAttribute("products", products);
                    return productService.getSumFromItemsList(products)
                            .map(sum -> {
                                model.addAttribute("sum", sum);
                                return "cart";
                            });
                });
    }


    @PostMapping("/order")
    public Mono<String> makeOrder() {
        return productService.makeOrder()
                .map(orderId -> "redirect:/shop/orders/" + orderId);
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
                                return "order"; // имя шаблона
                            });
                });
    }


    @GetMapping("/orders")
    public Mono<String> orders(Model model) {
        return productService.getAllOrders()
                .collectList()
                .map(orders -> {
                    model.addAttribute("orders", orders);
                    return "orders";
                });
    }

    @PostMapping("/add-products")
    public Mono<String> addProducts(@RequestBody List<ProductDto> products) {
        return Flux.fromIterable(products)
                .flatMap(productService::save)
                .then(Mono.just("redirect:/shop"));
    }
}
