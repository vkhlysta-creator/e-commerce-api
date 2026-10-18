package org.example.ecommerceapi.controller;

import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.model.enums.Role;
import org.example.ecommerceapi.security.JwtService;
import org.example.ecommerceapi.security.SecurityConfig;
import org.example.ecommerceapi.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
public class ProductControllerTestWithFiltersTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    public void returnForbidden_WhenWithRoleUser() throws Exception{

        User customer = new User("test-mail", "test-password", Role.USER);

        mockMvc.perform(delete("/api/products/{id}", 1L).with(user(customer)))
                .andExpect(status().isForbidden());

        verify(productService, never()).deleteById(anyLong());
    }
}
