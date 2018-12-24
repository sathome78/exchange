package me.exrates.service.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.service.util.RestUtil;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Log4j2
public class RestResponseErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public RestResponseErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String resp = objectMapper.writeValueAsString(response);
        log.error("Response error: {} {}", response.getStatusCode(), resp);
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return RestUtil.isError(response.getStatusCode());
    }
}
