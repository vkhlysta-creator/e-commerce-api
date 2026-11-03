package org.example.ecommerceapi.security;


import io.jsonwebtoken.security.SignatureException;
import org.example.ecommerceapi.controller.CartController;
import org.example.ecommerceapi.controller.ProductController;
import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.model.enums.Role;
import org.example.ecommerceapi.service.CartService;
import org.example.ecommerceapi.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({CartController.class, ProductController.class})
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class})
public class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private ProductService productService;


    @Test
    public void getCart_NoToken_Returns401() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    public void getCart_BadToken_Returns401() throws Exception {
        when(jwtService.extractUsername("invalid-token")).thenThrow(new SignatureException(""));

        mockMvc.perform(get("/api/cart").header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());

        verify(jwtService, times(1)).extractUsername("invalid-token");
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    public void getProduct_BadToken_Returns200() throws Exception {
        when(jwtService.extractUsername("invalid-token")).thenThrow(new SignatureException(""));

        mockMvc.perform(get("/api/products").header("Authorization", "Bearer invalid-token")).andExpect(status().isOk());

        verify(jwtService, times(1)).extractUsername("invalid-token");
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    public void postProduct_UserRole_Returns403() throws Exception {
        User user = new User("test-user@gmail.com", "test-password-123", Role.USER);
        when(jwtService.extractUsername("valid-token")).thenReturn(user.getUsername());
        when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);
        when(jwtService.isTokenValid("valid-token", user)).thenReturn(true);

        mockMvc.perform(post("/api/products").header("Authorization", "Bearer valid-token"))
                .andExpect(status().isForbidden());

        verify(userDetailsService, times(1)).loadUserByUsername(user.getUsername());
    }

}
