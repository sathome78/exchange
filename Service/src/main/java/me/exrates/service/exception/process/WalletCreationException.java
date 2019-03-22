package me.exrates.service.exception.process;

/**
 * Created by Valk on 19.05.2016.
 */
public class WalletCreationException extends ProcessingException {

    public WalletCreationException() {
        super();
    }

    public WalletCreationException(String message) {
        super(message);
    }

    public WalletCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public WalletCreationException(Throwable cause) {
        super(cause);
    }
}