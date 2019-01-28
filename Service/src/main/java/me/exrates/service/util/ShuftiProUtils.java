package me.exrates.service.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Slf4j
public class ShuftiProUtils {

    public static boolean checkMerchantSignature(String signature, String responseBody, String secretKey) {
        String buildMerchantSignature = merchantSignature(secretKey, responseBody);
        return Objects.equals(signature, buildMerchantSignature);
    }

    private static String merchantSignature(String secretKey, String responseBody) {
        return DigestUtils.sha256Hex(String.join(StringUtils.EMPTY, responseBody, secretKey));
    }
}