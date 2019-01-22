package me.exrates.service.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import me.exrates.model.User;
import me.exrates.model.dto.kyc.EventStatus;
import me.exrates.service.KYCService;
import me.exrates.service.UserService;
import me.exrates.service.exception.ShuftiProException;
import me.exrates.service.util.ShuftiProUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@PropertySource(value = {"classpath:/kyc.properties"})
@Slf4j
@Component
public class ShuftiProKYCService implements KYCService {

    public static final String SIGNATURE = "sp_signature";

    private static final String EVENT = "event";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String REFERENCE = "reference";
    private static final String VERIFICATION_URL = "verification_url";

    private final String secretKey;
    private final String verificationUrl;
    private final String statusUrl;
    private final String callbackUrl;
    private final String redirectUrl;
    private final int digitsNumber;
    private final String verificationMode;
    private final String smsText;

    private final List<String> documentSupportedTypes;
    private final List<String> addressSupportedTypes;

    private final RestTemplate restTemplate;

    private final UserService userService;

    @Autowired
    public ShuftiProKYCService(@Value("${shufti-pro.verification-url}") String verificationUrl,
                               @Value("${shufti-pro.status-url}") String statusUrl,
                               @Value("${shufti-pro.callback-url}") String callbackUrl,
                               @Value("${shufti-pro.redirect-url}") String redirectUrl,
                               @Value("${shufti-pro.reference-digits-number}") int digitsNumber,
                               @Value("${shufti-pro.verification-mode}") String verificationMode,
                               @Value("${shufti-pro.phone.sms-text}") String smsText,
                               @Value("#{'${shufti-pro.document.supported-types}'.split(',')}") List<String> documentSupportedTypes,
                               @Value("#{'${shufti-pro.address.supported-types}'.split(',')}") List<String> addressSupportedTypes,
                               @Value("${shufti-pro.username}") String username,
                               @Value("${shufti-pro.password}") String password,
                               UserService userService) {
        this.verificationUrl = verificationUrl;
        this.statusUrl = statusUrl;
        this.callbackUrl = callbackUrl;
        this.redirectUrl = redirectUrl;
        this.digitsNumber = digitsNumber;
        this.verificationMode = verificationMode;
        this.smsText = smsText;
        this.documentSupportedTypes = documentSupportedTypes;
        this.addressSupportedTypes = addressSupportedTypes;
        this.secretKey = password;
        this.userService = userService;
        this.restTemplate = new RestTemplate();
        this.restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
    }

    @Override
    public String getVerificationUrl(int userId, String language, String country) {
        User user = userService.getUserById(userId);

        VerificationRequest verificationRequest = buildVerificationRequest(user, language, country);

        HttpEntity<VerificationRequest> requestEntity = new HttpEntity<>(verificationRequest);

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(verificationUrl, requestEntity, String.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new ShuftiProException("ShuftiPro KYC verification service is not available");
            }
        } catch (Exception ex) {
            throw new ShuftiProException("ShuftiPro KYC verification service is not available");
        }

        final String signature = responseEntity.getHeaders().get(SIGNATURE).get(0);
        final String response = responseEntity.getBody();
        validateMerchantSignature(signature, response);

        JSONObject verificationObject = new JSONObject(response);
        final EventStatus eventStatus = EventStatus.of(verificationObject.getString(EVENT));
        if (!Objects.equals(eventStatus, EventStatus.PENDING)) {
            JSONObject errorObject = verificationObject.getJSONObject(ERROR);
            String errorMessage = nonNull(errorObject) ? errorObject.getString(MESSAGE) : StringUtils.EMPTY;
            throw new ShuftiProException(String.format("ShuftiPro KYC verification service: status: %s, error message: %s", eventStatus, errorMessage));
        }

        int affectedRowCount = userService.updateReferenceIdByUserId(userId, verificationObject.getString(REFERENCE));
        if (affectedRowCount == 0) {
            log.debug("User KYC reference have not updated in database");
        }
        return verificationObject.getString(VERIFICATION_URL).replace("\\", "");
    }

    private VerificationRequest buildVerificationRequest(User user, String language, String country) {
        return VerificationRequest.builder()
                .reference(RandomStringUtils.randomAlphanumeric(digitsNumber))
                .callbackUrl(callbackUrl)
                .redirectUrl(redirectUrl)
                .email(user.getEmail())
                .country(country)
                .language(language)
                .verificationMode(verificationMode)
                .face(Face.builder()
                        .proof(StringUtils.EMPTY)
                        .build())
                .document(Document.builder()
                        .proof(StringUtils.EMPTY)
                        .supportedTypes(documentSupportedTypes)
                        .name(StringUtils.EMPTY)
                        .dob(StringUtils.EMPTY)
                        .issueDate(StringUtils.EMPTY)
                        .expiryDate(StringUtils.EMPTY)
                        .documentNumber(StringUtils.EMPTY)
                        .build())
                .address(Address.builder()
                        .proof(StringUtils.EMPTY)
                        .supportedTypes(addressSupportedTypes)
                        .name(StringUtils.EMPTY)
                        .fullAddress(StringUtils.EMPTY)
                        .build())
                .phone(Phone.builder()
                        .text(smsText)
                        .build())
                .build();
    }

    @Override
    public Pair<String, EventStatus> getVerificationStatus(int userId) {
        final String reference = userService.getReferenceIdByUserId(userId);

        StatusRequest statusRequest = buildStatusRequest(reference);

        HttpEntity<StatusRequest> requestEntity = new HttpEntity<>(statusRequest);

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(statusUrl, requestEntity, String.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new ShuftiProException("ShuftiPro KYC status service is not available");
            }
        } catch (Exception ex) {
            throw new ShuftiProException("ShuftiPro KYC status service is not available");
        }

        final String signature = responseEntity.getHeaders().get(SIGNATURE).get(0);
        final String response = responseEntity.getBody();
        validateMerchantSignature(signature, response);

        JSONObject statusObject = new JSONObject(response);
        final EventStatus eventStatus = EventStatus.of(statusObject.getString(EVENT));

        int affectedRowCount = userService.updateVerificationStatusByUserId(userId, eventStatus);
        if (affectedRowCount == 0) {
            log.debug("Verification status have not updated in database");
        }
        return Pair.of(reference, eventStatus);
    }

    private StatusRequest buildStatusRequest(String reference) {
        return StatusRequest.builder()
                .reference(reference)
                .build();
    }

    @Override
    public Pair<String, EventStatus> checkResponseAndUpdateStatus(String signature, String response) {
        validateMerchantSignature(signature, response);

        JSONObject statusObject = new JSONObject(response);
        final String reference = statusObject.getString(REFERENCE);
        final EventStatus eventStatus = EventStatus.of(statusObject.getString(EVENT));

        int affectedRowCount = userService.updateVerificationStatusByReferenceId(reference, eventStatus);
        if (affectedRowCount == 0) {
            log.debug("Verification status have not updated in database");
        }
        return Pair.of(reference, eventStatus);
    }

    private void validateMerchantSignature(String signature, String response) {
        final boolean isMerchantSignatureSame = ShuftiProUtils.checkMerchantSignature(signature, response, secretKey);
        if (!isMerchantSignatureSame) {
            throw new ShuftiProException("Merchant signature is not the same with generated");
        }
    }

    @Builder(builderClassName = "Builder")
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class VerificationRequest {

        String reference;
        @JsonProperty("callback_url")
        String callbackUrl;
        @JsonProperty("redirect_url")
        String redirectUrl;
        String email;
        String country;
        String language;
        @JsonProperty("verification_mode")
        String verificationMode;
        @Valid
        Face face;
        @Valid
        Document document;
        @Valid
        Address address;
        @Valid
        Phone phone;
    }

    @Builder(builderClassName = "Builder")
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Face {

        String proof;
    }

    @Builder(builderClassName = "Builder")
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Document {

        String proof;
        @JsonProperty("supported_types")
        @Valid
        List<String> supportedTypes;
        String name;
        String dob;
        @JsonProperty("issue_date")
        String issueDate;
        @JsonProperty("expiry_date")
        String expiryDate;
        @JsonProperty("document_number")
        String documentNumber;
    }

    @Builder(builderClassName = "Builder")
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Address {

        String proof;
        @JsonProperty("supported_types")
        @Valid
        List<String> supportedTypes;
        String name;
        @JsonProperty("full_address")
        String fullAddress;
    }

    @Builder(builderClassName = "Builder")
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Phone {

        @JsonProperty("phone_number")
        String phoneNumber;
        String text;
    }

    @Builder(builderClassName = "Builder")
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class StatusRequest {

        public String reference;
    }
}