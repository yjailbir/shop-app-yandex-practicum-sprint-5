package ru.yjailbir.shopappyandexpracticumsprint5;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.ProductDto;
import ru.yjailbir.shopappyandexpracticumsprint5.service.ProductService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private final ProductService productService;

    private ProductDto testProduct;

    @Autowired
    public ShopControllerTest(ProductService productService) {
        this.productService = productService;
    }

    @BeforeEach
    void setUp() {
        testProduct = new ProductDto(1L, "Test", "Description", 100, "img.png", 1);
    }

    @Test
    void testHome() throws Exception {
        when(productService.getProductsCount(5)).thenReturn(1L);
        when(productService.getProducts(5, 0, "", "NO")).thenReturn(List.of(testProduct));

        mockMvc.perform(get("/shop"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("pageNumber", 0))
                .andExpect(model().attribute("sort", "NO"));

        verify(productService).getProductsCount(5);
        verify(productService).getProducts(5, 0, "", "NO");
    }

    @Test
    void testGetItem() throws Exception {
        when(productService.getProductById(1L)).thenReturn(testProduct);

        mockMvc.perform(get("/shop/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("product"));

        verify(productService).getProductById(1L);
    }

    @Test
    void testAddItem_RedirectToMain() throws Exception {
        mockMvc.perform(post("/shop/change/1")
                        .param("action", "plus")
                        .param("redirect", "main"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shop"));

        verify(productService).changeCountInCart(1L, "plus");
    }

    @Test
    void testAddItem_RedirectToCart() throws Exception {
        mockMvc.perform(post("/shop/change/1")
                        .param("action", "minus")
                        .param("redirect", "cart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shop/cart"));

        verify(productService).changeCountInCart(1L, "minus");
    }

    @Test
    void testCart() throws Exception {
        when(productService.getCart()).thenReturn(List.of(testProduct));
        when(productService.getSumFromItemsList(anyList())).thenReturn(100);

        mockMvc.perform(get("/shop/cart"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("sum", 100));

        verify(productService).getCart();
        verify(productService).getSumFromItemsList(anyList());
    }

    @Test
    void testMakeOrder() throws Exception {
        when(productService.makeOrder()).thenReturn(5L);

        mockMvc.perform(post("/shop/order"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shop/orders/5"));

        verify(productService).makeOrder();
    }

    @Test
    void testOrder() throws Exception {
        when(productService.getOrderById(1L)).thenReturn(List.of(testProduct));
        when(productService.getSumFromItemsList(anyList())).thenReturn(100);

        mockMvc.perform(get("/shop/orders/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attribute("orderId", 1L))
                .andExpect(model().attribute("sum", 100))
                .andExpect(model().attributeExists("products"));

        verify(productService).getOrderById(1L);
        verify(productService).getSumFromItemsList(anyList());
    }

    @Test
    void testOrders() throws Exception {
        when(productService.getAllOrders()).thenReturn(List.of());

        mockMvc.perform(get("/shop/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"));

        verify(productService).getAllOrders();
    }

    @Test
    void testAddProducts() throws Exception {
        String json = """
                [
                    {"id":1,"name":"Test","description":"Desc","price":100,"imgName":"img.png","count":2}
                ]
                """;

        mockMvc.perform(post("/shop/add-products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shop"));

        verify(productService, times(1)).save(any(ProductDto.class));
    }
}

