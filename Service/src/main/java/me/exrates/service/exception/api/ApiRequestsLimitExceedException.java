package me.exrates.service.exception.api;

/**
 * Created by Yuriy Berezin on 14.09.2018.
 */
public class ApiRequestsLimitExceedException extends Exception {

    public ApiRequestsLimitExceedException(String message) {
        super(message);
    }
}
