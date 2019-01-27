package me.exrates.ngcontroller.exception;

public class NgTwoFcatorOuathException extends RuntimeException {

    public NgTwoFcatorOuathException(String message) {
        super(message);
    }

    public NgTwoFcatorOuathException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
