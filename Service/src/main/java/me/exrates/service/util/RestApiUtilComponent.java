package me.exrates.service.util;

import me.exrates.service.exception.api.MissingBodyParamException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Map;

@PropertySource(value = {"classpath:/angular.properties"})
@Component
public class RestApiUtilComponent {

    @Value("${pass.encode.key}")
    private String passwordKey;

    public String decodePassword(String password) {
        final byte[] txt = Base64.getDecoder().decode(password);
        final byte[] key = passwordKey.getBytes();
        final byte[] result = new byte[txt.length];
        for (int i = 0; i < txt.length; i++) {
            result[i] = (byte) (txt[i] ^ key[i % key.length]);
        }
        return new String(result);
    }

    public String retrieveParamFormBody(Map<String, String> body, String paramName, boolean required) {
        String paramValue = body.get(paramName);
        if (required && StringUtils.isEmpty(paramValue)) {
            throw new MissingBodyParamException("Param " + paramName + " missing");
        }
        return paramValue;
    }

    public String constructAbsoluteURI(String host, String port, String endpoint) {
        return String.join("", host, ":", port, endpoint);
    }
}
