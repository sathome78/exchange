package me.exrates.controller.openAPI.v1;

import me.exrates.controller.model.BaseResponse;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.OrderCreationResultDto;
import me.exrates.model.dto.openAPI.OrderCreationResultOpenApiDto;
import me.exrates.model.dto.openAPI.OrderParamsDto;
import me.exrates.model.dto.openAPI.TransactionDto;
import me.exrates.model.dto.openAPI.UserOrdersDto;
import me.exrates.model.dto.openAPI.WalletBalanceDto;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.UserOperationAccessException;
import me.exrates.service.exception.api.ErrorCode;
import me.exrates.service.exception.api.OpenApiError;
import me.exrates.service.userOperation.UserOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static me.exrates.service.util.OpenApiUtils.transformCurrencyPair;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RequestMapping("/api/v1/private")
@RestController
public class OpenApiPrivateV1Controller {

    private final WalletService walletService;
    private final UserService userService;
    private final UserOperationService userOperationService;
    private final MessageSource messageSource;
    private final OrderService orderService;

    @Autowired
    public OpenApiPrivateV1Controller(WalletService walletService,
                                      UserService userService,
                                      UserOperationService userOperationService,
                                      MessageSource messageSource,
                                      OrderService orderService) {
        this.walletService = walletService;
        this.userService = userService;
        this.userOperationService = userOperationService;
        this.messageSource = messageSource;
        this.orderService = orderService;
    }

    @GetMapping(value = "/balances", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<WalletBalanceDto> userBalances() {
        return walletService.getBalancesForUser();
    }

    @PreAuthorize("hasAuthority('TRADE')")
    @RequestMapping(value = "/orders", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<OrderCreationResultOpenApiDto> createOrder(@RequestBody @Valid OrderParamsDto orderParamsDto) {
        String userEmail = userService.getUserEmailFromSecurityContext();
        String currencyPairName = validateUserAndCurrencyPair(orderParamsDto.getCurrencyPair(), userEmail);
        OrderCreationResultDto resultDto = orderService.prepareAndCreateOrderRest(currencyPairName, orderParamsDto.getOrderType().getOperationType(),
                orderParamsDto.getAmount(), orderParamsDto.getPrice(), userEmail);
        return new ResponseEntity<>(new OrderCreationResultOpenApiDto(resultDto), HttpStatus.CREATED);
    }


    @PreAuthorize("hasAuthority('TRADE')")
    @GetMapping(value = "/orders", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<UserOrdersDto>> getOrders(@RequestParam(value = "currency_pair", required = false) String currencyPair,
                                                         @RequestParam(required = false) Integer limit,
                                                         @RequestParam(required = false) Integer offset) {
        List<UserOrdersDto> allUserOrders = orderService.getAllUserOrders(currencyPair, limit, offset);
        return new ResponseEntity<>(allUserOrders, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('TRADE')")
    @DeleteMapping(value = "/orders/{order_id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<BaseResponse<Map<String, Boolean>>> cancelOrder(@PathVariable Integer order_id) {

        orderService.cancelOrder(order_id);
        return ResponseEntity.ok(BaseResponse.success(Collections.singletonMap("success", true)));
    }

    @PreAuthorize("hasAuthority('TRADE')")
    @GetMapping(value = "/orders/{order_id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ExOrder> getOrderById(@PathVariable Integer order_id) {
        return new ResponseEntity<>(orderService.getOrderById(order_id), HttpStatus.OK);
    }


    @GetMapping(value = "/orders/{order_id}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<TransactionDto>>> getOrderTransactions(@PathVariable(value = "order_id") Integer orderId) {
        return ResponseEntity.ok(BaseResponse.success(orderService.getOrderTransactions(orderId)));
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public OpenApiError OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INTERNAL_SERVER_ERROR, req.getRequestURL(), String.format("An internal error occurred: %s", exception.getMessage()));
    }

    private String validateUserAndCurrencyPair(String currencyPair, String userEmail) {
        String currencyPairName = transformCurrencyPair(currencyPair);
        int userId = userService.getIdByEmail(userEmail);
        Locale locale = new Locale(userService.getPreferedLang(userId));
        boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userId, UserOperationAuthority.TRADING);
        if (!accessToOperationForUser) {
            throw new UserOperationAccessException(messageSource.getMessage("merchant.operationNotAvailable", null, locale));
        }
        return currencyPairName;
    }
}