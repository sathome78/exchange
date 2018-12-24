package me.exrates.service.util;


import me.exrates.service.exception.MerchantInternalException;

import java.util.Random;

public class CryptoUtils {

    private CryptoUtils() {
    }

    public static String generateDestinationTag(int userId, int maxDigits) {
        String idInString = String.valueOf(userId);
        int randomNumberLength = maxDigits - idInString.length();
        if (randomNumberLength < 0) {
            throw new MerchantInternalException("error generating new destination tag for stellar" + userId);
        }
        String randomIntInstring = String.valueOf(100000000 + new Random().nextInt(100000000));
        return idInString.concat(randomIntInstring.substring(0, randomNumberLength));
    }
}
