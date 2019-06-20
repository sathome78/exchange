package me.exrates.service.exception.process;

/**
 * Created by Valk on 17.05.2016.
 */
public class OrderAcceptionException extends ProcessingException {

    private final static String ERR_MSG = "err.accept.error";

    public OrderAcceptionException() {
        super(ERR_MSG);
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
