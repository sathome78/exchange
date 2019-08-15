package me.exrates.service;

import me.exrates.dao.AdGroupDao;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.merchants.adgroup.AdGroupCommonRequestDto;
import me.exrates.model.dto.merchants.adgroup.AdGroupFetchTxDto;
import me.exrates.model.dto.merchants.adgroup.AdGroupRequestRefillBodyDto;
import me.exrates.model.dto.merchants.adgroup.CommonAdGroupHeaderDto;
import me.exrates.model.dto.merchants.adgroup.enums.TxStatus;
import me.exrates.model.dto.merchants.adgroup.responses.AdGroupResponseDto;
import me.exrates.model.dto.merchants.adgroup.responses.ResponseListTxDto;
import me.exrates.model.merchants.AdGroupTx;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.http.AdGroupHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
@PropertySource({"classpath:/merchants/adgroupe.properties"})
public class AdgroupeServiceImpl implements AdgroupeService {

    private final AdGroupHttpClient httpClient;
    private final AdGroupDao adGroupDao;
    @Value("${url}")
    private String url;
    @Value("${client_id}")
    private String clientId;
    @Value("${client_secret}")
    private String clientSecret;
    @Value("${walllet}")
    private Integer wallet;

    @Autowired
    public AdgroupeServiceImpl(AdGroupHttpClient httpClient,
                               AdGroupDao adGroupDao) {
        this.httpClient = httpClient;
        this.adGroupDao = adGroupDao;
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        CommonAdGroupHeaderDto header = new CommonAdGroupHeaderDto("p2pInvoiceRequest");
        AdGroupRequestRefillBodyDto reqBody = AdGroupRequestRefillBodyDto.builder()
                .amount(request.getAmount())
                .currency(request.getCurrencyName())
                .platform("YANDEX")
                .tel(wallet)
                .paymentMethod(request.getPaymentMethod())
                .build();

        AdGroupCommonRequestDto requestDto = new AdGroupCommonRequestDto<>(header, reqBody);
        String urlRequest = url + "/transfer/tx-merchant-wallet";

        AdGroupResponseDto response = httpClient.createInvoice(urlRequest, getAuthorizationKey(), requestDto);

        AdGroupTx tx = AdGroupTx.builder()
                .refillRequestId(request.getId())
                .tx(response.getResponseData().getId())
                .status("PENDING")
                .build();

        adGroupDao.save(tx);

        String link = response.getResponseData().getPaymentLink();
        return generateFullUrlMap(link, "GET", new Properties());
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {

    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        return null;
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return false;
    }

    private String getAuthorizationKey() {
        String forEncode = clientId + ":" + clientSecret;
        return Base64.getEncoder().encodeToString(forEncode.getBytes());
    }

    @Scheduled
    public void regularlyCheckStatusTransactions() {
        //get currency not approve transactions
        List<String> transactions = new ArrayList<>();


        final String requestUrl = url + "/transfer/get-merchant-tx";

        CommonAdGroupHeaderDto header = new CommonAdGroupHeaderDto("fetchMerchTx");
        AdGroupFetchTxDto requestBody = AdGroupFetchTxDto.builder()
                .start(0)
                .limit(transactions.size())
                .txStatus(new String[]{"PENDING", "APPROVED", "REJECTED", "CREATED"})
                .refId(transactions.toArray(new String[0]))
                .build();

        AdGroupCommonRequestDto requestDto = new AdGroupCommonRequestDto<>(header, requestBody);
        ResponseListTxDto responseListTx =
                httpClient.getTransactions(requestUrl, getAuthorizationKey(), requestDto);


        for (String transaction : transactions) {
            responseListTx.getTransactions()
                    .stream()
                    .filter(tx -> tx.getRefid().equalsIgnoreCase(transaction))
                    .peek(tx -> {
                        switch (TxStatus.valueOf(tx.getTxStatus())) {
                            case APPROVED:
                                Map<String, String> params = new HashMap<>();
                                params.put("amount", tx.getAmount().toString());
                                params.put("currency", tx.getCurrency());
                                try {
                                    processPayment(params);
                                } catch (RefillRequestAppropriateNotFoundException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case PENDING:
                                break;
                            case REJECTED:
                                //decline refill request id
                                break;
                            case CREATED:
                        }
                    });
        }
    }

}
