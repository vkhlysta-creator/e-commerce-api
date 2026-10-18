package org.example.ecommerceapi.controller;

import org.example.ecommerceapi.dto.ProductRequest;
import org.example.ecommerceapi.dto.ProductResponse;
import org.example.ecommerceapi.exception.ProductNotFoundException;
import org.example.ecommerceapi.security.JwtService;
import org.example.ecommerceapi.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserDetailsService userDetailsService;



    @Test
    public void findById_returnsOk_WhenProductExists() throws Exception{
        when(productService.findById(1L)).thenReturn(new ProductResponse(1L, "Laptop", "Gaming laptop", BigDecimal.valueOf(1500), 5));

        mockMvc.perform(get("/api/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("Gaming laptop"))
                .andExpect(jsonPath("$.price").value(BigDecimal.valueOf(1500)));
    }

    @Test
    public void findById_ReturnsNotFound_WhenProductDoesNotExist() throws Exception{
        when(productService.findById(999L)).thenThrow(new ProductNotFoundException("Not Found"));

        mockMvc.perform(get("/api/products/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createProduct_ReturnsBadRequest_WhenValidationFails() throws Exception{
        ProductRequest request = new ProductRequest("", "Desc", BigDecimal.valueOf(-100), 0);

        mockMvc.perform(
                post("/api/products").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.infos.name").exists())
                .andExpect(jsonPath("$.infos.price").exists())
                .andExpect(jsonPath("$.infos.inventory").exists());

        verify(productService, never()).createNewProduct(any());

    }

    @Test
    public void deleteProduct_ReturnsNoContent() throws Exception{

        mockMvc.perform(delete("/api/products/{id}", 1L))
                .andExpect(status().isNoContent());


        verify(productService, times(1)).deleteById(1L);

    }


}
