package me.exrates.service.exception;

public class MerchantServiceNotFoundException extends RuntimeException{
    public MerchantServiceNotFoundException(String message) {
        super(message);
    }
}
