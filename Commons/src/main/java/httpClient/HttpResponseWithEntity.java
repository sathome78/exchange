package httpClient;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.client.methods.CloseableHttpResponse;

@Data
@AllArgsConstructor
public class HttpResponseWithEntity {

    private CloseableHttpResponse closeableHttpResponse;
    private String responseEntity;
    private int status;

}
