package org.example.ecommerceapi.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentResponse(@NotBlank String clientSecret) {
}
