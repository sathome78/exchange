package httpClient;


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

    private final CloseableHttpClient client = HttpClients.createDefault();

    @Override
    public HttpResponseWithEntity execute(HttpUriRequest request) throws IOException {
        long start = System.currentTimeMillis();
        String requestBody = tryToGetBodyFromRequest(request);
        try (CloseableHttpResponse response = client.execute(request)) {
            String resEntityToString = getResponseEntityFromResponse(response.getEntity());
            HttpResponseWithEntity httpResponseWithEntity = new HttpResponseWithEntity(response, resEntityToString, response.getStatusLine().getStatusCode());
            log.debug(new HttpCallsLog(
                    request.getURI().toString(),
                    request.getMethod(),
                    Arrays.toString(request.getAllHeaders()),
                    requestBody,
                    getExecutionTime(start),
                    response.getStatusLine().toString(),
                    resEntityToString,
                    Arrays.toString(response.getAllHeaders()),
                    StringUtils.EMPTY));
            EntityUtils.consume(response.getEntity());
            return httpResponseWithEntity;
        } catch (Exception e) {
            log.debug(new HttpCallsLog(
                    request.getURI().toString(),
                    request.getMethod(),
                    Arrays.toString(request.getAllHeaders()),
                    requestBody,
                    getExecutionTime(start),
                    StringUtils.EMPTY,
                    StringUtils.EMPTY,
                    StringUtils.EMPTY,
                    formatException(e)));
            throw e;
        }
    }

    @Override
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException {
        long start = System.currentTimeMillis();
        String requestBody = tryToGetBodyFromRequest(request);
        try {
            T response = client.execute(request, responseHandler);
            log.debug(new HttpCallsLog(
                    request.getURI().toString(),
                    request.getMethod(),
                    Arrays.toString(request.getAllHeaders()),
                    requestBody,
                    getExecutionTime(start),
                    StringUtils.EMPTY,
                    response.toString(),
                    StringUtils.EMPTY,
                    StringUtils.EMPTY));
            return response;
        } catch (Exception e) {
            log.debug(new HttpCallsLog(
                    request.getURI().toString(),
                    request.getMethod(),
                    Arrays.toString(request.getAllHeaders()),
                    requestBody,
                    getExecutionTime(start),
                    StringUtils.EMPTY,
                    StringUtils.EMPTY,
                    StringUtils.EMPTY,
                    formatException(e)));
            throw e;
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


    public static void main(String[] args) throws UnsupportedEncodingException {
        CommonHttpClientImpl client = new CommonHttpClientImpl();
        HttpGet httpGet = new HttpGet("http://api.geetest.com/register.php?gt=034eb227cbea45e4780ec3624921356b&json_format=1&user_id=test&client_type=web&ip_address=127.0.0.1");
        HttpPost httpPost = new HttpPost("http://www.example.com");
        String json = "";
        httpPost.addHeader("content", "hidden");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", "John"));
        params.add(new BasicNameValuePair("password", "pass"));
        try {
            HttpResponseWithEntity response = client.execute(httpGet);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
