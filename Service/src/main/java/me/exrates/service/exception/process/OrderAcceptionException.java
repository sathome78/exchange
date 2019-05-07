package me.exrates.service.exception.process;

public class OrderAcceptionException extends ProcessingException {

    public OrderAcceptionException() {
        super();
    }

    public OrderAcceptionException(String message) {
        super(message);
    }

    public OrderAcceptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderAcceptionException(Throwable cause) {
        super(cause);
    }
}
