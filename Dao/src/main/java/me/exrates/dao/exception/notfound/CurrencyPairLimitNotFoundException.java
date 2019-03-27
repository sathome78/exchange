package me.exrates.dao.exception.notfound;

public class CurrencyPairLimitNotFoundException extends NotFoundException {

    public CurrencyPairLimitNotFoundException() {
        super();
    }

    public CurrencyPairLimitNotFoundException(String message) {
        super(message);
    }

    public CurrencyPairLimitNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyPairLimitNotFoundException(Throwable cause) {
        super(cause);
    }
}
