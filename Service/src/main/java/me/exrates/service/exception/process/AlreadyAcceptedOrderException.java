package me.exrates.service.exception.process;

/**
 * Created by OLEG on 13.09.2016.
 */
public class AlreadyAcceptedOrderException extends ProcessingException {

    public AlreadyAcceptedOrderException() {
        super();
    }

    public AlreadyAcceptedOrderException(String message) {
        super(message);
    }

    public AlreadyAcceptedOrderException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyAcceptedOrderException(Throwable cause) {
        super(cause);
    }
}
