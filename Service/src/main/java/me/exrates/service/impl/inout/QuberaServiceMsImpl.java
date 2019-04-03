package me.exrates.service.impl.inout;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.AccountCreateDto;
import me.exrates.model.dto.AccountQuberaResponseDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.qubera.AccountInfoDto;
import me.exrates.model.dto.qubera.ExternalPaymentDto;
import me.exrates.model.dto.qubera.PaymentRequestDto;
import me.exrates.model.dto.qubera.QuberaRequestDto;
import me.exrates.model.dto.qubera.ResponsePaymentDto;
import me.exrates.service.QuberaService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.properties.InOutProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
public class QuberaServiceMsImpl implements QuberaService {

    private static final String API_MERCHANTS_QUBERA_PROCESS_PAYMENT = "/api/merchant/qubera/processPayment";
    private static final String API_MERCHANTS_QUBERA_LOG_RESPONSE = "/api/merchant/qubera/logResponse";
    private final InOutProperties properties;
    private final RestTemplate template;
    private final ObjectMapper mapper;

    @Override
    @SneakyThrows
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANTS_QUBERA_PROCESS_PAYMENT);

        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(params));
        template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity, String.class);

    }

    @Override
    @SneakyThrows
    public boolean logResponse(QuberaRequestDto requestDto) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANTS_QUBERA_LOG_RESPONSE);

        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(requestDto));
        return template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity, Boolean.class).getBody();

    }

    @Override
    public AccountQuberaResponseDto createAccount(AccountCreateDto accountCreateDto) {
        return null;
    }

    @Override
    public boolean checkAccountExist(String email, String currency) {
        return false;
    }

    @Override
    public AccountInfoDto getInfoAccount(String principalEmail) {
        return null;
    }

    @Override
    public ResponsePaymentDto createPaymentToMaster(String email, PaymentRequestDto paymentRequestDto) {
        return null;
    }

    @Override
    public ResponsePaymentDto createPaymentFromMater(String email, PaymentRequestDto paymentRequestDto) {
        return null;
    }

    @Override
    public String confirmPaymentToMaster(Integer paymentId) {
        return null;
    }

    @Override
    public String confirmPaymentFRomMaster(Integer paymentId) {
        return null;
    }

    @Override
    public ResponsePaymentDto createExternalPayment(ExternalPaymentDto externalPaymentDto, String email) {
        return null;
    }

    @Override
    public String confirmExternalPayment(Integer paymentId) {
        return null;
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        return null;
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
