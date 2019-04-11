package me.exrates.model.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OpenApiException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String title;

    private OpenApiException(String message, String title, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.title = title;
    }

    public OpenApiException(String title, String message) {
        this(message, title, HttpStatus.BAD_REQUEST);
    }
}
