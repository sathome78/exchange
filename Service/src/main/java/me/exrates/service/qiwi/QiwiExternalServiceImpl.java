package me.exrates.service.qiwi;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.qiwi.request.QiwiRequest;
import me.exrates.model.dto.qiwi.request.QiwiRequestGetTransactions;
import me.exrates.model.dto.qiwi.request.QiwiRequestHeader;
import me.exrates.model.dto.qiwi.response.QiwiResponse;
import me.exrates.model.dto.qiwi.response.QiwiResponseP2PInvoice;
import me.exrates.model.dto.qiwi.response.QiwiResponseTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@Log4j2(topic = "Qiwi")
@PropertySource("classpath:/merchants/qiwi.properties")
public class QiwiExternalServiceImpl implements QiwiExternalService{

    private final static String URL_GET_TRANSACTIONS = "/transfer/get-merchant-tx";
    private final static String URL_GENERATE_P2P_INVOICE_WITH_UNIQ_MEMO = "/transfer/tx-merchant-wallet";

    @Value("${qiwi.base.production.url}")
    private String baseUrl;

    @Value("${qiwi.transaction.position.start}")
    private int startPosition;
    @Value("${qiwi.transaction.limit}")
    private int limit;

    @Qualifier(value = "qiwiRestTemplate")
    @Autowired
    private RestTemplate qiwiRestTemplate;

    public String generateUniqMemo(int userId) {
        QiwiRequestHeader requestHeader = new QiwiRequestHeader("p2pInvoiceRequest");

        QiwiRequest request = new QiwiRequest(requestHeader, null);

        ResponseEntity<QiwiResponseP2PInvoice> response = qiwiRestTemplate.postForEntity(baseUrl+URL_GENERATE_P2P_INVOICE_WITH_UNIQ_MEMO, request , QiwiResponseP2PInvoice.class );

        log.info("*** Qiwi *** | Generate new uniq memo. UserId:"+userId+" | Memo:"+response.getBody().getResponseData().getComment());

        return response.getBody().getResponseData().getComment();
    }

    public List<QiwiResponseTransaction> getLastTransactions() {
        QiwiRequestHeader requestHeader = new QiwiRequestHeader("fetchMerchTx");
        QiwiRequestGetTransactions requestBody = new QiwiRequestGetTransactions(startPosition, limit);

        QiwiRequest request = new QiwiRequest(requestHeader, requestBody);

        ResponseEntity<QiwiResponse> response = qiwiRestTemplate.postForEntity(baseUrl+URL_GET_TRANSACTIONS, request , QiwiResponse.class );

        QiwiResponseTransaction[] trans = response.getBody().getResponseData().getTransactions();

        return Arrays.asList(trans);
    }
}
