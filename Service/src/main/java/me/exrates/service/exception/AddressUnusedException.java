package me.exrates.service.exception;

public class AddressUnusedException extends RuntimeException{
    public AddressUnusedException(String message) {
        super(message);
    }
}
