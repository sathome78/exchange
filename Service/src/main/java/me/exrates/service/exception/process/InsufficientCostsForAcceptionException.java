package me.exrates.service.exception.process;

/**
 * Created by OLEG on 13.09.2016.
 */
public class InsufficientCostsForAcceptionException extends ProcessingException {

    public InsufficientCostsForAcceptionException() {
        super();
    }

    public InsufficientCostsForAcceptionException(String message) {
        super(message);
    }

    public InsufficientCostsForAcceptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientCostsForAcceptionException(Throwable cause) {
        super(cause);
    }
}
