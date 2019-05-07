package me.exrates.dao.exception.notfound;

public class CurrencyPairLimitNotFoundException extends NotFoundException {

    public CurrencyPairLimitNotFoundException(String message) {
        super(message);
    }
}
