package org.example.ecommerceapi.exception;

import org.example.ecommerceapi.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(
                new ErrorDto(
                        Timestamp.valueOf(LocalDateTime.now()),
                        HttpStatus.BAD_REQUEST,
                        "Validation failed",
                        errors
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> fallbackHandleException(Exception ex){
        return ResponseEntity.internalServerError().body(
                new ErrorDto(
                        Timestamp.valueOf(LocalDateTime.now()),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal Server Error"
                )
        );
    }
}
