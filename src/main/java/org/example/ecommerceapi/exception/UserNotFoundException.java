package org.example.ecommerceapi.exception;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(String detail) {
        super(detail);
    }
}
