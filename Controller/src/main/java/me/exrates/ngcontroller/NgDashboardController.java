package me.exrates.ngcontroller;

import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.User;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.ngcontroller.mobel.InputCreateOrderDto;
import me.exrates.ngcontroller.mobel.ResponseInfoCurrencyPairDto;
import me.exrates.ngcontroller.service.NgOrderService;
import me.exrates.ngcontroller.service.impl.NgMockService;
import me.exrates.ngcontroller.util.PagedResult;
import me.exrates.service.CurrencyService;
import me.exrates.service.DashboardService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.events.CreateOrderEvent;
import me.exrates.service.exception.api.OrderParamsWrongException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.PropertySource;
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

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(value = "/info/private/v2/dashboard/",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@PropertySource("classpath:angular.properties")
public class NgDashboardController {

    private static final Logger logger = LogManager.getLogger(NgDashboardController.class);

    private static Map<CurrencyPair, Set<OrderWideListDto>> OPEN_ORDERS = new HashMap<>();
    private static Map<CurrencyPair, Set<OrderWideListDto>> CLOSED_ORDERS = new HashMap<>();

    private final DashboardService dashboardService;
    private final CurrencyService currencyService;
    private final OrderService orderService;
    private final UserService userService;
    private final LocaleResolver localeResolver;
    private final NgOrderService ngOrderService;
    private final NgMockService ngMockService;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${angular.write.mode}")
    private boolean WRITE_MODE;

    @Autowired
    public NgDashboardController(DashboardService dashboardService,
                                 CurrencyService currencyService,
                                 OrderService orderService,
                                 UserService userService,
                                 LocaleResolver localeResolver,
                                 NgOrderService ngOrderService,
                                 NgMockService ngMockService,
                                 ApplicationEventPublisher eventPublisher) {
        this.dashboardService = dashboardService;
        this.currencyService = currencyService;
        this.orderService = orderService;
        this.userService = userService;
        this.localeResolver = localeResolver;
        this.ngOrderService = ngOrderService;
        this.ngMockService = ngMockService;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    private void initData() {
            ngMockService.initOpenOrders(OPEN_ORDERS);
            ngMockService.initClosedOrders(CLOSED_ORDERS);
    }

    @PostMapping("/order")
    public ResponseEntity createOrder(@RequestBody @Valid InputCreateOrderDto inputOrder) {

        String result;
        if (!WRITE_MODE) {
            result = ngOrderService.createOrder(inputOrder);
        } else {
            result = "TEST_MODE";
            eventPublisher.publishEvent(new CreateOrderEvent(ngMockService.mockOrderFromInputOrderDTO(inputOrder)));

        }
        HashMap<String, String> resultMap = new HashMap<>();

        if (!StringUtils.isEmpty(result)) {
            resultMap.put("message", "success");
            return new ResponseEntity<>(resultMap, HttpStatus.CREATED);
        } else {
            resultMap.put("message", "fail");
            return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/order/{id}")
    public ResponseEntity deleteOrderById(@PathVariable int id) {
        Integer result;
        if (!WRITE_MODE) {
            result = (Integer) orderService.deleteOrderByAdmin(id);
        } else {
            result = 0;
        }

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

        if (!WRITE_MODE) {
            switch (baseType) {
                case STOP_LIMIT:
                    result = ngOrderService.processUpdateStopOrder(user, inputOrder);
                    break;
                case LIMIT:
                    result = ngOrderService.processUpdateOrder(user, inputOrder);
                    break;
                case ICO:
                    throw new NgDashboardException("Not supported type - ICO");
                default:
                    throw new NgDashboardException("Unknown type - " + baseType);
            }
        } else {
            result = true;
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

        WalletsAndCommissionsForOrderCreationDto result =
                ngOrderService.getWalletAndCommision(email, orderType, currencyPairId);

        return new ResponseEntity<>(result, HttpStatus.OK);
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


    @GetMapping("/info/{currencyPairId}")
    public ResponseEntity getCurrencyPairInfo(@PathVariable int currencyPairId) {

        String userName = userService.getUserEmailFromSecurityContext();
        User user = userService.findByEmail(userName);
        ResponseInfoCurrencyPairDto result = ngOrderService.getCurrencyPairInfo(currencyPairId, user);

        return new ResponseEntity<>(result, HttpStatus.OK);
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
