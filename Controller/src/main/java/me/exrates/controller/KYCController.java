package me.exrates.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.dto.kyc.EventStatus;
import me.exrates.service.KYCService;
import me.exrates.service.exception.ShuftiProException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

import static java.util.Objects.nonNull;
import static me.exrates.service.impl.ShuftiProKYCService.SIGNATURE;

@Log4j2
@RestController
@RequestMapping("/kyc")
public class KYCController {

    private final KYCService kycService;

    @Autowired
    public KYCController(KYCService kycService) {
        this.kycService = kycService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/shufti-pro/callback", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity callback(HttpServletRequest request) {
        try (BufferedReader reader = new BufferedReader(request.getReader())) {
            final String response = reader.readLine();
            log.info("Callback response: {}", response);

            if (nonNull(response)) {
                final String signature = request.getHeader(SIGNATURE);

                Pair<String, EventStatus> statusPair = kycService.checkResponseAndUpdateStatus(signature, response);
                log.debug("Verification status: {} [{}]", statusPair.getLeft(), statusPair.getRight());
                return ResponseEntity.ok(statusPair.getRight());
            }
        } catch (IOException ex) {
            log.info("Callback response unmarshalling failed", ex);
        }
        return ResponseEntity.notFound().build();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/shufti-pro/verification-url", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> getVerificationUrl(@RequestParam("user_id") int userId,
                                                     @RequestParam("language") String language,
                                                     @RequestParam("country") String country) {
        log.debug("Start getting verification url...");
        final String verificationUrl = kycService.getVerificationUrl(userId, language, country);
        log.debug("Verification url: {}", verificationUrl);
        return ResponseEntity.ok(verificationUrl);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/shufti-pro/verification-status", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<EventStatus> getVerificationStatus(@RequestParam("user_id") int userId) {
        log.debug("Start getting status url...");
        Pair<String, EventStatus> statusPair = kycService.getVerificationStatus(userId);
        log.debug("Verification status: {} [{}]", statusPair.getLeft(), statusPair.getRight());
        return ResponseEntity.ok(statusPair.getRight());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ShuftiProException.class)
    @ResponseBody
    public ErrorInfo shuftiProExceptionHandler(HttpServletRequest req, Exception exception) {
        StringBuffer requestURL = req.getRequestURL();
        log.error("Invocation of request url: {} caused error:", requestURL, exception);
        return new ErrorInfo(requestURL, exception);
    }
}