package me.exrates.service.kyc.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.constants.Constants;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.AccountQuberaRequestDto;
import me.exrates.model.dto.AccountQuberaResponseDto;
import me.exrates.model.dto.kyc.CreateApplicantDto;
import me.exrates.model.dto.kyc.ResponseCreateApplicantDto;
import me.exrates.model.dto.kyc.request.RequestOnBoardingDto;
import me.exrates.model.dto.kyc.responces.KycResponseStatusDto;
import me.exrates.model.dto.kyc.responces.OnboardingResponseDto;
import me.exrates.model.dto.qubera.AccountInfoDto;
import me.exrates.model.dto.qubera.QuberaPaymentToMasterDto;
import me.exrates.model.dto.qubera.QuberaRequestPaymentShortDto;
import me.exrates.model.dto.qubera.ResponseConfirmDto;
import me.exrates.model.dto.qubera.ResponsePaymentDto;
import me.exrates.model.dto.qubera.responses.ExternalPaymentResponseDto;
import me.exrates.model.dto.qubera.responses.ResponseVerificationStatusDto;
import me.exrates.model.ngExceptions.NgDashboardException;
import me.exrates.service.util.JsonUtils;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Log4j2(topic = "qubera_log")
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
        log.info("Starting http request createApplicant(), {}", JsonUtils.toJson(createApplicantDto));
        String finalUrl = uriApi + "/verification/cis/file";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("apiKey", apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        builder.queryParam("synchronous", "true");
        URI uri = builder.build(true).toUri();

        HttpEntity<?> request = new HttpEntity<>(createApplicantDto, headers);

        ResponseEntity<ResponseCreateApplicantDto> responseEntity;
        try {
            responseEntity =
                    template.exchange(uri, HttpMethod.POST, request, ResponseCreateApplicantDto.class);
        } catch (HttpClientErrorException e) {
            log.error("HttpClientErrorException http request while create createApplicant {}",
                    e.getResponseBodyAsString(),
                    e);
            throw new NgDashboardException(ErrorApiTitles.QUBERA_RESPONSE_CREATE_APPLICANT_ERROR);
        } catch (Exception e) {
            log.error("Error create createApplicant", e);
            throw new NgDashboardException("Error while creating account",
                    Constants.ErrorApi.QUBERA_RESPONSE_CREATE_APPLICANT_ERROR);
        }

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            String errorString = "Error while creating applicant ";
            log.error(errorString + " {}", responseEntity);
            throw new NgDashboardException("Error while response from service, create applicant",
                    Constants.ErrorApi.QUBERA_RESPONSE_CREATE_APPLICANT_ERROR);
        }
        log.info("Finish createApplicant http request, response \n {}", JsonUtils.toJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }

    public OnboardingResponseDto createOnBoarding(RequestOnBoardingDto requestDto) {
        log.info("Starting http request createOnBoarding (), {}", JsonUtils.toJson(requestDto));
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
        } catch (HttpClientErrorException e) {
            log.error("HttpClientErrorException http request createApplicant while createOnBoarding {}",
                    e.getResponseBodyAsString(),
                    e);
            throw new NgDashboardException(ErrorApiTitles.QUBERA_RESPONSE_CREATE_ONBOARDING_ERROR);
        } catch (Exception e) {
            log.error("Error createOnBoarding", e);
            throw new NgDashboardException("Error while creating onboarding",
                    Constants.ErrorApi.QUBERA_RESPONSE_CREATE_ONBOARDING_ERROR);
        }

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            log.error("Error while creating onboarding {}", responseEntity);
            throw new NgDashboardException("Error while creating onboarding",
                    Constants.ErrorApi.QUBERA_RESPONSE_CREATE_ONBOARDING_ERROR);
        }
        log.info("Finish createOnBoarding http request, response \n {}", JsonUtils.toJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }

    public AccountQuberaResponseDto createAccount(AccountQuberaRequestDto accountQuberaRequestDto) {
        log.info("Starting http request createAccount (), {}", JsonUtils.toJson(accountQuberaRequestDto));
        String finalUrl = uriApi + "/account/create";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("apiKey", apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();

        HttpEntity<?> request = new HttpEntity<>(accountQuberaRequestDto, headers);
        ResponseEntity<AccountQuberaResponseDto> responseEntity;
        try {
            responseEntity =
                    template.exchange(uri, HttpMethod.POST, request, AccountQuberaResponseDto.class);
        } catch (HttpClientErrorException e) {
            log.error("HttpClientErrorException http request while create account {}",
                    e.getResponseBodyAsString(),
                    e);
            throw new NgDashboardException(ErrorApiTitles.QUBERA_CREATE_ACCOUNT_RESPONSE_ERROR);
        } catch (Exception e) {
            log.error("Error create account", e);
            throw new NgDashboardException("Error while creating account",
                    Constants.ErrorApi.QUBERA_CREATE_ACCOUNT_RESPONSE_ERROR);
        }

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            log.error("Error create account {}", responseEntity.getBody());
            throw new NgDashboardException("Response not success while create account",
                    Constants.ErrorApi.QUBERA_CREATE_ACCOUNT_RESPONSE_ERROR);
        }
        log.info("Finish createAccount http request, response \n {}", JsonUtils.toJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }

    public AccountInfoDto getBalanceAccount(String account) {
        String finalUrl = uriApi + "/v2/account/" + account + "/balance";
        log.info("Starting http request getBalanceAccount (), url {}", finalUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("apiKey", apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<AccountInfoDto> responseEntity;
        try {
            responseEntity =
                    template.exchange(uri, HttpMethod.GET, request, AccountInfoDto.class);
        } catch (HttpClientErrorException e) {
            log.error("HttpClientErrorException http request createApplicant while create getBalanceAccount {}",
                    e.getResponseBodyAsString(),
                    e);
            throw new NgDashboardException(ErrorApiTitles.QUBERA_RESPONSE_GET_BALANCE_ERROR);
        } catch (Exception e) {
            log.error("Error getBalanceAccount", e);
            throw new NgDashboardException("Error response account balance",
                    Constants.ErrorApi.QUBERA_ACCOUNT_RESPONSE_ERROR);
        }
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            log.error("Error create account {}", responseEntity.getBody());
            throw new NgDashboardException("Error response account balance",
                    Constants.ErrorApi.QUBERA_ACCOUNT_RESPONSE_ERROR);
        }
        log.info("Finish getBalanceAccount http request, response \n {}",
                JsonUtils.toJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }

    public ResponsePaymentDto createPaymentInternal(QuberaPaymentToMasterDto paymentToMasterDto, boolean toMaster) {
        String finalUrl = toMaster ? uriApi + "/payment/master" : uriApi + "/payment/internal";
        log.info("Starting http request createPaymentInternal, url {}, {}",
                finalUrl,
                JsonUtils.toJson(paymentToMasterDto));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("apiKey", apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();

        HttpEntity<?> request = new HttpEntity<>(paymentToMasterDto, headers);
        ResponseEntity<ResponsePaymentDto> responseEntity;
        try {
            responseEntity =
                    template.exchange(uri, HttpMethod.POST, request, ResponsePaymentDto.class);
        } catch (HttpClientErrorException e) {
            log.error("HttpClientErrorException http request createApplicant while createPaymentInternal {}",
                    e.getResponseBodyAsString(),
                    e);
            throw new NgDashboardException(ErrorApiTitles.QUBERA_PAYMENT_INTERNAL_ERROR);
        } catch (Exception e) {
            log.error("Error createPaymentInternal", e);
            throw new NgDashboardException(ErrorApiTitles.QUBERA_PAYMENT_INTERNAL_ERROR);
        }
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            log.error("Error create account {}", responseEntity.getBody());
            throw new NgDashboardException(ErrorApiTitles.QUBERA_PAYMENT_INTERNAL_ERROR);
        } else {
            log.info("Finish createPaymentInternal http request, response \n {}",
                    JsonUtils.toJson(responseEntity.getBody()));
            return responseEntity.getBody();
        }
    }

    public boolean confirmInternalPayment(Integer paymentId, boolean toMaster) {
        String finalUrl;
        if (toMaster) {
            finalUrl = String.format("%s/payment/master/%d/confirm", uriApi, paymentId);
        } else {
            finalUrl = String.format("%s/payment/internal/%d/confirm", uriApi, paymentId);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("apiKey", apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity;
        try {
            responseEntity =
                    template.exchange(uri, HttpMethod.PUT, request, String.class);
        } catch (HttpClientErrorException e) {
            log.error("HttpClientErrorException http request createApplicant while confirmInternalPayment {}",
                    e.getResponseBodyAsString(),
                    e);
            throw new NgDashboardException(ErrorApiTitles.QUBERA_CONFIRM_PAYMENT_TO_MASTER_ERROR);
        } catch (Exception e) {
            log.error("Error confirm payment to master ", e);
            throw new NgDashboardException("Error confirm payment to master",
                    Constants.ErrorApi.QUBERA_CONFIRM_PAYMENT_TO_MASTER_ERROR);
        }

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            log.error("Error confirm payment to master {}", responseEntity.getBody());
            throw new NgDashboardException("Error confirm payment to master",
                    Constants.ErrorApi.QUBERA_CONFIRM_PAYMENT_TO_MASTER_ERROR);
        } else {
            ObjectMapper mapper = new ObjectMapper();
            ResponseConfirmDto responseConfirmDto = null;
            log.info("Finish confirmInternalPayment http request, response \n {}",
                    JsonUtils.toJson(responseEntity.getBody()));
            try {
                responseConfirmDto = mapper.readValue(responseEntity.getBody(), new TypeReference<ResponseConfirmDto>() {
                });
            } catch (Exception e) {
                log.info("Exception read value  {} to map {}", responseEntity.getBody(), e);
            }
            return responseConfirmDto != null && responseConfirmDto.getResult().equalsIgnoreCase("ok");
        }
    }

    public ExternalPaymentResponseDto createExternalPayment(QuberaRequestPaymentShortDto externalPaymentDto) {
        log.info("createExternalPayment(), {}", JsonUtils.toJson(externalPaymentDto));
        String finalUrl = uriApi + "/payment/v2/external/short";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("apiKey", apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();

        HttpEntity<?> request = new HttpEntity<>(externalPaymentDto, headers);
        ResponseEntity<ExternalPaymentResponseDto> responseEntity;
        try {
            responseEntity =
                    template.exchange(uri, HttpMethod.POST, request, ExternalPaymentResponseDto.class);
        } catch (HttpClientErrorException e) {
            log.error("HttpClientErrorException http request createApplicant while createExternalPayment {}",
                    e.getResponseBodyAsString(),
                    e);
            throw new NgDashboardException(ErrorApiTitles.QUBERA_EXTERNAL_PAYMENT_ERROR);
        } catch (Exception e) {
            log.error("Error createExternalPayment", e);
            throw new NgDashboardException(ErrorApiTitles.QUBERA_EXTERNAL_PAYMENT_ERROR);
        }

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            log.error("Error create external payment {}", responseEntity.getBody());
            throw new NgDashboardException("Error while creating external payment",
                    Constants.ErrorApi.QUBERA_ERROR_RESPONSE_CREATE_EXTERNAL_PAYMENT);
        } else {
            log.info("Finish createExternalPayment http request, response \n {}",
                    JsonUtils.toJson(responseEntity.getBody()));
            return responseEntity.getBody();
        }
    }

    public boolean confirmExternalPayment(Integer paymentId) {
        String finalUrl = String.format("%s/payment/external/%d/confirm", uriApi, paymentId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("apiKey", apiKey);
        log.info("Start http request confirmExternalPayment, url \n {}", finalUrl);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = template.exchange(uri, HttpMethod.PUT, request, String.class);
        } catch (HttpClientErrorException e) {
            log.error("HttpClientErrorException http request createApplicant while confirmExternalPayment {}",
                    e.getResponseBodyAsString(),
                    e);
            throw new NgDashboardException(ErrorApiTitles.QUBERA_EXTERNAL_PAYMENT_ERROR);
        } catch (Exception e) {
            log.error("Error confirmExternalPayment", e);
            throw new NgDashboardException(ErrorApiTitles.QUBERA_EXTERNAL_PAYMENT_ERROR);
        }

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            log.error("Error confirm external payment to master {}", responseEntity.getBody());
            throw new NgDashboardException("Error confirm payment to master",
                    Constants.ErrorApi.QUBERA_CONFIRM_PAYMENT_TO_MASTER_ERROR);
        } else {
            ObjectMapper mapper = new ObjectMapper();
            ResponseConfirmDto responseConfirmDto = null;
            log.info("Finish confirmExternalPayment http request, response \n {}",
                    JsonUtils.toJson(responseEntity.getBody()));
            try {
                responseConfirmDto = mapper.readValue(responseEntity.getBody(), new TypeReference<ResponseConfirmDto>() {
                });
            } catch (Exception e) {
                log.info("Exception read value  {} to map {}", responseEntity.getBody(), e);
            }
            return responseConfirmDto != null && responseConfirmDto.getResult().equalsIgnoreCase("ok");
        }
    }

    public ResponseVerificationStatusDto getCurrentStatusKyc(String fileUuid) {
        String finalUrl = String.format("%s/verification/cis/file/%s", uriApi, fileUuid);
        log.info("Start http request getCurrentStatusKyc(), url \n {}", finalUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("apiKey", apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<ResponseVerificationStatusDto> responseEntity;
        try {
            responseEntity =
                    template.exchange(uri, HttpMethod.GET, request, ResponseVerificationStatusDto.class);
        } catch (HttpClientErrorException e) {
            log.error("HttpClientErrorException http request createApplicant whilegetCurrentStatusKyc {}",
                    e.getResponseBodyAsString(),
                    e);
            throw new NgDashboardException(ErrorApiTitles.QUBERA_CHECK_VERIFICATION_ERROR);
        } catch (Exception e) {
            log.error("Error check verification {}", fileUuid);
            throw new NgDashboardException(ErrorApiTitles.QUBERA_CHECK_VERIFICATION_ERROR);
        }

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            log.error("Response not 200 while check verification status", responseEntity.getBody());
            throw new NgDashboardException(
                    ErrorApiTitles.QUBERA_CHECK_VERIFICATION_ERROR);
        } else {
            log.info("Finish getCurrentStatusKyc http request, response \n {}",
                    JsonUtils.toJson(responseEntity.getBody()));
            return responseEntity.getBody();
        }
    }

    public KycResponseStatusDto getCurrentKycStatus(String referenceUid) {
        String finalUrl = String.format("%s/verification/onboarding/%s/status", uriApi, referenceUid);
        log.info("Start http request getCurrentKycStatus, url  \n{}", finalUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("apiKey", apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<KycResponseStatusDto> responseEntity;

        try {
            responseEntity =
                    template.exchange(uri, HttpMethod.GET, request, KycResponseStatusDto.class);
        } catch (HttpClientErrorException e) {
            log.error("HttpClientErrorException http request createApplicant while getCurrentKycStatus {}",
                    e.getResponseBodyAsString(),
                    e);
            throw new NgDashboardException(ErrorApiTitles.QUBERA_KYC_ERROR_GET_STATUS);
        } catch (Exception e) {
            log.error("Error getting status", e);
            return new KycResponseStatusDto("none", referenceUid);
        }

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            log.error("Error response get kyc status {}", JsonUtils.toJson(responseEntity.getBody()));
            throw new NgDashboardException("Error response kyc status",
                    Constants.ErrorApi.QUBERA_KYC_RESPONSE_ERROR_GET_STATUS);
        }
        log.info("Finish getCurrentKycStatus http request, response \n {}",
                JsonUtils.toJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }
}
