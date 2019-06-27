package me.exrates.ngcontroller;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.dto.kyc.EventStatus;
import me.exrates.model.dto.kyc.IdentityDataRequest;
import me.exrates.model.dto.kyc.KycCountryDto;
import me.exrates.model.dto.kyc.KycLanguageDto;
import me.exrates.model.dto.kyc.KycProvider;
import me.exrates.model.dto.kyc.responces.KycStatusResponseDto;
import me.exrates.model.dto.kyc.responces.OnboardingResponseDto;
import me.exrates.model.exceptions.KycException;
import me.exrates.model.ngModel.response.ResponseModel;
import me.exrates.service.KYCService;
import me.exrates.service.KYCSettingsService;
import me.exrates.service.exception.ShuftiProException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static me.exrates.service.impl.KYCServiceImpl.SIGNATURE;

@Log4j2
@RestController
@RequestMapping(
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class NgKYCController {

    private final static String PUBLIC_KYC = "/api/public/v2/kyc";
    private final static String PRIVATE_KYC = "/api/private/v2/kyc/";

    private final KYCService kycService;
    private final KYCSettingsService kycSettingsService;

    @Autowired
    public NgKYCController(KYCService kycService,
                           KYCSettingsService kycSettingsService) {
        this.kycService = kycService;
        this.kycSettingsService = kycSettingsService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = PUBLIC_KYC + "/callback", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity callback(HttpServletRequest request) {
        try (BufferedReader reader = new BufferedReader(request.getReader())) {
            final String response = reader.readLine();
            log.info("Callback response: {}", response);

            if (nonNull(response)) {
                final String signature = request.getHeader(SIGNATURE);
                log.info("Signature: {}", signature);

                Pair<String, EventStatus> statusPair = kycService.checkResponseAndUpdateVerificationStatus(signature, response);
                log.debug("Verification status: {} [{}]", statusPair.getLeft(), statusPair.getRight());
                return ResponseEntity.ok(statusPair.getRight());
            }
        } catch (IOException ex) {
            log.info("Callback response unmarshalling failed", ex);
        }
        return ResponseEntity.notFound().build();
    }

    // /api/public/v2/webhook/{referenceId}
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = PUBLIC_KYC + "/webhook/{referenceId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> callback(@PathVariable String referenceId,
                                         @RequestBody KycStatusResponseDto kycStatusResponseDto) {
        log.info("CALLBACK_KYC ref {}, {}", referenceId, kycStatusResponseDto);
        kycService.processingCallBack(referenceId, kycStatusResponseDto);
        return ResponseEntity.ok().build();
    }

    /**
     * /api/private/v2/kyc/verification-url
     *
     * @param languageCode - from submitted list
     * @param countryCode  - from submitted list
     * @return - verificationUrl to load in iframe (https://shuftipro.com/process/verification/g63K7XCZGdD6mRC5S7mQw5Lc112ioqLMqYGrDQvhzg3qezdUg4ZJ0VAGTLEWjkC8)
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = PRIVATE_KYC + "/verification-url")
    public ResponseEntity<String> getVerificationUrl(@RequestParam(value = "language_code", required = false) String languageCode,
                                                     @RequestParam("country_code") String countryCode) {
        log.debug("Start getting verification url...");
        final String verificationUrl = kycService.getVerificationUrl(languageCode, countryCode);
        log.debug("Verification url: {}", verificationUrl);
        return ResponseEntity.ok(verificationUrl);
    }

    /**
     * /api/private/v2/kyc/countries
     * <p>
     * {
     * countryName: Ukraine
     * countryCode: UA
     * }
     */
    @GetMapping(value = PRIVATE_KYC + "/countries")
    public ResponseEntity<List<KycCountryDto>> getCountries() {
        return ResponseEntity.ok(kycSettingsService.getCountriesDictionary());
    }

    /**
     * /api/private/v2/kyc/languages
     * <p>
     * {
     * languageName: English
     * languageCode: EN
     * }
     */
    @GetMapping(value = PRIVATE_KYC + "/languages")
    public ResponseEntity<List<KycLanguageDto>> getLanguages() {
        return ResponseEntity.ok(kycSettingsService.getLanguagesDictionary());
    }

    /**
     * /api/private/v2/kyc/status
     *
     * @param kycProvider - kyc provider (for example: SHUFTI_PRO, QUBERA), by default - SHUFTI_PRO
     * @return - verification status
     */
    @GetMapping(PRIVATE_KYC + "/status")
    public ResponseModel<String> getStatusKyc(@RequestParam(value = "kyc_provider", required = false) String kycProvider) {
        String email = getPrincipalEmail();

        if (isNull(kycProvider) || KycProvider.of(kycProvider) == KycProvider.SHUFTI_PRO) {
            return new ResponseModel<>(kycService.getShuftiProKycStatus(email));
        } else if (KycProvider.of(kycProvider) == KycProvider.QUBERA) {
            return new ResponseModel<>(kycService.getQuberaKycStatus(email));
        } else {
            return new ResponseModel<>();
        }
    }

    @PostMapping(value = PRIVATE_KYC + "/start", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseModel<OnboardingResponseDto> startKycProcessing(@RequestBody @Valid IdentityDataRequest identityDataRequest) {
        String email = getPrincipalEmail();
        return new ResponseModel<>(kycService.startKyCProcessing(identityDataRequest, email));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ShuftiProException.class)
    @ResponseBody
    public ErrorInfo shuftiProExceptionHandler(HttpServletRequest req, Exception exception) {
        StringBuffer requestURL = req.getRequestURL();
        log.error("Invocation of request url: {} caused error:", requestURL, exception);
        return new ErrorInfo(requestURL, exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(KycException.class)
    @ResponseBody
    public ErrorInfo exceptionHandler(HttpServletRequest req, Exception exception) {
        StringBuffer requestURL = req.getRequestURL();
        log.error("Invocation of request url: {} caused error:", requestURL, exception);
        return new ErrorInfo(requestURL, exception);
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}