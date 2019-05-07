package me.exrates.dao.exception;

public class DuplicatedMerchantTransactionIdOrAttemptToRewriteException extends Exception{
    public DuplicatedMerchantTransactionIdOrAttemptToRewriteException(String message) {
        super(message);
    }
}
