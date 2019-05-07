package me.exrates.service.exception;

public class RefillRequestNotFoundException extends RuntimeException{
    public RefillRequestNotFoundException(String message) {
        super(message);
    }
}
