package org.example.ecommerceapi.exception;

public class CartItemNotFoundException extends NotFoundException {

    public CartItemNotFoundException(String detail) {
        super(detail);
    }
}
