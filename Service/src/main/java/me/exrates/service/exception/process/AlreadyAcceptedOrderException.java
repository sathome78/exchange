package me.exrates.service.exception.process;

public class AlreadyAcceptedOrderException extends ProcessingException {

    public AlreadyAcceptedOrderException(String message) {
        super(message);
    }

}
