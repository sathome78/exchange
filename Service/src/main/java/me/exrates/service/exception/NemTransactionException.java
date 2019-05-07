package me.exrates.service.exception;

public class NemTransactionException extends RuntimeException{

    public NemTransactionException(String message) {
        super(message);
    }
}
