package me.exrates.ngcontroller;

import com.google.gson.JsonObject;
import com.sun.corba.se.spi.resolver.LocalResolver;
import lombok.extern.log4j.Log4j;
import me.exrates.controller.exception.RequestsLimitExceedException;
import me.exrates.model.dto.TransferDto;
import me.exrates.model.dto.TransferRequestFlatDto;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.TransferStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.TransferService;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import me.exrates.service.util.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.PRESENT_VOUCHER;

@RestController
@RequestMapping(value = "/info/private/v2/balances/transfer",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Log4j
public class NgTransferController {

    private final RateLimitService rateLimitService;
    private final TransferService transferService;

    @Autowired
    public NgTransferController(RateLimitService rateLimitService,
                                TransferService transferService) {
        this.rateLimitService = rateLimitService;
        this.transferService = transferService;
    }

    // /info/private/v2/balances/transfer/accept  PAYLOAD: {"CODE": "kdbfeyue743467"}
    /**
     * this method processes user refill request by using voucher
     *
     * @param params - map KEY - "CODE", VALUE - VOUCHER_CODE
     * @return 200 OK with body { userToNickName: string, currencyId: number,
     *     userFromId: number, userToId: number, commission: Commission, notyAmount: string,
     *     initialAmount: string, comissionAmount: string },
     *     404 - voucher not found
     *     400 - exceeded limits and or many invoices
     */
    @PostMapping(value = "/accept")
    public ResponseEntity<TransferDto> acceptTransfer(@RequestBody Map<String, String> params) {
        String email = getPrincipalEmail();
        if (!rateLimitService.checkLimitsExceed(email)) {
            log.info("Limits exceeded for user " + email);
            return ResponseEntity.badRequest().build();
        }
        InvoiceActionTypeEnum action = PRESENT_VOUCHER;
        List<InvoiceStatus> requiredStatus = TransferStatusEnum.getAvailableForActionStatusesList(action);
        if (requiredStatus.size() > 1) {
            log.info("To many invoices: " + requiredStatus.size());
            return ResponseEntity.badRequest().build();
        }
        String code = params.getOrDefault("CODE", "");
        Optional<TransferRequestFlatDto> dto = transferService
                .getByHashAndStatus(code, requiredStatus.get(0).getCode(), true);
        if (!dto.isPresent() || !transferService.checkRequest(dto.get(), email)) {
            rateLimitService.registerRequest(email);
            return ResponseEntity.notFound().build();
        }
        TransferRequestFlatDto flatDto = dto.get();
        flatDto.setInitiatorEmail(email);
        TransferDto resDto = transferService.performTransfer(flatDto, Locale.ENGLISH, action);
        return ResponseEntity.ok(resDto);
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
