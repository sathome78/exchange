package me.exrates.ngcontroller;

import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.User;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderStatus;
import me.exrates.ngcontroller.mobel.CreateOrderDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.DashboardService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/info/private/v2/dashboard/",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class NgDashboardController {

    private final DashboardService dashboardService;
    private final CurrencyService currencyService;
    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public NgDashboardController(DashboardService dashboardService,
                                 CurrencyService currencyService,
                                 OrderService orderService,
                                 UserService userService) {
        this.dashboardService = dashboardService;
        this.currencyService = currencyService;
        this.orderService = orderService;
        this.userService = userService;
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

}
