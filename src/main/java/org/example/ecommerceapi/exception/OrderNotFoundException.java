package org.example.ecommerceapi.exception;

public class OrderNotFoundException extends NotFoundException {
    public OrderNotFoundException() {
        super("Order wasn't found!");
    }
}
