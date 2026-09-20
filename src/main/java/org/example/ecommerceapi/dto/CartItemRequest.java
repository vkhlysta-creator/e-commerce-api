package org.example.ecommerceapi.dto;

import jakarta.validation.constraints.NotNull;

public record CartItemRequest(@NotNull Long productId,@NotNull int quantity) {
}
