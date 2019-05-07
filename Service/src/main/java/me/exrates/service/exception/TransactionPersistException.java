package me.exrates.service.exception;

public class TransactionPersistException extends RuntimeException{
    public TransactionPersistException(final String message) {
        super(message);
    }
}