package me.exrates.service.exception.process;

/**
 * Created by Valk on 04.04.16.
 */
public class NotCreatableOrderException extends ProcessingException {

    public NotCreatableOrderException() {
        super();
    }

    public NotCreatableOrderException(String message) {
        super(message);
    }

    public NotCreatableOrderException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotCreatableOrderException(Throwable cause) {
        super(cause);
    }
}
