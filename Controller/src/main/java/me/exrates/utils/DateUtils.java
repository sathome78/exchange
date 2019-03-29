package me.exrates.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class DateUtils {
    private static final Logger logger = LogManager.getLogger(DateUtils.class);

    public static LocalDateTime convert(String input, boolean endOfDay) {
        if (input == null) {
            return null;
        }
        DateTime dateTime;
        try {
            dateTime = DateTime.parse(decodeStringFromUrl(input));
        } catch (Exception e) {
            logger.warn("Can't parse date: {}", input);
            dateTime = DateTime.now();
        }
        LocalDateTime time = LocalDateTime.ofInstant(new Date(dateTime.getMillis()).toInstant(), ZoneOffset.UTC);
        if (endOfDay) {
            time = time.plusDays(1);
        }
        return time;
    }

    private static String decodeStringFromUrl(String input) {
        try {
            return URLDecoder.decode(input, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            logger.warn("Can't decode url param: {}", input);
            return null;
        }
    }
}
