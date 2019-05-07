package me.exrates.service.exception;

public class TransactionProvidingException extends RuntimeException {
    public TransactionProvidingException(String message) {
        super(message);
    }
}