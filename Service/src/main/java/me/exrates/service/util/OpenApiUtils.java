package me.exrates.service.util;

import me.exrates.service.exception.api.InvalidCurrencyPairFormatException;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class OpenApiUtils {

    private static final Predicate<String> currencyPairNamePattern = Pattern.compile("^[a-z]{2,8}_[a-z]{2,8}$").asPredicate();

    public static String formatCurrencyPairNameParam(String currencyPair) {
        if (!currencyPairNamePattern.test(currencyPair)) {
            throw new InvalidCurrencyPairFormatException(currencyPair);
        }
        return currencyPair.replace('_', '/').toUpperCase();
    }


}
