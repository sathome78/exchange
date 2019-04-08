package me.exrates.model.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class IeoException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String title;

    public IeoException(String message, String title, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.title = title;
    }

    public IeoException(String message) {
        this(message, "", HttpStatus.BAD_REQUEST);
    }

    public IeoException(String title, String message) {
        this(message, title, HttpStatus.BAD_REQUEST);
    }
}
