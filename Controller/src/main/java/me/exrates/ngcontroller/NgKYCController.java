package me.exrates.ngcontroller;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.dto.kyc.EventStatus;
import me.exrates.model.dto.kyc.KycCountryDto;
import me.exrates.model.dto.kyc.KycLanguageDto;
import me.exrates.model.dto.kyc.VerificationStep;
import me.exrates.service.KYCService;
import me.exrates.service.KYCSettingsService;
import me.exrates.service.UserService;
import me.exrates.service.exception.ShuftiProException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import static java.util.Objects.nonNull;
import static me.exrates.service.impl.ShuftiProKYCService.SIGNATURE;

@Log4j2
@RestController
@RequestMapping("/kyc")
public class NgKYCController {

    private final UserService userService;
    private final KYCService kycService;
    private final KYCSettingsService kycSettingsService;

    @Autowired
    public NgKYCController(UserService userService,
                           KYCService kycService,
                           KYCSettingsService kycSettingsService) {
        this.userService = userService;
        this.kycService = kycService;
        this.kycSettingsService = kycSettingsService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/shufti-pro/callback", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity callback(HttpServletRequest request) {
        try (BufferedReader reader = new BufferedReader(request.getReader())) {
            final String response = reader.readLine();
            log.info("Callback response: {}", response);

            if (nonNull(response)) {
                final String signature = request.getHeader(SIGNATURE);

                Pair<String, EventStatus> statusPair = kycService.checkResponseAndUpdateVerificationStep(signature, response);
                log.debug("Verification status: {} [{}]", statusPair.getLeft(), statusPair.getRight());
                return ResponseEntity.ok(statusPair.getRight());
            }
        } catch (IOException ex) {
            log.info("Callback response unmarshalling failed", ex);
        }
        return ResponseEntity.notFound().build();
    }

    // /private/v2/shufti-pro/verification-url/step/{stepNumber}

    /**
     *  /private/v2/shufti-pro/verification-url/step/{step}
     * @param step - possible values (LEVEL_ONE, LEVEL_TWO)
     * @param languageCode - from submitted list
     * @param countryCode - from submitted list
     * @return - verificationUrl to load in iframe
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/private/v2/shufti-pro/verification-url/step/{step}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> getVerificationUrl(@PathVariable VerificationStep step,
                                                     @RequestParam(value = "language_code", required = false) String languageCode,
                                                     @RequestParam("country_code") String countryCode) {
        log.debug("Start getting verification url...");
        final String verificationUrl = kycService.getVerificationUrl(step.getStep(), languageCode, countryCode);
        log.debug("Verification url: {}", verificationUrl);
        return ResponseEntity.ok(verificationUrl);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/private/v2/shufti-pro/verification-status", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<EventStatus> getVerificationStatus() {
        log.debug("Start getting status url...");
        Pair<String, EventStatus> statusPair = kycService.getVerificationStatus();
        log.debug("Verification status: {} [{}]", statusPair.getLeft(), statusPair.getRight());
        return ResponseEntity.ok(statusPair.getRight());
    }

    /**
     *  /private/v2/shufti-pro/countries
     *
     * {
     *     countryName: Ukraine
     *     countryCode: UA
     * }
     */
    @GetMapping(value = "/private/v2/shufti-pro/countries", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<KycCountryDto>> getCountries() {
        return ResponseEntity.ok(kycSettingsService.getCountriesDictionary());
    }


    /**
     * /private/v2/shufti-pro/languages
     *
     * {
     *     languageName: English
     *     languageCode: EN
     * }
     */
    @GetMapping(value = "/private/v2/shufti-pro/languages", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<KycLanguageDto>> getLanguages() {
        return ResponseEntity.ok(kycSettingsService.getLanguagesDictionary());
    }

    /**
     * /private/v2/shufti-pro/current-step
     *
     *  returns (NOT_VERIFIED, LEVEL_ONE, LEVEL_TWO)
     */
    @GetMapping(value = "/private/v2/shufti-pro/current-step", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<VerificationStep> getCurrentVerificationStep() {
        return ResponseEntity.ok(userService.getVerificationStep());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ShuftiProException.class)
    @ResponseBody
    public ErrorInfo shuftiProExceptionHandler(HttpServletRequest req, Exception exception) {
        StringBuffer requestURL = req.getRequestURL();
        log.error("Invocation of request url: {} caused error:", requestURL, exception);
        return new ErrorInfo(requestURL, exception);
    }
}