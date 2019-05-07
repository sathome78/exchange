package httpClient;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public interface CommonHttpClient {
    HttpResponseWithEntity execute(HttpUriRequest request) throws IOException;

    <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException;
}
