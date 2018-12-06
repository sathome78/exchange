package me.exrates.service.exception;

public class InterKassaMerchantNotFound extends RuntimeException {
    public InterKassaMerchantNotFound(final String exceptionMessage) {
        super(exceptionMessage);
    }
}
