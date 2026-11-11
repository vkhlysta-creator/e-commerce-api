package org.example.ecommerceapi.exception;


import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

public abstract class ConflictException extends ApiException {
    public ConflictException(String detail,@Nullable Throwable cause) {
        super(HttpStatus.CONFLICT, detail, cause);
    }

    public ConflictException(String detail) {
        this(detail, null);
    }
}
