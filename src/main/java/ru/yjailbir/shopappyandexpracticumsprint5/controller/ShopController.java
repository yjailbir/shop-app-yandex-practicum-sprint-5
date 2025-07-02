package ru.yjailbir.shopappyandexpracticumsprint5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.ProductDto;
import ru.yjailbir.shopappyandexpracticumsprint5.service.ProductService;

import java.util.List;

@Controller
@RequestMapping("/shop")
public class ShopController {
    private final ProductService productService;

    @Value("${values.img_folder}")
    private String imgFolder;

    @Autowired
    public ShopController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String home(Model model) {

        return "main";
    }

    @PostMapping("/add-products")
    public String addProducts(@RequestBody List<ProductDto> products) {
        for (ProductDto product : products) {
            productService.save(product);
        }

        return "redirect:/shop/main";
    }
}
