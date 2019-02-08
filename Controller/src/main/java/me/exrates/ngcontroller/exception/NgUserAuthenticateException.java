package me.exrates.ngcontroller.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NgUserAuthenticateException extends NgResponseException {

    public NgUserAuthenticateException(String message) {
        super(message);
    }

    public NgUserAuthenticateException(String message, String cause, HttpStatus httpStatus) {
        super(message, cause, httpStatus);
    }
}
