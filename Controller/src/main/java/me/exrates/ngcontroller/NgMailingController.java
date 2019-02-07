package me.exrates.ngcontroller;

import me.exrates.model.mail.ListingRequest;
import me.exrates.ngcontroller.exception.ValidationException;
import me.exrates.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/info/public/v2/listing")
public class NgMailingController {

    private final SendMailService sendMailService;

    @Autowired
    public NgMailingController(SendMailService sendMailService) {
        this.sendMailService = sendMailService;
    }

    @PostMapping(value = "/mail/send", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity sendEmail(@Valid @RequestBody ListingRequest request,
                                    Errors result) {
        if (result.hasErrors()) {
            throw new ValidationException(result.getAllErrors());
        }
        sendMailService.sendListingRequestEmail(request);
        return ResponseEntity.ok().build();
    }
}