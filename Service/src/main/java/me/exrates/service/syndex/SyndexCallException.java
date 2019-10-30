package me.exrates.service.syndex;

public class SyndexCallException extends RuntimeException {


    public SyndexCallException(SyndexClient.Error error) {
        super(error.message);
    }

    public SyndexCallException(String message, Throwable ex) {
        super(message);
    }
}
