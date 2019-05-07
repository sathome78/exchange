package me.exrates.service.exception;

public class IncorrectCoreWalletPasswordException extends RuntimeException {

    public IncorrectCoreWalletPasswordException(String message) {
        super(message);
    }
}
