package me.exrates.service.exception.process;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class NotEnoughUserWalletMoneyException extends ProcessingException {

    public NotEnoughUserWalletMoneyException() {
        super();
    }

    public NotEnoughUserWalletMoneyException(String message) {
        super(message);
    }

    public NotEnoughUserWalletMoneyException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughUserWalletMoneyException(Throwable cause) {
        super(cause);
    }
}