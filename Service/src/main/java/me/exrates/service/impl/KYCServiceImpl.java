package me.exrates.service.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import me.exrates.dao.KycDao;
import me.exrates.dao.UserVerificationInfoDao;
import me.exrates.model.Email;
import me.exrates.model.User;
import me.exrates.model.UserVerificationInfo;
import me.exrates.model.dto.kyc.CreateApplicantDto;
import me.exrates.model.dto.kyc.DocTypeEnum;
import me.exrates.model.dto.kyc.EventStatus;
import me.exrates.model.dto.kyc.IdentityDataKyc;
import me.exrates.model.dto.kyc.IdentityDataRequest;
import me.exrates.model.dto.kyc.PersonKycDto;
import me.exrates.model.dto.kyc.ResponseCreateApplicantDto;
import me.exrates.model.dto.kyc.request.RequestOnBoardingDto;
import me.exrates.model.dto.kyc.responces.KycStatusResponseDto;
import me.exrates.model.dto.kyc.responces.OnboardingResponseDto;
import me.exrates.model.exceptions.KycException;
import me.exrates.service.KYCService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.exception.ShuftiProException;
import me.exrates.service.kyc.http.KycHttpClient;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@PropertySource(value = {"classpath:/kyc.properties", "classpath:/angular.properties"})
@Slf4j
@Component
public class KYCServiceImpl implements KYCService {

    public static final String SIGNATURE = "Signature";

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
    private final String emailSubject;
    private final String emailMessagePattern;

    private final List<String> documentSupportedTypes;
    private final List<String> addressSupportedTypes;

    private final RestTemplate restTemplate;

    private final UserService userService;
    private final SendMailService sendMailService;
    private final KycHttpClient kycHttpClient;
    private final KycDao kycDao;
    private final UserVerificationInfoDao userVerificationInfoDao;

    @Value("${front-host}")
    private String host;

    @Autowired
    public KYCServiceImpl(@Value("${shufti-pro.verification-url}") String verificationUrl,
                          @Value("${shufti-pro.status-url}") String statusUrl,
                          @Value("${shufti-pro.callback-url}") String callbackUrl,
                          @Value("${shufti-pro.redirect-url}") String redirectUrl,
                          @Value("${shufti-pro.reference-digits-number}") int digitsNumber,
                          @Value("${shufti-pro.verification-mode}") String verificationMode,
                          @Value("${shufti-pro.phone.sms-text}") String smsText,
                          @Value("${shufti-pro.email.subject}") String emailSubject,
                          @Value("${shufti-pro.email.message-pattern}") String emailMessagePattern,
                          @Value("#{'${shufti-pro.document.supported-types}'.split(',')}") List<String> documentSupportedTypes,
                          @Value("#{'${shufti-pro.address.supported-types}'.split(',')}") List<String> addressSupportedTypes,
                          @Value("${shufti-pro.username}") String username,
                          @Value("${shufti-pro.password}") String password,
                          UserService userService,
                          KycDao kycDao,
                          SendMailService sendMailService,
                          KycHttpClient kycHttpClient,
                          UserVerificationInfoDao userVerificationInfoDao) {
        this.verificationUrl = verificationUrl;
        this.statusUrl = statusUrl;
        this.callbackUrl = callbackUrl;
        this.redirectUrl = redirectUrl;
        this.digitsNumber = digitsNumber;
        this.verificationMode = verificationMode;
        this.smsText = smsText;
        this.emailSubject = emailSubject;
        this.emailMessagePattern = emailMessagePattern;
        this.documentSupportedTypes = documentSupportedTypes;
        this.addressSupportedTypes = addressSupportedTypes;
        this.secretKey = password;
        this.userService = userService;
        this.sendMailService = sendMailService;
        this.kycHttpClient = kycHttpClient;
        this.kycDao = kycDao;
        this.userVerificationInfoDao = userVerificationInfoDao;
        this.restTemplate = new RestTemplate();
        this.restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
    }

    @Override
    public String getVerificationUrl(int stepNumber, String languageCode, String countryCode) {
        final String userEmail = userService.getUserEmailFromSecurityContext();

        VerificationRequest verificationRequest = buildVerificationRequest(userEmail, languageCode, countryCode, stepNumber);

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
        if (!EventStatus.PENDING.equals(eventStatus)) {
            JSONObject errorObject = verificationObject.getJSONObject(ERROR);
            String errorMessage = nonNull(errorObject) ? errorObject.getString(MESSAGE) : StringUtils.EMPTY;
            throw new ShuftiProException(String.format("ShuftiPro KYC verification service: status: %s, error message: %s", eventStatus, errorMessage));
        }
        int affectedRowCount = userService.updateReferenceId(verificationObject.getString(REFERENCE));
        if (affectedRowCount == 0) {
            log.debug("Reference id have not been updated in database");
        }
        return verificationObject.getString(VERIFICATION_URL).replace("\\", "");
    }

    private VerificationRequest buildVerificationRequest(String userEmail, String languageCode, String countryCode, int stepNumber) {
        VerificationRequest.Builder builder = VerificationRequest.builder()
                .reference(RandomStringUtils.randomAlphanumeric(digitsNumber))
                .callbackUrl(callbackUrl)
                .email(userEmail)
                .country(countryCode)
                .verificationMode(verificationMode);

        if (StringUtils.isNotEmpty(redirectUrl)) {
            builder.redirectUrl(redirectUrl);
        }

        if (nonNull(languageCode)) {
            builder.language(languageCode);
        }

        if (stepNumber == 1) {
            builder
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
                    .phone(Phone.builder()
                            .text(smsText)
                            .build());
        } else if (stepNumber == 2) {
            builder
                    .address(Address.builder()
                            .proof(StringUtils.EMPTY)
                            .supportedTypes(addressSupportedTypes)
                            .name(StringUtils.EMPTY)
                            .fullAddress(StringUtils.EMPTY)
                            .build());
        } else {
            throw new ShuftiProException(String.format("Unknown step number: %s", stepNumber));
        }
        return builder.build();
    }

    @Override
    public Pair<String, EventStatus> getVerificationStatus() {
        final String reference = userService.getReferenceId();

        return Pair.of(reference, getVerificationStatus(reference));
    }

    private EventStatus getVerificationStatus(String reference) {
        if (isNull(reference)) {
            throw new ShuftiProException("Process of verification data has not started. Reference id is undefined");
        }

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

        return EventStatus.of(statusObject.getString(EVENT));
    }

    private StatusRequest buildStatusRequest(String reference) {
        return StatusRequest.builder()
                .reference(reference)
                .build();
    }

    @Override
    public Pair<String, EventStatus> checkResponseAndUpdateVerificationStep(String signature, String response) {
        //todo: temporary unavailable signature validation
//        validateMerchantSignature(signature, response);

        JSONObject statusObject = new JSONObject(response);
        final String reference = statusObject.getString(REFERENCE);
        final EventStatus eventStatus = EventStatus.of(statusObject.getString(EVENT));

        final String userEmail = userService.getEmailByReferenceId(reference);

        int affectedRowCount = -1;
        switch (eventStatus) {
            case ACCEPTED:
                log.debug("Verification status: {}. Data have been accepted", eventStatus);
                affectedRowCount = userService.updateVerificationStep(userEmail);
                break;
            case CHANGED:
                final EventStatus changedTo = getVerificationStatus(reference);

                boolean isAccepted = EventStatus.ACCEPTED.equals(changedTo);

                log.debug("Verification status changed to: {}. Data have been {}", eventStatus, isAccepted ? "accepted" : "declined");
                if (isAccepted) {
                    affectedRowCount = userService.updateVerificationStep(userEmail);
                }
                break;
        }
        if (affectedRowCount == 0) {
            log.debug("Verification step have not been updated in database");
        }
        sendStatusNotification(userEmail, eventStatus.getEvent());
        log.debug("Notification have been send successfully");

        return Pair.of(reference, eventStatus);
    }

    @Override
    public OnboardingResponseDto startKyCProcessing(IdentityDataRequest identityDataRequest, String email) {
        User user = userService.findByEmail(email);
        if (user.getKycStatus().equalsIgnoreCase("success")) {
            throw new KycException("Already passed KYC");
        }

        //start create applicant
        String uuid = UUID.randomUUID().toString();
        userService.updateKycReferenceByEmail(email, uuid);
        PersonKycDto personKycDto = new PersonKycDto(Collections.singletonList(IdentityDataKyc.of(identityDataRequest)));
        CreateApplicantDto createApplicantDto = new CreateApplicantDto(uuid, personKycDto);

        ResponseCreateApplicantDto response = kycHttpClient.createApplicant(createApplicantDto);

        if (!response.getState().equalsIgnoreCase("INITIAL")) {
            throw new KycException("Error while start processing KYC, state " + response.getState()
                    + " uid " + response.getUid() + " lastReportStatus " + response.getLastReportStatus());
        }
        String docId = RandomStringUtils.random(18, true, false);

        String callBackUrl = String.format("%s/api/public/v2/kyc/webhook/%s", "https://demo.exrates.me", uuid);

        RequestOnBoardingDto onBoardingDto = RequestOnBoardingDto.createOfParams(callBackUrl, email, uuid, docId);
        userVerificationInfoDao.saveUserVerificationDoc(new UserVerificationInfo(user.getId(), DocTypeEnum.P, docId));
        log.info("Sending to create applicant {}", onBoardingDto);
        OnboardingResponseDto onBoarding = kycHttpClient.createOnBoarding(onBoardingDto);
        userService.updateKycStatusById(user.getEmail(), "Pending");
        return onBoarding;
    }

    @Override
    public boolean updateUserVerificationInfo(User user, KycStatusResponseDto kycStatusResponseDto) {
        userService.updateKycStatusById(user.getEmail(), kycStatusResponseDto.getStatus());
        return kycDao.updateUserVerification(user.getId(), kycStatusResponseDto);
    }

    @Override
    public void processingCallBack(String referenceId, KycStatusResponseDto kycStatusResponseDto) {
        User user = userService.findByKycReferenceId(referenceId);
        updateUserVerificationInfo(user, kycStatusResponseDto);

        sendStatusNotification(user.getEmail(), kycStatusResponseDto.getStatus());
    }

    private void sendStatusNotification(String userEmail, String eventStatus) {
        Email email = Email.builder()
                .to(userEmail)
                .subject(emailSubject)
                .message(String.format(emailMessagePattern, eventStatus))
                .build();

        sendMailService.sendMailMandrill(email);
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