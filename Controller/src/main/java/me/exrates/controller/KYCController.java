package me.exrates.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.dto.kyc.EventStatus;
import me.exrates.service.exception.ShuftiProException;
import me.exrates.service.impl.ShuftiProKYCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import java.util.Locale;

import static java.util.Objects.nonNull;
import static me.exrates.service.impl.ShuftiProKYCService.SIGNATURE;

@Log4j2
@RestController
@RequestMapping("/kyc/shufti-pro")
public class KYCController {

    private final ShuftiProKYCService kycService;

    @Autowired
    public KYCController(ShuftiProKYCService kycService) {
        this.kycService = kycService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/callback", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity callback(HttpServletRequest request) {
        try (BufferedReader reader = new BufferedReader(request.getReader())) {
            final String response = reader.readLine();
            log.info("Callback response: {}", response);

            if (nonNull(response)) {
                final String signature = request.getHeader(SIGNATURE);

                kycService.checkResponseAndUpdateStatus(signature, response);
                return ResponseEntity.ok().build();
            }
        } catch (IOException ex) {
            log.info("Callback response unmarshalling failed", ex);
        }
        return ResponseEntity.notFound().build();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/verification-url")
    public ResponseEntity<String> getVerificationUrl(@RequestParam("user_id") int userId,
                                                     Locale locale) {
        return ResponseEntity.ok(kycService.getVerificationUrl(userId, locale));
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/verification-status")
    public ResponseEntity<EventStatus> getVerificationStatus(@RequestParam("user_id") int userId) {
        return ResponseEntity.ok(kycService.getVerificationStatus(userId));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ShuftiProException.class)
    @ResponseBody
    public ErrorInfo shuftiProExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }
}