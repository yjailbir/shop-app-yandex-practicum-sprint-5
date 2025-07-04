package ru.yjailbir.shopappyandexpracticumsprint5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    public String home(
            @RequestParam(name = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "sort", defaultValue = "NO") String sort,
            Model model
    ) {
        model.addAttribute("name", search);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("sort", sort);
        model.addAttribute("totalPages", productService.getProductsCount(pageSize));
        model.addAttribute("products", productService.getProducts(pageSize, pageNumber, search, sort));

        return "main";
    }

    @GetMapping("/{id}")
    public String getItem(@PathVariable("id") Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));

        return "item";
    }

    @PostMapping("/change/{id}")
    public String addItem(
            @PathVariable("id") Long id,
            @RequestParam("action") String action,
            @RequestParam("redirect") String redirect
    ) {
        productService.changeCountInCart(id, action);

        if (redirect.equals("main")) {
            return "redirect:/shop";
        } else if (redirect.equals("cart")) {
            return "redirect:/shop/cart";
        }

        return null;
    }

    @GetMapping("/cart")
    public String cart(Model model) {
        List<ProductDto> products = productService.getCart();
        model.addAttribute("products", products);
        model.addAttribute("sum", productService.getSumFromItemsList(products));

        return "cart";
    }

    @PostMapping("/order")
    public String makeOrder() {
        return "redirect:/shop/orders/" + productService.makeOrder();
    }

    @GetMapping("/orders/{id}")
    public String order(@PathVariable("id") Long id, Model model) {
        List<ProductDto> products = productService.getOrderById(id);
        model.addAttribute("orderId", id);
        model.addAttribute("products", products);
        model.addAttribute("sum", productService.getSumFromItemsList(products));

        return "order";
    }

    @PostMapping("/add-products")
    public String addProducts(@RequestBody List<ProductDto> products) {
        for (ProductDto product : products) {
            productService.save(product);
        }

        return "redirect:/shop";
    }
}
