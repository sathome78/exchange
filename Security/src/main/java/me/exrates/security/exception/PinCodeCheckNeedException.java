package me.exrates.security.exception;

import org.springframework.security.core.AuthenticationException;

public class PinCodeCheckNeedException extends AuthenticationException {

    public PinCodeCheckNeedException(String msg) {
        super(msg);
    }
}
