package me.exrates.service.omni;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.RefillRequestAddressShortDto;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestPutOnBchExamDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.merchants.omni.OmniBalanceDto;
import me.exrates.model.dto.merchants.omni.OmniTxDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.properties.InOutProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@Conditional(MicroserviceConditional.class)
public class OmniServiceMsImpl implements OmniService {

    private static final String API_GET_BTC_BALANCE = "/api/usdt/getBtcBalance";
    private static final String API_GET_USDT_BALANCES = "/api/usdt/getUsdtBalances";
    private static final String API_GET_USDT_TRANSACTIONS = "/api/usdt/getUsdtTransactions";
    private static final String API_GET_BLOCKED_ADDERSSES = "/api/usdt/getUsdtBlockedAddersses";
    private static final String API_CREATE_TRANSACTION = "/api/usdt/createUsdtTransaction";
    private static final String API_USDT_MIN_CONFIRMATIONS_REFILL = "/api/usdt/minConfirmationsRefill";
    private final CurrencyService currencyService;
    private final MerchantService merchantService;
    private final InOutProperties properties;
    private final RestTemplate template;
    private final ObjectMapper mapper;
    private Merchant merchant;
    private Currency currency;
    private static final String CURRENCY_NAME = "USDT";
    private static final String MERCHANT_NAME = "USDT";
    private static final String USDT_TOKEN_NAME = "USDT";
    private static final Integer USDT_PROPERTY_ID = 31;

    public OmniServiceMsImpl(CurrencyService currencyService, MerchantService merchantService, InOutProperties properties, RestTemplate template, ObjectMapper mapper) {
        this.currencyService = currencyService;
        this.merchantService = merchantService;
        this.properties = properties;
        this.template = template;
        this.mapper = mapper;
    }

    @PostConstruct
    private void init() {
        currency = currencyService.findByName(CURRENCY_NAME);
        merchant = merchantService.findByName(MERCHANT_NAME);
    }

    @Override
    public void putOnBchExam(RefillRequestPutOnBchExamDto dto) {

    }

    @Override
    public RefillRequestAcceptDto createRequest(String address, String hash, BigDecimal amount) {
        return null;
    }

    @Override
    public void frozeCoins(String address, BigDecimal amount) {

    }

    @Override
    public Merchant getMerchant() {
        return merchant;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public String getWalletPassword() {
        return null;
    }

    @Override
    public OmniBalanceDto getUsdtBalances() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_GET_USDT_BALANCES);

        ResponseEntity<OmniBalanceDto> response = template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                HttpEntity.EMPTY, new ParameterizedTypeReference<OmniBalanceDto>() {});

        return response.getBody();
    }

    @Override
    public BigDecimal getBtcBalance() {
        return template.getForObject(properties.getUrl() + API_GET_BTC_BALANCE, BigDecimal.class);
    }

    @Override
    public Integer getUsdtPropertyId() {
        return null;
    }

    @Override
    public List<OmniTxDto> getAllTransactions() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_GET_USDT_TRANSACTIONS);

        ResponseEntity<List<OmniTxDto>> response = template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                HttpEntity.EMPTY, new ParameterizedTypeReference<List<OmniTxDto>>() {});

        return response.getBody();
    }

    @Override
    public List<RefillRequestAddressShortDto> getBlockedAddressesOmni() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_GET_BLOCKED_ADDERSSES);

        ResponseEntity<List<RefillRequestAddressShortDto>> response = template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                HttpEntity.EMPTY, new ParameterizedTypeReference<List<RefillRequestAddressShortDto>>() {});

        return response.getBody();
    }

    @Override
    public void createRefillRequestAdmin(Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_CREATE_TRANSACTION);

        HttpEntity<?> entity;
        try {
            entity = new HttpEntity<>(mapper.writeValueAsString(params));
        } catch (JsonProcessingException e) {
            log.error("error createRefillRequestAdmin", e);
            throw new RuntimeException(e);
        }
        template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity, String.class);

    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        return null;
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

    @Override
    public Integer minConfirmationsRefill() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_USDT_MIN_CONFIRMATIONS_REFILL);

        HttpEntity<Integer> entity = new HttpEntity<Integer>(1);
        ResponseEntity<Integer> response = template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity, Integer.class);
        return response.getBody();
    }
}
