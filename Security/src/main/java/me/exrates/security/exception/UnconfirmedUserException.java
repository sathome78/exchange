package me.exrates.security.exception;

import org.springframework.security.core.AuthenticationException;

public class UnconfirmedUserException extends AuthenticationException {

    public UnconfirmedUserException(String msg) {
        super(msg);
    }
}
