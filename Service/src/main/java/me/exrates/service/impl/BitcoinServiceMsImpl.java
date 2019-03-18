package me.exrates.service.impl;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.sun.research.ws.wadl.HTTPMethods;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.PagingData;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.merchants.btc.*;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.*;
import me.exrates.service.btcCore.CoreWalletService;
import me.exrates.service.exception.*;
import me.exrates.service.properties.InOutProperties;
import me.exrates.service.util.ParamMapUtils;
import me.exrates.service.util.WithdrawUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2(topic = "bitcoin_core")
@PropertySource(value = {"classpath:/job.properties"})
public class BitcoinServiceMsImpl implements BitcoinService {

    private final static String ADMIN_BITCOIN_WALLET_URL = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}";

    @Value("${btcInvoice.blockNotifyUsers}")
    private Boolean BLOCK_NOTIFYING;

    @Autowired
    private RefillService refillService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MerchantSpecParamsDao merchantSpecParamsDao;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CoreWalletService bitcoinWalletService;
    @Autowired
    private WithdrawUtils withdrawUtils;
    @Autowired
    private GtagService gtagService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private InOutProperties inOutProperties;

//    private String inOutMicroserviceHost = inOutProperties.getUrl();
    private String inOutMicroserviceHost = "http://localhost:8081/";


    private String name;

    @Override
    public Integer minConfirmationsRefill() {
        throw new RuntimeException("Not implemented");
    }

    public BitcoinServiceMsImpl(String name){
        this.name = name;
    }

    @Override
    public boolean isRawTxEnabled() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    @Transactional
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto){
        throw new RuntimeException("Not implemented");
    }

    @Override
    @Transactional
    public Map<String, String> refill(RefillRequestCreateDto request) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void processPayment(Map<String, String> params) {
       restTemplate.postForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/transaction/create", params, String.class, name);
    }

    @Override
    public void onPayment(BtcTransactionDto transactionDto) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void onIncomingBlock(BtcBlockDto blockDto) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void backupWallet() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public BtcWalletInfoDto getWalletInfo() {
        throw new RuntimeException("Need implementation@!!!!");

//        return bitcoinWalletService.getWalletInfo();
    }

    @Override
    public List<BtcTransactionHistoryDto> listAllTransactions() {
        return restTemplate.exchange(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/transactions", HttpMethod.GET,
                null, new ParameterizedTypeReference<List<BtcTransactionHistoryDto>>() {}, name).getBody();
    }

  @Override
  public List<BtcTransactionHistoryDto> listTransactions(int page) {
        throw new NotImplimentedMethod("Not implemented");
  }

  @Override
  public DataTable<List<BtcTransactionHistoryDto>> listTransactions(Map<String, String> tableParams){
      return restTemplate.exchange(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/transactions/pagination", HttpMethod.GET,
              null, new ParameterizedTypeReference<DataTable<List<BtcTransactionHistoryDto>>>() {}, name, tableParams).getBody();
  }

  @Override
  public BigDecimal estimateFee() {
      throw new NotImplimentedMethod("Not implemented");
  }

    @Override
    public String getEstimatedFeeString() {
        return restTemplate.getForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/estimatedFee", String.class, name);
    }

    @Override
    public BigDecimal getActualFee() {
        return restTemplate.getForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/actualFee", BigDecimal.class, name);
    }

    @Override
    public void setTxFee(BigDecimal fee) {
        restTemplate.postForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/setFee", fee, String.class, name);
    }

    @Override
    public void submitWalletPassword(String password) {
        restTemplate.postForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/setFee", password, String.class, name);
    }

    @Override
    public List<BtcPaymentResultDetailedDto> sendToMany(List<BtcWalletPaymentItemDto> payments) {
        ResponseEntity responseEntity = restTemplate.postForEntity(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/sendToMany", payments, ResponseEntity.class);
        JSONArray jsonArray = new JSONArray(responseEntity);
        Arrays.asList(jsonArray);

        throw new RuntimeException("Need implementation@!!!!");
    }

    @Override
    public BtcAdminPreparedTxDto prepareRawTransactions(List<BtcWalletPaymentItemDto> payments) {
/*        List<Map<String, BigDecimal>> paymentGroups = groupPaymentsForSeparateTransactions(payments);
        BigDecimal feeRate = getActualFee();

        return new BtcAdminPreparedTxDto(paymentGroups.stream().map(group -> bitcoinWalletService.prepareRawTransaction(group))
                .collect(Collectors.toList()), feeRate);*/
        throw new RuntimeException("Need implementation@!!!!");

    }

    @Override
    public BtcAdminPreparedTxDto updateRawTransactions(List<BtcPreparedTransactionDto> preparedTransactions) {
//        BigDecimal feeRate = getActualFee();
//        return new BtcAdminPreparedTxDto(preparedTransactions.stream()
//                .map(transactionDto -> bitcoinWalletService.prepareRawTransaction(transactionDto.getPayments(), transactionDto.getHex()))
//                .collect(Collectors.toList()), feeRate);
        throw new RuntimeException("Need implementation@!!!!");

    }

    @Override
    public List<BtcPaymentResultDetailedDto> sendRawTransactions(List<BtcPreparedTransactionDto> preparedTransactions) {
/*        return preparedTransactions.stream().flatMap(preparedTx -> {
            BtcPaymentResultDto resultDto = bitcoinWalletService.signAndSendRawTransaction(preparedTx.getHex());
            return preparedTx.getPayments().entrySet().stream()
                    .map(payment -> new BtcPaymentResultDetailedDto(payment.getKey(), payment.getValue(), resultDto));
        }).collect(Collectors.toList());*/
        throw new RuntimeException("Need implementation@!!!!");

    }

    @Override
    public void scanForUnprocessedTransactions(@Nullable String blockHash) {
        restTemplate.postForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/checkPayments", blockHash, String.class, name);
    }

    @Override
    public String getNewAddressForAdmin() {
        return restTemplate.getForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/getNewAddressForAdmin", String.class, name);
    }

    @Override
    public void setSubtractFeeFromAmount(boolean subtractFeeFromAmount) {
        restTemplate.postForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/setSubtractFee", subtractFeeFromAmount, String.class, name);
    }

    @Override
    public boolean getSubtractFeeFromAmount() {
        return restTemplate.getForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/getSubtractFeeStatus", Boolean.class, name);
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        throw new RuntimeException("Not implemented");
    }

  @Override
  public long getBlocksCount() throws BitcoindException, CommunicationException {
        throw new RuntimeException("Need implementation@!!!!");
//    return bitcoinWalletService.getBlocksCount();
  }

  @Override
  public Long getLastBlockTime() throws BitcoindException, CommunicationException {
      throw new RuntimeException("Need implementation@!!!!");
//      return bitcoinWalletService.getLastBlockTime();
  }

}
