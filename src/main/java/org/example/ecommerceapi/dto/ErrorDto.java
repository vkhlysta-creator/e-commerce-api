package org.example.ecommerceapi.dto;

import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public record ErrorDto(Timestamp timestamp, HttpStatus status, String message, Map<String, String> infos) {
    public ErrorDto(Timestamp timestamp, HttpStatus status, String message){
        this(timestamp, status, message, new HashMap<>());
    }
}
