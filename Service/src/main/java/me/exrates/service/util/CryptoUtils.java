package me.exrates.service.util;


import me.exrates.service.RefillService;
import me.exrates.service.exception.MerchantInternalException;

import java.util.Optional;
import java.util.Random;

public class CryptoUtils {

    private CryptoUtils() {
    }

    public static String generateUniqDestinationTagForUserForSpecificCurrency(int userId, int maxTagDestinationDigits, RefillService refillService, String currencyName, int currencyId, int merchantId){
        Optional<Integer> id;
        String destinationTag;
        do {
            destinationTag = generateDestinationTag(userId, maxTagDestinationDigits, currencyName);
            id = refillService.getRequestIdReadyForAutoAcceptByAddressAndMerchantIdAndCurrencyId(destinationTag, currencyId, merchantId);
        } while (id.isPresent());

        return destinationTag;
    }

    public static String generateDestinationTag(int userId, int maxDigits, String currencyTicker) {
        String idInString = String.valueOf(userId);
        int randomNumberLength = maxDigits - idInString.length();
        if (randomNumberLength < 0) {
            throw new MerchantInternalException("Error generating new destination tag for *" + currencyTicker + "* UserId: " + userId);
        }
        String randomIntInstring = String.valueOf(100000000 + new Random().nextInt(100000000));
        return idInString.concat(randomIntInstring.substring(0, randomNumberLength));
    }
}
