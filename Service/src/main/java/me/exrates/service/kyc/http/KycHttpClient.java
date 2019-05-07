package me.exrates.service.kyc.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import httpClient.CommonHttpClientImpl;
import httpClient.HttpResponseWithEntity;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.constants.Constants;
import me.exrates.model.dto.AccountQuberaRequestDto;
import me.exrates.model.dto.AccountQuberaResponseDto;
import me.exrates.model.dto.kyc.CreateApplicantDto;
import me.exrates.model.dto.kyc.ResponseCreateApplicantDto;
import me.exrates.model.dto.kyc.request.RequestOnBoardingDto;
import me.exrates.model.dto.kyc.responces.KycResponseStatusDto;
import me.exrates.model.dto.kyc.responces.OnboardingResponseDto;
import me.exrates.model.dto.qubera.AccountInfoDto;
import me.exrates.model.dto.qubera.ExternalPaymentDto;
import me.exrates.model.dto.qubera.QuberaPaymentToMasterDto;
import me.exrates.model.dto.qubera.ResponsePaymentDto;
import me.exrates.model.ngExceptions.NgDashboardException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.io.IOException;
import java.net.URI;

@Log4j2
@Component
@PropertySource("classpath:/merchants/qubera.properties")
public class KycHttpClient {

    private @Value("${qubera.kyc.url}")
    String uriApi;
    private @Value("${qubera.kyc.apiKey}")
    String apiKey;

    private CommonHttpClientImpl client;
    private RequestConfig requestConfig;

    @Autowired
    private ObjectMapper objectMapper;

    public KycHttpClient() {
        requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000).build();
        this.client = new CommonHttpClientImpl();
    }


    @SneakyThrows
    public ResponseCreateApplicantDto createApplicant(CreateApplicantDto createApplicantDto) {
        log.info("createApplicant(), {}", toJson(createApplicantDto));
        String finalUrl = uriApi + "/verification/cis/file";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        builder.queryParam("synchronous", "true");
        URI uri = builder.build(true).toUri();
        HttpResponseWithEntity response = postRequest(uri, createApplicantDto);
        if (!HttpStatus.valueOf(response.getStatus()).is2xxSuccessful()) {
            String errorString = "Error while creating applicant ";
            log.error(errorString + " {}", response.getResponseEntity());
            throw new NgDashboardException("Error while response from service, create applicant",
                    Constants.ErrorApi.QUBERA_RESPONSE_CREATE_APPLICANT_ERROR);
        }
        return objectMapper.readValue(response.getResponseEntity(), ResponseCreateApplicantDto.class);
    }

    @SneakyThrows
    public OnboardingResponseDto createOnBoarding(RequestOnBoardingDto requestDto) {
        log.info("createOnBoarding (), {}", toJson(requestDto));
        String finalUrl = uriApi + "/verification/onboarding/sendlink";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();
        HttpResponseWithEntity responseWithEntity;
        try {
            responseWithEntity = postRequest(uri, requestDto);
        } catch (Exception e) {
            log.error("Error response {}", ExceptionUtils.getStackTrace(e));
            throw new NgDashboardException("Error while creating onboarding",
                    Constants.ErrorApi.QUBERA_RESPONSE_CREATE_ONBOARDING_ERROR);
        }
        HttpStatus httpStatus = HttpStatus.valueOf(responseWithEntity.getStatus());
        if (!httpStatus.is2xxSuccessful()) {
            log.error("Error while creating onboarding {}", responseWithEntity.getResponseEntity());
            throw new NgDashboardException("Error while creating onboarding",
                    Constants.ErrorApi.QUBERA_RESPONSE_CREATE_ONBOARDING_ERROR);
        }
        return objectMapper.readValue(responseWithEntity.getResponseEntity(), OnboardingResponseDto.class);
    }

    @SneakyThrows
    public AccountQuberaResponseDto createAccount(AccountQuberaRequestDto accountQuberaRequestDto) {
        log.info("createAccount (), {}", toJson(accountQuberaRequestDto));
        String finalUrl = uriApi + "/account/create";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();
        HttpResponseWithEntity responseWithEntity = postRequest(uri, accountQuberaRequestDto);
        if (!HttpStatus.valueOf(responseWithEntity.getStatus()).is2xxSuccessful()) {
            log.error("Error create account {}", responseWithEntity.getResponseEntity());
            throw new NgDashboardException("Error while creating account",
                    Constants.ErrorApi.QUBERA_CREATE_ACCOUNT_RESPONSE_ERROR);
        }
        return objectMapper.readValue(responseWithEntity.getResponseEntity(), AccountQuberaResponseDto.class);
    }

    @SneakyThrows
    public AccountInfoDto getBalanceAccount(String account) {
        String finalUrl = uriApi + "/v2/account/" + account + "/balance";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();
        HttpResponseWithEntity responseWithEntity = getRequest(uri);
        HttpStatus httpStatus = HttpStatus.valueOf(responseWithEntity.getStatus());
        if (!httpStatus.is2xxSuccessful()) {
            log.error("Error create account {}", responseWithEntity.getResponseEntity());
            throw new NgDashboardException("Error response account balance",
                    Constants.ErrorApi.QUBERA_ACCOUNT_RESPONSE_ERROR);
        }
        return objectMapper.readValue(responseWithEntity.getResponseEntity(), AccountInfoDto.class);
    }

    @SneakyThrows
    public ResponsePaymentDto createPaymentInternal(QuberaPaymentToMasterDto paymentToMasterDto, boolean toMaster) {
        String finalUrl = toMaster ? uriApi + "/payment/master" : uriApi + "/payment/internal";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();
        HttpResponseWithEntity responseWithEntity = postRequest(uri, paymentToMasterDto);
        HttpStatus httpStatus = HttpStatus.valueOf(responseWithEntity.getStatus());
        if (!httpStatus.is2xxSuccessful()) {
            log.error("Error create account {}", responseWithEntity.getResponseEntity());
            throw new NgDashboardException("Error while creating payment to master",
                    Constants.ErrorApi.QUBERA_PAYMENT_TO_MASTER_ERROR);
        } else {
            return objectMapper.readValue(responseWithEntity.getResponseEntity(), ResponsePaymentDto.class);
        }
    }

    @SneakyThrows
    public String confirmInternalPayment(Integer paymentId, boolean toMaster) {
        String finalUrl;
        if (toMaster) {
            finalUrl = String.format("%s/payment/master/%d/confirm", uriApi, paymentId);
        } else {
            finalUrl = String.format("%s/payment/internal/%d/confirm", uriApi, paymentId);
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();
        HttpResponseWithEntity responseWithEntity = putRequest(uri);
        HttpStatus httpStatus = HttpStatus.valueOf(responseWithEntity.getStatus());
        if (!httpStatus.is2xxSuccessful()) {
            log.error("Error confirm payment to master {}", responseWithEntity.getResponseEntity());
            throw new NgDashboardException("Error confirm payment to master",
                    Constants.ErrorApi.QUBERA_CONFIRM_PAYMENT_TO_MASTER_ERROR);
        } else {
            return responseWithEntity.getResponseEntity();
        }
    }

    @SneakyThrows
    public ResponsePaymentDto createExternalPayment(ExternalPaymentDto externalPaymentDto) {
        log.info("createExternalPayment(), {}", toJson(externalPaymentDto));
        String finalUrl = uriApi + "payment/external";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();
        HttpResponseWithEntity responseWithEntity = postRequest(uri, externalPaymentDto);
        HttpStatus httpStatus = HttpStatus.valueOf(responseWithEntity.getStatus());
        if (!httpStatus.is2xxSuccessful()) {
            log.error("Error create external payment {}", responseWithEntity.getResponseEntity());
            throw new NgDashboardException("Error while creating external payment",
                    Constants.ErrorApi.QUBERA_ERROR_RESPONSE_CREATE_EXTERNAL_PAYMENT);
        } else {
            return objectMapper.readValue(responseWithEntity.getResponseEntity(), ResponsePaymentDto.class);
        }
    }

    @SneakyThrows
    public String confirmExternalPayment(Integer paymentId) {
        String finalUrl = String.format("%s/payment/external/%d/confirm", uriApi, paymentId);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();
        HttpResponseWithEntity responseWithEntity = putRequest(uri);
        HttpStatus httpStatus = HttpStatus.valueOf(responseWithEntity.getStatus());
        if (!httpStatus.is2xxSuccessful()) {
            log.error("Error confirm external payment to master {}", toJson(responseWithEntity.getResponseEntity()));
            throw new NgDashboardException("Error confirm payment to master",
                    Constants.ErrorApi.QUBERA_CONFIRM_PAYMENT_TO_MASTER_ERROR);
        } else {
            return responseWithEntity.getResponseEntity();
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

    @SneakyThrows
    public KycResponseStatusDto getCurrentKycStatus(String referenceUid) {
        String finalUrl = String.format("%s/verification/onboarding/%s/status", uriApi, referenceUid);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();
        HttpResponseWithEntity responseWithEntity;
        try {
            responseWithEntity = getRequest(uri);
        } catch (Exception e) {
            log.error("Error getting status {}", referenceUid);
            return new KycResponseStatusDto("none", referenceUid);
        }
        HttpStatus httpStatus = HttpStatus.valueOf(responseWithEntity.getStatus());
        if (!httpStatus.is2xxSuccessful()) {
            log.error("Error response get kyc status {}", toJson(responseWithEntity.getResponseEntity()));
            throw new NgDashboardException("Error response kyc status",
                    Constants.ErrorApi.QUBERA_KYC_RESPONSE_ERROR_GET_STATUS);
        }
        return objectMapper.readValue(responseWithEntity.getResponseEntity(), KycResponseStatusDto.class);
    }

    private HttpResponseWithEntity postRequest(URI uri, Object payload) throws IOException {
        HttpPost httpPost = new HttpPost(uri.toString());
        httpPost.addHeader("apiKey", apiKey);
        httpPost.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8.getType());
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(payload));
        httpPost.setEntity(entity);
        httpPost.setConfig(requestConfig);
        return client.execute(httpPost);
    }

    private HttpResponseWithEntity getRequest(URI uri) throws IOException {
        HttpGet httpGet = new HttpGet(uri.toString());
        httpGet.addHeader("apiKey", apiKey);
        httpGet.setConfig(requestConfig);
        return client.execute(httpGet);
    }

    private HttpResponseWithEntity putRequest(URI uri) throws IOException {
        HttpPut httpPut = new HttpPut(uri.toString());
        httpPut.addHeader("apiKey", apiKey);
        httpPut.setConfig(requestConfig);
        return client.execute(httpPut);
    }
}
