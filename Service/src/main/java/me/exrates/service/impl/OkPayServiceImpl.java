package me.exrates.service.impl;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import me.exrates.dao.RefillRequestDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.*;
import me.exrates.service.exception.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

@Service
@PropertySource("classpath:/merchants/okpay.properties")
public class OkPayServiceImpl implements OkPayService {

    private @Value("${okpay.ok_receiver}") String ok_receiver;
    private @Value("${okpay.ok_receiver_email}") String ok_receiver_email;
    private @Value("${okpay.ok_item_1_name}") String ok_item_1_name;
    private @Value("${okpay.ok_s_title}") String ok_s_title;
    private @Value("${okpay.url}") String url;
    private @Value("${okpay.urlReturn}") String urlReturn;

    private static final Logger logger = LogManager.getLogger("merchant");

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private RefillRequestDao refillRequestDao;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private RefillService refillService;

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        throw new NotImplimentedMethod("for "+withdrawMerchantOperationDto);
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {

        Integer requestId = request.getId();
        if (requestId == null) {
            throw new RefillRequestIdNeededException(request.toString());
        }
        BigDecimal sum = request.getAmount();
        String currency = request.getCurrencyName();
        BigDecimal amountToPay = sum.setScale(2, BigDecimal.ROUND_HALF_UP);

        Properties properties = new Properties();

        properties.put("ok_receiver", ok_receiver);
        properties.put("ok_currency", currency);
        properties.put("ok_invoice", String.valueOf(requestId));
        properties.put("ok_item_1_name", ok_item_1_name);
        properties.put("ok_item_1_price", amountToPay.toString());
        properties.put("ok_s_title", ok_s_title);

        return generateFullUrlMap(url, "POST", properties);
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {

        if (!sendReturnRequest(params)){
            throw new RefillRequestAppropriateNotFoundException(params.toString());
        }

        Integer requestId = Integer.valueOf(params.get("ok_invoice"));
        String merchantTransactionId = params.get("ok_txn_id");
        Currency currency = currencyService.findByName(params.get("ok_txn_currency"));
        Merchant merchant = merchantService.findByName("OkPay");
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(params.get("ok_txn_gross"))).setScale(9);

        RefillRequestFlatDto refillRequest = refillRequestDao.getFlatByIdAndBlock(requestId)
                .orElseThrow(() -> new RefillRequestNotFoundException(String.format("refill request id: %s", requestId)));

        if(refillRequest.getAmount().equals(amount)
                && currency.equals(currencyService.getById(refillRequest.getCurrencyId()))
                && params.get("ok_txn_status").equals("completed")
                && params.get("ok_receiver_email").equals(ok_receiver_email)){

            RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                    .requestId(requestId)
                    .merchantId(merchant.getId())
                    .currencyId(currency.getId())
                    .amount(amount)
                    .merchantTransactionId(merchantTransactionId)
                    .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                    .build();
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        }

    }

    private boolean sendReturnRequest(Map<String,String> params) {

        final OkHttpClient client = new OkHttpClient();
        final FormEncodingBuilder formBuilder = new FormEncodingBuilder();
        formBuilder.add("ok_verify", "true");


        for (Map.Entry<String, String> entry : params.entrySet()){
            formBuilder.add(entry.getKey(), entry.getValue());
        }

        final Request request = new Request.Builder()
                .url(urlReturn)
                .post(formBuilder.build())
                .build();
        final String returnResponse;

        try {
            returnResponse =client
                    .newCall(request)
                    .execute()
                    .body()
                    .string();
            logger.info("returnResponse: " + returnResponse);
        } catch (IOException e) {
            throw new MerchantInternalException(e);
        }

        return returnResponse.equals("VERIFIED");
    }
}
