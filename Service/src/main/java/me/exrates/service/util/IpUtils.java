package me.exrates.service.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by maks on 04.07.2017.
 */
public class IpUtils {

    private static final int IP_LENGTH_FOR_DB_LOG = 45;

    private IpUtils() {
    }

    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR" };

    public static String getClientIpAddress(HttpServletRequest request) {
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    public static String getClientIpAddress(HttpServletRequest request, int maxLength) {
        String fullAddress = getClientIpAddress(request);
        return StringUtils.substring(fullAddress, 0, maxLength);
    }

    public static String getIpForDbLog(HttpServletRequest request) {
        return getClientIpAddress(request, IP_LENGTH_FOR_DB_LOG).replaceFirst(",", "");
    }
}
