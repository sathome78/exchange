package me.exrates.dao.exception.notfound;

/**
 * Created by OLEG on 12.09.2016.
 */
public class CurrencyPairNotFoundException extends NotFoundException {

    public CurrencyPairNotFoundException() {
        super();
    }

    public CurrencyPairNotFoundException(String message) {
        super(message);
    }

    public CurrencyPairNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyPairNotFoundException(Throwable cause) {
        super(cause);
    }
}
