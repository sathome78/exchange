package me.exrates.service.kyc.http;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.kyc.CreateApplicantDto;
import me.exrates.model.dto.kyc.ResponseCreateAplicantDto;
import me.exrates.model.dto.kyc.request.RequestOnBoardingDto;
import me.exrates.model.dto.kyc.responces.OnboardingResponseDto;
import me.exrates.model.exceptions.KycException;
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

    public ResponseCreateAplicantDto createApplicant(CreateApplicantDto createApplicantDto) {

        String finalUrl = uriApi + "/verification/cis/file";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("apiKey", apiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        builder.queryParam("synchronous", "true");
        URI uri = builder.build(true).toUri();

        HttpEntity<?> request = new HttpEntity<>(createApplicantDto, headers);

        ResponseEntity<ResponseCreateAplicantDto> responseEntity =
                template.exchange(uri, HttpMethod.POST, request, ResponseCreateAplicantDto.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new KycException("Error while creating applicant");
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
}
