package me.exrates.ngcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.User;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.exceptions.RabbitMqException;
import me.exrates.ngcontroller.exception.NgDashboardException;
import me.exrates.ngcontroller.model.response.ResponseModel;
import me.exrates.ngcontroller.service.NgOrderService;
import me.exrates.ngcontroller.util.PagedResult;
import me.exrates.service.CurrencyService;
import me.exrates.service.DashboardService;
import me.exrates.service.OrderService;
import me.exrates.service.RabbitMqService;
import me.exrates.service.UserService;
import me.exrates.service.exception.CurrencyPairNotFoundException;
import me.exrates.service.exception.OrderAcceptionException;
import me.exrates.service.exception.OrderCancellingException;
import me.exrates.service.exception.api.OrderParamsWrongException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping(value = "/info/private/v2/dashboard",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class NgDashboardController {

    private static final Logger logger = LogManager.getLogger(NgDashboardController.class);

    private final DashboardService dashboardService;
    private final CurrencyService currencyService;
    private final OrderService orderService;
    private final UserService userService;
    private final LocaleResolver localeResolver;
    private final NgOrderService ngOrderService;
    private final ObjectMapper objectMapper;
    private final RabbitMqService rabbitMqService;
    private final SimpMessagingTemplate messagingTemplate;


    @Autowired
    public NgDashboardController(DashboardService dashboardService,
                                 CurrencyService currencyService,
                                 OrderService orderService,
                                 UserService userService,
                                 LocaleResolver localeResolver,
                                 NgOrderService ngOrderService,
                                 ObjectMapper objectMapper,
                                 RabbitMqService rabbitMqService,
                                 SimpMessagingTemplate messagingTemplate) {
        this.dashboardService = dashboardService;
        this.currencyService = currencyService;
        this.orderService = orderService;
        this.userService = userService;
        this.localeResolver = localeResolver;
        this.ngOrderService = ngOrderService;
        this.rabbitMqService = rabbitMqService;
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    // /info/private/v2/dashboard/order
    @PostMapping("/order")
    public ResponseEntity createOrder(@RequestBody @Valid InputCreateOrderDto inputOrder) {
        rabbitMqService.sendOrderInfo(inputOrder, RabbitMqService.JSP_QUEUE);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/order/{id}")
    public ResponseEntity deleteOrderById(@PathVariable int id) {
        Integer result = (Integer) orderService.deleteOrderByAdmin(id);
        return result == 1 ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/order")
    public ResponseEntity updateOrder(@RequestBody @Valid InputCreateOrderDto inputOrder) {

        if (inputOrder.getOrderId() == null) {
            throw new OrderParamsWrongException();
        }

        String userName = userService.getUserEmailFromSecurityContext();
        User user = userService.findByEmail(userName);

        OrderBaseType baseType = OrderBaseType.convert(inputOrder.getBaseType());
        boolean result;

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

        if (result) {
            String destination = "/topic/myorders/".concat(userName);
            messagingTemplate.convertAndSend(destination, fromResult(result));
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
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
     * /info/private/v2/dashboard/orders/{status}
     *
     * @param status         - user’s order status
     * @param currencyPairId - single currency pair, , not required,  default 0, when 0 then all currency pair are queried
     * @param currencyName   - filter if currency pair join name contains value, default is empty
     * @param page           - requested page, not required,  default 1
     * @param limit          - defines quantity rows per page, not required,  default 14
     * @param sortByCreated  - enables ASC sort by created date, not required,  default DESC
     * @param scope          - defines requested order type, values ["" - only created, "ACCEPTED" - only accepted,
     *                       "ALL" - both], not required,  default "" - created by user
     * @param hideCanceled   - hide cancelled orders if true
     * @param initial        - if true shows last 15 records despite of filter
     * @param dateFrom       - specifies the start of temporal range, must be in ISO_DATE format (yyyy-MM-dd), if null excluded
     * @param dateTo         - specifies the end of temporal range, must be in ISO_DATE format (yyyy-MM-dd), if null excluded
     * @param request        - HttpServletRequest, used by backend to resolve locale
     * @return - Pageable list of defined orders with meta info about total orders' count
     * @throws - 403 bad request
     */
    @GetMapping("/orders/{status}")
    public ResponseEntity<PagedResult<OrderWideListDto>> getOpenOrders(
            @PathVariable("status") String status,
            @RequestParam(required = false, name = "currencyPairId", defaultValue = "0") Integer currencyPairId,
            @RequestParam(required = false, name = "currencyName", defaultValue = "") String currencyName,
            @RequestParam(required = false, name = "page", defaultValue = "1") Integer page,
            @RequestParam(required = false, name = "limit", defaultValue = "15") Integer limit,
            @RequestParam(required = false, name = "sortByCreated", defaultValue = "DESC") String sortByCreated,
            @RequestParam(required = false, name = "scope", defaultValue = "") String scope,
            @RequestParam(required = false, name = "hideCanceled", defaultValue = "false") Boolean hideCanceled,
            @RequestParam(required = false, name = "initial", defaultValue = "false") Boolean initial,
            @RequestParam(required = false, name = "dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false, name = "dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            HttpServletRequest request) {

        OrderStatus orderStatus = OrderStatus.valueOf(status);

        int userId = userService.getIdByEmail(getPrincipalEmail());
        CurrencyPair currencyPair = currencyPairId > 0
                ? currencyService.findCurrencyPairById(currencyPairId)
                : null;
        Locale locale = localeResolver.resolveLocale(request);
        int offset = (page - 1) * limit;
        Map<String, String> sortedColumns = sortByCreated.equals("DESC")
                ? Collections.emptyMap()
                : Collections.singletonMap("date_creation", sortByCreated);
        try {
            Pair<Integer, List<OrderWideListDto>> ordersTuple =
                    this.orderService.getMyOrdersWithStateMap(userId, currencyPair, currencyName, orderStatus, scope, offset,
                            limit, hideCanceled, initial, locale, sortedColumns, dateFrom, dateTo);
            PagedResult<OrderWideListDto> pagedResult = new PagedResult<>();
            pagedResult.setCount(ordersTuple.getKey());
            pagedResult.setItems(ordersTuple.getValue());

            LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
            boolean beforeYesterday = pagedResult
                    .getItems()
                    .stream()
                    .anyMatch(order -> order.getDateCreation().isBefore(yesterday));

            if (beforeYesterday) {
                return new ResponseEntity<>(pagedResult, HttpStatus.MULTI_STATUS); // 207
            }
            return ResponseEntity.ok(pagedResult); // 200
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * Cancel one open order by order id
     *
     * @param orderId order id
     * @return {@link me.exrates.ngcontroller.model.response.ResponseModel}
     */
    @PostMapping("/cancel")
    public ResponseModel cancelOrder(@RequestParam("order_id") int orderId) {
        return new ResponseModel<>(orderService.cancelOrder(orderId));
    }

    /**
     * Cancel open orders by order ids
     *
     * @param ids list of orders (can be one or more)
     * @return {@link me.exrates.ngcontroller.model.response.ResponseModel}
     */
    @PostMapping("/cancel/list")
    public ResponseModel cancelOrders(@RequestParam("order_ids") Collection<Integer> ids) {
        return new ResponseModel<>(orderService.cancelOrders(ids));
    }

    /**
     * Cancel open orders by currency pair (if currency pair have not set - cancel all open orders)
     *
     * @param pairName pair name
     * @return {@link me.exrates.ngcontroller.model.response.ResponseModel}
     */
    @PostMapping("/cancel/all")
    public ResponseModel cancelOrdersByCurrencyPair(@RequestParam(value = "currency_pair", required = false) String pairName) {

        boolean canceled;
        if (nonNull(pairName)) {
            pairName = pairName.toUpperCase();

            canceled = orderService.cancelOpenOrdersByCurrencyPair(pairName);
        } else {
            canceled = orderService.cancelAllOpenOrders();
        }
        return new ResponseModel<>(canceled);
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/info/{currencyPairId}")
    public ResponseEntity<Map<String, Map<String, String>>> getCurrencyPairInfo(@PathVariable int currencyPairId)
            throws CurrencyPairNotFoundException {

        String userName = userService.getUserEmailFromSecurityContext();
        User user = userService.findByEmail(userName);
        Map<String, Map<String, String>> result = ngOrderService.getBalanceByCurrencyPairId(currencyPairId, user);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler({NgDashboardException.class, IllegalArgumentException.class, CurrencyPairNotFoundException.class})
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    @ExceptionHandler({RabbitMqException.class, UnsupportedOperationException.class})
    @ResponseBody
    public ErrorInfo RabbitMqErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, OrderAcceptionException.class,
            OrderCancellingException.class})
    @ResponseBody
    public ErrorInfo OtherErrorsHandlerMethodArgumentNotValidException(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    private String fromResult(boolean result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            logger.info("Failed to convert result value {}", result);
            return "";
        }
    }

}
