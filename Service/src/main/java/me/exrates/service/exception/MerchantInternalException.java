package me.exrates.service.exception;

public class MerchantInternalException extends RuntimeException {

    public MerchantInternalException(String message) {
        super(message);
    }

    public MerchantInternalException(final Throwable cause) {
        super(cause);
    }
}