package org.example.ecommerceapi.repository;

import org.example.ecommerceapi.model.Order;
import org.example.ecommerceapi.model.OrderItem;
import org.example.ecommerceapi.model.Product;
import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.model.enums.OrderStatus;
import org.example.ecommerceapi.model.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


    @Test
    void findAllByUserOrderByCreatedAtDesc_ReturnsSortedOrdersForUser(){
        User firstUser = new User("test-user-1", "test-password-1", Role.USER);
        User secondUser = new User("test-user-2", "test-password-2", Role.USER);
        userRepository.save(firstUser);
        userRepository.save(secondUser);

        Product product = new Product("Apple Iphone 15", "Smartphone (apple)", BigDecimal.valueOf(650), 10);
        productRepository.save(product);

        Order oldOrderFirstUser = new Order(product.getPrice(), OrderStatus.PAID, LocalDateTime.now().minusDays(2L), firstUser);
        OrderItem oldOrderItem = new OrderItem(1, product.getPrice(), oldOrderFirstUser, product);
        oldOrderFirstUser.addItem(oldOrderItem);
        orderRepository.save(oldOrderFirstUser);

        Order newOrderFirstUser = new Order(product.getPrice(), OrderStatus.PAID, LocalDateTime.now().plusDays(2L), firstUser);
        OrderItem newOrderItem = new OrderItem(1, product.getPrice(), newOrderFirstUser, product);
        newOrderFirstUser.addItem(newOrderItem);
        orderRepository.save(newOrderFirstUser);

        Order orderSecondUser = new Order(BigDecimal.TEN, OrderStatus.PAID, LocalDateTime.now().minusDays(2L), secondUser);
        OrderItem orderItemNewUser = new OrderItem(1, product.getPrice(), orderSecondUser, product);
        orderSecondUser.addItem(orderItemNewUser);
        orderRepository.save(orderSecondUser);

        entityManager.flush();
        entityManager.clear();


        List<Order> orders = orderRepository.findAllByUserOrderByCreatedAtDesc(firstUser);


        assertThat(orders.size()).isEqualTo(2);
        assertThat(orders.get(0).getId()).isEqualTo(newOrderFirstUser.getId());
        assertThat(orders.get(1).getId()).isEqualTo(oldOrderFirstUser.getId());
        assertThat(orders.get(0).getItems().getFirst().getProduct().getName()).isEqualTo("Apple Iphone 15");
    }
}
