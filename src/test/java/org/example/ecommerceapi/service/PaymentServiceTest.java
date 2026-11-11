package org.example.ecommerceapi.service;

import com.stripe.exception.StripeException;
import org.example.ecommerceapi.exception.OrderNotFoundException;
import org.example.ecommerceapi.model.Order;
import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.model.enums.OrderStatus;
import org.example.ecommerceapi.model.enums.Role;
import org.example.ecommerceapi.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;


    @Test
    void createPaymentIntent_OrderNotFound_ThrowsOrderNotFound() throws StripeException {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> paymentService.createPaymentIntent(1L, "test_user@gmail.com"));

    }

    @Test
    void createPaymentIntent_AnotherCustomerOrder_ThrowsOrderNotFound() throws StripeException {
        User foundUser = new User("testUser@gmail.com", "testPassword", Role.USER);
        Order foundOrder = new Order(BigDecimal.TEN, OrderStatus.PENDING, LocalDateTime.now().minusHours(2), foundUser);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(foundOrder));

        assertThrows(OrderNotFoundException.class, () -> paymentService.createPaymentIntent(1L, "notThatUser@gmail.com"));
    }

}
