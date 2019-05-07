package me.exrates.service.exception.process;

public class OrderCreationException extends ProcessingException {

    public OrderCreationException() {
        super();
    }

    public OrderCreationException(String message) {
        super(message);
    }

    public OrderCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderCreationException(Throwable cause) {
        super(cause);
    }
}
