package org.example.ecommerceapi.dto;

import jakarta.validation.constraints.NotNull;
import org.example.ecommerceapi.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(@NotNull Long id,@NotNull BigDecimal totalPrice,@NotNull OrderStatus status,@NotNull LocalDateTime createdAt,@NotNull List<OrderItemDto> items) {
}
