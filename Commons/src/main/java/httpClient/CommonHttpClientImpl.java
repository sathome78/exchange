package httpClient;


import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Span;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import processIdManager.ProcessIDManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/*wrapper around apache CloseableHttpClient
* how to use apache http client: https://www.baeldung.com/httpclient-post-http-request
*                                https://www.baeldung.com/httpclient4
*/
@Log4j2(topic = "http_client")
public class CommonHttpClientImpl implements CommonHttpClient {

    private static final String NAME = "CommonHttpClient_REQUEST";

    private static final String TYPE = "3RD_party_request";

    private final CloseableHttpClient client = HttpClients.createDefault();

    @Override
    public HttpResponseWithEntity execute(HttpUriRequest request) throws IOException {
        long start = System.currentTimeMillis();
        String authUser = getAuthenticatedUser();
        String requestBody = tryToGetBodyFromRequest(request);
        Span span = ElasticApm.currentTransaction().startSpan(TYPE, StringUtils.EMPTY, TYPE);
        span.setName(NAME);
        String prId = ProcessIDManager.getCurrentOrRegisterNewProcess(getClass());
        HttpCallsLog callsLog = null;
        try (CloseableHttpResponse response = client.execute(request)) {
            String resEntityToString = getResponseEntityFromResponse(response.getEntity());
            HttpResponseWithEntity httpResponseWithEntity = new HttpResponseWithEntity(response, resEntityToString, response.getStatusLine().getStatusCode());
            callsLog = new HttpCallsLog(
                    request.getURI().toString(),
                    request.getMethod(),
                    Arrays.toString(request.getAllHeaders()),
                    requestBody,
                    getExecutionTime(start),
                    authUser,
                    response.getStatusLine().toString(),
                    resEntityToString,
                    Arrays.toString(response.getAllHeaders()),
                    StringUtils.EMPTY);
            EntityUtils.consume(response.getEntity());
            return httpResponseWithEntity;
        } catch (Exception e) {
            callsLog = new HttpCallsLog(
                    request.getURI().toString(),
                    request.getMethod(),
                    Arrays.toString(request.getAllHeaders()),
                    requestBody,
                    getExecutionTime(start),
                    authUser,
                    StringUtils.EMPTY,
                    StringUtils.EMPTY,
                    StringUtils.EMPTY,
                    formatException(e));
            span.captureException(e);
            throw e;
        } finally {
            span.addLabel("process_id", prId);
            span.addLabel("full_request_log", callsLog != null ? callsLog.toString() : null);
            log.debug(callsLog);
            span.end();
        }
    }

    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException {
        long start = System.currentTimeMillis();
        String authUser = getAuthenticatedUser();
        String requestBody = tryToGetBodyFromRequest(request);
        Span span = ElasticApm.currentTransaction().startSpan(TYPE, StringUtils.EMPTY, TYPE);
        span.setName(NAME);
        String prId = ProcessIDManager.getCurrentOrRegisterNewProcess(getClass());
        HttpCallsLog callsLog = null;
        try {
            T response = client.execute(request, responseHandler);
            callsLog = new HttpCallsLog(
                    request.getURI().toString(),
                    request.getMethod(),
                    Arrays.toString(request.getAllHeaders()),
                    requestBody,
                    getExecutionTime(start),
                    authUser,
                    StringUtils.EMPTY,
                    response.toString(),
                    StringUtils.EMPTY,
                    StringUtils.EMPTY);
            return response;
        } catch (Exception e) {
            callsLog = new HttpCallsLog(
                    request.getURI().toString(),
                    request.getMethod(),
                    Arrays.toString(request.getAllHeaders()),
                    requestBody,
                    getExecutionTime(start),
                    authUser,
                    StringUtils.EMPTY,
                    StringUtils.EMPTY,
                    StringUtils.EMPTY,
                    formatException(e));
            span.captureException(e);
            throw e;
        } finally {
            span.addLabel("process_id", prId);
            span.addLabel("full_request_log", callsLog != null ? callsLog.toString() : null);
            span.end();
            log.debug(callsLog);
        }
    }


    private static String formatException(Throwable throwable) {
        return String.join(" ", throwable.getClass().getName(), throwable.getMessage());
    }

    private static long getExecutionTime(long start) {
        return System.currentTimeMillis() - start;
    }

    private String tryToGetBodyFromRequest(HttpUriRequest request) throws IOException {
        String requestBody = StringUtils.EMPTY;
        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest req = (HttpEntityEnclosingRequest)request;
            if (req.getEntity() != null) {
                requestBody = String.join(" ",
                        EntityUtils.toString(req.getEntity()),
                        String.valueOf(req.getEntity().getContentType().toString()),
                        String.valueOf(req.getEntity().getContentEncoding()));
            }
        }
        return requestBody;
    }

    private String getResponseEntityFromResponse(HttpEntity httpEntity) throws IOException {
        return httpEntity.getContent() == null ? StringUtils.EMPTY : EntityUtils.toString(httpEntity);
    }


    public static String getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? null : authentication.getName();
    }

}
