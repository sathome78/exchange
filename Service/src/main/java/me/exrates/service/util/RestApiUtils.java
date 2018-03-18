package me.exrates.service.util;

import me.exrates.service.exception.api.MissingBodyParamException;
import org.apache.commons.lang.StringUtils;

import java.util.Base64;
import java.util.Map;

/**
 * Created by OLEG on 01.09.2016.
 */
public class RestApiUtils {
    private static final String PASSWORD_ENCODE_KEY = "3255c246-4b9f-43a5-b2dd-63524f959953";


    public static String decodePassword(String password) {
//        System.out.println("$$$$$$$ util in decoded password: " + password);
        final byte[] txt = Base64.getDecoder().decode(password);
        final byte[] key = PASSWORD_ENCODE_KEY.getBytes();
        final byte[] result = new byte[txt.length];
        for (int i = 0; i < txt.length; i++) {
            result[i] = (byte) (txt[i] ^ key[i % key.length]);
        }
//        String pass = new String(result);
//        System.out.println("$$$$$$$ util out decoded password: " + pass + " length: " + pass.length());
        return new String(result);
    }

    public static String retrieveParamFormBody(Map<String, String> body, String paramName, boolean required) {
        String paramValue = body.get(paramName);
        if (required && StringUtils.isEmpty(paramValue)) {
            throw new MissingBodyParamException("Param " + paramName + " missing");
        }
        return paramValue;
    }

    public static String constructAbsoluteURI(String host, String port, String endpoint) {
        return String.join("", host, ":", port, endpoint);
    }
}
