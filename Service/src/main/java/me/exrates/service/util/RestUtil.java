package me.exrates.service.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

public class RestUtil {

    private static final int MAX_URL_LENGTH = 125;

    public static boolean isError(HttpStatus status) {
        HttpStatus.Series series = status.series();
        return (HttpStatus.Series.CLIENT_ERROR.equals(series)
                || HttpStatus.Series.SERVER_ERROR.equals(series) || HttpStatus.Series.REDIRECTION.equals(series)
                || HttpStatus.Series.CLIENT_ERROR.equals(series));
    }

    public static String getUrlFromRequest(HttpServletRequest request) {
        String url = request.getScheme()
                + "://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getRequestURI() +
                "?" +
                request.getQueryString();
        return StringUtils.abbreviate(url, MAX_URL_LENGTH);
    }
}
