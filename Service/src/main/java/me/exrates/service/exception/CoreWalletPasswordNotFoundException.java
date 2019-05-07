package me.exrates.service.exception;

public class CoreWalletPasswordNotFoundException extends RuntimeException {

    public CoreWalletPasswordNotFoundException(String message) {
        super(message);
    }
}
