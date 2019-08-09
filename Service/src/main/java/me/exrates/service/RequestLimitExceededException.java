package me.exrates.service;

/**
 * Created by ajet on 13.04.2017.
 */
public class RequestLimitExceededException extends RuntimeException {

    private static String REASON_CODE = "merchants.OutputRequestsLimit";

    public RequestLimitExceededException(String message) {
        super(message);
    }

    public RequestLimitExceededException() {
        super(REASON_CODE);
    }
}
