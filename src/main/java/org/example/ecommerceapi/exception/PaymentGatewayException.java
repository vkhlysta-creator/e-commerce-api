package org.example.ecommerceapi.exception;


import org.springframework.http.HttpStatus;


public class PaymentGatewayException extends ApiException {
    public PaymentGatewayException(Throwable cause) {
        super(HttpStatus.BAD_GATEWAY, "An unexpected external error occurred", cause);
    }
}
