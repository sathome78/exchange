package me.exrates.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class DateUtils {
    private static final Logger logger = LogManager.getLogger(DateUtils.class);

    public static LocalDateTime convert(String input) {
        if (input == null) {
            return null;
        }
        DateTime dateTime;
        try {
            dateTime = DateTime.parse(input);
        } catch (Exception e) {
            logger.warn("Can't parse date: {}", input);
            dateTime = DateTime.now();
        }
        return LocalDateTime.ofInstant(new Date(dateTime.getMillis()).toInstant(), ZoneOffset.UTC);
    }
}
