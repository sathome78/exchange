package me.exrates.service.omni;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.RefillRequestAddressShortDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestPutOnBchExamDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.merchants.omni.OmniBalanceDto;
import me.exrates.model.dto.merchants.omni.OmniTxDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.CoreWalletPasswordNotFoundException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2(topic = "omni_log")
@Service
public class OmniServiceImpl implements OmniService {

    private final WithdrawUtils withdrawUtils;
    private final OmniNodeService omniNodeService;
    private final MessageSource messageSource;
    private final RefillService refillService;
    private final MerchantService merchantService;
    private final CurrencyService currencyService;
    private final ObjectMapper objectMapper;
    private Merchant merchant;
    private Currency currency;
    private static final String CURRENCY_NAME = "USDT";
    private static final String MERCHANT_NAME = "USDT";
    private static final String USDT_TOKEN_NAME = "USDT";
    private static final Integer USDT_PROPERTY_ID = 31;
    private Object createRequestSync = new Object();

    @Override
    public Integer minConfirmationsRefill() {
        return 6;
    }

    @Autowired
    public OmniServiceImpl(WithdrawUtils withdrawUtils, OmniNodeService omniNodeService, MessageSource messageSource, RefillService refillService,
                           MerchantService merchantService, CurrencyService currencyService, ObjectMapper objectMapper) {
        this.withdrawUtils = withdrawUtils;
        this.omniNodeService = omniNodeService;
        this.messageSource = messageSource;
        this.refillService = refillService;
        this.merchantService = merchantService;
        this.currencyService = currencyService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void inti() {
        currency = currencyService.findByName(CURRENCY_NAME);
        merchant = merchantService.findByName(MERCHANT_NAME);
    }

    @Override
    public void putOnBchExam(RefillRequestPutOnBchExamDto dto) {
        try {
            refillService.putOnBchExamRefillRequest(dto);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error(e);
        }
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String address = omniNodeService.generateNewAddress();
        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{address}, request.getLocale());
        return new HashMap<String, String>() {{
            put("address", address);
            put("message", message);
            put("qr", address);
        }};
    }

    @Synchronized(value = "createRequestSync")
    @Override
    public RefillRequestAcceptDto createRequest(String address, String hash, BigDecimal amount) {
        if (isTransactionDuplicate(hash, currency.getId(), merchant.getId())) {
            log.error("USDT transaction allready received!!! {}", hash);
            throw new RuntimeException("USDT transaction allready received!!!");
        }
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        int requestId = refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(address, merchant.getId(), currency.getId(), hash)
                .orElse(refillService.createRequestByFactAndSetHash(requestAcceptDto));
        requestAcceptDto.setRequestId(requestId);
        return requestAcceptDto;
    }

    @Synchronized
    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("txId");
        BigDecimal amount = new BigDecimal(params.get("amount"));
        int id = Integer.parseInt(params.get("id"));
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .requestId(id)
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        refillService.autoAcceptRefillRequest(requestAcceptDto);
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return withdrawUtils.isValidDestinationAddress(address);
    }

    @Override
    public void frozeCoins(String address, BigDecimal amount) {
        refillService.blockUserByFrozeTx(address, merchant.getId(), currency.getId());
    }

    private boolean isTransactionDuplicate(String hash, int currencyId, int merchantId) {
        return StringUtils.isEmpty(hash)
                || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchantId, currencyId, hash).isPresent();
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        throw new RuntimeException("Not implemented");
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
        return merchantService.getCoreWalletPassword(MERCHANT_NAME, CURRENCY_NAME)
                .orElseThrow(() -> new CoreWalletPasswordNotFoundException(String.format("pass not found for merchant %s currency %s", MERCHANT_NAME, CURRENCY_NAME)));
    }

    @Override
    public BigDecimal getBtcBalance() {
        return new JSONObject(omniNodeService.getBtcInfo()).getBigDecimal("balance");
    }

    @Override
    public OmniBalanceDto getUsdtBalances() {
        try {
            List<OmniBalanceDto> dtos = objectMapper.readValue(omniNodeService.getOmniBalances(), new TypeReference<List<OmniBalanceDto>>(){});
            return dtos.stream().filter(p -> USDT_PROPERTY_ID.equals(p.getPropertyid())).findFirst().orElse(OmniBalanceDto.getZeroBalancesDto(USDT_PROPERTY_ID, USDT_TOKEN_NAME));
        } catch (IOException e) {
           log.error(e);
            return null;
        }
    }

    @Override
    public Integer getUsdtPropertyId() {
        return USDT_PROPERTY_ID;
    }

    @Override
    public List<OmniTxDto> getAllTransactions() {
        String rawTxs = omniNodeService.listAllTransactions();
        try {
            return objectMapper.readValue(rawTxs, new TypeReference<List<OmniTxDto>>(){});
        } catch (IOException e) {
            throw new RuntimeException("error getting transactions");
        }
    }

    @Override
    public List<RefillRequestAddressShortDto> getBlockedAddressesOmni() {
        return refillService.getBlockedAddresses(merchant.getId(), currency.getId());
    }

    @SneakyThrows
    @Override
    public void createRefillRequestAdmin(Map<String, String> params) {
        String hash = params.get("txId");
        OmniTxDto dto = objectMapper.readValue(omniNodeService.getTransaction(hash), new TypeReference<OmniTxDto>(){});
        RefillRequestAcceptDto acceptDto = createRequest(dto.getReferenceaddress(), dto.getTxid(), dto.getAmount());
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("txId", hash);
        paramsMap.put("address", dto.getReferenceaddress());
        paramsMap.put("amount", dto.getAmount().toPlainString());
        paramsMap.put("id", acceptDto.getRequestId().toString());
        processPayment(paramsMap);
    }
}
