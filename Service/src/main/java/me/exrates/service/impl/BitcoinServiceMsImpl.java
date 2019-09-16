package me.exrates.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.model.dto.BtcWalletInfoDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.merchants.btc.*;
import me.exrates.service.BitcoinService;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.properties.InOutProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Log4j2(topic = "bitcoin_core")
public class BitcoinServiceMsImpl implements BitcoinService {

    private final static String ADMIN_BITCOIN_WALLET_URL = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}";

    private RestTemplate restTemplate;
    private ObjectMapper mapper;

    private String merchantName;
    private String inOutMicroserviceHost;

    public BitcoinServiceMsImpl(RestTemplate restTemplate, InOutProperties properties, ObjectMapper mapper, String merchantName) {
        this.restTemplate= restTemplate;
        this.inOutMicroserviceHost = properties.getUrl();
        this.mapper = mapper;
        this.merchantName = merchantName;
    }


    @Override
    public Integer minConfirmationsRefill() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isRawTxEnabled() {
        return restTemplate.getForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/isRawTxEnabled", Boolean.class, merchantName);
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
        try {
            restTemplate.postForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/transaction/create", mapper.writeValueAsString(params), String.class);
        } catch (JsonProcessingException e) {
            log.error("error send process payment", e);
            throw new RuntimeException(e);
        }
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
        return restTemplate.getForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/getWalletInfo", BtcWalletInfoDto.class, merchantName);
    }

    @Override
    public List<BtcTransactionHistoryDto> listAllTransactions() {
        return restTemplate.exchange(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/transactions", HttpMethod.GET,
                HttpEntity.EMPTY, new ParameterizedTypeReference<List<BtcTransactionHistoryDto>>() {}, merchantName).getBody();
    }

    @Override
    public BtcTransactionDto getTransactionByHash(String transactionHash) {
        throw new NotImplimentedMethod("Not implemented");
    }

  @Override
  public List<BtcTransactionHistoryDto> listTransactions(int page) {
        throw new NotImplimentedMethod("Not implemented");
  }

  @Override
  public DataTable<List<BtcTransactionHistoryDto>> listTransactions(Map<String, String> tableParams){
      try {
          return restTemplate.exchange(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/transactions/pagination", HttpMethod.GET,
                  HttpEntity.EMPTY, new ParameterizedTypeReference<DataTable<List<BtcTransactionHistoryDto>>>() {}, merchantName, mapper.writeValueAsString(tableParams)).getBody();
      } catch (JsonProcessingException e) {
          log.error("error getting list of transactions", e);
          throw new RuntimeException(e);
      }
  }

  @Override
  public BigDecimal estimateFee() {
      throw new NotImplimentedMethod("Not implemented");
  }

    @Override
    public String getEstimatedFeeString() {
        return restTemplate.getForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/estimatedFee", String.class, merchantName);
    }

    @Override
    public BigDecimal getActualFee() {
        return restTemplate.getForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/actualFee", BigDecimal.class, merchantName);
    }

    @Override
    public void setTxFee(BigDecimal fee) {
        try {
            restTemplate.postForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/setFee", mapper.writeValueAsString(fee), String.class, merchantName);
        } catch (JsonProcessingException e) {
            log.error("error process request to change tx fee", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void submitWalletPassword(String password) {
        try {
            restTemplate.postForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/setFee", mapper.writeValueAsString(password), String.class, merchantName);
        } catch (JsonProcessingException e) {
            log.error("error process request to submit password", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<BtcPaymentResultDetailedDto> sendToMany(List<BtcWalletPaymentItemDto> payments) {
        try {
            return restTemplate.exchange(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/sendToMany",
                    HttpMethod.POST,
                    new HttpEntity<>(mapper.writeValueAsString(payments)),
                    new ParameterizedTypeReference<List<BtcPaymentResultDetailedDto>>(){{}}).getBody();
        } catch (JsonProcessingException e) {
            log.error("error process request btc send to many", e);
            throw new RuntimeException(e);
        }
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
        try {
            restTemplate.postForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/checkPayments", mapper.writeValueAsString(blockHash), String.class, merchantName);
        } catch (JsonProcessingException e) {
            log.error("error process request to scan unprocessed", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getNewAddressForAdmin() {
        return restTemplate.getForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/getNewAddressForAdmin", String.class, merchantName);
    }

    @Override
    public void setSubtractFeeFromAmount(boolean subtractFeeFromAmount) {
        try {
            restTemplate.postForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/setSubtractFee", mapper.writeValueAsString(subtractFeeFromAmount), String.class, merchantName);
        } catch (JsonProcessingException e) {
            log.error("error process request set substract fee", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean getSubtractFeeFromAmount() {
        return restTemplate.getForObject(inOutMicroserviceHost + ADMIN_BITCOIN_WALLET_URL + "/getSubtractFeeStatus", Boolean.class, merchantName);
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        throw new RuntimeException("Not implemented");
    }

  @Override
  public long getBlocksCount(){
        throw new RuntimeException("Need implementation@!!!!");
//    return bitcoinWalletService.getBlocksCount();
  }

  @Override
  public Long getLastBlockTime(){
      throw new RuntimeException("Need implementation@!!!!");
//      return bitcoinWalletService.getLastBlockTime();
  }

}
