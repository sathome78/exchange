package me.exrates.service.exception;

public class RefillRequestDuplicatedMerchantTransactionIdOrAttemptToRewriteException extends RuntimeException{
    public RefillRequestDuplicatedMerchantTransactionIdOrAttemptToRewriteException(String message) {
        super(message);
    }
}
