package me.exrates.service.exception.process;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class WalletPersistException extends ProcessingException {

    public WalletPersistException() {
        super();
    }

    public WalletPersistException(String message) {
        super(message);
    }

    public WalletPersistException(String message, Throwable cause) {
        super(message, cause);
    }

    public WalletPersistException(Throwable cause) {
        super(cause);
    }
}