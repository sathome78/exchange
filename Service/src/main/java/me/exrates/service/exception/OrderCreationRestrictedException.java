package me.exrates.service.exception;

public class OrderCreationRestrictedException extends RuntimeException {

    public OrderCreationRestrictedException(String message) {
        super(message);
    }
}
