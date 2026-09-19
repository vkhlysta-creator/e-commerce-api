package org.example.ecommerceapi.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(@NotNull List<CartItemDto> items,@NotNull BigDecimal totalPrice) {

}
