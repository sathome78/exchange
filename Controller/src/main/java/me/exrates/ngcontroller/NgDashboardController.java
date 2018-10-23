package me.exrates.ngcontroller;

import me.exrates.controller.exception.ErrorInfo;
import me.exrates.dao.OrderDao;
import me.exrates.dao.StopOrderDao;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.StockExchangeStats;
import me.exrates.model.StopOrder;
import me.exrates.model.User;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.ChartTimeFramesEnum;
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
import me.exrates.service.exception.api.OrderParamsWrongException;
import me.exrates.service.stopOrder.StopOrderService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.QueryParam;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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
                                 StockExchangeService stockExchangeService) {
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
        HashMap<String, String> resultMap = new HashMap<>();

        if (StringUtils.isEmpty(result)) {
            resultMap.put("message", "success");
            return new ResponseEntity<>(resultMap, HttpStatus.CREATED);
        } else {
            resultMap.put("message", "fail");
            return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
        }

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
                throw new NgDashboardException("Not supported type - ICO");
            default:
                throw new NgDashboardException("Unknown type - " + baseType);
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
     * Returns a list of user orders path variables status defines which order's status to be retrieved
     * http method: get
     * http url: http://exrates_domain.me/info/private/v2/dashboard/orders/{status}
     * <p>
     * returns:
     * {
     * "count": number, -- entire quantity of items in storage
     * "items": [
     * {
     * "id": number,
     * "userId": number,
     * "operationType": string,
     * "operationTypeEnum": string, -- values: INPUT, OUTPUT, SELL, BUY, WALLET_INNER_TRANSFER, REFERRAL, STORNO, MANUAL, USER_TRANSFER
     * "stopRate": string, -- for stop orders
     * "exExchangeRate": string,
     * "amountBase": string,
     * "amountConvert": string,
     * "comissionId": number,
     * "commissionFixedAmount": string,
     * "amountWithCommission": string,
     * "userAcceptorId": number,
     * "dateCreation": Date,
     * "dateAcception": Date,
     * "status": string,  -- values INPROCESS, OPENED, CLOSED, CANCELLED, DELETED, DRAFT, SPLIT_CLOSED
     * "dateStatusModification": Date,
     * "commissionAmountForAcceptor": string,
     * "amountWithCommissionForAcceptor": string,
     * "currencyPairId": number,
     * "currencyPairName": string,
     * "statusString": string,
     * "orderBaseType": string  -- values: LIMIT, STOP_LIMIT, ICO
     * },
     * ...
     * ]
     * }
     *
     * @param status         - user’s order status
     * @param currencyPairId - single currency pair, , not required,  default 0, when 0 then all currency pair are queried
     * @param page           - requested page, not required,  default 1
     * @param limit          - defines quantity rows per page, not required,  default 14
     * @param sortByCreated  - enables ASC sort by created date, not required,  default DESC
     * @param scope          - defines requested order type, values ["" - only created, "ACCEPTED" - only accepted,
     *                       "ALL" - both], not required,  default "" - created by user
     * @param request        - HttpServletRequest, used by backend to resolve locale
     * @return - Pageable list of defined orders with meta info about total orders' count
     * @throws - 403 bad request
     */
    @GetMapping("/orders/{status}")
    public ResponseEntity<PagedResult<OrderWideListDto>> getOpenOrders(
            @PathVariable("status") String status,
            @RequestParam(required = false, name = "currencyPairId", defaultValue = "0") Integer currencyPairId,
            @RequestParam(required = false, name = "page", defaultValue = "1") Integer page,
            @RequestParam(required = false, name = "limit", defaultValue = "14") Integer limit,
            @RequestParam(required = false, name = "sortByCreated", defaultValue = "DESC") String sortByCreated,
            @RequestParam(required = false, name = "scope") String scope,
            HttpServletRequest request) {
        int userId = userService.getIdByEmail(getPrincipalEmail());
        OrderStatus orderStatus = OrderStatus.valueOf(status);
        CurrencyPair currencyPair = currencyPairId > 0
                ? currencyService.findCurrencyPairById(currencyPairId)
                : null;
        Locale locale = localeResolver.resolveLocale(request);
        int offset = page > 1 ? page * limit : 0;
        Map<String, String> sortedColumns = sortByCreated.equals("DESC")
                ? Collections.emptyMap()
                : Collections.singletonMap("date_creation", sortByCreated);
        try {
            Map<Integer, List<OrderWideListDto>> ordersMap =
                    this.orderService.getMyOrdersWithStateMap(userId, currencyPair, orderStatus, scope, offset,
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

        if (stopOrder == null) {
            throw new NgDashboardException("Order is not exist");
        }

        OperationType operationType = OperationType.valueOf(inputOrder.getOrderType());

        if (operationType != stopOrder.getOperationType()) {
            throw new NgDashboardException("Wrong operationType - " + operationType);
        }

        if (stopOrder.getCurrencyPair().getId() != inputOrder.getCurrencyPairId()) {
            throw new NgDashboardException("Not support change currency pair");
        }

        if (stopOrder.getUserId() != user.getId()) {
            throw new NgDashboardException("Order was created by another user");
        }
        if (stopOrder.getStatus() != OrderStatus.OPENED) {
            throw new NgDashboardException("Order status is not open");
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
        if (order == null) {
            throw new NgDashboardException("Order is not exist");
        }
        OperationType operationType = OperationType.valueOf(inputOrder.getOrderType());

        if (operationType != order.getOperationType()) {
            throw new NgDashboardException("Wrong operationType - " + operationType);
        }

        if (order.getCurrencyPair().getId() != inputOrder.getCurrencyPairId()) {
            throw new NgDashboardException("Not support change currency pair");
        }

        if (order.getUserId() != user.getId()) {
            throw new NgDashboardException("Order was created by another user");
        }
        if (order.getStatus() != OrderStatus.OPENED) {
            throw new NgDashboardException("Order status is not open");
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

            BigDecimal rateNow = new BigDecimal(currencyRate.get(0).getLastOrderRate());
            BigDecimal rateYesterday = new BigDecimal(currencyRate.get(0).getPredLastOrderRate());
            BigDecimal subtract = rateNow.subtract(rateYesterday);
            result.setChangedValue(String.valueOf(subtract.intValue()));
            BigDecimal rate = new BigDecimal(currencyRate.get(0).getLastOrderRate());
            balanceByCurrency2 = balanceByCurrency1.multiply(rate);
        }
        result.setBalanceByCurrency2(balanceByCurrency2);

        //get daily statistic by 2 ways ---  what way is correct ???

        //1 method
        LocalDateTime fromLocalDate = LocalDateTime.now().minusDays(1).withHour(1);
        LocalDateTime toLocalDate = LocalDateTime.now().minusDays(1).withHour(23);
        Date from = Date.from(fromLocalDate.atZone(ZoneId.systemDefault()).toInstant());
        Date to = Date.from(toLocalDate.atZone(ZoneId.systemDefault()).toInstant());

        List<StockExchangeStats> statistics =
                stockExchangeService.getStockExchangeStatisticsByPeriod(currencyPairId, from, to);

        //set rateHigh
        statistics.stream()
                .map(StockExchangeStats::getPriceHigh)
                .max(Comparator.naturalOrder())
                .ifPresent(high -> result.setRateHigh(high.toString()));

        //set rateLow
        statistics.stream()
                .map(StockExchangeStats::getPriceLow)
                .max(Comparator.reverseOrder())
                .ifPresent(low -> result.setRateLow(low.toString()));

        //set volume24h
        statistics.stream()
                .map(StockExchangeStats::getVolume)
                .max(Comparator.naturalOrder())
                .ifPresent(volume -> result.setVolume24h(volume.toString()));

//        //2 method
//        List<CoinmarketApiDto> coinmarketDataForActivePairs =
//                orderService.getDailyCoinmarketData(currencyPair.getName());
//
//        result.setDailyStatistic(coinmarketDataForActivePairs);
        result.setStatistic(statistics);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping("/history")
    public ResponseEntity getCandleChartHistoryData(
            @QueryParam("symbol") String symbol,
            @QueryParam("to") Long to,
            @QueryParam("from") Long from,
            @QueryParam("resolution") String resolution) {

        CurrencyPair currencyPair = currencyService.getCurrencyPairByName(symbol);
        List<CandleDto> result = new ArrayList<>();
        if (currencyPair == null) {
            HashMap<String, Object> errors = new HashMap<>();
            errors.putAll(filterDataPeriod(result, from, to, resolution));
            errors.put("s", "error");
            errors.put("errmsg", "can not find currencyPair");
            return new ResponseEntity(errors, HttpStatus.NOT_FOUND);
        }

        String rsolutionForChartTime = (resolution.equals("W") || resolution.equals("M")) ? "D" : resolution;
        result = orderService.getCachedDataForCandle(currencyPair,
                ChartTimeFramesEnum.ofResolution(rsolutionForChartTime).getTimeFrame())
                .stream().map(CandleDto::new).collect(Collectors.toList());
        return new ResponseEntity(filterDataPeriod(result, from, to, resolution), HttpStatus.OK);
    }

    private Map<String, Object> filterDataPeriod(List<CandleDto> data, long fromSeconds, long toSeconds, String resolution) {
        List<CandleDto> filteredData = new ArrayList<>(data);
        HashMap<String, Object> filterDataResponse = new HashMap<>();
        if (filteredData.isEmpty()) {
            filterDataResponse.put("s", "ok");
            getData(filterDataResponse, filteredData, resolution);
            return filterDataResponse;
        }

        if ((filteredData.get(data.size() - 1).getTime() / 1000) < fromSeconds) {
            filterDataResponse.put("s", "no_data");
            filterDataResponse.put("nextTime", filteredData.get(data.size() - 1).getTime() / 1000);
            return filterDataResponse;

        }

        int fromIndex = -1;
        int toIndex = -1;

        for (int i = 0; i < filteredData.size(); i++) {
            long time = filteredData.get(i).getTime() / 1000;
            if (fromIndex == -1 && time >= fromSeconds) {
                fromIndex = i;
            }
            if (toIndex == -1 && time >= toSeconds) {
                toIndex = time > toSeconds ? i - 1 : i;
            }
            if (fromIndex != -1 && toIndex != -1) {
                break;
            }
        }

        fromIndex = fromIndex > 0 ? fromIndex : 0;
        toIndex = toIndex > 0 ? toIndex + 1 : filteredData.size();


        toIndex = Math.min(fromIndex + 1000, toIndex); // do not send more than 1000 bars for server capacity reasons

        String s = "ok";

        if (toSeconds < filteredData.get(0).getTime() / 1000) {
            s = "no_data";
        }
        filterDataResponse.put("s", s);

        toIndex = Math.min(fromIndex + 1000, toIndex);

        if (fromIndex > toIndex) {
            filterDataResponse.put("s", "no_data");
            filterDataResponse.put("nextTime", filteredData.get(data.size() - 1).getTime() / 1000);
            return filterDataResponse;
        }

        filteredData = filteredData.subList(fromIndex, toIndex);
        getData(filterDataResponse, filteredData, resolution);
        return filterDataResponse;

    }

    private void getData(HashMap<String, Object> response, List<CandleDto> result, String resolution) {
        List<Long> t = new ArrayList<>();
        List<BigDecimal> o = new ArrayList<>();
        List<BigDecimal> h = new ArrayList<>();
        List<BigDecimal> l = new ArrayList<>();
        List<BigDecimal> c = new ArrayList<>();
        List<BigDecimal> v = new ArrayList<>();

        LocalDateTime first = LocalDateTime.ofEpochSecond((result.get(0).getTime() / 1000), 0, ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.DAYS);
        t.add(first.toEpochSecond(ZoneOffset.UTC));
        o.add(BigDecimal.ZERO);
        h.add(BigDecimal.ZERO);
        l.add(BigDecimal.ZERO);
        c.add(BigDecimal.ZERO);
        v.add(BigDecimal.ZERO);

        for (CandleDto r : result) {
            LocalDateTime now = LocalDateTime.ofEpochSecond((r.getTime() / 1000), 0, ZoneOffset.UTC)
                    .truncatedTo(ChronoUnit.MINUTES);
            LocalDateTime actualDateTime;
            long currentMinutesOfHour = now.getLong(ChronoField.MINUTE_OF_HOUR);
            long currentHourOfDay = now.getLong(ChronoField.HOUR_OF_DAY);

            switch (resolution) {
                case "30":
                    long minutes = Math.abs(currentMinutesOfHour - 30);
                    actualDateTime = now.minusMinutes(currentMinutesOfHour <= 30 ? currentMinutesOfHour : minutes);
                    break;
                case "60":
                    actualDateTime = now.minusMinutes(currentMinutesOfHour);
                    break;
                case "240":
                    actualDateTime = now.minusMinutes(currentMinutesOfHour).minusHours(currentHourOfDay % 4);
                    break;
                case "720":
                    actualDateTime = now.minusMinutes(currentMinutesOfHour).minusHours(currentHourOfDay % 12);
                    break;
                case "M":
                    actualDateTime = now.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);
                    break;
                default:
                    actualDateTime = now.minusMinutes(currentMinutesOfHour);

            }

            t.add(actualDateTime.toEpochSecond(ZoneOffset.UTC));
            o.add(r.getOpen());
            h.add(r.getHigh());
            l.add(r.getLow());
            c.add(r.getClose());
            v.add(r.getVolume());
        }
        response.put("t", t);
        response.put("o", o);
        response.put("h", h);
        response.put("l", l);
        response.put("c", c);
        response.put("v", v);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NgDashboardException.class)
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorInfo OtherErrorsHandlerMethodArgumentNotValidException(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

}
