package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedTransactionSourceTypeException;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Created by Valk on 23.05.2016.
 */
public enum TransactionSourceType {
    ORDER,
    MERCHANT,
    REFERRAL,
    ACCRUAL,
    MANUAL,
    USER_TRANSFER,
    INVOICE;

    public static TransactionSourceType convert(String typeName) {
        switch (typeName) {
            case "ORDER":
                return ORDER;
            case "MERCHANT":
                return MERCHANT;
            case "REFERRAL":
                return REFERRAL;
            case "ACCRUAL":
                return ACCRUAL;
            case "MANUAL":
                return MANUAL;
            case "USER_TRANSFER":
                return USER_TRANSFER;
            case "INVOICE":
                return INVOICE;
            default:
                throw new UnsupportedTransactionSourceTypeException(typeName);
        }
    }

    public String toString(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage("transactionsourcetype." + this.name(), null, locale);

    }
}
