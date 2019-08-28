package me.exrates.service.exception;

public class ChartApiException extends RuntimeException {

    public ChartApiException() {
        super();
    }

    public ChartApiException(String message) {
        super(message);
    }

    public ChartApiException(String message, Throwable cause) {
        super(message, cause);
    }
}