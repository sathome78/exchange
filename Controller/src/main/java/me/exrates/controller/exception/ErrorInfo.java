package me.exrates.controller.exception;

import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Valk on 04.04.16.
 */
public class ErrorInfo {
    public final String url;
    public final String cause;
    public final String detail;
    public String uuid;

    public ErrorInfo(CharSequence url, Throwable ex) {
        this.url = url.toString();
        this.cause = ex.getClass().getSimpleName();
        String detail = ex.getLocalizedMessage() == null ? ex.getLocalizedMessage() : ex.getMessage();
        while (ex.getCause() != null) ex = ex.getCause();
        this.detail = ex.getLocalizedMessage() == null ? detail : ex.getLocalizedMessage();
    }

    public ErrorInfo(CharSequence url, Throwable ex, String reason) {
        this.url = url.toString();
        this.cause = ex.getClass().getSimpleName();
        while (ex.getCause() != null) ex = ex.getCause();
        this.detail = reason;
    }

    public ErrorInfo(CharSequence url, Throwable ex, String reason, String uuid) {
        this.url = url.toString();
        this.cause = ex.getClass().getSimpleName();
        while (ex.getCause() != null) ex = ex.getCause();
        this.detail = reason;
        this.uuid = uuid;
    }

    public ErrorInfo(CharSequence url, MethodArgumentNotValidException ex) {
        this.url = url.toString();
        this.cause = ex.getClass().getSimpleName();
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        if (!errors.isEmpty()) {
            this.detail = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(";\n"));
        } else {
            this.detail = ex.getLocalizedMessage();
        }

    }

    public ErrorInfo(CharSequence url, BindException ex) {
        this.url = url.toString();
        this.cause = ex.getClass().getSimpleName();

        List<ObjectError> errors = ex.getAllErrors();
        if (!errors.isEmpty()) {
            this.detail = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(";\n"));
            System.out.println(detail);
        } else {
            this.detail = ex.getLocalizedMessage();
        }

    }

}
