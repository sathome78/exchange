package me.exrates.ngcontroller;

import com.google.common.io.ByteSource;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.ReportDto;
import me.exrates.model.dto.referral.RequestReferral;
import me.exrates.service.referral.ReferralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/private/v2/referral")
@Log4j2(topic = "referral_log")
public class NgReferralController {

    private final ReferralService referralService;

    @Autowired
    public NgReferralController(ReferralService referralService) {
        this.referralService = referralService;
    }

    @GetMapping(value = "/my", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getMyReferralStructure() {
        String email = getPrincipalEmail();
        return new ResponseEntity<>(referralService.getReferralStructure(email), HttpStatus.OK);
    }

    @GetMapping(value = "/structure", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getReferralDetails(@RequestParam(required = false) Integer userId,
                                                @RequestParam(required = false, defaultValue = "0") int level,
                                                @RequestParam(required = false) String link) {
        String email = getPrincipalEmail();
        return new ResponseEntity<>(referralService.getChildReferralStructure(email, userId, level, link), HttpStatus.OK);
    }

    @PutMapping(value = "/{link}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> updateNameReferralLink(@PathVariable String link,
                                                    @RequestBody RequestReferral updateReferral) {
        boolean result = referralService.updateReferralName(getPrincipalEmail(), link, updateReferral.getName());
        return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> createReferralLink(@RequestBody RequestReferral createReferral) {
        String email = getPrincipalEmail();
        return new ResponseEntity<>(referralService.createReferralLink(email, createReferral.getName()), HttpStatus.OK);
    }

    @GetMapping(value = "/referral_income", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getReferralIncome() {
        String email = getPrincipalEmail();
        return new ResponseEntity<>(referralService.getReferralIncome(email), HttpStatus.OK);
    }

    @PostMapping(value = "/transfer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> createTransfer(@RequestParam String currency) {
        String email = getPrincipalEmail();
        boolean result = referralService.createTransferRequest(email, currency);
        return result ? ResponseEntity.status(HttpStatus.CREATED).build()
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/download")
    public ResponseEntity downloadExcel() {
        String email = getPrincipalEmail();
        ReportDto reportDto;
        try {
            reportDto = referralService.downloadExcel(email);
            final byte[] content = reportDto.getContent();
            final String fileName = reportDto.getFileName();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentLength(content.length);
            headers.setContentDispositionFormData("attachment", fileName);

            InputStreamResource isr = new InputStreamResource(ByteSource.wrap(content).openStream());
            return new ResponseEntity<>(isr, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Downloaded file is corrupted");
            return ResponseEntity.noContent().build();
        }
    }


    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
