package me.exrates.ngcontroller.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NgResponseException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String title;

    public NgResponseException(String message, String title, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.title = title;
    }

    public NgResponseException(String message) {
        this(message, "", HttpStatus.BAD_REQUEST);
    }

    public NgResponseException(String title, String message) {
        this(message, title, HttpStatus.BAD_REQUEST);
    }
}
