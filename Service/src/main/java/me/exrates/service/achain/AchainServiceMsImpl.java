package me.exrates.service.achain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.merchants.omni.OmniBalanceDto;
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
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigDecimal;
import java.util.Map;

import static me.exrates.service.achain.AchainServiceImpl.MERCHANT_NAME;

@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
public class AchainServiceMsImpl implements AchainService {

    private final InOutProperties properties;
    private final RestTemplate template;

    @Override
    public BigDecimal countSpecCommission(BigDecimal amount, String destinationTag, Integer merchantId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + "/api/countSpecCommission/" + AchainServiceImpl.MERCHANT_NAME);

        ResponseEntity<BigDecimal> response = template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                HttpEntity.EMPTY, new ParameterizedTypeReference<BigDecimal>() {});

        return response.getBody();
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        throw new NotImplementedException();
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        throw new NotImplementedException();

    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new NotImplementedException();
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        throw new NotImplementedException();
    }
}
