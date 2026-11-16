package org.example.ecommerceapi.exception;

public class OrderAlreadyPaidException extends ConflictException {
    public OrderAlreadyPaidException() {
        super("Order was already paid!");
    }
}
