package me.exrates.service.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.merchants.adgroup.AdGroupCommonRequestDto;
import me.exrates.model.dto.merchants.adgroup.responses.AdGroupResponseDto;
import me.exrates.model.dto.merchants.adgroup.responses.InvoiceDto;
import me.exrates.model.dto.merchants.adgroup.responses.ResponseListTxDto;
import me.exrates.model.dto.merchants.adgroup.responses.ResponsePayOutDto;
import me.exrates.model.ngExceptions.NgDashboardException;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Log4j2(topic = "adgroup_log")
@Service
public class AdGroupHttpClient {

    private RestTemplate template;

    public AdGroupHttpClient() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(30000);
        this.template = new RestTemplate(requestFactory);
    }

    public AdGroupResponseDto<InvoiceDto> createInvoice(String url, String authorizationKey, AdGroupCommonRequestDto requestDto) {
        log.info("createInvoice(), {}", toJson(requestDto));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("Authorization", "Basic " + authorizationKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build(true).toUri();

        HttpEntity<?> request = new HttpEntity<>(requestDto, headers);
        ResponseEntity<AdGroupResponseDto<InvoiceDto>> responseEntity;
        try {
            responseEntity = template.exchange(uri, HttpMethod.POST, request,
                    new ParameterizedTypeReference<AdGroupResponseDto<InvoiceDto>>() {
                    });
        } catch (Exception e) {
            log.error("Error http request while create invoice", e);
            throw new NgDashboardException(ErrorApiTitles.AD_GROUP_ERROR_HTTP_CLIENT);
        }

        HttpStatus httpStatus = responseEntity.getStatusCode();

        if (!httpStatus.is2xxSuccessful()) {
            String errorString = "Error while creating invoice ";
            log.error(errorString + " {}", responseEntity);
            throw new NgDashboardException(ErrorApiTitles.AD_GROUP_HTTP_CLIENT_RESPONSE_NOT_200);
        }
        log.info("Response: {}", toJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }

    public AdGroupResponseDto<ResponseListTxDto> getTransactions(String url, String authorizationKey, AdGroupCommonRequestDto requestDto) {
        log.info("getApprovedTransactions(), {}", toJson(requestDto));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("Authorization", "Basic " + authorizationKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build(true).toUri();

        HttpEntity<?> request = new HttpEntity<>(requestDto, headers);
        ResponseEntity<AdGroupResponseDto<ResponseListTxDto>> responseEntity;
        try {
            responseEntity =
                    template.exchange(uri, HttpMethod.POST, request,
                            new ParameterizedTypeReference<AdGroupResponseDto<ResponseListTxDto>>() {
                            });
        } catch (Exception e) {
            log.error("Error http request while fetch list transactions", e);
            throw new NgDashboardException(ErrorApiTitles.AD_GROUP_ERROR_HTTP_CLIENT);
        }

        HttpStatus httpStatus = responseEntity.getStatusCode();

        if (!httpStatus.is2xxSuccessful()) {
            String errorString = "Error while creating invoice ";
            log.error(errorString + " {}", responseEntity);
            throw new NgDashboardException(ErrorApiTitles.AD_GROUP_HTTP_CLIENT_RESPONSE_NOT_200);
        }
        log.info("Response : {}", toJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }


    public AdGroupResponseDto<ResponsePayOutDto> createPayOut(String url, String authorizationKey, AdGroupCommonRequestDto requestDto) {
        log.info("createPayOut(), {}", toJson(requestDto));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("Authorization", "Basic " + authorizationKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build(true).toUri();

        HttpEntity<?> request = new HttpEntity<>(requestDto, headers);
        ResponseEntity<AdGroupResponseDto<ResponsePayOutDto>> responseEntity;
        try {
            responseEntity =
                    template.exchange(uri, HttpMethod.POST, request,
                            new ParameterizedTypeReference<AdGroupResponseDto<ResponsePayOutDto>>() {
                            });
        } catch (Exception e) {
            log.error("Error http request while createPayOut", e);
            throw new NgDashboardException(ErrorApiTitles.AD_GROUP_ERROR_HTTP_CLIENT);
        }

        HttpStatus httpStatus = responseEntity.getStatusCode();

        if (!httpStatus.is2xxSuccessful()) {
            String errorString = "Error while createPayOut ";
            log.error(errorString + " {}", toJson(responseEntity));
            throw new NgDashboardException(ErrorApiTitles.AD_GROUP_HTTP_CLIENT_RESPONSE_NOT_200);
        }
        log.info("Response from createPayOut(): {}", toJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }

    @SuppressWarnings("Duplicated")
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
