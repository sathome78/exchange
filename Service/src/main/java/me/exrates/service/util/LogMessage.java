package me.exrates.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ValkSam
 */
@Log4j2
public class LogMessage {
  public static String requestLogMessage(HttpServletRequest request) {
    try {
      return "\n\t".concat(request.getMethod()).concat("\n\t")
          .concat(request.getServletPath()).concat("\n\t")
          .concat(getAuthTokenMessagePart(request))
          .concat("params: ").concat(new ObjectMapper().writeValueAsString(request.getParameterMap())).concat("\n\t");
    } catch (Exception e) {
      log.error(ExceptionUtils.getStackTrace(e));
      return "error while logging";
    }
  }

  public static String requestLogMessage(HttpServletRequest request, Object body) {
    try {
      return "\n\t".concat(request.getMethod()).concat("\n\t")
          .concat(request.getServletPath()).concat("\n\t")
          .concat(getAuthTokenMessagePart(request))
          .concat("params: ").concat(new ObjectMapper().writeValueAsString(request.getParameterMap())).concat("\n\t")
          .concat("body: ").concat(new ObjectMapper().writeValueAsString(body)).concat("\n\t");
    } catch (Exception e) {
      log.error(ExceptionUtils.getStackTrace(e));
      return "error while logging";
    }
  }

  public static String requestLogMessageWithResult(HttpServletRequest request, Object body, Object result) {
    try {
      return requestLogMessage(request, body)
          .concat(result instanceof Throwable ? "error: " : "response: ")
          .concat(new ObjectMapper().writeValueAsString(result)).concat("\n\t");
    } catch (Exception e) {
      log.error(ExceptionUtils.getStackTrace(e));
      return "error while logging";
    }
  }

  public static String requestLogMessageWithResult(HttpServletRequest request, Object result) {
    try {
      return requestLogMessage(request)
          .concat(result instanceof Throwable ? "error: " : "response: ")
          .concat(new ObjectMapper().writeValueAsString(result)).concat("\n\t");
    } catch (Exception e) {
      log.error(ExceptionUtils.getStackTrace(e));
      return "error while logging";
    }
  }

  private static String getAuthTokenMessagePart(HttpServletRequest request) {
    String result = "X-Auth-Token=";
    String token = request.getHeader("X-Auth-Token");
    if (StringUtils.isEmpty(token)) {
      return result.concat("<absent>").concat("\n\t");
    }
    return result.concat(
        maskToken(token))
        .concat("\n\t");
  }

  public static String maskToken(String token) {
    if (token.length() < 5) {
      return token;
    }
    return token.substring(0, 5)
        .concat(" *** ")
        .concat(token.substring(token.length() - 5, token.length()));
  }
}
