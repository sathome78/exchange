package me.exrates.service.kyc.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.constants.Constants;
import me.exrates.model.dto.AccountInfoDto;
import me.exrates.model.dto.AccountQuberaRequestDto;
import me.exrates.model.dto.AccountQuberaResponseDto;
import me.exrates.model.dto.QuberaPaymentToMasterDto;
import me.exrates.model.dto.kyc.CreateApplicantDto;
import me.exrates.model.dto.kyc.ResponseCreateApplicantDto;
import me.exrates.model.dto.kyc.request.RequestOnBoardingDto;
import me.exrates.model.dto.kyc.responces.OnboardingResponseDto;
import me.exrates.model.ngExceptions.NgDashboardException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Log4j2
@Component
@PropertySource("classpath:/merchants/qubera.properties")
public class KycHttpClient {

    private @Value("${qubera.kyc.url}")
    String uriApi;
    private @Value("${qubera.kyc.apiKey}")
    String apiKey;

    private RestTemplate template;

    public KycHttpClient() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(30000);
        this.template = new RestTemplate(requestFactory);
    }

    public ResponseCreateApplicantDto createApplicant(CreateApplicantDto createApplicantDto) {
        log.info("createApplicant(), {}", toJson(createApplicantDto));
        String finalUrl = uriApi + "/verification/cis/file";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("apiKey", apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        builder.queryParam("synchronous", "true");
        URI uri = builder.build(true).toUri();

        HttpEntity<?> request = new HttpEntity<>(createApplicantDto, headers);

        ResponseEntity<ResponseCreateApplicantDto> responseEntity =
                template.exchange(uri, HttpMethod.POST, request, ResponseCreateApplicantDto.class);

        HttpStatus httpStatus = responseEntity.getStatusCode();

        if (!httpStatus.is2xxSuccessful()) {
            String errorString = "Error while creating applicant ";
            log.error(errorString + " {}", responseEntity);
            throw new NgDashboardException("Error while response from service, create applicant",
                    Constants.ErrorApi.QUBERA_RESPONSE_CREATE_APPLICANT_ERROR);
        }
        return responseEntity.getBody();
    }

    public OnboardingResponseDto createOnBoarding(RequestOnBoardingDto requestDto) {
        log.info("createOnBoarding (), {}", toJson(requestDto));
        String finalUrl = uriApi + "/verification/onboarding/sendlink";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("apiKey", apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();

        HttpEntity<?> request = new HttpEntity<>(requestDto, headers);

        ResponseEntity<OnboardingResponseDto> responseEntity = null;

        try {
            responseEntity =
                    template.exchange(uri, HttpMethod.POST, request, OnboardingResponseDto.class);
        } catch (Exception e) {
            log.error("Error response {}", ExceptionUtils.getStackTrace(e));
            log.error("Response error {}", toJson(responseEntity));
            throw new NgDashboardException("Error while creating onboarding",
                    Constants.ErrorApi.QUBERA_RESPONSE_CREATE_ONBOARDING_ERROR);
        }

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            log.error("Error while creating onboarding {}", responseEntity);
            throw new NgDashboardException("Error while creating onboarding",
                    Constants.ErrorApi.QUBERA_RESPONSE_CREATE_ONBOARDING_ERROR);
        }

        return responseEntity.getBody();
    }

    public AccountQuberaResponseDto createAccount(AccountQuberaRequestDto accountQuberaRequestDto) {
        log.info("createAccount (), {}", toJson(accountQuberaRequestDto));
        String finalUrl = uriApi + "/account/create";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("apiKey", apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();

        HttpEntity<?> request = new HttpEntity<>(accountQuberaRequestDto, headers);

        ResponseEntity<AccountQuberaResponseDto> responseEntity =
                template.exchange(uri, HttpMethod.POST, request, AccountQuberaResponseDto.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            log.error("Error create account {}", responseEntity.getBody());
            throw new NgDashboardException("Error while creating account",
                    Constants.ErrorApi.QUBERA_CREATE_ACCOUNT_RESPONSE_ERROR);
        }

        return responseEntity.getBody();
    }

    public AccountInfoDto getBalanceAccount(String account) {
        String finalUrl = uriApi + "/v2/account/" + account + "/balance";
        HttpHeaders headers = new HttpHeaders();
        headers.set("apiKey", apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<AccountInfoDto> responseEntity =
                template.exchange(uri, HttpMethod.GET, request, AccountInfoDto.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            log.error("Error create account {}", responseEntity.getBody());
            throw new NgDashboardException("Error response account balance",
                    Constants.ErrorApi.QUBERA_ACCOUNT_RESPONSE_ERROR);
        }

        return responseEntity.getBody();
    }

    public Boolean createPaymentInternal(QuberaPaymentToMasterDto paymentToMasterDto, boolean toMaster) {
        String finalUrl = toMaster ? uriApi + "/payment/master" : uriApi + "/payment/internal";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("apiKey", apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();

        HttpEntity<?> request = new HttpEntity<>(paymentToMasterDto, headers);

        ResponseEntity<AccountQuberaResponseDto> responseEntity =
                template.exchange(uri, HttpMethod.POST, request, AccountQuberaResponseDto.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            log.error("Error create account {}", responseEntity.getBody());
            throw new NgDashboardException("Error while creating payment to master",
                    Constants.ErrorApi.QUBERA_PAYMENT_TO_MASTER_ERROR);
        } else {
            return true;
        }
    }


    private String toJson(Object input) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            log.error("Error create json from object");
            return StringUtils.EMPTY;
        }
    }
}
