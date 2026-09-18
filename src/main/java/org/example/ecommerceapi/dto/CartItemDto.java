package org.example.ecommerceapi.dto;

import java.math.BigDecimal;

public record CartItemDto(Long productId, String name, BigDecimal price, int quantity) {
}
