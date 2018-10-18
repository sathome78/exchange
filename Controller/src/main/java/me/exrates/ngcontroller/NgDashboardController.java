package me.exrates.ngcontroller;

import me.exrates.model.Currency;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderStatus;
import me.exrates.service.CurrencyService;
import me.exrates.service.DashboardService;
import me.exrates.service.OrderService;
import me.exrates.service.exception.TransferRequestRevokeException;
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

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping(value = "/info/private/v2/dashboard/",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class NgDashboardController {

    private DashboardService dashboardService;
    private CurrencyService currencyService;
    private OrderService orderService;

    @Autowired
    public NgDashboardController(DashboardService dashboardService,
                                 CurrencyService currencyService,
                                 OrderService orderService) {
        this.dashboardService = dashboardService;
        this.currencyService = currencyService;
        this.orderService = orderService;
    }

    @PostMapping("/order")
    public ResponseEntity createOrder(@RequestBody OrderCreateDto order, Principal principal) {
        if (principal == null) {
            throw new TransferRequestRevokeException();
        }
        order.setStatus(OrderStatus.OPENED);
        order.setUserId(Integer.parseInt(principal.getName()));
        int result = orderService.createOrder(order, OrderActionEnum.CREATE);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/balance/{currency}")
    public ResponseEntity<BigDecimal> getBalanceByCurrency(@PathVariable("currency") String currencyName,
                                                           Principal principal) {
        if (principal == null) {
            throw new TransferRequestRevokeException();
        }
        int userId = Integer.parseInt(principal.getName());
        Currency currency = currencyService.findByName(currencyName);
        return new ResponseEntity<>(dashboardService.getBalanceByCurrency(userId, currency.getId()), HttpStatus.OK);
    }

}
