package me.exrates.service.util;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.exceptions.OpenApiException;
import me.exrates.service.exception.api.InvalidCurrencyPairFormatException;
import org.omg.SendingContext.RunTimeOperations;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@Log4j2
public class OpenApiUtils {

    private static final Predicate<String> CURRENCY_PAIR_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9.]{1,8}[_-][a-zA-Z0-9.]{1,8}$").asPredicate();

    public static String transformCurrencyPair(String currencyPair) {
        if (!CURRENCY_PAIR_NAME_PATTERN.test(currencyPair)) {
            String message = String.format("Failed to parse currency pair name (%s) as expected: btc_usd", currencyPair);
            log.warn(message);
            throw new OpenApiException(ErrorApiTitles.API_WRONG_CURRENCY_PAIR_PATTERN, message);
        }
        return currencyPair.replace('_', '/').replace('-', '/').toUpperCase();
    }

    public static String transformCurrencyPairBack(String currencyPair) {
        return currencyPair.replace('/', '_').toLowerCase();
    }
}
