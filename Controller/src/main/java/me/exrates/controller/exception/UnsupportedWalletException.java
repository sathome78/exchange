package me.exrates.controller.exception;

/**
 * Created by OLEG on 08.05.2017.
 */
public class UnsupportedWalletException extends RuntimeException {

    public UnsupportedWalletException() {
    }

    public UnsupportedWalletException(String message) {
        super(message);
    }
}
