package me.exrates.service.exception;

/**
 * Throw when address of coin not found
 */
public class AddressNotFoundException extends RuntimeException{
    public AddressNotFoundException(String message) {
        super(message);
    }
}
