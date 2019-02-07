package me.exrates.service.exception;

public class CallBackUrlAlreadyExistException extends RuntimeException {

    public CallBackUrlAlreadyExistException(String callback_already_present) {
        super(callback_already_present);
    }
}
