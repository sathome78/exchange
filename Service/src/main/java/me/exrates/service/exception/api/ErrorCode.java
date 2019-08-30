package me.exrates.service.exception.api;

/**
 * Created by OLEG on 08.09.2016.
 */
public enum ErrorCode {
    REQUEST_NOT_READABLE,
    INVALID_PARAM_VALUE,
    INTERNAL_SERVER_ERROR,
    MISSING_AUTHENTICATION_TOKEN,
    INVALID_AUTHENTICATION_TOKEN,
    EXPIRED_AUTHENTICATION_TOKEN,
    TOKEN_NOT_FOUND,
    FAILED_AUTHENTICATION,
    MISSING_REQUIRED_PARAM,
    INVALID_CURRENCY_PAIR_FORMAT,
    ACCESS_DENIED,
    BLOCKED_TRADING,
    CALL_BACK_URL_ALREADY_EXISTS,
    INVALID_NUMBER_FORMAT,
    USER_MISMATCH,
    NOT_FOUND_ERROR,
    PROCESSING_ERROR,
    ORDER_CREATION_RESTRICTED,
    NEED_VERIFICATION_EXCEPTION;
}
