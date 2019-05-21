package me.exrates.model.dto.logging;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ControllerLog {

    private String url;
    private String httpMethod;
    private String userEmail;
    private long processingTime;
    private int responseStatusCode;
    private String userAgent;
    private String clientIP;
    private String jwtToken;
    private String jSessionId;
    private String requestBody;
    private String responseBody;
    private String args;
    private Integer querriesCount;
}
