package me.exrates.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.exrates.controller.exception.InvalidNumberParamException;
import me.exrates.service.exception.api.InvalidCurrencyPairFormatException;

import java.util.regex.Pattern;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ValidationUtil {

    private static final Pattern CURRENCY_PAIR_NAME_PATTERN = Pattern.compile("^[A-Z0-9]{2,8}/[A-Z0-9]{2,8}$");

    public static void validateCurrencyPair(String pair) {
        boolean isValid = CURRENCY_PAIR_NAME_PATTERN.matcher(pair).matches();
        if (!isValid) {
            throw new InvalidCurrencyPairFormatException(String.format("Currency pair name %s not valid", pair));
        }
    }

    public static void validateNaturalInt(Integer number) {
        if (nonNull(number) && number <= 0) {
            throw new InvalidNumberParamException(String.format("Number shouldn't be equals to zero or be negative: %s", number));
        }
    }
}
