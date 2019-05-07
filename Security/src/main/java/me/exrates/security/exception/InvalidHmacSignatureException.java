package me.exrates.security.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidHmacSignatureException extends AuthenticationException {

    public InvalidHmacSignatureException(String msg) {
        super(msg);
    }
}
