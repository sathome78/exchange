package me.exrates.service.merchantStrategy;

import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.RefillRequestIdNeededException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by ValkSam on 24.03.2017.
 */
public interface IMerchantService {
  Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception;

  Map<String, String> refill(RefillRequestCreateDto request);

  void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException;

  Boolean createdRefillRequestRecordNeeded();

  Boolean needToCreateRefillRequestRecord();

  Boolean toMainAccountTransferringConfirmNeeded();

  Boolean generatingAdditionalRefillAddressAvailable();

  Boolean additionalTagForWithdrawAddressIsUsed();

  Boolean withdrawTransferringConfirmNeeded();

  default String generateFullUrl(String url, Properties properties) {
    return url.concat("?").concat(
        properties.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&"))
    );
  }

  default Map<String, String> generateFullUrlMap(String url, String method, Properties properties) {
    Map<String, String> result = new HashMap<String, String>() {{
      put("$__redirectionUrl", url);
      put("$__method", method);
    }};
    properties.entrySet().forEach(e -> result.put(e.getKey().toString(), e.getValue().toString()));
    return result;
  }

  default String getMainAddress() {
    return "qwqwqqqw";
  }
}
