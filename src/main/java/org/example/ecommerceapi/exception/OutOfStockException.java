package org.example.ecommerceapi.exception;

public class OutOfStockException extends BusinessRuleException {
    public OutOfStockException(String detail) {
        super(detail);
    }
}
