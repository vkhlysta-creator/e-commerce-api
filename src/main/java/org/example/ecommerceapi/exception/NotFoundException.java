package org.example.ecommerceapi.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

public abstract class NotFoundException extends ApiException {

    public NotFoundException(String detail) {
        this(detail, null);
    }

    public NotFoundException(String detail, @Nullable Throwable cause) {
        super(HttpStatus.NOT_FOUND, detail, cause);
    }

}
