package org.example.ecommerceapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrderItemDto(@NotNull Long productId, @NotBlank String name,@NotNull BigDecimal price,@NotNull int quantity) {}
