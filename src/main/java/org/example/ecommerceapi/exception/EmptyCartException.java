package org.example.ecommerceapi.exception;

public class EmptyCartException extends BusinessRuleException {
    public EmptyCartException() {
        super("Cart is empty!");
    }
}
