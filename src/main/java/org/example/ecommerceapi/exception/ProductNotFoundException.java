package org.example.ecommerceapi.exception;

public class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException(String detail) {
        super(detail);
    }
}
