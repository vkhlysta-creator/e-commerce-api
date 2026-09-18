package org.example.ecommerceapi.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(List<CartItemDto> items, BigDecimal totalPrice) {

}
