package org.example.ecommerceapi.exception;

import org.springframework.http.HttpStatus;


public class InvalidWebhookSignatureException extends ApiException {
    public InvalidWebhookSignatureException(Throwable cause) {
        super(HttpStatus.BAD_REQUEST, "Invalid webhook signature", cause);
    }
}
