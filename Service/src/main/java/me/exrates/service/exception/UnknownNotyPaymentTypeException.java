package me.exrates.service.exception;

public class UnknownNotyPaymentTypeException extends RuntimeException {

    public UnknownNotyPaymentTypeException(String message) {
        super(message);
    }

    public UnknownNotyPaymentTypeException() {
    }
}
