package me.exrates.controller.ngContorollers;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.TableParams;
import me.exrates.model.dto.mobileApiDto.TransferMerchantApiDto;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.enums.PagingDirection;
import me.exrates.model.vo.CacheData;
import me.exrates.service.*;
import org.apache.logging.log4j.core.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.core.methods.response.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Maks on 08.02.2018.
 */
@Log4j2
@RestController
public class StatNgController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserService userService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantService merchantService;



    @RequestMapping(value = "/info/private/myStatementData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<AccountStatementDto> getMyAccountStatementData(
            @RequestParam("currencyId") Integer currencyId,
            @RequestParam(required = false) Integer page) {
        int pageSize  = 40;
        if (page == null || page < 1) page = 1;
        int offset = (page - 1) * pageSize;
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        int walletId = walletService.getWalletId(userService.getIdByEmail(userEmail), currencyId);
        List<AccountStatementDto> dtos = transactionService.getAccountStatement(walletId, offset, pageSize, locale);
        Integer finalPage = page;
        dtos.forEach(p->p.setPage(finalPage));
        return dtos;
    }





}
