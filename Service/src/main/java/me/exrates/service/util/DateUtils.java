package me.exrates.service.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public static String decodeStringFromUrl(String input) {
        try {
            return URLDecoder.decode(input, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            logger.warn("Can't decode url param: {}", input);
            return null;
        }
    }

    public static Date getDateFromStringForKyc(int year, int month, int day) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        try {
            return simpleDateFormat.parse(year + "-" + month + "-" + day);
        } catch (ParseException e) {
            logger.error("Error parse date {} {} {}", year, month, day);
            return null;
        }
    }
}
