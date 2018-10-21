package me.exrates.ngcontroller;

import me.exrates.dao.OrderDao;
import me.exrates.dao.StopOrderDao;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.StockExchangeStats;
import me.exrates.model.StopOrder;
import me.exrates.model.User;
import me.exrates.model.dto.CoinmarketApiDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.ngcontroller.mobel.InputCreateOrderDto;
import me.exrates.ngcontroller.mobel.ResponseInfoCurrencyPairDto;
import me.exrates.ngcontroller.service.NgOrderService;
import me.exrates.ngcontroller.util.PagedResult;
import me.exrates.service.CurrencyService;
import me.exrates.service.DashboardService;
import me.exrates.service.OrderService;
import me.exrates.service.StockExchangeService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.cache.ExchangeRatesHolderImpl;
import me.exrates.service.exception.api.OrderParamsWrongException;
import me.exrates.service.stopOrder.StopOrderService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping(value = "/info/private/v2/dashboard/",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class NgDashboardController {

    private static final Logger logger = LogManager.getLogger(NgDashboardController.class);


    private final DashboardService dashboardService;
    private final CurrencyService currencyService;
    private final OrderService orderService;
    private final UserService userService;
    private final LocaleResolver localeResolver;
    private final StopOrderService stopOrderService;
    private final NgOrderService ngOrderService;
    private final WalletService walletService;
    private final StopOrderDao stopOrderDao;
    private final OrderDao orderDao;
    private final StockExchangeService stockExchangeService;
    private final ExchangeRatesHolderImpl exchangeRatesHolder;

    @Autowired
    public NgDashboardController(DashboardService dashboardService,
                                 CurrencyService currencyService,
                                 OrderService orderService,
                                 UserService userService,
                                 LocaleResolver localeResolver,
                                 StopOrderService stopOrderService,
                                 NgOrderService ngOrderService,
                                 WalletService walletService,
                                 StopOrderDao stopOrderDao,
                                 OrderDao orderDao,
                                 StockExchangeService stockExchangeService,
                                 ExchangeRatesHolderImpl exchangeRatesHolder) {
        this.dashboardService = dashboardService;
        this.currencyService = currencyService;
        this.orderService = orderService;
        this.userService = userService;
        this.localeResolver = localeResolver;
        this.stopOrderService = stopOrderService;
        this.ngOrderService = ngOrderService;
        this.walletService = walletService;
        this.stopOrderDao = stopOrderDao;
        this.orderDao = orderDao;
        this.stockExchangeService = stockExchangeService;
        this.exchangeRatesHolder = exchangeRatesHolder;
    }

    @PostMapping("/order")
    public ResponseEntity createOrder(@RequestBody @Valid InputCreateOrderDto inputOrder) {

        OrderCreateDto prepareNewOrder = ngOrderService.prepareOrder(inputOrder);

        String result;
        switch (prepareNewOrder.getOrderBaseType()) {
            case STOP_LIMIT: {
                result = stopOrderService.create(prepareNewOrder, OrderActionEnum.CREATE, null);
                break;
            }
            default: {
                result = orderService.createOrder(prepareNewOrder, OrderActionEnum.CREATE, null);
            }
        }

        return StringUtils.isEmpty(result) ? new ResponseEntity<>(HttpStatus.BAD_REQUEST) :
                new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/order/{id}")
    public ResponseEntity deleteOrderById(@PathVariable int id) {
        Integer result = (Integer) orderService.deleteOrderByAdmin(id);
        return result == 1 ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/order")
    public ResponseEntity updateOrder(@RequestBody @Valid InputCreateOrderDto inputOrder) {

        if (StringUtils.isEmpty(inputOrder.getOrderId()) || !StringUtils.isNumeric(inputOrder.getOrderId())) {
            throw new OrderParamsWrongException();
        }

        String userName = userService.getUserEmailFromSecurityContext();
        User user = userService.findByEmail(userName);

        OrderBaseType baseType = OrderBaseType.convert(inputOrder.getBaseType());
        boolean result;
        switch (baseType) {
            case STOP_LIMIT:
                result = processUpdateStopOrder(user, inputOrder);
                break;
            case LIMIT:
                result = processUpdateOrder(user, inputOrder);
                break;
            case ICO:
                throw new UnsupportedOperationException();
            default:
                throw new RuntimeException();
        }

        return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @GetMapping("/balance/{currency}")
    public ResponseEntity<BigDecimal> getBalanceByCurrency(@PathVariable("currency") String currencyName) {

        String userName = userService.getUserEmailFromSecurityContext();
        User user = userService.findByEmail(userName);
        Currency currency = currencyService.findByName(currencyName);
        BigDecimal balanceByCurrency;
        try {
            balanceByCurrency = dashboardService.getBalanceByCurrency(user.getId(), currency.getId());
        } catch (Exception e) {
            logger.error("Error while get balance by currency user {}, currency {} , e {}",
                    user.getEmail(), currency.getName(), e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(balanceByCurrency, HttpStatus.OK);
    }


    @GetMapping("/commission/{orderType}/{currencyPairId}")
    public ResponseEntity<WalletsAndCommissionsForOrderCreationDto> getCommission(@PathVariable OperationType orderType,
                                                                                  @PathVariable int currencyPairId) {

        String email = userService.getUserEmailFromSecurityContext();
        CurrencyPair activeCurrencyPair = currencyService.findCurrencyPairById(currencyPairId);
        if (activeCurrencyPair == null) {
            throw new RuntimeException("Wrong currency pair");
        }

        Currency spendCurrency = null;

        if (orderType == OperationType.SELL) {
            spendCurrency = activeCurrencyPair.getCurrency1();
        } else if (orderType == OperationType.BUY) {
            spendCurrency = activeCurrencyPair.getCurrency2();
        }

        return new ResponseEntity<>(orderService.getWalletAndCommission(email, spendCurrency, orderType), HttpStatus.OK);
    }

    /**
     * Returns a list of user open orders
     *
     * @param currencyPairId - currency pair id must be valid
     * @param page           - requested page, not required,  default 1
     * @param limit          - defines quantity rows per page, not required,  default 14
     * @param sortByCreated  - enables ASC sort by created date, not required,  default DESC
     * @param scope          - defines requested order type, values ["" - only created, "ACCEPTED" - only accepted,
     *                       "ALL" - both], not required,  default ""
     * @param request        - HttpServletRequest
     * @return - Pageable list of open orders with meta info about total orders' count
     * @throws - 403 bad request
     */
    @GetMapping("/open_orders/{currencyPairId}")
    public ResponseEntity<PagedResult<OrderWideListDto>> getOpenOrders(
            @PathVariable("currencyPairId") Integer currencyPairId,
            @RequestParam(required = false, name = "page", defaultValue = "1") Integer page,
            @RequestParam(required = false, name = "limit", defaultValue = "14") Integer limit,
            @RequestParam(required = false, name = "sortByCreated", defaultValue = "DESC") String sortByCreated,
            @RequestParam(required = false, name = "scope") String scope,
            HttpServletRequest request) {
        int userId = userService.getIdByEmail(getPrincipalEmail());
        CurrencyPair currencyPair = currencyService.findCurrencyPairById(currencyPairId);
        Locale locale = localeResolver.resolveLocale(request);
        if (currencyPair == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        int offset = page > 1 ? page * limit : 0;
        Map<String, String> sortedColumns = sortByCreated.equals("DESC")
                ? Collections.emptyMap()
                : Collections.singletonMap("date_creation", sortByCreated);

        try {
            Map<Integer, List<OrderWideListDto>> ordersMap =
                    this.orderService.getMyOrdersWithStateMap(userId, currencyPair, OrderStatus.OPENED, scope, offset,
                            limit, locale, sortedColumns);
            PagedResult<OrderWideListDto> pagedResult = new PagedResult<>();
            pagedResult.setCount(ordersMap.keySet().iterator().next());
            pagedResult.setItems(ordersMap.values().stream().findFirst().orElse(Collections.emptyList()));

            return ResponseEntity.ok(pagedResult);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean processUpdateStopOrder(User user, InputCreateOrderDto inputOrder) {
        boolean result = false;
        int orderId = Integer.parseInt(inputOrder.getOrderId());
        OrderCreateDto stopOrder = stopOrderService.getOrderById(orderId, true);

        if (stopOrder.getCurrencyPair().getId() != inputOrder.getCurrencyPairId()) {
            throw new OrderParamsWrongException("Not support change currency pair");
        }

        if (stopOrder.getUserId() != user.getId()) {
            throw new OrderParamsWrongException();
        }
        if (stopOrder.getStatus() != OrderStatus.OPENED) {
            throw new RuntimeException("Order status is not open");
        }

        OrderCreateDto prepareOrder = ngOrderService.prepareOrder(inputOrder);

        int outWalletId;
        BigDecimal outAmount;

        if (prepareOrder.getOperationType() == OperationType.BUY) {
            outWalletId = prepareOrder.getWalletIdCurrencyConvert();
            outAmount = prepareOrder.getTotalWithComission();
        } else {
            outWalletId = prepareOrder.getWalletIdCurrencyBase();
            outAmount = prepareOrder.getAmount();
        }

        if (walletService.ifEnoughMoney(outWalletId, outAmount)) {
            ExOrder exOrder = new ExOrder(prepareOrder);
            OrderBaseType orderBaseType = prepareOrder.getOrderBaseType();
            if (orderBaseType == null) {
                CurrencyPairType type = exOrder.getCurrencyPair().getPairType();
                orderBaseType = type == CurrencyPairType.ICO ? OrderBaseType.ICO : OrderBaseType.LIMIT;
                exOrder.setOrderBaseType(orderBaseType);
            }
            StopOrder order = new StopOrder(exOrder);
            result = stopOrderDao.updateOrder(orderId, order);
        }
        return result;
    }

    private boolean processUpdateOrder(User user, InputCreateOrderDto inputOrder) {

        boolean result = false;

        int orderId = Integer.parseInt(inputOrder.getOrderId());
        ExOrder order = orderService.getOrderById(orderId);


        if (order.getCurrencyPair().getId() != inputOrder.getCurrencyPairId()) {
            throw new OrderParamsWrongException("Not support change currency pair");
        }

        if (order.getUserId() != user.getId()) {
            throw new OrderParamsWrongException();
        }
        if (order.getStatus() != OrderStatus.OPENED) {
            throw new RuntimeException("Order status is not open");
        }

        OrderCreateDto prepareOrder = ngOrderService.prepareOrder(inputOrder);

        int outWalletId;
        BigDecimal outAmount;
        if (prepareOrder.getOperationType() == OperationType.BUY) {
            outWalletId = prepareOrder.getWalletIdCurrencyConvert();
            outAmount = prepareOrder.getTotalWithComission();
        } else {
            outWalletId = prepareOrder.getWalletIdCurrencyBase();
            outAmount = prepareOrder.getAmount();
        }

        if (walletService.ifEnoughMoney(outWalletId, outAmount)) {
            ExOrder exOrder = new ExOrder(prepareOrder);
            OrderBaseType orderBaseType = prepareOrder.getOrderBaseType();
            if (orderBaseType == null) {
                CurrencyPairType type = exOrder.getCurrencyPair().getPairType();
                orderBaseType = type == CurrencyPairType.ICO ? OrderBaseType.ICO : OrderBaseType.LIMIT;
                exOrder.setOrderBaseType(orderBaseType);
            }
            result = orderDao.updateOrder(orderId, exOrder);
        }
        return result;
    }


    @GetMapping("/info/{currencyPairId}")
    public ResponseEntity getCurrencyPairInfo(@PathVariable int currencyPairId) {
        ResponseInfoCurrencyPairDto result = new ResponseInfoCurrencyPairDto();
        String userName = userService.getUserEmailFromSecurityContext();
        User user = userService.findByEmail(userName);

        CurrencyPair currencyPair = currencyService.findCurrencyPairById(currencyPairId);
        BigDecimal balanceByCurrency1 =
                dashboardService.getBalanceByCurrency(user.getId(), currencyPair.getCurrency1().getId());

        List<ExOrderStatisticsShortByPairsDto> currencyRate =
                orderService.getStatForSomeCurrencies(Collections.singletonList(currencyPairId));
        result.setBalanceByCurrency1(balanceByCurrency1);

        BigDecimal balanceByCurrency2 = new BigDecimal(0);
        if (!currencyRate.isEmpty()) {
            result.setCurrencyRate(currencyRate.get(0).getLastOrderRate());
            result.setPercentChange(currencyRate.get(0).getPercentChange());
            result.setLastCurrencyRate(currencyRate.get(0).getPredLastOrderRate());
            BigDecimal rate = new BigDecimal(currencyRate.get(0).getLastOrderRate());
            balanceByCurrency2 = balanceByCurrency1.multiply(rate);
        }
        result.setBalanceByCurrency2(balanceByCurrency2);

        //get daily statistic by 2 ways ---  what way is correct ???

        //1 method
        Date now = new Date();
        Date minusDay = DateUtils.addDays(now, -1);

        List<StockExchangeStats> statistics =
                stockExchangeService.getStockExchangeStatisticsByPeriod(currencyPairId, minusDay, now);

        //2 method
        List<CoinmarketApiDto> coinmarketDataForActivePairs =
                orderService.getDailyCoinmarketData(currencyPair.getName());

        result.setDailyStatistic(coinmarketDataForActivePairs);
        result.setStatistic(statistics);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
