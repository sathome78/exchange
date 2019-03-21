package me.exrates.service.util;

import me.exrates.service.exception.api.InvalidCurrencyPairFormatException;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class OpenApiUtils {

    private static final Predicate<String> CURRENCY_PAIR_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9.]{1,8}_[a-zA-Z0-9.]{1,8}$").asPredicate();

    public static String transformCurrencyPair(String currencyPair) {
        if (!CURRENCY_PAIR_NAME_PATTERN.test(currencyPair)) {
            throw new InvalidCurrencyPairFormatException(currencyPair);
        }
        return currencyPair.replace('_', '/').toUpperCase();
    }

    public static String transformCurrencyPairBack(String currencyPair) {
        return currencyPair.replace('/', '_').toLowerCase();
    }
}
