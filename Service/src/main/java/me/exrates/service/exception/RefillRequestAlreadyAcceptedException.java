package me.exrates.service.exception;

public class RefillRequestAlreadyAcceptedException extends RuntimeException{
    public RefillRequestAlreadyAcceptedException(String message) {
        super(message);
    }
}
