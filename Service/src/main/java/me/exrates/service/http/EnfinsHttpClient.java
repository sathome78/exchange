package me.exrates.service.http;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.merchants.enfins.EnfinsResponseDto;
import me.exrates.model.dto.merchants.enfins.EnfinsResponsePaymentDto;
import me.exrates.model.dto.merchants.enfins.EnfinsResponsePaymentRefillDto;
import me.exrates.model.ngExceptions.NgDashboardException;
import me.exrates.service.util.JsonUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Log4j2(topic = "enfins_log")
@Component
public class EnfinsHttpClient {
    private RestTemplate template;

    public EnfinsHttpClient() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(30000);
        this.template = new RestTemplate(requestFactory);
    }

    public EnfinsResponseDto<EnfinsResponsePaymentRefillDto> createRefillRequest(String url) {

        log.info("createInvoice(), url {}", url);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<EnfinsResponseDto<EnfinsResponsePaymentRefillDto>> responseEntity;
        try {
            responseEntity = template.exchange(url, HttpMethod.POST, request,
                    new ParameterizedTypeReference<EnfinsResponseDto<EnfinsResponsePaymentRefillDto>>() {
                    });
        } catch (HttpClientErrorException e) {
            log.error("HttpClientErrorException http request while create invoice {}", e.getResponseBodyAsString(), e);
            throw new NgDashboardException(ErrorApiTitles.ENFINS_ERROR_HTTP_CLIENT);
        } catch (Exception e) {
            log.error("Exception http request while create invoice", e);
            throw new NgDashboardException(ErrorApiTitles.ENFINS_ERROR_HTTP_CLIENT);
        }

        HttpStatus httpStatus = responseEntity.getStatusCode();
        if (!httpStatus.is2xxSuccessful()) {
            String errorString = "Error while creating refill request ";
            log.error(errorString + " {}", responseEntity);
            throw new NgDashboardException(ErrorApiTitles.ENFINS_HTTP_CLIENT_RESPONSE_NOT_200);
        }
        log.info("Response: {}", JsonUtils.toJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }

    public EnfinsResponseDto<EnfinsResponsePaymentDto> createPayOut(String url) {

        log.info("createPayOut(), {}", url);
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<EnfinsResponseDto<EnfinsResponsePaymentDto>> responseEntity;
        try {
            responseEntity = template.exchange(url, HttpMethod.POST, request,
                    new ParameterizedTypeReference<EnfinsResponseDto<EnfinsResponsePaymentDto>>() {
                    });
        } catch (HttpClientErrorException e) {
            log.error("Error http request while create invoice {}", e.getResponseBodyAsString(), e);
            throw new NgDashboardException(ErrorApiTitles.ENFINS_ERROR_HTTP_CLIENT);
        } catch (Exception e) {
            log.error("Exception http request while create invoice", e);
            throw new NgDashboardException(ErrorApiTitles.ENFINS_ERROR_HTTP_CLIENT);
        }

        HttpStatus httpStatus = responseEntity.getStatusCode();

        if (!httpStatus.is2xxSuccessful()) {
            String errorString = "Error while creating refill request ";
            log.error(errorString + " {}", responseEntity);
            throw new NgDashboardException(ErrorApiTitles.ENFINS_HTTP_CLIENT_RESPONSE_NOT_200);
        }
        log.info("Response: {}", JsonUtils.toJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }


}
