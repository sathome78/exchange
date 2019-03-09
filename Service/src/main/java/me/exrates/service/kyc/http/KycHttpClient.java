package me.exrates.service.kyc.http;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.constants.Constants;
import me.exrates.model.dto.AccountInfoDto;
import me.exrates.model.dto.AccountQuberaRequestDto;
import me.exrates.model.dto.AccountQuberaResponseDto;
import me.exrates.model.dto.kyc.CreateApplicantDto;
import me.exrates.model.dto.kyc.ResponseCreateApplicantDto;
import me.exrates.model.dto.kyc.request.RequestOnBoardingDto;
import me.exrates.model.dto.kyc.responces.OnboardingResponseDto;
import me.exrates.model.exceptions.KycException;
import me.exrates.model.ngExceptions.NgDashboardException;
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

    private @Value("${qubera.kyc.url}") String uriApi;
    private @Value("${qubera.kyc.apiKey}") String apiKey;

    private RestTemplate template;

    public KycHttpClient() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(30000);
        this.template = new RestTemplate(requestFactory);
    }

    public ResponseCreateApplicantDto createApplicant(CreateApplicantDto createApplicantDto) {

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

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            String errorString = "Error while creating applicant ";
            throw new KycException(errorString.concat(responseEntity.getBody() != null
                    && responseEntity.getBody().getError() != null ?
                    responseEntity.getBody().getError() : null));
        }
        return responseEntity.getBody();
    }

    public OnboardingResponseDto createOnBoarding(RequestOnBoardingDto requestDto) {
        String finalUrl = uriApi + "/verification/onboarding/sendlink";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("apiKey", apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();

        HttpEntity<?> request = new HttpEntity<>(requestDto, headers);

        ResponseEntity<OnboardingResponseDto> responseEntity =
                template.exchange(uri, HttpMethod.POST, request, OnboardingResponseDto.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new KycException("Error while creating onboarding");
        }

        return responseEntity.getBody();
    }

    public AccountQuberaResponseDto createAccount(AccountQuberaRequestDto accountQuberaRequestDto) {
        String finalUrl = uriApi + "/account/create";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("apiKey", apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        URI uri = builder.build(true).toUri();

        HttpEntity<?> request = new HttpEntity<>(accountQuberaRequestDto, headers);

        ResponseEntity<AccountQuberaResponseDto> responseEntity =
                template.exchange(uri, HttpMethod.POST, request, AccountQuberaResponseDto.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            log.error("Error create account {}", responseEntity.getBody());
            throw new NgDashboardException("Error while creating account",
                    Constants.ErrorApi.QUBERA_CREATE_ACCOUNT_RESPONSE_ERROR);
        }

        return responseEntity.getBody();
    }

    public AccountInfoDto getBalanceAccount(String account) {
        String finalUrl = uriApi + "/account/" + account + "/balance";
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
}
