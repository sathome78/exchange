package me.exrates.model.exceptions;

public class CheckNodeStateException extends RuntimeException {

    private static String errorText = "Error while checking nodestate for %s";

    public CheckNodeStateException(String currencyName) {
        super(String.format(errorText, currencyName));
    }
}
