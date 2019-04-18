package me.exrates.service.nem;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.*;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.properties.InOutProperties;
import org.nem.core.model.Account;
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
public class NemServiceMsImpl implements NemService {

    private final InOutProperties properties;
    private final RestTemplate template;
    private final ObjectMapper mapper;

    @Override
    public BigDecimal countSpecCommission(BigDecimal amount, String destinationTag, Integer merchantId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + "/api/countSpecCommission/" + NemServiceImpl.NEM_MERCHANT);

        ResponseEntity<BigDecimal> response = template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                HttpEntity.EMPTY, new ParameterizedTypeReference<BigDecimal>() {});

        return response.getBody();
    }

    @Override
    public Account getAccount() {
        return null;
    }

    @Override
    public void processMosaicPayment(List<NemMosaicTransferDto> mosaics, Map<String, String> params) {

    }

    @Override
    public void checkRecievedTransaction(RefillRequestFlatDto dto) throws RefillRequestAppropriateNotFoundException {

    }

    @Override
    public boolean checkSendedTransaction(String hash, String additionalParams) throws RefillRequestAppropriateNotFoundException {
        return false;
    }

    @Override
    public List<MosaicIdDto> getDeniedMosaicList() {
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
