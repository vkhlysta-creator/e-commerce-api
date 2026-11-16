package org.example.ecommerceapi.exception;

public class PaymentNotCompletedException extends ConflictException {
    public PaymentNotCompletedException() {
        super("Payment not succeeded!");
    }
}
