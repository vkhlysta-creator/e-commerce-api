package org.example.ecommerceapi;

import org.example.ecommerceapi.model.CartItem;
import org.example.ecommerceapi.model.Product;
import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.model.enums.Role;
import org.example.ecommerceapi.repository.AbstractIntegrationTest;
import org.example.ecommerceapi.repository.CartItemRepository;
import org.example.ecommerceapi.repository.ProductRepository;
import org.example.ecommerceapi.repository.UserRepository;
import org.example.ecommerceapi.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class OrderServiceIntegrationTest  extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;

    @Test
    void checkoutViaHttpWithRealJwt_Success() throws Exception{
        User testUser = new User("test@gmail.com", "test-password", Role.USER);
        Product testProduct = new Product("Iphone 15 Pro Max", "old phone from apple", BigDecimal.valueOf(600L), 2);
        CartItem item = new CartItem(1, testUser, testProduct);

        userRepository.save(testUser);
        productRepository.save(testProduct);
        cartItemRepository.save(item);

        String token = jwtService.generateToken(testUser);

        mockMvc.perform(post("/api/orders/checkout").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(600));
        assertThat(productRepository.findById(testProduct.getId()))
                .isPresent()
                .get()
                .extracting(Product::getInventory)
                .isEqualTo(1);


    }
}
