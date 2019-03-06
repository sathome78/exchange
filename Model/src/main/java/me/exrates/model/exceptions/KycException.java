package me.exrates.model.exceptions;

public class KycException extends RuntimeException {
    public KycException(String message) {
        super(message);
    }
}
