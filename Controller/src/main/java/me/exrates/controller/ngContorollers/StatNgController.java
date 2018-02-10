package me.exrates.controller.ngContorollers;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.RefFilterData;
import me.exrates.model.dto.RefsListContainer;
import me.exrates.model.dto.TableParams;
import me.exrates.model.dto.mobileApiDto.TransferMerchantApiDto;
import me.exrates.model.dto.onlineTableDto.*;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.PagingDirection;
import me.exrates.model.vo.CacheData;
import me.exrates.security.annotation.OnlineMethod;
import me.exrates.service.*;
import me.exrates.service.stopOrder.StopOrderService;
import org.apache.logging.log4j.core.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.web3j.protocol.core.methods.response.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Collections;
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
    @Autowired
    private ReferralService referralService;
    @Autowired
    private NewsService newsService;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private InputOutputService inputOutputService;
    @Autowired
    private StopOrderService stopOrderService;
    @Autowired
    private OrderService orderService;




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


    @RequestMapping(value = "/info/private/myReferralStructure")
    public RefsListContainer getMyReferralData(
            @RequestParam("action") String action,
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "onPage", defaultValue = "20") int onPage,
            @RequestParam(value = "page", defaultValue = "1") int page,
            RefFilterData refFilterData) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        /**/
        return referralService.getRefsContainerForReq(action, userId, userService.getIdByEmail(userEmail), onPage, page, refFilterData);
    }

    @ResponseBody
    @RequestMapping(value = "/info/private/getAllCurrencies")
    public List getAllCurrencies() {
        return currencyService.findAllCurrenciesWithHidden();
    }

    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns list the news of current language to show in right sider
     *
     * @param page,            direction - used for pgination. Details see in class TableParam
     * @return list the news
     */
    @OnlineMethod
    @RequestMapping(value = "/info/public/news/{tableId}", method = RequestMethod.GET)
    public List<NewsDto> getNewsList(
            @RequestParam(required = false) Integer page,
            @RequestParam String locale) {
        int pageSize = 5;
        if (page == null || page < 1) page = 1;
        Integer offset = (page - 1) * pageSize;
        List<NewsDto> result = newsService.getNewsBriefList(offset, pageSize, Locale.forLanguageTag(locale));
        Integer finalPage = page;
        result.forEach(p->p.setPage(finalPage));
        return result;
    }

    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns list the data of user's input/output orders to show in pages "History input/output"
     *
     * @param page,            direction - used for pgination. Details see in class TableParams
     * @return list the data of user's orders
     */
    @OnlineMethod
    @RequestMapping(value = "/info/private/myInputoutputData/", method = RequestMethod.GET)
    public List<MyInputOutputHistoryDto> getMyInputoutputData(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) PagingDirection direction) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        int pageSize  = 10;
        if (page == null || page < 1) page = 1;
        int offset = (page - 1) * pageSize;
        List<MyInputOutputHistoryDto> result = inputOutputService.getMyInputOutputHistory(userEmail, offset, pageSize, locale);
        Integer finalPage = page;
        result.forEach(p->p.setPage(finalPage));
        return result;
    }


    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns list the data of user's orders to show in pages "History"
     *
     * @param page,            direction - used for pgination. Details see in class TableParams
     * @return list the data of user's orders
     */
    @OnlineMethod
    @RequestMapping(value = "/info/private/myReferralData", method = RequestMethod.GET)
    public List<MyReferralDetailedDto> getMyReferralData(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) PagingDirection direction) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        int pageSize  = 10;
        if (page == null || page < 1) page = 1;
        int offset = (page - 1) * pageSize;
        List<MyReferralDetailedDto> result = referralService.findAllMyReferral(userEmail, offset, pageSize, locale);
        Integer finalPage = page;
        result.forEach(p->p.setPage(finalPage));
        return result;
    }


    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns list the data of user's orders to show in pages "History" and "Orders"
     *
     * @param status           determines status the order
     * @param page,            direction - used for pgination. Details see in class TableParams
     * @return list the data of user's orders
     * @author ValkSam
     */
    @OnlineMethod
    @RequestMapping(value = "/info/private/myOrdersData/", method = RequestMethod.GET)
    public List<OrderWideListDto> getMyOrdersData(
            @RequestParam Integer pairId,
            @RequestParam(required = false) OperationType type,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) PagingDirection direction,
            @RequestParam(value = "baseType", defaultValue = "LIMIT") OrderBaseType orderBaseType) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        if (pairId == null) {
            return Collections.EMPTY_LIST;
        }
        int pageSize  = 10;
        if (page == null || page < 1) page = 1;
        int offset = (page - 1) * pageSize;
        Boolean showAllPairs = pairId.equals(0);
        CurrencyPair currencyPair = null;
        if (!showAllPairs) {
            currencyPair = currencyService.findCurrencyPairById(pairId);
        }
        List<OrderWideListDto> result;
        switch (orderBaseType) {
            case STOP_LIMIT: {
                result = stopOrderService.getMyOrdersWithState(userEmail,
                        showAllPairs ? null : currencyPair,
                        status, type, scope, offset, pageSize, locale);
                break;
            }
            default: {
                result = orderService.getMyOrdersWithState(userEmail,
                        showAllPairs ? null : currencyPair,
                        status, type, scope, offset, pageSize, locale);
            }
        }
        Integer finalPage = page;
        result.forEach(p->p.setPage(finalPage));
        return result;
    }

}
