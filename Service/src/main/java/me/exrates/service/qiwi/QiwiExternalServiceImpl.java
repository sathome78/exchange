package me.exrates.service.qiwi;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.qiwi.request.QiwiRequest;
import me.exrates.model.dto.qiwi.request.QiwiRequestGetTransactions;
import me.exrates.model.dto.qiwi.request.QiwiRequestHeader;
import me.exrates.model.dto.qiwi.response.QiwiResponse;
import me.exrates.model.dto.qiwi.response.QiwiResponseError;
import me.exrates.model.dto.qiwi.response.QiwiResponseP2PInvoice;
import me.exrates.model.dto.qiwi.response.QiwiResponseTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2(topic = "Qiwi")
@PropertySource("classpath:/merchants/qiwi.properties")
@Conditional(MonolitConditional.class)
public class QiwiExternalServiceImpl implements QiwiExternalService {

    private final static String URL_GET_TRANSACTIONS = "/transfer/get-merchant-tx";
    private final static String URL_GENERATE_P2P_INVOICE_WITH_UNIQ_MEMO = "/transfer/tx-merchant-wallet";

    @Value("${qiwi.base.url}")
    private String baseUrl;

    @Value("${qiwi.transaction.position.start}")
    private int startPosition;
    @Value("${qiwi.transaction.limit}")
    private int limit;

    @Qualifier(value = "qiwiRestTemplate")
    @Autowired
    private RestTemplate qiwiRestTemplate;

    @Override
    public Map<String, String> getResponseParams(int userId) {
        QiwiRequestHeader requestHeader = new QiwiRequestHeader("p2pInvoiceRequest");

        QiwiRequest request = new QiwiRequest(requestHeader, null);

        ResponseEntity<QiwiResponseP2PInvoice> responseEntity;
        try {
            responseEntity = qiwiRestTemplate.postForEntity(baseUrl + URL_GENERATE_P2P_INVOICE_WITH_UNIQ_MEMO, request, QiwiResponseP2PInvoice.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new RuntimeException("Qiwi server is not available");
            }
        } catch (Exception ex) {
            log.warn("Qiwi service did not return valid data: server not available");
            throw new RuntimeException(String.format("Qiwi service did not return valid data: %s", ex.getMessage()));
        }
        QiwiResponseP2PInvoice body = responseEntity.getBody();
        if (Objects.nonNull(body.getErrors())) {
            log.warn("Qiwi service did not return valid data: server not available");
            throw new RuntimeException(String.format("Qiwi service did not return valid data: %s", Arrays.stream(body.getErrors()).map(QiwiResponseError::getMessage).collect(Collectors.joining(", "))));
        }
        log.info("*** Qiwi *** | Generate new uniq memo. UserId: {} | Memo: {}", userId, body.getResponseData().getComment());

        Map<String, String> responseParams = new HashMap<>();
        responseParams.put("address", body.getResponseData().getComment());
        responseParams.put("paymentLink", body.getResponseData().getPaymentLink());

        return responseParams;
    }

    @Override
    public List<QiwiResponseTransaction> getLastTransactions() {
        QiwiRequestHeader requestHeader = new QiwiRequestHeader("fetchMerchTx");

        QiwiRequestGetTransactions requestBody = new QiwiRequestGetTransactions(startPosition, limit);

        QiwiRequest request = new QiwiRequest(requestHeader, requestBody);

        ResponseEntity<QiwiResponse> responseEntity;
        try {
            responseEntity = qiwiRestTemplate.postForEntity(baseUrl + URL_GET_TRANSACTIONS, request, QiwiResponse.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new RuntimeException("Qiwi server is not available");
            }
        } catch (Exception ex) {
            log.warn("Qiwi service did not return valid data: server not available");
            throw new RuntimeException(String.format("Qiwi service did not return valid data: %s", ex.getMessage()));
        }
        QiwiResponse body = responseEntity.getBody();
        if (Objects.nonNull(body.getErrors())) {
            log.warn("Qiwi service did not return valid data: server not available");
            throw new RuntimeException(String.format("Qiwi service did not return valid data: %s", Arrays.stream(body.getErrors()).map(QiwiResponseError::getMessage).collect(Collectors.joining(", "))));
        }

        return Arrays.asList(body.getResponseData().getTransactions());
    }
}