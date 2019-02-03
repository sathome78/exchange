package me.exrates.service.util;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Log4j2
public class ShuftiProUtils {

    public static boolean checkMerchantSignature(String signature, String responseBody, String secretKey) {
        String buildMerchantSignature = merchantSignature(secretKey, responseBody);
        log.debug("Merchant signature: {}, generated signature: {}", signature, buildMerchantSignature);

        return Objects.equals(signature, buildMerchantSignature);
    }

    private static String merchantSignature(String secretKey, String responseBody) {
        return DigestUtils.sha256Hex(String.join(StringUtils.EMPTY, responseBody, secretKey));
    }
}