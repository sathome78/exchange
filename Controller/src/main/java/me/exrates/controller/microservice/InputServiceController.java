package me.exrates.controller.microservice;

import lombok.RequiredArgsConstructor;
import me.exrates.model.Wallet;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.WalletInnerTransferDto;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.service.WalletService;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = "/inout", produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Conditional(MicroserviceConditional.class)
public class InputServiceController {

    private final WalletService walletService;

    @GetMapping("/findWalletByUserAndCurrency")
    public Wallet findByUserAndCurrency(@RequestParam("userId") int userId, @RequestParam("currencyId") int currencyId){
        return walletService.findByUserAndCurrency(userId, currencyId);
    }

    @GetMapping("/isEnoughWalletMoney")
    public boolean ifEnoughMoney(@RequestParam("walletId") int walletId, @RequestParam("amountForCheck") BigDecimal amountForCheck){
        return walletService.ifEnoughMoney(walletId, amountForCheck);
    }

    @GetMapping("/getWalletABalance")
    public BigDecimal getWalletABalance(@RequestParam("walletId") int walletId){
        return walletService.getWalletABalance(walletId);
    }

    @PostMapping("/walletInnerTransfer")
    public WalletTransferStatus walletInnerTransfer(@RequestBody WalletInnerTransferDto dto){
        return walletService.walletInnerTransfer(dto.getWalletId(), dto.getAmount(), dto.getSourceType(), dto.getSourceId(), dto.getDescription());
    }

}
