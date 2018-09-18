package me.exrates.api;

/**
 * Created by Yuriy Berezin on 14.09.2018.
 */
public class RequestsLimitExceedException extends Exception {

    public RequestsLimitExceedException(String message) {
        super(message);
    }
}
