package org.example.ecommerceapi.exception;


import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

public abstract class BusinessRuleException extends ApiException {
    public BusinessRuleException(String detail,@Nullable Throwable cause) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, detail, cause);
    }

    public BusinessRuleException(String detail) {
        this(detail, null);
    }
}
