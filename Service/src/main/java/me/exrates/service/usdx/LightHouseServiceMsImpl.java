package me.exrates.service.usdx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.*;
import me.exrates.service.properties.InOutProperties;
import me.exrates.service.usdx.model.UsdxAccountBalance;
import me.exrates.service.usdx.model.UsdxTransaction;
import me.exrates.service.util.WithdrawUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Log4j2(topic = "usdx_log")
@Service
@PropertySource("classpath:/merchants/usdx.properties")
@Conditional(MicroserviceConditional.class)
public class LightHouseServiceMsImpl implements UsdxService {

    private static final String API_MERCHANT_LHT = "/api/merchant/lht/";
    private static final String API_MERCHANT_LHT_PROCESS_PAYMENT = API_MERCHANT_LHT.concat("processPayment");
    private static final String API_MERCHANT_LHT_CREATE_REFILL_ADMIN = API_MERCHANT_LHT.concat("create/refill/admin");
    private static final String API_MERCHANT_LHT_CREATE_WITHDRAW_ADMIN = API_MERCHANT_LHT.concat("create/withdraw/admin");

    private static final String API_MERCHANT_LHT_GET_MAIN_ADDRESS = API_MERCHANT_LHT.concat("mainAddress");
    private static final String API_MERCHANT_LHT_GET_MERCHANT= API_MERCHANT_LHT.concat("merchant");
    private static final String API_MERCHANT_LHT_GET_CURRENCY = API_MERCHANT_LHT.concat("currency");
    private static final String API_MERCHANT_LHT_GET_USDX_ACCOUNT_BALANCE = API_MERCHANT_LHT.concat("accountBalance");
    private static final String API_MERCHANT_LHT_GET_TRANSACTIONS = API_MERCHANT_LHT.concat("transactions");
    private static final String API_MERCHANT_LHT_GET_TRANSACTION_BY_TRANSFER_ID = API_MERCHANT_LHT.concat("transaction");

    private static final String DESTINATION_TAG_ERR_MSG = "message.usdx.tagError";

    @Autowired
    private WithdrawUtils withdrawUtils;

    @Autowired
    @Qualifier("inoutRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private InOutProperties properties;
    @Autowired
    private ObjectMapper mapper;

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        return null;
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        return null;
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        postWithParam(params, API_MERCHANT_LHT_PROCESS_PAYMENT);
    }

    @Override
    public String getMainAddress() {
        return restTemplate.getForObject(properties.getUrl() + API_MERCHANT_LHT_GET_MAIN_ADDRESS, String.class);
    }

    /*must bee only unsigned int = Memo.id - unsigned 64-bit number, MAX_SAFE_INTEGER  memo 0 - 9007199254740991*/
    @Override
    public void checkDestinationTag(String destinationTag) {
        if (!(NumberUtils.isDigits(destinationTag) && Long.valueOf(destinationTag) <= 9007199254740991L) || destinationTag.length() > 26) {
            throw new CheckDestinationTagException(DESTINATION_TAG_ERR_MSG, this.additionalWithdrawFieldName());
        }
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return withdrawUtils.isValidDestinationAddress(getMainAddress(), address);
    }

    @Override
    public Merchant getMerchant(){
        return restTemplate.getForObject(properties.getUrl() + API_MERCHANT_LHT_GET_MERCHANT, Merchant.class);
    }

    @Override
    public Currency getCurrency(){
        return restTemplate.getForObject(properties.getUrl() + API_MERCHANT_LHT_GET_CURRENCY, Currency.class);
    }

    @Override
    public UsdxAccountBalance getUsdxAccountBalance(){
        return restTemplate.getForObject(properties.getUrl() + API_MERCHANT_LHT_GET_USDX_ACCOUNT_BALANCE, UsdxAccountBalance.class);
    }

    @Override
    public List<UsdxTransaction> getAllTransactions(){
        return restTemplate.exchange(properties.getUrl() + API_MERCHANT_LHT_GET_TRANSACTIONS,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<UsdxTransaction>>() {}).getBody();
    }

    @Override
    public UsdxTransaction getTransactionByTransferId(String transferId){
        return restTemplate.getForObject(properties.getUrl() + API_MERCHANT_LHT_GET_TRANSACTION_BY_TRANSFER_ID, UsdxTransaction.class);
    }

    @Override
    public void checkHeaderOnValidForSecurity(String securityHeaderValue, UsdxTransaction usdxTransaction) {
        throw new NotImplimentedMethod("Not implement method");
    }

    @Override
    public void createRefillRequestAdmin(Map<String, String> params) throws JsonProcessingException {
        postWithParam(params, API_MERCHANT_LHT_CREATE_REFILL_ADMIN);
    }

    @Override
    public UsdxTransaction sendUsdxTransactionToExternalWallet(String password, UsdxTransaction usdxTransaction){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANT_LHT_CREATE_WITHDRAW_ADMIN)
                .queryParam("password", password);

        ResponseEntity<UsdxTransaction> responseTx = restTemplate.postForEntity(builder.toUriString(), usdxTransaction, UsdxTransaction.class);

        if(responseTx.getStatusCode().equals(HttpStatus.FORBIDDEN)){
            log.info("USDX Wallet. Invalid password. Time to try: {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            throw new IncorrectCoreWalletPasswordException("Invalid password");
        } else if(responseTx.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)){
            throw new InoutMicroserviceInternalServerException("Internal server error in InOut microservice");
        }

        return restTemplate.postForEntity(builder.toUriString(), usdxTransaction, UsdxTransaction.class).getBody();
    }

    private void postWithParam(Map<String, String> params, String apiMerchantLhtUrl) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + apiMerchantLhtUrl);

        HttpEntity<String> entity ;
        try {
            entity = new HttpEntity<>(mapper.writeValueAsString(params));
        }catch (JsonProcessingException JsonProcessingException){
            throw new InoutMicroserviceInternalServerException("Internal server error in InOut microservice");
        }

        ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);

        if(response.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)){
            throw new InoutMicroserviceInternalServerException("Internal server error in InOut microservice");
        }
    }


}
