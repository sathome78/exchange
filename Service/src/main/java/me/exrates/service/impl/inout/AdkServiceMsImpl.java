package me.exrates.service.impl.inout;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.RefillRequestAddressShortDto;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.*;
import me.exrates.model.dto.merchants.btc.BtcPaymentResultDetailedDto;
import me.exrates.model.dto.merchants.btc.BtcWalletPaymentItemDto;
import me.exrates.service.MerchantService;
import me.exrates.service.aidos.AdkService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
public class AdkServiceMsImpl implements AdkService {
    public static final String API_ADK_GET_BALANCE = "/api/adk/getBalance";
    private final InOutProperties properties;
    private final RestTemplate template;
    private final ObjectMapper mapper;

    @Override
    public Merchant getMerchant() {
        return null;
    }

    @Override
    public Currency getCurrency() {
        return null;
    }

    @Override
    public MerchantService getMerchantService() {
        return null;
    }

    @Override
    public RefillRequestAcceptDto createRequest(String address, String hash, BigDecimal amount) {
        return null;
    }

    @Override
    public void putOnBchExam(RefillRequestAcceptDto requestAcceptDto) {

    }

    @Override
    public String getBalance() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_ADK_GET_BALANCE);

        ResponseEntity<String> response = template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<String>() {});

        return response.getBody();
    }

    @Override
    public BtcWalletInfoDto getWalletInfo() {
        return null;
    }

    @Override
    public List<BtcTransactionHistoryDto> listAllTransactions() {
        return null;
    }

    @Override
    public void submitWalletPassword(String password) {

    }

    @Override
    public List<BtcPaymentResultDetailedDto> sendToMany(List<BtcWalletPaymentItemDto> payments) {
        return null;
    }

    @Override
    public String getNewAddressForAdmin() {
        return null;
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
}
