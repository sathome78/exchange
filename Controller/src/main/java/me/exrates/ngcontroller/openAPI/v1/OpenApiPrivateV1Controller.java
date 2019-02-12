package me.exrates.ngcontroller.openAPI.v1;


import me.exrates.controller.model.BaseResponse;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.OrderCreationResultDto;
import me.exrates.model.dto.openAPI.*;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.UserOperationAccessException;
import me.exrates.service.userOperation.UserOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static me.exrates.service.util.OpenApiUtils.transformCurrencyPair;
import static me.exrates.service.util.RestApiUtils.retrieveParamFormBody;

@RequestMapping("/info/private/v2/apinew")
@RestController
public class OpenApiPrivateV1Controller {


    private final WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserOperationService userOperationService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private OrderService orderService;

    @Autowired
    public OpenApiPrivateV1Controller(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping(value = "/balances", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<WalletBalanceDto> userBalances() {
        return walletService.getBalancesForUser();
    }

    @RequestMapping(value = "/orders", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<OrderCreationResultOpenApiDto> createOrder(@RequestBody @Valid OrderParamsDto orderParamsDto) {
        String currencyPairName = transformCurrencyPair(orderParamsDto.getCurrencyPair());
        String userEmail = userService.getUserEmailFromSecurityContext();
        int userId = userService.getIdByEmail(userEmail);
        Locale locale = new Locale(userService.getPreferedLang(userId));
        boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userId, UserOperationAuthority.TRADING);
        if (!accessToOperationForUser) {
            throw new UserOperationAccessException(messageSource.getMessage("merchant.operationNotAvailable", null, locale));
        }
        OrderCreationResultDto resultDto = orderService.prepareAndCreateOrderRest(currencyPairName, orderParamsDto.getOrderType().getOperationType(),
                orderParamsDto.getAmount(), orderParamsDto.getPrice(), userEmail);
        return new ResponseEntity<>(new OrderCreationResultOpenApiDto(resultDto), HttpStatus.CREATED);
    }


    @GetMapping(value = "/orders", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<UserOrdersDto>> getOrders(@RequestParam(value = "currency_pair", required = false) String currencyPair,
                                                         @RequestParam(required = false) Integer limit,
                                                         @RequestParam(required = false) Integer offset) {
        List<UserOrdersDto> allUserOrders = orderService.getAllUserOrders(currencyPair, limit, offset);
        return new ResponseEntity<>(allUserOrders, HttpStatus.OK);
    }

    @DeleteMapping(value = "/orders/{order_id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<BaseResponse<Map<String, Boolean>>> cancelOrder(@PathVariable Integer order_id) {

        orderService.cancelOrder(order_id);
        return ResponseEntity.ok(BaseResponse.success(Collections.singletonMap("success", true)));
    }

    @GetMapping(value = "/orders/{order_id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ExOrder> getOrderById(@PathVariable Integer order_id) {
        return new ResponseEntity<>(orderService.getOrderById(order_id), HttpStatus.OK);
    }


    @GetMapping(value = "/orders/{order_id}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<TransactionDto>>> getOrderTransactions(@PathVariable(value = "order_id") Integer orderId) {
        return ResponseEntity.ok(BaseResponse.success(orderService.getOrderTransactions(orderId)));
    }

}
