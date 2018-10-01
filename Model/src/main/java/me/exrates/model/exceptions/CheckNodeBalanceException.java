package me.exrates.model.exceptions;

public class CheckNodeBalanceException extends RuntimeException{

    private static String errorText = "Error while checking balance for %s";

    public CheckNodeBalanceException(String currencyName) {
        super(String.format(errorText, currencyName));
    }
}
