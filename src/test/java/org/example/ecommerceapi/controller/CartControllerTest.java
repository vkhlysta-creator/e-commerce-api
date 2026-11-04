package org.example.ecommerceapi.controller;

import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.model.enums.Role;
import org.example.ecommerceapi.security.CustomAuthenticationEntryPoint;
import org.example.ecommerceapi.security.JwtService;
import org.example.ecommerceapi.security.SecurityConfig;
import org.example.ecommerceapi.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class})
public class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    public void addInCart_NegativeQuantity_ReturnsBadRequest() throws Exception{
        User customer = new User("buyer@gmail.com", "test-password", Role.USER);


        mockMvc.perform(post("/api/cart/add").with(user(customer))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\": 1, \"quantity\": -5}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.infos.quantity").exists());

        verify(cartService, never()).addProductToCart(anyString(), anyLong(), anyInt());

    }

    @Test
    public void addInCart_EmptyQuantity_ReturnsBadRequest() throws Exception{
        User customer = new User("buyer@gmail.com", "test-password", Role.USER);


        mockMvc.perform(post("/api/cart/add").with(user(customer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.infos.quantity").exists());

        verify(cartService, never()).addProductToCart(anyString(), anyLong(), anyInt());
    }

    @Test
    public void addInCart_RequestWithoutBody_ReturnsBadRequest() throws Exception{
        User customer = new User("buyer@gmail.com", "test-password", Role.USER);

        mockMvc.perform(post("/api/cart/add").with(user(customer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Body of Request missing or containing invalid JSON"));

        verify(cartService, never()).addProductToCart(anyString(), anyLong(), anyInt());
    }



}
