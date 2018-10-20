package me.exrates.ngcontroller;

import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.User;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderStatus;
import me.exrates.ngcontroller.mobel.CreateOrderDto;
import me.exrates.ngcontroller.util.PagedResult;
import me.exrates.service.CurrencyService;
import me.exrates.service.DashboardService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping(value = "/info/private/v2/dashboard/",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class NgDashboardController {

    private final DashboardService dashboardService;
    private final CurrencyService currencyService;
    private final OrderService orderService;
    private final UserService userService;
    private final LocaleResolver localeResolver;

    @Autowired
    public NgDashboardController(DashboardService dashboardService,
                                 CurrencyService currencyService,
                                 OrderService orderService,
                                 UserService userService,
                                 LocaleResolver localeResolver) {
        this.dashboardService = dashboardService;
        this.currencyService = currencyService;
        this.orderService = orderService;
        this.userService = userService;
        this.localeResolver = localeResolver;
    }

    @PostMapping("/order/{orderType}")
    public ResponseEntity createOrder(@Valid @RequestBody CreateOrderDto order, @PathVariable OperationType operationType) {

        String userName = userService.getUserEmailFromSecurityContext();
        User user = userService.findByEmail(userName);
        CurrencyPair currencyPair = currencyService.getNotHiddenCurrencyPairByName(order.getCurrencyPair());


        OrderCreateDto orderCreateDto = new OrderCreateDto();
        orderCreateDto.setUserId(user.getId());
        orderCreateDto.setStatus(OrderStatus.OPENED);
        orderCreateDto.setAmount(order.getAmount());
        orderCreateDto.setOperationType(operationType);
        orderCreateDto.setComission(order.getCommission());
        orderCreateDto.setCurrencyPair(currencyPair);

        int result = orderService.createOrder(orderCreateDto, OrderActionEnum.CREATE);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/balance/{currency}")
    public ResponseEntity<BigDecimal> getBalanceByCurrency(@PathVariable("currency") String currencyName) {

        String userName = userService.getUserEmailFromSecurityContext();
        User user = userService.findByEmail(userName);
        Currency currency = currencyService.findByName(currencyName);
        return new ResponseEntity<>(dashboardService.getBalanceByCurrency(user.getId(), currency.getId()), HttpStatus.OK);
    }


    @GetMapping("/commission/{orderType}/{currencyPair}")
    public ResponseEntity<WalletsAndCommissionsForOrderCreationDto> getCommission(@PathVariable OperationType orderType,
                                        @PathVariable String currencyPair){

        String email = userService.getUserEmailFromSecurityContext();
        CurrencyPair activeCurrencyPair = currencyService.getNotHiddenCurrencyPairByName(currencyPair);
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
     * @param currencyPairId    - currency pair id must be valid
     * @param page              - requested page, not required,  default 1
     * @param limit             - defines quantity rows per page, not required,  default 14
     * @param sortByCreated     - enables ASC sort by created date, not required,  default DESC
     * @param scope             - defines requested order type, values ["" - only created, "ACCEPTED" - only accepted,
     *                        "ALL" - both], not required,  default ""
     * @param request           - HttpServletRequest
     * @return                  - Pageable list of open orders with meta info about total orders' count
     * @throws                  - 403 bad request
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

}
