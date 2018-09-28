package me.exrates.service.aidos;

import com.google.common.base.Preconditions;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestPutOnBchExamDto;
import me.exrates.model.dto.TxReceivedByAddressFlatDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.merchants.btc.BtcAdminPaymentResponseDto;
import me.exrates.model.dto.merchants.btc.BtcPaymentResultDetailedDto;
import me.exrates.model.dto.merchants.btc.BtcWalletPaymentItemDto;
import me.exrates.service.BitcoinLikeCurrency;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.IncorrectCoreWalletPasswordException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.*;


@Log4j2(topic = "adk_log")
@PropertySource("classpath:/merchants/adk.properties")
@Service
public class AdkServiceImpl implements AdkService, BitcoinLikeCurrency {

    private final AidosNodeService aidosNodeService;
    private final MessageSource messageSource;
    private final MerchantService merchantService;
    private final CurrencyService currencyService;
    private final RefillService refillService;

    private static final String CURRENCY_NAME = "ADK";
    private static final String MERCHANT_NAME = "ADK";
    private Merchant merchant;
    private Currency currency;
    private static final Integer SECONDDS_TO_UNLOCK_WALLET = 60;
    private @Value("${wallet.password}") String walletPassword;
    private static final Object SEND_MONITOR = new Object();

    @Autowired
    public AdkServiceImpl(AidosNodeService aidosNodeService, MessageSource messageSource, MerchantService merchantService, CurrencyService currencyService, RefillService refillService) {
        this.aidosNodeService = aidosNodeService;
        this.messageSource = messageSource;
        this.merchantService = merchantService;
        this.currencyService = currencyService;
        this.refillService = refillService;
    }

    @PostConstruct
    private void inti() {
        currency = currencyService.findByName(CURRENCY_NAME);
        merchant = merchantService.findByName(MERCHANT_NAME);
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
    public MerchantService getMerchantService() {
        return merchantService;
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        BigDecimal amount = new BigDecimal(params.get("amount"));
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
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
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String address = aidosNodeService.generateNewAddress();
        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{address}, request.getLocale());
        return new HashMap<String, String>() {{
            put("address", address);
            put("message", message);
            put("qr", address);
        }};
    }

    @Override
    public RefillRequestAcceptDto createRequest(TxReceivedByAddressFlatDto transactionDto) {
        if (isTransactionDuplicate(transactionDto.getTxId(), currency.getId(), merchant.getId())) {
            log.error("ADK transaction allready received!!! {}", transactionDto);
            throw new RuntimeException("ADK transaction allready received!!!");
        }
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(transactionDto.getAddress())
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(transactionDto.getAmount())
                .merchantTransactionId(transactionDto.getTxId())
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
        requestAcceptDto.setRequestId(requestId);
        return requestAcceptDto;
    }

    @Override
    public void putOnBchExam(RefillRequestAcceptDto requestAcceptDto) {
        try {
            refillService.putOnBchExamRefillRequest(
                    RefillRequestPutOnBchExamDto.builder()
                            .requestId(requestAcceptDto.getRequestId())
                            .merchantId(merchant.getId())
                            .currencyId(currency.getId())
                            .address(requestAcceptDto.getAddress())
                            .amount(requestAcceptDto.getAmount())
                            .hash(requestAcceptDto.getMerchantTransactionId())
                            .build());
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error(e);
        }
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getBalance() {
        return aidosNodeService.getBalance().toPlainString();
    }

    @Override
    public List<BtcTransactionHistoryDto> listAllTransactions() {
        JSONArray array = aidosNodeService.getAllTransactions();
        Map<String, List<BtcTransactionHistoryDto>> map = StreamSupport.stream(array.spliterator(), false)
                .map(transaction -> dtoMapper((JSONObject) transaction))
                .collect(groupingBy(BtcTransactionHistoryDto::getTxId));
        List<BtcTransactionHistoryDto> resultList = new ArrayList<>();
        map.forEach((k,v) -> {
                    if (v.stream().anyMatch(p -> Double.valueOf(p.getAmount()) < 0)) {
                        resultList.add(v.stream().reduce((a,b) -> new BtcTransactionHistoryDto(a.getTxId(), "", "send",
                                                        new BigDecimal(a.getAmount()).add(new BigDecimal(b.getAmount())).setScale(8, RoundingMode.HALF_DOWN).toPlainString(),
                                                        a.getConfirmations(), a.getTime())).orElse(new BtcTransactionHistoryDto(v.get(0).getTxId())));
                    } else {
                        resultList.addAll(v);
                    }
        });
        return resultList;
    }

    @Override
    public void unlockWallet(String password) {
        if (password == null || !password.equals(walletPassword)) {
            throw new IncorrectCoreWalletPasswordException("Incorrect password: " + password);
        }
        Preconditions.checkState(aidosNodeService.unlockWallet(password, SECONDDS_TO_UNLOCK_WALLET), "Wallet unlocking error");
    }

    @Synchronized(value = "SEND_MONITOR")
    @Override
    public BtcAdminPaymentResponseDto sendManyTransactions(List<BtcWalletPaymentItemDto> payments) {
        JSONObject response = aidosNodeService.sendMany(payments);
        String bundleId = response.getString("result");
        String error = response.optString("error");
        List<BtcPaymentResultDetailedDto> results = payments.stream().map(p -> new BtcPaymentResultDetailedDto(p.getAddress(), p.getAmount().toPlainString(), bundleId, error)).collect(toList());
        BtcAdminPaymentResponseDto responseDto = new BtcAdminPaymentResponseDto();
        responseDto.setResults(results);
        return responseDto;
    }

    @Override
    public String getNewAddressForAdmin() {
        return aidosNodeService.generateNewAddress();
    }

    private BtcTransactionHistoryDto dtoMapper(JSONObject jsonObject) {
        BtcTransactionHistoryDto dto = new BtcTransactionHistoryDto();
        dto.setAddress(jsonObject.getString("address"));
        dto.setAmount(jsonObject.getBigDecimal("amount").toString());
        dto.setCategory(jsonObject.getString("category"));
        dto.setConfirmations(jsonObject.getInt("confirmations"));
        dto.setTxId(jsonObject.getString("txid"));
        dto.setTime(new Timestamp(jsonObject.getInt("time")).toLocalDateTime());
        return dto;
    }

    public static String getCurrencyName() {
        return CURRENCY_NAME;
    }

    public static String getMerchantName() {
        return MERCHANT_NAME;
    }

    private boolean isTransactionDuplicate(String hash, int currencyId, int merchantId) {
        return StringUtils.isEmpty(hash)
                || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchantId, currencyId, hash).isPresent();
    }
}
