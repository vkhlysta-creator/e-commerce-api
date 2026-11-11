package org.example.ecommerceapi.exception;

public class EmailAlreadyRegisteredException extends ConflictException {
    public EmailAlreadyRegisteredException() {
        super("Email already registered!");
    }
}
