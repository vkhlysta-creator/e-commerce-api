package org.example.ecommerceapi.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthResponse(@NotBlank String token) {
}
