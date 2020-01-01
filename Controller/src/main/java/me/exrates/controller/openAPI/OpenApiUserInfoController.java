package me.exrates.controller.openAPI;

import me.exrates.controller.exception.InvalidNumberParamException;
import me.exrates.controller.model.BaseResponse;
import me.exrates.dao.exception.notfound.CurrencyPairNotFoundException;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.migrate.ExtendedUserDto;
import me.exrates.model.dto.openAPI.OpenApiCommissionDto;
import me.exrates.model.dto.openAPI.TransactionDto;
import me.exrates.model.dto.openAPI.UserOrdersDto;
import me.exrates.model.dto.openAPI.UserTradeHistoryDto;
import me.exrates.model.dto.openAPI.WalletBalanceDto;
import me.exrates.model.exceptions.OpenApiException;
import me.exrates.service.MigrateService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static java.util.Objects.nonNull;
import static me.exrates.service.util.OpenApiUtils.transformCurrencyPair;
import static me.exrates.utils.ValidationUtil.validateNaturalInt;

@SuppressWarnings("DanglingJavadoc")
@RestController
@RequestMapping("/openapi/v1/user")
public class OpenApiUserInfoController {

    private final WalletService walletService;
    private final OrderService orderService;
    private final UserService userService;
    private final MigrateService migrateService;

    @Autowired
    public OpenApiUserInfoController(WalletService walletService,
                                     OrderService orderService,
                                     UserService userService,
                                     MigrateService migrateService) {
        this.walletService = walletService;
        this.orderService = orderService;
        this.userService = userService;
        this.migrateService = migrateService;
    }

    @GetMapping(value = "/balances", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<WalletBalanceDto> userBalances() {
        return walletService.getBalancesForUser();
    }

    @GetMapping(value = "/orders/open", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserOrdersDto> userOpenOrders(@RequestParam(value = "currency_pair", required = false) String currencyPair) {
        final String currencyPairName = nonNull(currencyPair)
                ? transformCurrencyPair(currencyPair)
                : null;

        return orderService.getUserOpenOrders(currencyPairName);
    }

    @GetMapping(value = "/orders/closed", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserOrdersDto> userClosedOrders(@RequestParam(value = "currency_pair", required = false) String currencyPair,
                                                @RequestParam(required = false) Integer limit,
                                                @RequestParam(required = false) Integer offset) {
        final String currencyPairName = nonNull(currencyPair)
                ? transformCurrencyPair(currencyPair)
                : null;

        try {
            validateNaturalInt(limit);
            validateNaturalInt(offset);
        } catch (InvalidNumberParamException e) {
            throw new OpenApiException(ErrorApiTitles.API_VALIDATE_NUMBER_ERROR, e.getMessage());
        }
        try {
            return orderService.getUserClosedOrders(currencyPairName, limit, offset);
        } catch (CurrencyPairNotFoundException e) {
            throw new OpenApiException(ErrorApiTitles.API_INVALID_CURRENCY_PAIR_NAME, e.getMessage());
        } catch (Exception e) {
            throw new OpenApiException(ErrorApiTitles.API_ORDER_NOT_FOUND, "Failed to closed orders");
        }
    }

    @GetMapping(value = "/orders/canceled", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<UserOrdersDto>>> userCanceledOrders(@RequestParam(value = "currency_pair", required = false) String currencyPair,
                                                                                @RequestParam(required = false) Integer limit,
                                                                                @RequestParam(required = false) Integer offset) {
        final String currencyPairName = nonNull(currencyPair)
                ? transformCurrencyPair(currencyPair)
                : null;
        try {
            validateNaturalInt(limit);
            validateNaturalInt(offset);
        } catch (InvalidNumberParamException e) {
            throw new OpenApiException(ErrorApiTitles.API_VALIDATE_NUMBER_ERROR, e.getMessage());
        }

        return ResponseEntity.ok(BaseResponse.success(orderService.getUserCanceledOrders(currencyPairName, limit, offset)));
    }

    @GetMapping(value = "/commissions", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public OpenApiCommissionDto getCommissions() {
        return new OpenApiCommissionDto(orderService.getAllCommissions());
    }

    @GetMapping(value = "/history/{currency_pair}/trades", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<UserTradeHistoryDto>>> getUserTradeHistoryByCurrencyPair(@PathVariable(value = "currency_pair") String currencyPair,
                                                                                                     @RequestParam(value = "from_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                                                     @RequestParam(value = "to_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                                                                     @RequestParam(required = false, defaultValue = "50") Integer limit) {
        if (fromDate.isAfter(toDate)) {
            throw new OpenApiException(ErrorApiTitles.API_REQUEST_ERROR_DATES, "From date is after to date");
        }
        if (nonNull(limit) && limit <= 0) {
            throw new OpenApiException(ErrorApiTitles.API_REQUEST_ERROR_LIMIT, "Limit value equals or less than zero");
        }

        final String transformedCurrencyPair = transformCurrencyPair(currencyPair);

        return ResponseEntity.ok(BaseResponse.success(orderService.getUserTradeHistoryByCurrencyPair(transformedCurrencyPair, fromDate, toDate, limit)));
    }

    @GetMapping(value = "/history/{order_id}/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<TransactionDto>>> getOrderTransactions(@PathVariable(value = "order_id") Integer orderId) {
        return ResponseEntity.ok(BaseResponse.success(orderService.getOrderTransactions(orderId)));
    }

    @PostMapping("/migrate")
    public ResponseEntity<ExtendedUserDto> migrateUser(@RequestParam("email") String email) {
        return ResponseEntity.ok(migrateService.migrate(email));
    }
}