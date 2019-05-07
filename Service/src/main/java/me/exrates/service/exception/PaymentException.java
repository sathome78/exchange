package me.exrates.service.exception;

import me.exrates.model.enums.WalletTransferStatus;

public class PaymentException extends RuntimeException {
    public PaymentException(WalletTransferStatus walletTransferStatus) {
    }
}
