package me.exrates.service.exception.api;

/**
 * Created by OLEG on 08.09.2016.
 */
public enum ErrorCode {
    REQUEST_NOT_READABLE, INVALID_PARAM_VALUE, EXISTING_NICKNAME, EXISTING_EMAIL, INTERNAL_SERVER_ERROR,
    EMAIL_NOT_EXISTS, ACCOUNT_DISABLED, ACCOUNT_NOT_CONFIRMED, INCORRECT_PASSWORD, MISSING_CREDENTIALS, MISSING_AUTHENTICATION_TOKEN,
    INVALID_AUTHENTICATION_TOKEN, EXPIRED_AUTHENTICATION_TOKEN, TOKEN_NOT_FOUND, FAILED_AUTHENTICATION, MISSING_REQUIRED_PARAM,
    CURRENCY_PAIR_NOT_FOUND, ORDER_KEY_NOT_FOUND, INSUFFICIENT_FUNDS, WALLET_NOT_FOUND, CAUSED_NEGATIVE_BALANCE,
    WALLET_UPDATE_ERROR, TRANSACTION_CREATION_ERROR, ORDER_NOT_FOUND, ALREADY_ACCEPTED_ORDER, INVALID_PAYMENT_AMOUNT,
    INVALID_FILE, INVALID_APP_KEY, ABSENT_FIN_PASSWORD, UNCONFIRMED_FIN_PASSWORD, INCORRECT_FIN_PASSWORD, LANGUAGE_NOT_SUPPORTED,
    INVALID_SESSION_ID, INVOICE_NOT_FOUND, USER_NOT_FOUND, SELF_TRANSFER_NOT_ALLOWED;
}
