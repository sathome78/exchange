package httpClient;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HttpCallsLog {

    private String url;
    private String method;
    private String requestHeaders;
    private String requestBody;
    private long executionTime;
    private String responseStatus;
    private String response;
    private String responseHeaders;
    private String error;


}
