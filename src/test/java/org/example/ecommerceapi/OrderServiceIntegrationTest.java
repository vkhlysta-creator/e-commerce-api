package org.example.ecommerceapi;

import org.example.ecommerceapi.dto.OrderDto;
import org.example.ecommerceapi.model.CartItem;
import org.example.ecommerceapi.model.Product;
import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.model.enums.Role;
import org.example.ecommerceapi.repository.CartItemRepository;
import org.example.ecommerceapi.repository.ProductRepository;
import org.example.ecommerceapi.repository.UserRepository;
import org.example.ecommerceapi.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.assertThat;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
public class OrderServiceIntegrationTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("Successful checkout!: Order creates, cart clears, inventory is decreasing")
    void testSuccessfulCheckout(){
        User testUser = new User("test@gmail.com", "test-password", Role.USER);
        Product testProduct = new Product("Iphone 15 Pro Max", "old phone from apple", BigDecimal.valueOf(600L), 2);
        CartItem item = new CartItem(1, testUser, testProduct);

        userRepository.save(testUser);
        productRepository.save(testProduct);
        cartItemRepository.save(item);

        OrderDto result = orderService.checkout(testUser.getUsername());

        assertThat(result).isNotNull();
        assertThat(result.totalPrice()).isEqualByComparingTo(item.getProduct().getPrice());
        assertThat(cartItemRepository.getCartItemsByUser(testUser)).isEmpty();
        assertThat(productRepository.findById(testProduct.getId()))
                .isPresent()
                .get()
                .extracting(Product::getInventory)
                .isEqualTo(1);
    }
}
