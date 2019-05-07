package me.exrates.service.exception;

public class NisTransactionException extends RuntimeException {

    public NisTransactionException() {
    }

    public NisTransactionException(String message) {
        super(message);
    }
}
