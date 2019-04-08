package me.exrates.ngcontroller;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.ieo.ClaimDto;
import me.exrates.model.dto.ieo.IEOStatusInfo;
import me.exrates.model.ngModel.response.ResponseModel;
import me.exrates.service.IEOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/api/private/v2/ieo")
@RestController
@Log4j2
public class NgIEOController {

    private final IEOService ieoService;

    @Autowired
    public NgIEOController(IEOService ieoService) {
        this.ieoService = ieoService;
    }

    @PostMapping(value = "/claim", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseModel<?> saveClaim(@RequestBody @Valid ClaimDto claimDto) {
        return new ResponseModel<>(ieoService.addClaim(claimDto, getPrincipalEmail()));
    }

    @GetMapping(value = "/check/{idIeo}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseModel<?> checkAvailable(@PathVariable int idIeo) {
        String email = getPrincipalEmail();
        IEOStatusInfo result = ieoService.checkUserStatusForIEO(email, idIeo);
        return new ResponseModel<>(result);
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
