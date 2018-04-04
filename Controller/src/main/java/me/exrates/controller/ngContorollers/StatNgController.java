package me.exrates.controller.ngContorollers;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.*;
import me.exrates.model.dto.mobileApiDto.TransferMerchantApiDto;
import me.exrates.model.dto.onlineTableDto.*;
import me.exrates.model.enums.*;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.model.vo.CacheData;
import me.exrates.security.annotation.OnlineMethod;
import me.exrates.service.*;
import me.exrates.service.stopOrder.StopOrderService;
import org.apache.logging.log4j.core.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.web3j.protocol.core.methods.response.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CommissionService comissionService;

    /*default depth the interval for chart*/
    final public static BackDealInterval BACK_DEAL_INTERVAL_DEFAULT = new BackDealInterval("24 HOUR");
    /*depth the accepted order history*/
    final public static BackDealInterval ORDER_HISTORY_INTERVAL = new BackDealInterval("24 HOUR");
    /*limit the data fetching of order history (additional to ORDER_HISTORY_INTERVAL). (-1) means no limit*/
    final public static Integer ORDER_HISTORY_LIMIT = 100;




    @RequestMapping(value = "/info/private/myStatementData/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public StatTableDto<AccountStatementDto> getMyAccountStatementData(
            @PathVariable("name") String currencyName,
            @RequestParam(required = false) Integer page) {
        int pageSize  = 40;
        if (page == null || page < 1) page = 1;
        int offset = (page - 1) * pageSize;

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Currency currency = currencyService.findByName(currencyName);
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        int walletId = walletService.getWalletId(userService.getIdByEmail(userEmail), currency.getId());
        List<AccountStatementDto> dtos = transactionService.getAccountStatement(walletId, offset, pageSize + 1, locale);
        return new StatTableDto<>(page, pageSize, dtos);

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
    @RequestMapping(value = "/info/public/news", method = RequestMethod.GET)
    public StatTableDto<NewsDto> getNewsList(
            @RequestParam(required = false) Integer page,
            @RequestParam String locale) {
        int pageSize = 5;
        if (page == null || page < 1) page = 1;
        Integer offset = (page - 1) * pageSize;
        List<NewsDto> dtos = newsService.getNewsBriefList(offset, pageSize, Locale.forLanguageTag(locale));
        return new StatTableDto<>(page, pageSize, dtos);
    }

    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns list the data of user's input/output orders to show in pages "History input/output"
     *
     * @param page,            direction - used for pgination. Details see in class TableParams
     * @return list the data of user's orders
     */
    @OnlineMethod
    @RequestMapping(value = "/info/private/myInputoutputData", method = RequestMethod.GET)
    public StatTableDto<MyInputOutputHistoryDto> getMyInputoutputData(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) PagingDirection direction) {

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        int pageSize  = 10;
        if (page == null || page < 1) page = 1;
        int offset = (page - 1) * pageSize;
        List<MyInputOutputHistoryDto> dtos = inputOutputService.getMyInputOutputHistory(userEmail, offset, pageSize, locale);
        return new StatTableDto<>(page, pageSize, dtos);
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
    public StatTableDto<MyReferralDetailedDto> getMyReferralData(
            @RequestParam(required = false) Integer page) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        int pageSize  = 10;
        if (page == null || page < 1) page = 1;
        int offset = (page - 1) * pageSize;
        List<MyReferralDetailedDto> result = referralService.findAllMyReferral(userEmail, offset, pageSize, locale);
        return new StatTableDto<>(page, pageSize, result);
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
    public StatTableDto<OrderWideListDto> getMyOrdersData(
            @RequestParam(required = false) Integer pairId,
            @RequestParam(required = false) OperationType type,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) PagingDirection direction,
            @RequestParam(value = "baseType", defaultValue = "LIMIT") OrderBaseType orderBaseType) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        int pageSize  = 10;
        if (page == null || page < 1) page = 1;
        int offset = (page - 1) * pageSize;
        if (pairId == null) {
            return new StatTableDto<>(page, pageSize, Collections.EMPTY_LIST);
        }
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
        return new StatTableDto<>(page, pageSize, result);
    }

    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns list the data of user's wallet to show in page "Balance"
     * @return list the data of user's wallet
     * @author ValkSam
     */
    @OnlineMethod
    @RequestMapping(value = "/info/private/myWalletsData", method = RequestMethod.GET)
    public List<MyWalletsDetailedDto> getMyWalletsData() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        List<MyWalletsDetailedDto> result = walletService.getAllWalletsForUserDetailed(userEmail, locale);
        return result;
    }



    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns data with statistics for orders of current CurrencyPair to show above the graphics
     *
     * @param pairId - id of currency pair
     * @param interval - interval for statistic
     * @param loc - current user locale on client-side
     * @return: data with statistics for orders of current CurrencyPair
     * @author ValkSam
     */
    @OnlineMethod
    @RequestMapping(value = "/info/public/ordersForPairStatistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ExOrderStatisticsDto getNewCurrencyPairData(Integer pairId, String interval, String loc) {
        Locale locale = Locale.forLanguageTag(loc);
        CurrencyPair currencyPair = currencyService.findCurrencyPairById(pairId);
        BackDealInterval backDealInterval = new BackDealInterval(interval);
        if (currencyPair == null || backDealInterval == null) {
            return null;
        }
        /**/
        ExOrderStatisticsDto exOrderStatisticsDto = orderService.getOrderStatistic(currencyPair, backDealInterval, locale);
        return exOrderStatisticsDto;
    }

    @OnlineMethod
    @RequestMapping(value = "/info/private/myWalletsStatistic", method = RequestMethod.GET)
    public List<MyWalletsStatisticsDto> getMyWalletsStatisticsForAllCurrencies() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(email);
        return walletService.getAllWalletsForUserReduced(email, locale);
    }

}
