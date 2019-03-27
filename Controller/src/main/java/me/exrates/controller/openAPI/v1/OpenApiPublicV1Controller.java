package me.exrates.controller.openAPI.v1;

import me.exrates.model.dto.openAPI.OpenOrderDto;
import me.exrates.model.enums.OrderType;
import me.exrates.service.OrderService;
import me.exrates.service.exception.api.ErrorCode;
import me.exrates.service.exception.api.OpenApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static me.exrates.service.util.OpenApiUtils.transformCurrencyPair;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RequestMapping("/api/v1/public")
@RestController
public class OpenApiPublicV1Controller {


    private final OrderService orderService;

    @Autowired
    public OpenApiPublicV1Controller(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(value = "/open-orders/{order_type}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<OpenOrderDto> openOrders(@PathVariable("order_type") OrderType orderType,
                                         @RequestParam("currency_pair") String currencyPair) {
        String currencyPairName = transformCurrencyPair(currencyPair);
        return orderService.getOpenOrders(currencyPairName, orderType);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public OpenApiError OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INTERNAL_SERVER_ERROR, req.getRequestURL(), String.format("An internal error occurred: %s", exception.getMessage()));
    }
}