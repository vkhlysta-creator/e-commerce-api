package org.example.ecommerceapi.controller;

import org.example.ecommerceapi.dto.OrderDto;
import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.model.enums.OrderStatus;
import org.example.ecommerceapi.model.enums.Role;
import org.example.ecommerceapi.security.JwtService;
import org.example.ecommerceapi.security.SecurityConfig;
import org.example.ecommerceapi.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;


    @Test
    public void checkout_ReturnsOk_WhenUserIsAuthenticated() throws Exception{
        User customer = new User("buyer@gmail.com", "test-password", Role.USER );
        OrderDto orderDto = new OrderDto(10L, BigDecimal.valueOf(500), OrderStatus.PENDING, LocalDateTime.now(), List.of());
        when(orderService.checkout(customer.getUsername())).thenReturn(orderDto);

        mockMvc.perform(post("/api/orders/checkout").with(user(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService, times(1)).checkout(customer.getUsername());

    }

    @Test
    public void checkout_ReturnsForbidden_WhenUserIsNotAuthenticated() throws Exception{
        mockMvc.perform(post("/api/orders/checkout"))
                .andExpect(status().isForbidden());

        verify(orderService, never()).checkout(any());
    }
}
