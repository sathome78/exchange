package me.exrates.service.handler;


import lombok.extern.log4j.Log4j2;
import me.exrates.service.util.RestUtil;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Log4j2
public class RestResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        System.out.println("error status " + response.getStatusCode());
        System.out.println("error body " + response.getBody());
        log.error("Response error: {} {}", response.getStatusCode(), response.getBody());
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return RestUtil.isError(response.getStatusCode());
    }
}
