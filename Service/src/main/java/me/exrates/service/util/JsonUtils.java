package me.exrates.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;

public class JsonUtils {
    public static String toJson(Object input) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            return StringUtils.EMPTY;
        }
    }
}
