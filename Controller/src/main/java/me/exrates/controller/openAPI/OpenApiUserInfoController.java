package me.exrates.controller.openAPI;

import me.exrates.model.dto.openAPI.OpenApiCommissionDto;
import me.exrates.model.dto.openAPI.UserOrdersDto;
import me.exrates.model.dto.openAPI.WalletBalanceDto;
import me.exrates.service.OrderService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.CurrencyPairNotFoundException;
import me.exrates.service.exception.api.ErrorCode;
import me.exrates.service.exception.api.InvalidCurrencyPairFormatException;
import me.exrates.service.exception.api.OpenApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.Objects.nonNull;
import static me.exrates.service.util.OpenApiUtils.formatCurrencyPairNameParam;
import static me.exrates.utils.ValidationUtil.validateNaturalInt;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@SuppressWarnings("DanglingJavadoc")
@RestController
@RequestMapping("/openapi/v1/user")
public class OpenApiUserInfoController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private OrderService orderService;

    /**
     * @apiDefine NonPublicAuth
     *  See Authentication API doc section
     */

    /**
     * @api {get} /openapi/v1/user/balances User Balances
     * @apiName User Balances
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns array of wallet objects
     * @apiParamExample Request Example:
     * /openapi/v1/user/balances
     * @apiSuccess {Array} Wallet objects result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {String} data.currencyName Name of currency
     * @apiSuccess {Number} data.activeBalance Balance that is available for spending
     * @apiSuccess {Number} data.reservedBalance Balance reserved for orders or withdraw
     */
    @GetMapping(value = "/balances", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<WalletBalanceDto> userBalances() {
        return walletService.getBalancesForUser();
    }

    /**
     * @api {get} /openapi/v1/user/orders/open?currency_pair User's open orders
     * @apiName Open orders
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns collection of user open orders
     * @apiParam {String} currency_pair Name of currency pair (optional)
     * @apiParamExample Request Example:
     * /openapi/v1/user/orders/open?currency_pair=btc_usd
     * @apiSuccess {Array} User orders result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.id Order id
     * @apiSuccess {String} data.currency_pair Name of currency pair (e.g. btc_usd)
     * @apiSuccess {Number} data.amount Amount in base currency
     * @apiSuccess {String} data.order_type Type of order (BUY or SELL)
     * @apiSuccess {Number} data.price Exchange rate
     * @apiSuccess {Number} data.date_created Creation time as UNIX timestamp in millis
     * @apiSuccess {Number} data.date_accepted Acceptance time as UNIX timestamp in millis
     */
    @GetMapping(value = "/orders/open", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserOrdersDto> userOpenOrders(@RequestParam(value = "currency_pair", required = false) String currencyPair) {

        String currencyPairName = null;
        if (currencyPair != null) {
            currencyPairName = formatCurrencyPairNameParam(currencyPair);
        }
        return orderService.getUserOpenOrders(currencyPairName);
    }

    /**
     * @api {get} /openapi/v1/user/orders/closed?currency_pair&limit&offset User's closed orders
     * @apiName Closed orders
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns collection of user closed orders sorted by creation time
     * @apiParam {String} currency_pair Name of currency pair (optional)
     * @apiParam {Integer} limit Number of orders returned (default - 20, max - 100) (optional)
     * @apiParam {Integer} offset Number of orders skipped (optional)
     * @apiParamExample Request Example:
     * /openapi/v1/user/orders/closed?currency_pair=btc_usd&limit=100&offset=10
     * @apiSuccess {Array} User orders result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.id Order id
     * @apiSuccess {String} data.currency_pair Name of currency pair (e.g. btc_usd)
     * @apiSuccess {Number} data.amount Amount in base currency
     * @apiSuccess {String} data.order_type Type of order (BUY or SELL)
     * @apiSuccess {Number} data.price Exchange rate
     * @apiSuccess {Number} data.date_created Creation time as UNIX timestamp in millis
     * @apiSuccess {Number} data.date_accepted Acceptance time as UNIX timestamp in millis
     */
    @GetMapping(value = "/orders/closed", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserOrdersDto> userClosedOrders(@RequestParam(value = "currency_pair", required = false) String currencyPair,
                                                @RequestParam(required = false) Integer limit,
                                                @RequestParam(required = false) Integer offset) {
        final String currencyPairName = nonNull(currencyPair) ? formatCurrencyPairNameParam(currencyPair) : null;

        validateNaturalInt(limit);
        validateNaturalInt(offset);

        return orderService.getUserClosedOrders(currencyPairName, limit, offset);
    }

    /**
     * @api {get} /openapi/v1/user/orders/canceled?currency_pair&limit&offset User's canceled orders
     * @apiName Canceled orders
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns collection of user canceled orders sorted by creation time
     * @apiParam {String} currency_pair Name of currency pair (optional)
     * @apiParam {Integer} limit Number of orders returned (default - 20, max - 100) (optional)
     * @apiParam {Integer} offset Number of orders skipped (optional)
     * @apiParamExample Request Example:
     * /openapi/v1/user/orders/canceled?currency_pair=btc_usd&limit=100&offset=10
     * @apiSuccess {Array} User orders result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.id Order id
     * @apiSuccess {String} data.currency_pair Name of currency pair (e.g. btc_usd)
     * @apiSuccess {Number} data.amount Amount in base currency
     * @apiSuccess {String} data.order_type Type of order (BUY or SELL)
     * @apiSuccess {Number} data.price Exchange rate
     * @apiSuccess {Number} data.date_created Creation time as UNIX timestamp in millis
     * @apiSuccess {Number} data.date_accepted Acceptance time as UNIX timestamp in millis
     */
    @GetMapping(value = "/orders/canceled", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserOrdersDto>> userCanceledOrders(@RequestParam(value = "currency_pair", required = false) String currencyPair,
                                                                  @RequestParam(required = false) Integer limit,
                                                                  @RequestParam(required = false) Integer offset) {
        final String currencyPairName = nonNull(currencyPair) ? formatCurrencyPairNameParam(currencyPair) : null;

        validateNaturalInt(limit);
        validateNaturalInt(offset);

        return ResponseEntity.ok(orderService.getUserCanceledOrders(currencyPairName, limit, offset));
    }

    /**
     * @api {get} /openapi/v1/user/commissions User’s commission rates
     * @apiName Commissions
     * @apiGroup User API
     * @apiPermission NonPublicAuth
     * @apiDescription Returns info on user’s commission rates
     * (as per cent - for example, 0.5 rate means 0.5% of amount) by operation type.
     * Commissions for orders (sell and buy) are calculated and withdrawn from amount in quote currency.
     * @apiParamExample Request Example:
     * /openapi/v1/user/commissions
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Number} data.input Commission for input operations
     * @apiSuccess {Number} data.output Commission for output operations
     * @apiSuccess {Number} data.sell Commission for sell operations
     * @apiSuccess {Number} data.buy Commission for buy operations
     * @apiSuccess {Number} data.transfer Commission for transfer operations
     */
    @GetMapping(value = "/commissions", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public OpenApiCommissionDto getCommissions() {
        return new OpenApiCommissionDto(orderService.getAllCommissions());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = AccessDeniedException.class)
    public OpenApiError accessDeniedExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.ACCESS_DENIED, req.getRequestURL(), exception);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public OpenApiError mismatchArgumentsErrorHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INVALID_PARAM_VALUE, req.getRequestURL(), exception);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public OpenApiError missingServletRequestParameterHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.MISSING_REQUIRED_PARAM, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(CurrencyPairNotFoundException.class)
    @ResponseBody
    public OpenApiError currencyPairNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.CURRENCY_PAIR_NOT_FOUND, req.getRequestURL(), exception);
    }


    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(InvalidCurrencyPairFormatException.class)
    @ResponseBody
    public OpenApiError invalidCurrencyPairFormatExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INVALID_CURRENCY_PAIR_FORMAT, req.getRequestURL(), exception);
    }


    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public OpenApiError OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INTERNAL_SERVER_ERROR, req.getRequestURL(), exception);
    }


}
