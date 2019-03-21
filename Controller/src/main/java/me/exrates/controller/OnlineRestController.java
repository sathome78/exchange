package me.exrates.controller;

import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.CandleChartItemDto;
import me.exrates.model.dto.CurrentParams;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.OrderCommissionsDto;
import me.exrates.model.dto.RefFilterData;
import me.exrates.model.dto.ReferralInfoDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefsListContainer;
import me.exrates.model.dto.TableParams;
import me.exrates.model.dto.WalletTotalUsdDto;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.MyReferralDetailedDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.model.dto.onlineTableDto.NewsDto;
import me.exrates.model.dto.onlineTableDto.NotificationDto;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.ChartType;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.PagingDirection;
import me.exrates.model.enums.UserRole;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.model.vo.CacheData;
import me.exrates.security.annotation.OnlineMethod;
import me.exrates.service.CommissionService;
import me.exrates.service.CurrencyService;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.NewsService;
import me.exrates.service.NotificationService;
import me.exrates.service.OrderService;
import me.exrates.service.ReferralService;
import me.exrates.service.RefillService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.WithdrawService;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.exception.RefillRequestMerchantException;
import me.exrates.service.stopOrder.StopOrderService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * The controller contains online methods. "Online method" is the handler of online requests,
 * which updates data on browser page in online mode.
 * The online request is the automatic request and is not sign of user activity and should not update
 * session param "sessionEndTime", which stores the time of end the current session.
 * Another (not online) methods, excluding resources request, reset session param "sessionEndTime" and session life starts from begin
 * Updating session param "sessionEndTime" happens in class XssRequestFilter.
 * <p>
 * IMPORTANT!
 * The OnlineRestController can contain not online methods. But all online methods must be placed in the OnlineRestController
 * Online methods must be annotated with @OnlineMethod
 *
 * @author ValkSam
 */
@Log4j2
@PropertySource(value = {"classpath:/mobile.properties", "classpath:session.properties"})
@RestController
public class OnlineRestController {
    private static final Logger LOGGER = LogManager.getLogger(OnlineRestController.class);
    /* if SESSION_LIFETIME_HARD set, session will be killed after time expired, regardless of activity the session
    set SESSION_LIFETIME_HARD = 0 to ignore it*/
    /* public static final long SESSION_LIFETIME_HARD = Math.round(90 * 60); //SECONDS*/
    /* if SESSION_LIFETIME_INACTIVE set, session will be killed if it is inactive during the time
     * set SESSION_LIFETIME_INACTIVE = 0 to ignore it and session lifetime will be set to default value (30 mins)
     * The time of end the current session is stored in session param "sessionEndTime", which calculated in millisec as
     * new Date().getTime() + SESSION_LIFETIME_HARD * 1000*/
    /*public static final int SESSION_LIFETIME_INACTIVE = 0; //SECONDS*/
    /*default depth the interval for chart*/
    final public static BackDealInterval BACK_DEAL_INTERVAL_DEFAULT = new BackDealInterval("24 HOUR");
    /*depth the accepted order history*/
    final public static BackDealInterval ORDER_HISTORY_INTERVAL = new BackDealInterval("24 HOUR");
    /*limit the data fetching of order history (additional to ORDER_HISTORY_INTERVAL). (-1) means no limit*/
    final public static Integer ORDER_HISTORY_LIMIT = 100;
    /*default limit the data fetching for all tables. (-1) means no limit*/
    final public static Integer TABLES_LIMIT_DEFAULT = -1;
    /*default type of the chart*/
    final public static ChartType CHART_TYPE_DEFAULT = ChartType.STOCK;
    /*it's need to install only one: SESSION_LIFETIME_HARD or SESSION_LIFETIME_INACTIVE*/

    private @Value("${session.timeParamName}")
    String sessionTimeMinutes;
    private @Value("${session.lastRequestParamName}")
    String sessionLastRequestParamName;

    @Autowired
    CommissionService commissionService;

    @Autowired
    OrderService orderService;

    @Autowired
    WalletService walletService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    NewsService newsService;

    @Autowired
    ReferralService referralService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    MerchantService merchantService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;

    @Autowired
    WithdrawService withdrawService;

    @Autowired
    InputOutputService inputOutputService;

    @Autowired
    StopOrderService stopOrderService;

    @Autowired
    private ExchangeRatesHolder exchangeRatesHolder;
    @Autowired
    private RefillService refillService;

    private final String HEADER_SECURITY = "username";

    @PostMapping(value = "/afgssr/call/refill", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, String> generateRefill(@RequestBody RefillRequestCreateDto requestDto, HttpServletRequest servletRequest) {
        try {
            Preconditions.checkNotNull(requestDto.getServiceBeanName(), "wrong params");
            String usernameHeader = servletRequest.getHeader(HEADER_SECURITY);
            Preconditions.checkArgument(!StringUtils.isEmpty(usernameHeader), "invalid request");
            String username = new String(Base64.getDecoder().decode(usernameHeader.getBytes()));
            Preconditions.checkArgument(username.equals(requestDto.getUserEmail()) && userService.findByEmail(username) != null, "user not found or wrong user");
            return refillService.callRefillIRefillable(requestDto);
        } catch (Exception e) {
            log.error(e);
            throw new RefillRequestMerchantException(e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            RefillRequestMerchantException.class,
    })
    @ResponseBody
    public ErrorInfo RefillException(HttpServletRequest req, Exception exception) {
        log.error(exception);
        return new ErrorInfo(req.getRequestURL(), exception, exception.getMessage());
    }


    @GetMapping("/trade_pairs")
    public Map<String, Integer> getAllAvailableMainPairs() {
        return currencyService.getAllCurrencyPairs(CurrencyPairType.MAIN)
                .stream()
                .collect(Collectors.toMap(CurrencyPair::getName, CurrencyPair::getId));
    }


    @RequestMapping(value = "/dashboard/commission/{type}", method = RequestMethod.GET)
    public BigDecimal getCommissions(@PathVariable("type") String type) {
        UserRole userRole = userService.getUserRoleFromSecurityContext();
        try {
            switch (type) {
                case "sell":
                    return commissionService.findCommissionByTypeAndRole(OperationType.SELL, userRole).getValue();
                case "buy":
                    return commissionService.findCommissionByTypeAndRole(OperationType.BUY, userRole).getValue();
                default:
                    return null;
            }
        } finally {
        }
    }

    @OnlineMethod
    @RequestMapping(value = "/dashboard/myWalletsStatistic", method = RequestMethod.GET)
    public Map<String, Object> getMyWalletsStatisticsForAllCurrencies(@RequestParam(required = false) Boolean refreshIfNeeded,
                                                                      @RequestParam(defaultValue = "MAIN") CurrencyPairType type,
                                                                      Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        String cacheKey = "myWalletsStatistic" + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<MyWalletsStatisticsDto> resultWallet = walletService.getAllWalletsForUserReduced(cacheData, email, localeResolver.resolveLocale(request), type);
        Map<String, Object> map = new HashMap<>();
        map.put("mapWallets", resultWallet);

        if (resultWallet.size() > 1) {

            List<ExOrderStatisticsShortByPairsDto> resultOrders = exchangeRatesHolder.getAllRates();

            final HashMap<String, BigDecimal> ratesBTC_ETH = new HashMap<>();
            resultOrders
                    .stream()
                    .filter(p -> p.getCurrencyPairName().contains("BTC/USD") || p.getCurrencyPairName().contains("ETH/USD"))
                    .forEach(p -> ratesBTC_ETH.put(p.getCurrencyPairName(), new BigDecimal(p.getLastOrderRate())));

            final List<WalletTotalUsdDto> walletTotalUsdDtoList = new ArrayList<>();
            for (MyWalletsStatisticsDto myWalletsStatisticsDto : resultWallet) {
                WalletTotalUsdDto walletTotalUsdDto = new WalletTotalUsdDto(myWalletsStatisticsDto.getCurrencyName());
                Map<String, BigDecimal> mapWalletTotalUsdDto = new HashMap<>();
                if (myWalletsStatisticsDto.getCurrencyName().equals("USD")) {
                    walletTotalUsdDto.setSumUSD(new BigDecimal(myWalletsStatisticsDto.getTotalBalance()));
                    walletTotalUsdDto.setRates(mapWalletTotalUsdDto);
                    walletTotalUsdDtoList.add(walletTotalUsdDto);
                }
                resultOrders
                        .stream()
                        .filter(o -> o.getCurrencyPairName().equals(myWalletsStatisticsDto.getCurrencyName().concat("/USD"))
                                || o.getCurrencyPairName().equals(myWalletsStatisticsDto.getCurrencyName().concat("/BTC"))
                                || o.getCurrencyPairName().equals(myWalletsStatisticsDto.getCurrencyName().concat("/ETH"))
                        )
                        .forEach(o -> {
                            mapWalletTotalUsdDto.put(o.getCurrencyPairName(), new BigDecimal(o.getLastOrderRate()));
                        });
                if (!mapWalletTotalUsdDto.isEmpty()) {
                    walletTotalUsdDto.setTotalBalance(new BigDecimal(myWalletsStatisticsDto.getTotalBalance()));
                    walletTotalUsdDto.setRates(mapWalletTotalUsdDto);
                    walletTotalUsdDtoList.add(walletTotalUsdDto);
                }
            }

            walletTotalUsdDtoList.forEach(wallet -> {
                if (wallet.getRates().containsKey(wallet.getCurrency().concat("/USD"))) {
                    wallet.setSumUSD(wallet.getRates().get(wallet.getCurrency().concat("/USD")).multiply(wallet.getTotalBalance()));
                } else if (wallet.getRates().containsKey(wallet.getCurrency().concat("/BTC"))) {
                    wallet.setSumUSD(wallet.getRates().get(wallet.getCurrency().concat("/BTC"))
                            .multiply(wallet.getTotalBalance()).multiply(ratesBTC_ETH.get("BTC/USD")));
                } else if (wallet.getRates().containsKey(wallet.getCurrency().concat("/ETH"))) {
                    wallet.setSumUSD(wallet.getRates().get(wallet.getCurrency().concat("/ETH"))
                            .multiply(wallet.getTotalBalance()).multiply(ratesBTC_ETH.get("ETH/USD")));
                }
            });

            map.put("sumTotalUSD", walletTotalUsdDtoList
                    .stream()
                    .mapToDouble(w -> w.getSumUSD().doubleValue())
                    .sum());
        }

        return map;

    }


    /**
     * this method do two function:
     * - one of online methods. Retrieves current statistics (states) for all currency pairs
     * - controls the session state and start new session when necessary
     * <p>
     * control the session state:
     * the method is being called by EACH of main pages (main pages are mapped in class EntryController).
     * Therefore in it we process the change the session.
     * <p>
     * For changing the session two variants a used:
     * - session lives during fixed time (set in SESSION_LIFETIME_HARD)
     * - session lives while it is active (lifetime is set in SESSION_LIFETIME_INACTIVE).
     * As after the start of a new session, it can be intercepted by any of onlne methods in which necessary
     * data have not been populated, then we use the session parameter "firstEntry", which indicates that new session
     * starts in correct sequence.
     * <p>
     * Sequence of session start is:
     * url "/dashboard"
     * -> leftSider.js: LeftSiderClass.init()
     * -> url "/dashboard/firstentry"  (here the session parameter "firstEntry" will be set)
     * -> leftSider.js: getStatisticsForAllCurrencies()
     * -> url "/dashboard/currencyPairStatistic" (mappet to this method)
     *
     * @param refreshIfNeeded - if set true, method returns data of currency pair statistics only if data has been changed in DB.
     *                        if data has not been changed in DB, method returns corresponding
     *                        info (field class OnlineTableDto.needRefresh" = false) and view on browser will not be repainted
     *                        - if set false, method returns data of currency pair statistics in any cases (data has been changed in DB or not)
     *                        and view on browser will be repainted
     * @param request
     * @return map with one of two keys:
     * - "redirect": if new session has started
     * - "list": data of currency pairs statistics
     * @throws IOException
     * @author ValkSam
     */
    /*@OnlineMethod
    @RequestMapping(value = "/dashboard/currencyPairStatistic", method = RequestMethod.GET)
    public Map<String, ?> getCurrencyPairStatisticsForAllCurrencies(
            @RequestParam(required = false) Boolean refreshIfNeeded,
            HttpServletRequest request, Principal principal) throws IOException {
        try {
            HttpSession session = request.getSession(true);
     *//* if (session.getAttribute("sessionEndTime") == null) {
        session.setAttribute("sessionEndTime", new Date().getTime() + SESSION_LIFETIME_HARD * 1000);
      }
      String s = "";
      if (SESSION_LIFETIME_HARD != 0) {
        if (session.getAttribute("sessionEndTime") != null) {
          s = " Remain to SESSION_LIFETIME_HARD killing: " + ((Long) session.getAttribute("sessionEndTime") - new Date().getTime()) / 1000 + " sec";
        }
      }
      LOGGER.trace(" SESSION: " + session.getId() + " firstEntry: " + session.getAttribute("firstEntry") + s);
      if ((!session.isNew()) &&
          (SESSION_LIFETIME_HARD != 0) &&
          new Date().getTime() >= (Long) session.getAttribute("sessionEndTime")) {
        long st = (Long) session.getAttribute("sessionEndTime");
        try {
          request.logout();
        } catch (ServletException e) {
          e.printStackTrace();
        }
        session = request.getSession(true);
        LOGGER.debug(" SESSION_LIFETIME_HARD. NEW SESSION STARTED: " + session.getId() + " by time: " + st + " new time: " + session.getAttribute("sessionEndTime"));
      }*//*
     *//* if (session.isNew() || session.getAttribute("firstEntry") == null) {

            "session.isNew() == true" indicates that "/dashboard/currencyPairStatistic" is called first after previous
            session has expired, and opened new session (by calling request.getSession(true))
            "firstEntry" == null indicates that new session was started by other online method
            * and "/dashboard/currencyPairStatistic" ought to start new session and redirect to "/dashboard"
        session.setAttribute("sessionEndTime", new Date().getTime() + SESSION_LIFETIME_HARD * 1000);
        LOGGER.debug(" REDIRECT to /dashboard. SESSION: " + session.getId() + " is new: " + session.isNew() + " firstEntry: " + session.getAttribute("firstEntry"));
        return new HashMap<String, HashMap<String, String>>() {{
          put("redirect", new HashMap<String, String>() {{
            put("url", "/dashboard");
            put("urlParam1", messageSource.getMessage("session.expire", null, localeResolver.resolveLocale(request)));
          }});
        }};
      }*//*
            *//*if (session.getAttribute("QR_LOGGED_IN") != null) {
             *//**//*after authentication via QR main page must be reloaded*//**//*
        session.removeAttribute("QR_LOGGED_IN");
        LOGGER.debug(" REDIRECT to /dashboard. SESSION: " + session.getId() + " is new: " + session.isNew() + " firstEntry: " + session.getAttribute("firstEntry"));
        return new HashMap<String, HashMap<String, String>>() {{
          put("redirect", new HashMap<String, String>() {{
            put("url", "/dashboard");
            put("successQR", messageSource.getMessage("dashboard.qrLogin.successful", null, localeResolver.resolveLocale(request)));
          }});
        }};
      }*//*
            String cacheKey = "currencyPairStatistic" + request.getHeader("windowid");
            refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
            CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
            return new HashMap<String, List<ExOrderStatisticsShortByPairsDto>>() {{
                put("list", orderService.getOrdersStatisticByPairs(cacheData, localeResolver.resolveLocale(request)));
            }};
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }*/

    /**
     * when session has expired, any of online methods can start new session (through calling the request.getSession())
     * But in this case the started session will not be populated with necessary data.
     * To populate the session with necessary data, it's need redirection to "/dashboard"
     * "firstEntry" != null indicates that such redirection happened:
     * 1. "/dashboard/currencyPairStatistic" catch request.
     * 2. If new session was started by other online method - firstEntry is null.
     * 3. In this case "/dashboard/currencyPairStatistic" inits the redirection to "/dashboard", "/dashboard/firstentry" will be
     * called and set "firstEntry"
     * In other words, "firstEntry" == null indicates that new session was started by other online method (not "/dashboard/currencyPairStatistic")
     * and "/dashboard/currencyPairStatistic" ought to start new session and redirect to "/dashboard"
     *
     * @param request
     */
    @RequestMapping(value = {"/dashboard/firstentry"})
    public void setFirstEntryFlag(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("firstEntry", true);
        LOGGER.debug(" SESSION: " + session.getId() + " firstEntry: " + session.getAttribute("firstEntry"));

   /* if (SESSION_LIFETIME_INACTIVE != 0) {
      session.setMaxInactiveInterval(SESSION_LIFETIME_INACTIVE);
    }*/
    }

    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns the data for graphic
     * method has not param "refreshIfNeeded", but it is called if the data, which indicates that graphic must be repainted, has been changed
     *
     * @param request
     * @return: "null" if user is not login. List the data of user's wallet current statistics
     * @author ValkSam
     */
    /*@OnlineMethod
    @RequestMapping(value = "/dashboard/chartArray/{type}", method = RequestMethod.GET)
    public ArrayList chartArray(HttpServletRequest request) {
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        if (currencyPair == null) {
            return new ArrayList();
        }
        final BackDealInterval backDealInterval = (BackDealInterval) request.getSession().getAttribute("currentBackDealInterval");
        ChartType chartType = (ChartType) request.getSession().getAttribute("chartType");
        log.error("chartType {}", chartType);
        *//**//*
        ArrayList<List> arrayListMain = new ArrayList<>();
        *//*in first row return backDealInterval - to synchronize period menu with it*//*
        arrayListMain.add(new ArrayList<Object>() {{
            add(backDealInterval);
        }});
        *//**//*
        if (chartType == ChartType.AREA) {
            *//*GOOGLE*//*
            List<Map<String, Object>> rows = orderService.getDataForAreaChart(currencyPair, backDealInterval);
            for (Map<String, Object> row : rows) {
                Timestamp dateAcception = (Timestamp) row.get("dateAcception");
                BigDecimal exrate = (BigDecimal) row.get("exrate");
                BigDecimal volume = (BigDecimal) row.get("volume");
                if (dateAcception != null) {
                    ArrayList<Object> arrayList = new ArrayList<>();
                    *//*values*//*
                    arrayList.add(dateAcception.toString());
                    arrayList.add(exrate.doubleValue());
                    arrayList.add(volume.doubleValue());
                    *//*titles of values for chart tip*//*
                    arrayList.add(messageSource.getMessage("orders.date", null, localeResolver.resolveLocale(request)));
                    arrayList.add(messageSource.getMessage("orders.exrate", null, localeResolver.resolveLocale(request)));
                    arrayList.add(messageSource.getMessage("orders.volume", null, localeResolver.resolveLocale(request)));
                    arrayListMain.add(arrayList);
                }
            }
        } else if (chartType == ChartType.CANDLE) {
            *//*GOOGLE*//*
            List<CandleChartItemDto> rows = orderService.getDataForCandleChart(currencyPair, backDealInterval);
            for (CandleChartItemDto candle : rows) {
                ArrayList<Object> arrayList = new ArrayList<>();
                *//*values*//*
                arrayList.add(candle.getBeginPeriod().toString());
                arrayList.add(candle.getEndPeriod().toString());
                arrayList.add(candle.getOpenRate());
                arrayList.add(candle.getCloseRate());
                arrayList.add(candle.getLowRate());
                arrayList.add(candle.getHighRate());
                arrayList.add(candle.getBaseVolume());
                arrayListMain.add(arrayList);
            }
        } else if (chartType == ChartType.STOCK) {
            *//*AMCHARTS*//*
            List<CandleChartItemDto> rows = orderService.getDataForCandleChart(currencyPair, backDealInterval);
            for (CandleChartItemDto candle : rows) {
                ArrayList<Object> arrayList = new ArrayList<>();
                *//*values*//*
                arrayList.add(candle.getBeginDate().toString());
                arrayList.add(candle.getEndDate().toString());
                arrayList.add(candle.getOpenRate());
                arrayList.add(candle.getCloseRate());
                arrayList.add(candle.getLowRate());
                arrayList.add(candle.getHighRate());
                arrayList.add(candle.getBaseVolume());
                arrayListMain.add(arrayList);
            }
        }
        request.getSession().setAttribute("currentBackDealInterval", backDealInterval);
        return arrayListMain;
    }*/

    /**
     * Sets (init or reset) and returns current params:
     * - current currency pair
     * - current period
     * - current chart
     * - showAllPairs which determines the order for current currency pair only must be shown or for all pairs
     *
     * @param currencyPairName
     * @param period
     * @param request
     * @return object with values of params
     */
    @RequestMapping(value = "/dashboard/currentParams", method = RequestMethod.GET)
    public CurrentParams setCurrentParams(
            @RequestParam(required = false) String currencyPairName,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String chart,
            @RequestParam(required = false) Boolean showAllPairs,
            @RequestParam(required = false) Boolean orderRoleFilterEnabled,
            @RequestParam(defaultValue = "ALL") CurrencyPairType currencyPairType,
            HttpServletRequest request) {
        CurrencyPair currencyPair = getPairFormSessionOrRequest(request, currencyPairName, currencyPairType);
        currencyPair = resolveCurrentOrDefaultPairForType(currencyPair, currencyPairType);
        request.getSession().setAttribute("currentCurrencyPair", currencyPair);
        /**/
        if (showAllPairs == null) {
            if (request.getSession().getAttribute("showAllPairs") == null) {
                showAllPairs = false;
            } else {
                showAllPairs = (Boolean) request.getSession().getAttribute("showAllPairs");
            }
        }
        request.getSession().setAttribute("showAllPairs", showAllPairs);
        /**/
        BackDealInterval backDealInterval;
        if (period == null) {
            backDealInterval = (BackDealInterval) request.getSession().getAttribute("currentBackDealInterval");
            if (backDealInterval == null) {
                backDealInterval = BACK_DEAL_INTERVAL_DEFAULT;
            }
        } else {
            backDealInterval = new BackDealInterval(period);
        }
        request.getSession().setAttribute("currentBackDealInterval", backDealInterval);
        /**/
        ChartType chartType;
        if (chart == null) {
            chartType = (ChartType) request.getSession().getAttribute("chartType");
            if (chartType == null) {
                chartType = CHART_TYPE_DEFAULT;
            }
        } else {
            chartType = ChartType.convert(chart);
        }
        request.getSession().setAttribute("chartType", chartType);
        /**/
        if (orderRoleFilterEnabled == null) {
            if (request.getSession().getAttribute("orderRoleFilterEnabled") == null) {
                orderRoleFilterEnabled = false;
            } else {
                orderRoleFilterEnabled = (Boolean) request.getSession().getAttribute("orderRoleFilterEnabled");
            }
        }
        request.getSession().setAttribute("orderRoleFilterEnabled", orderRoleFilterEnabled);

        CurrentParams currentParams = new CurrentParams();
        currentParams.setCurrencyPair((CurrencyPair) request.getSession().getAttribute("currentCurrencyPair"));
        currentParams.setPeriod(((BackDealInterval) request.getSession().getAttribute("currentBackDealInterval")).getInterval());
        currentParams.setChartType(((ChartType) request.getSession().getAttribute("chartType")).getTypeName());
        currentParams.setShowAllPairs(((Boolean) request.getSession().getAttribute("showAllPairs")));
        currentParams.setOrderRoleFilterEnabled(((Boolean) request.getSession().getAttribute("orderRoleFilterEnabled")));
        return currentParams;
    }

    private CurrencyPair getPairFormSessionOrRequest(HttpServletRequest request, String currencyPairName, CurrencyPairType type) {
        CurrencyPair currencyPair = null;
        if (StringUtils.isEmpty(currencyPairName)) {
            if (request.getSession().getAttribute("currentCurrencyPair") != null) {
                currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
            }
        } else {
            List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs(type);
            if (!currencyPairs.isEmpty()) {
                currencyPair = currencyPairs
                        .stream()
                        .filter(e -> e.getName().equalsIgnoreCase(currencyPairName))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Unsupported pair"));
            }
        }
        return currencyPair;
    }

    private CurrencyPair resolveCurrentOrDefaultPairForType(CurrencyPair currencyPair, CurrencyPairType type) {
        if (currencyPair != null && type != CurrencyPairType.ALL && currencyPair.getPairType() != type) {
            currencyPair = null;
        }
        if (currencyPair == null) {
            switch (type) {
                case MAIN: {
                }
                case ALL: {
                    currencyPair = currencyService.getCurrencyPairByName("BTC/USD");
                    break;
                }
                case ICO: {
                    List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs(type);
                    if (currencyPairs.isEmpty()) {
                        throw new RuntimeException("no pairs for thios type");
                    } else {
                        currencyPair = currencyPairs.get(0);
                    }
                    break;
                }
            }
        }
        return currencyPair;
    }


    /**
     * Sets (init or reset) and returns table params for <b>tableId</b>:
     * - current limit
     *
     * @param tableId
     * @param limitValue is page size for pagination
     * @param request
     * @return object with values of params
     */
    @OnlineMethod
    @RequestMapping(value = "/dashboard/tableParams/{tableId}", method = RequestMethod.GET)
    public TableParams setTableParams(
            @PathVariable String tableId,
            @RequestParam(required = false) Integer limitValue,
            @RequestParam(required = false) OrderStatus orderStatusValue,
            HttpServletRequest request) {
        /**/
        String attributeName = tableId + "Params";
        TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
        if (tableParams == null) {
            tableParams = new TableParams();
            tableParams.setTableId(tableId);
        }
        /**/
        Integer limit;
        if (limitValue == null) {
            limit = tableParams.getPageSize();
            if (limit == null) {
                limit = TABLES_LIMIT_DEFAULT;
            }
        } else {
            limit = limitValue;
        }
        tableParams.setPageSize(limit);
        /**/
        request.getSession().setAttribute(attributeName, tableParams);
        return tableParams;
    }

    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns data with statistics for orders of current CurrencyPair to show above the graphics
     *
     * @param request
     * @return: data with statistics for orders of current CurrencyPair
     * @author ValkSam
     */
    @OnlineMethod
    @RequestMapping(value = "/dashboard/ordersForPairStatistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ExOrderStatisticsDto getNewCurrencyPairData(HttpServletRequest request) {
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        if (currencyPair == null) {
            return null;
        }
        BackDealInterval backDealInterval = (BackDealInterval) request.getSession().getAttribute("currentBackDealInterval");
        /**/
        ExOrderStatisticsDto exOrderStatisticsDto = orderService.getOrderStatistic(currencyPair, backDealInterval, localeResolver.resolveLocale(request));
        return exOrderStatisticsDto;
    }

    /**
     * returns map the data to create currency pairs menu
     * <market name, list<currencyPair name>>
     *
     * @return: list the data to create currency pairs menu
     * @author ValkSam
     */
    @RequestMapping(value = "/dashboard/createPairSelectorMenu", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<CurrencyPair>> getCurrencyPairNameList(@RequestParam(value = "pairs", defaultValue = "MAIN") String pairType, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        CurrencyPairType cpType = Preconditions.checkNotNull(CurrencyPairType.valueOf(pairType));
        List<CurrencyPair> list = currencyService.getAllCurrencyPairsInAlphabeticOrder(cpType);
        if (cpType == CurrencyPairType.ALL) {
            list.forEach(p -> {
                if (p.getPairType() == CurrencyPairType.ICO) {
                    p.setMarket(CurrencyPairType.ICO.name());
                }
            });
        }
        list.forEach(p -> {
            try {
                p.setMarketName(messageSource.getMessage("message.cp.".concat(p.getMarket()), null, locale));
            } catch (NoSuchMessageException e) {
                p.setMarketName(p.getMarket());
            }
        });
        return list
                .stream()
                .sorted(Comparator.comparing(CurrencyPair::getName))
                .collect(Collectors.groupingBy(CurrencyPair::getMarket));
    }


    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns list the data of accepted orders during last 24 hours
     *
     * @param refreshIfNeeded: - "true" if view ought to repainted if data in DB was changed only.
     *                         - "false" if data must repainted in any cases
     * @param scope:           "ALL" to retrieve all accepted orders. Other value or empty to retrieve "my orders" only
     * @param principal
     * @param request
     * @return list the data of accepted orders during last 24 hours
     * @author ValkSam
     */
    @OnlineMethod
    @RequestMapping(value = "/dashboard/acceptedOrderHistory/{scope}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderAcceptedHistoryDto> getOrderHistory(@RequestParam(required = false) Boolean refreshIfNeeded,
                                                         @PathVariable String scope,
                                                         Principal principal,
                                                         HttpServletRequest request) {
        String email = principal == null || "ALL".equals(scope.toUpperCase()) ? "" : principal.getName();
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        if (currencyPair == null) {
            return Collections.EMPTY_LIST;
        }
        String cacheKey = "acceptedOrderHistory" + email + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<OrderAcceptedHistoryDto> result = orderService.getOrderAcceptedForPeriod(cacheData, email, ORDER_HISTORY_INTERVAL, ORDER_HISTORY_LIMIT, currencyPair, localeResolver.resolveLocale(request));
        return result;
    }

    /**
     * Returns current commissions for creating and accepting orders
     *
     * @return current commissions for operation SELL and BUY
     * @author ValkSam
     */
    @RequestMapping(value = "/dashboard/orderCommissions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderCommissionsDto getOrderCommissions(HttpServletRequest request) {
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        if (currencyPair.getName().contains("EDR")) {
            return OrderCommissionsDto.zeroComissions();
        }
        OrderCommissionsDto result = orderService.getCommissionForOrder();
        return result;
    }

    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns list the data of SELL open orders
     *
     * @param refreshIfNeeded: - "true" if view ought to repainted if data in DB was changed only.
     *                         - "false" if data must repainted in any cases
     * @param principal
     * @param request
     * @return list the data of of SELL open orders
     * @author ValkSam
     */
    @OnlineMethod
    @RequestMapping(value = "/dashboard/sellOrders", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderListDto> getSellOrdersList(@RequestParam(required = false) Boolean refreshIfNeeded,
                                                Principal principal, HttpServletRequest request) {
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        if (currencyPair == null) {
            return Collections.EMPTY_LIST;
        }
        Boolean orderRoleFilterEnabled = (Boolean) request.getSession().getAttribute("orderRoleFilterEnabled");
        if (orderRoleFilterEnabled == null) {
            orderRoleFilterEnabled = false;
        }
        String cacheKey = "sellOrders" + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<OrderListDto> result = orderService.getAllSellOrders(cacheData, currencyPair, localeResolver.resolveLocale(request), orderRoleFilterEnabled);
        return result;
    }

    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns list the data of BUY open orders
     *
     * @param refreshIfNeeded: - "true" if view ought to repainted if data in DB was changed only.
     *                         - "false" if data must repainted in any cases
     * @param principal
     * @param request
     * @return list the data of of BUY open orders
     * @author ValkSam
     */
    @OnlineMethod
    @RequestMapping(value = "/dashboard/BuyOrders", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderListDto> getBuyOrdersList(@RequestParam(required = false) Boolean refreshIfNeeded,
                                               Principal principal, HttpServletRequest request) {
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        if (currencyPair == null) {
            return Collections.EMPTY_LIST;
        }
        Boolean orderRoleFilterEnabled = (Boolean) request.getSession().getAttribute("orderRoleFilterEnabled");

        if (orderRoleFilterEnabled == null) {
            orderRoleFilterEnabled = false;
        }
        String cacheKey = "BuyOrders" + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<OrderListDto> result = orderService.getAllBuyOrders(cacheData, currencyPair, localeResolver.resolveLocale(request), orderRoleFilterEnabled);
        return result;
    }

    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns list the data of user's wallet to show in page "Balance"
     *
     * @param refreshIfNeeded: - "true" if view ought to repainted if data in DB was changed only.
     *                         - "false" if data must repainted in any cases
     * @param principal
     * @param request
     * @return list the data of user's wallet
     * @author ValkSam
     */
    @OnlineMethod
    @RequestMapping(value = "/dashboard/myWalletsData", method = RequestMethod.GET)
    public List<MyWalletsDetailedDto> getMyWalletsData(@RequestParam(required = false) Boolean refreshIfNeeded,
                                                       Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        String cacheKey = "myWalletsData" + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<MyWalletsDetailedDto> result = walletService.getAllWalletsForUserDetailed(cacheData, email, Locale.ENGLISH);
        return result;
    }

    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns list the data of user's orders to show in pages "History" and "Orders"
     *
     * @param refreshIfNeeded: - "true" if view ought to repainted if data in DB was changed only.
     *                         - "false" if data must repainted in any cases
     * @param tableId          determines concrete table o pages "History" and "Orders" to show data
     * @param status           determines status the order
     * @param page,            direction - used for pgination. Details see in class TableParams
     * @param principal
     * @param request
     * @return list the data of user's orders
     * @author ValkSam
     */
    @OnlineMethod
    @RequestMapping(value = "/dashboard/myOrdersData/{tableId}", method = RequestMethod.GET)
    public List<OrderWideListDto> getMyOrdersData(
            @RequestParam(required = false) Boolean refreshIfNeeded,
            @PathVariable("tableId") String tableId,
            @RequestParam(required = false) OperationType type,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) PagingDirection direction,
            @RequestParam(value = "baseType", defaultValue = "LIMIT") OrderBaseType orderBaseType,
            Principal principal,
            HttpServletRequest request) {
        if (principal == null) {
            return null;
        }
        LOGGER.debug("Scope: " + scope);
        String email = principal.getName();
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        if (currencyPair == null) {
            return Collections.EMPTY_LIST;
        }
        Boolean showAllPairs = (Boolean) request.getSession().getAttribute("showAllPairs");
        /**/
        String attributeName = tableId + "Params";
        TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
        Assert.requireNonNull(tableParams, "The parameters are not populated for the " + tableId);
        tableParams.setOffsetAndLimitForSql(page, direction);
        /**/
        String cacheKey = "myOrdersData" + tableId + status + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<OrderWideListDto> result;
        switch (orderBaseType) {
            case STOP_LIMIT: {
                result = stopOrderService.getMyOrdersWithState(cacheData, email,
                        showAllPairs == null || !showAllPairs ? currencyPair : null,
                        status, type, scope, tableParams.getOffset(), tableParams.getLimit(), localeResolver.resolveLocale(request));
                break;
            }
            default: {
                result = orderService.getMyOrdersWithState(cacheData, email,
                        showAllPairs == null || !showAllPairs ? currencyPair : null,
                        status, type, scope, tableParams.getOffset(), tableParams.getLimit(), localeResolver.resolveLocale(request));
            }
        }
        if (!result.isEmpty()) {
            result.get(0).setPage(tableParams.getPageNumber());
        }
        tableParams.updateEofState(result);
        return result;
    }

    @RequestMapping(value = "/dashboard/myOrdersData", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Future<List<OrderWideListDto>> getMyOrders(@RequestParam("tableType") String tableType,
                                                      @RequestParam(required = false) String scope,
                                                      Principal principal, HttpServletRequest request) {

        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        Boolean showAllPairs = (Boolean) request.getSession().getAttribute("showAllPairs");
        String email = principal != null ? principal.getName() : "";
        return CompletableFuture.supplyAsync(() -> getOrderWideListDtos(tableType, showAllPairs == null || !showAllPairs ? currencyPair : null, scope, email, localeResolver.resolveLocale(request)));
    }

    private List<OrderWideListDto> getOrderWideListDtos(String tableType, CurrencyPair currencyPair, String scope, String email, Locale locale) {
        List<OrderWideListDto> result = new ArrayList<>();
        switch (tableType) {
            case "CLOSED":
                List<OrderWideListDto> ordersSellClosed = orderService.getMyOrdersWithState(email, currencyPair, OrderStatus.CLOSED, null, scope, 0, -1, locale);
                result = ordersSellClosed;
                break;
            case "CANCELLED":
                List<OrderWideListDto> ordersSellCancelled = orderService.getMyOrdersWithState(email, currencyPair, OrderStatus.CANCELLED, null, scope, 0, -1, locale);
                result = ordersSellCancelled;
                break;
            case "OPENED":
                List<OrderWideListDto> ordersSellOpened = orderService.getMyOrdersWithState(email, currencyPair, OrderStatus.OPENED, null, scope, 0, -1, locale);
                result = ordersSellOpened;
                break;
        }
        return result;
    }

    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns list the data of user's orders to show in pages "History"
     *
     * @param refreshIfNeeded: - "true" if view ought to repainted if data in DB was changed only.
     *                         - "false" if data must repainted in any cases
     * @param tableId          determines table on pages "History" to show data
     * @param page,            direction - used for pgination. Details see in class TableParams
     * @param principal
     * @param request
     * @return list the data of user's orders
     */
    @OnlineMethod
    @RequestMapping(value = "/dashboard/myReferralData/{tableId}", method = RequestMethod.GET)
    public List<MyReferralDetailedDto> getMyReferralData(
            @RequestParam(required = false) Boolean refreshIfNeeded,
            @PathVariable("tableId") String tableId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) PagingDirection direction,
            Principal principal,
            HttpServletRequest request) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        /**/
        String attributeName = tableId + "Params";
        TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
        Assert.requireNonNull(tableParams, "The parameters are not populated for the " + tableId);
        tableParams.setOffsetAndLimitForSql(page, direction);
        /**/
        String cacheKey = "myReferralData" + tableId + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<MyReferralDetailedDto> result = referralService.findAllMyReferral(cacheData, email, tableParams.getOffset(), tableParams.getLimit(), localeResolver.resolveLocale(request));
        if (!result.isEmpty()) {
            result.get(0).setPage(tableParams.getPageNumber());
        }
        Locale locale = localeResolver.resolveLocale(request);

        tableParams.updateEofState(result);
        return result;
    }

    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns list the data of user's wallet statement to show in pages "Balance" button "History"
     *
     * @param refreshIfNeeded: - "true" if view ought to repainted if data in DB was changed only.
     *                         - "false" if data must repainted in any cases
     * @param tableId          determines table to show data
     * @param walletId         is id of user's wallet
     * @param page,            direction - used for pgination. Details see in class TableParams
     * @param request
     * @return list the data of user's wallet statement
     */
    @OnlineMethod
    @RequestMapping(value = "/dashboard/myStatementData/{tableId}/{walletId}", method = RequestMethod.GET)
    public List<AccountStatementDto> getMyAccountStatementData(
            @RequestParam(required = false) Boolean refreshIfNeeded,
            @PathVariable("tableId") String tableId,
            @PathVariable("walletId") String walletId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) PagingDirection direction,
            HttpServletRequest request) {
        /**/
        String attributeName = tableId + "Params";
        TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
        Assert.requireNonNull(tableParams, "The parameters are not populated for the " + tableId);
        tableParams.setOffsetAndLimitForSql(page, direction);
        /**/
        String cacheKey = "myAccountStatement" + tableId + walletId + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<AccountStatementDto> result = transactionService.getAccountStatement(cacheData, Integer.valueOf(walletId), tableParams.getOffset(), tableParams.getLimit(), localeResolver.resolveLocale(request));
        if (!result.isEmpty()) {
            result.get(0).setPage(tableParams.getPageNumber());
        }
        tableParams.updateEofState(result);
        return result;
    }

    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns list the data of user's input/output orders to show in pages "History input/output"
     *
     * @param refreshIfNeeded: - "true" if view ought to repainted if data in DB was changed only.
     *                         - "false" if data must repainted in any cases
     * @param tableId          determines table on pages "History" to show data
     * @param page,            direction - used for pgination. Details see in class TableParams
     * @param principal
     * @param request
     * @return list the data of user's orders
     */
    @OnlineMethod
    @RequestMapping(value = "/dashboard/myInputoutputData/{tableId}", method = RequestMethod.GET)
    public List<MyInputOutputHistoryDto> getMyInputoutputData(
            @RequestParam(required = false) Boolean refreshIfNeeded,
            @PathVariable("tableId") String tableId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) PagingDirection direction,
            Principal principal,
            HttpServletRequest request) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        /**/
        String attributeName = tableId + "Params";
        TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
        Assert.requireNonNull(tableParams, "The parameters are not populated for the " + tableId);
        tableParams.setOffsetAndLimitForSql(page, direction);
        /**/
        String cacheKey = "myInputoutputData" + tableId + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<MyInputOutputHistoryDto> result = inputOutputService.getMyInputOutputHistory(cacheData, email, tableParams.getOffset(), tableParams.getLimit(), localeResolver.resolveLocale(request));
        if (!result.isEmpty()) {
            result.get(0).setPage(tableParams.getPageNumber());
        }
        tableParams.updateEofState(result);
        return result;
    }

    /**
     * it's one of onlines methods, which retrieves data from DB for repaint on view in browser page
     * returns list the news of current language to show in right sider
     *
     * @param refreshIfNeeded: - "true" if view ought to repainted if data in DB was changed only.
     *                         - "false" if data must repainted in any cases
     * @param tableId          determines table to show data
     * @param page,            direction - used for pgination. Details see in class TableParams
     * @param request
     * @return list the news
     */
    @OnlineMethod
    @RequestMapping(value = "/dashboard/news/{tableId}", method = RequestMethod.GET)
    public List<NewsDto> getNewsList(
            @PathVariable("tableId") String tableId,
            @RequestParam(required = false) Boolean refreshIfNeeded,
            @RequestParam(required = false) Integer page,
            HttpServletRequest request) {
        String attributeName = tableId + "Params";
        TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
        Assert.requireNonNull(tableParams, "The parameters are not populated for the " + tableId);
        Integer offset = page == null || tableParams.getPageSize() == -1 ? 0 : (page - 1) * tableParams.getPageSize();
        String cacheKey = "newsList" + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<NewsDto> result = newsService.getNewsBriefList(cacheData, offset, tableParams.getPageSize(), localeResolver.resolveLocale(request));
        return result;
    }

    @OnlineMethod
    @RequestMapping(value = "/dashboard/newsTwitter", method = RequestMethod.GET)
    public List<NewsDto> getTwitterNewsList(@RequestParam(value = "amount", defaultValue = "50") int amount) {
        return newsService.getTwitterNews(amount);
    }

    @OnlineMethod
    @RequestMapping(value = "/dashboard/notifications/{tableId}", method = GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<NotificationDto> findNotificationsByUser(@PathVariable("tableId") String tableId,
                                                         @RequestParam(required = false) Boolean refreshIfNeeded,
                                                         @RequestParam(required = false) Integer page,
                                                         Principal principal,
                                                         HttpServletRequest request) {
    /*long before = System.currentTimeMillis();
    String attributeName = tableId + "Params";
    TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
    Assert.requireNonNull(tableParams, "The parameters are not populated for the " + tableId);
    Integer offset = page == null || tableParams.getPageSize() == -1 ? 0 : (page - 1) * tableParams.getPageSize();
    String cacheKey = "notifications" + request.getHeader("windowid");
    refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
    CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
    List<NotificationDto> result = notificationService.findByUser(principal.getName(), cacheData, offset, tableParams.getPageSize());
    long after = System.currentTimeMillis();
    LOGGER.debug("completed... ms: " + (after - before));
    return result;*/
        return Collections.emptyList();
    }

    @RequestMapping(value = "/dashboard/myReferralStructure")
    public RefsListContainer getMyReferralData(
            @RequestParam("action") String action,
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "onPage", defaultValue = "20") int onPage,
            @RequestParam(value = "page", defaultValue = "1") int page,
            RefFilterData refFilterData,
            Principal principal) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        /**/
        RefsListContainer container = referralService.getRefsContainerForReq(action, userId,
                userService.getIdByEmail(email), onPage, page, refFilterData);
        hideEmails(container.getReferralInfoDtos());
        return container;
    }

    @ResponseBody
    @RequestMapping(value = "/dashboard/getAllCurrencies")
    public List getAllCurrencies() {
        return currencyService.findAllCurrenciesWithHidden();
    }

    private void hideEmails(List<ReferralInfoDto> referralInfoDtos) {

        for (ReferralInfoDto dto : referralInfoDtos) {
            String email = dto.getEmail();
            StringBuilder buf = new StringBuilder(email);
            int start = 2;
            int end = email.length() - 5;
            for (int i = start; i < end; i++) {
                buf.setCharAt(i, '*');
            }
            dto.setEmail(buf.toString());
        }
    }

}
