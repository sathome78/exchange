package me.exrates.service.exception;

public class CoinpayException extends RuntimeException {

    public CoinpayException() {
        super();
    }

    public CoinpayException(String message) {
        super(message);
    }

    public CoinpayException(String message, Throwable cause) {
        super(message, cause);
    }
}