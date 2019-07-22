package me.exrates.service.impl.inout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.AccountQuberaResponseDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.kyc.IdentityDataRequest;
import me.exrates.model.dto.kyc.responces.KycStatusResponseDto;
import me.exrates.model.dto.kyc.responces.OnboardingResponseDto;
import me.exrates.model.dto.qubera.AccountInfoDto;
import me.exrates.model.dto.qubera.ExternalPaymentShortDto;
import me.exrates.model.dto.qubera.PaymentRequestDto;
import me.exrates.model.dto.qubera.QuberaLog;
import me.exrates.model.dto.qubera.QuberaPaymentInfoDto;
import me.exrates.model.dto.qubera.ResponsePaymentDto;
import me.exrates.model.dto.qubera.responses.ExternalPaymentResponseDto;
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

@Log4j2
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
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANTS_QUBERA_PROCESS_PAYMENT);

        HttpEntity<String> entity;
        try {
            entity = new HttpEntity<>(mapper.writeValueAsString(params));
        } catch (JsonProcessingException e) {
            log.error("error quebera processPayment", e);
            throw new RuntimeException(e);
        }
        template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity, String.class);

    }

    @Override
    public boolean logResponse(QuberaLog requestDto) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANTS_QUBERA_LOG_RESPONSE);

        HttpEntity<String> entity;
        try {
            entity = new HttpEntity<>(mapper.writeValueAsString(requestDto));
        } catch (JsonProcessingException e) {
            log.error("error quebera logResponse", e);
            throw new RuntimeException(e);
        }
        return template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity, Boolean.class).getBody();

    }

    @Override
    public AccountQuberaResponseDto createAccount(String email) {
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
    public boolean confirmPaymentToMaster(Integer paymentId) {
        return true;
    }

    @Override
    public boolean confirmPaymentFRomMaster(Integer paymentId) {
        return true;
    }

    @Override
    public ExternalPaymentResponseDto createExternalPayment(ExternalPaymentShortDto externalPaymentDto, String email) {
        return null;
    }

    @Override
    public QuberaPaymentInfoDto getInfoForPayment(String email) {
        return null;
    }

    @Override
    public void sendNotification(QuberaLog quberaRequestDto) {

    }

    @Override
    public String getUserVerificationStatus(String email) {
        return null;
    }

    @Override
    public void processingCallBack(String referenceId, KycStatusResponseDto kycStatusResponseDto) {

    }

    @Override
    public OnboardingResponseDto startVerificationProcessing(IdentityDataRequest identityDataRequest, String email) {
        return null;
    }

    @Override
    public boolean confirmExternalPayment(Integer paymentId) {
        return false;
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
