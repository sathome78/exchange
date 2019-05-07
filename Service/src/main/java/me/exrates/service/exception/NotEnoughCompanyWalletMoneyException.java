package me.exrates.service.exception;

public class NotEnoughCompanyWalletMoneyException extends RuntimeException {
    public NotEnoughCompanyWalletMoneyException(String message) {
        super(message);
    }
}