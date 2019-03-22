package me.exrates.service.exception.process;

public class OrderCancellingException extends ProcessingException {

    public OrderCancellingException() {
        super();
    }

    public OrderCancellingException(String message) {
        super(message);
    }

    public OrderCancellingException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderCancellingException(Throwable cause) {
        super(cause);
    }
}
