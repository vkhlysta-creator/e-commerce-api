package org.example.ecommerceapi.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public record ErrorDto(LocalDateTime timestamp, HttpStatus status, String message, Map<String, String> infos) {
    public ErrorDto(LocalDateTime timestamp, HttpStatus status, String message){
        this(timestamp, status, message, new HashMap<>());
    }
}
