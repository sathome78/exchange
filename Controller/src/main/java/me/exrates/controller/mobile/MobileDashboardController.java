package me.exrates.controller.mobile;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.exrates.model.CurrencyPair;
import me.exrates.model.StockExchangeStats;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.mobileApiDto.CandleChartItemReducedDto;
import me.exrates.model.dto.mobileApiDto.TransferLimitDto;
import me.exrates.model.dto.mobileApiDto.dashboard.*;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.IntervalType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.*;
import me.exrates.service.exception.CurrencyPairNotFoundException;
import me.exrates.service.exception.api.ApiError;
import me.exrates.service.exception.api.ErrorCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static me.exrates.controller.OnlineRestController.ORDER_HISTORY_INTERVAL;
import static me.exrates.controller.OnlineRestController.ORDER_HISTORY_LIMIT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by OLEG on 23.08.2016.
 */

/**
 * ALL controleers oommented for security reasons
 * */
@RestController
@RequestMapping(value = "/api/dashboard")
public class MobileDashboardController {

    private static final Logger logger = LogManager.getLogger("mobileAPI");

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private UserService userService;

    @Autowired
    private StockExchangeService stockExchangeService;

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    InputOutputService inputOutputService;


    /**
     * @apiDefine TokenHeader
     * @apiHeader (Exrates - Rest - Token) {String} token Authentication token
     * @apiHeaderExample {json} Header Example:
     *      {
     *          "Exrates-Rest-Token": "eyJhbGciOiJIUzUxMiJ9.eyJjbGllbnRUeXBlIjoidXNlciIsInRva2VuX2V4cGlyYXRpb25fZGF0ZSI6MTQ3MjE5NTc5OTI4NiwidXNlcm5hbWUiOiJzZW50aW5lbDc3N0BiaWdtaXIubmV0IiwidG9rZW5fY3JlYXRlX2RhdGUiOjE0NzIxMDkzOTkyODZ9.ricKp_eTolDzFsUWCBlLaDaFnqoKxjpJpLdcYpoyLzVugTZohr5JB13M3rlN1pWy9xSFXOFxYmVx2Ii3o9qWcw"
     *      }
     * */

    /**
     * @apiDefine CurrencyPair
     * @apiSuccess (200) {Object} currencyPair Currency pair
     * @apiSuccess (200) {Number} currencyPair.id Currecny pair id
     * @apiSuccess (200) {String} currencyPair.name Currency pair name
     * @apiSuccess (200) {Object} currencyPair.currency1 Base currency
     * @apiSuccess (200) {Number} currencyPair.currency1.id Base currency id
     * @apiSuccess (200) {String} currencyPair.currency1.name Base currency name
     * @apiSuccess (200) {String} currencyPair.currency1.detail Base currency detail
     * @apiSuccess (200) {Object} currencyPair.currency2 Currency to convert to
     * @apiSuccess (200) {Number} currencyPair.currency2.id Second currency id
     * @apiSuccess (200) {String} currencyPair.currency2.name Second currency name
     * @apiSuccess (200) {String} currencyPair.currency2.detail Second currency detail
     *
     * */


    /**
     * @apiDefine MissingParamError
     * @apiError (400) {String} errorCode error code
     * @apiError (400) {String} url request URL
     * @apiError (400) {String} cause name of root exception
     * @apiError (400) {String} details detail of root exception
     * @apiErrorExample {json} Missing Parameter:
     * HTTP/1.1 400 Bad Request
     *      {
     *          "errorCode": "MISSING_REQUIRED_PARAM",
     *          "url": "http://127.0.0.1:8080/api/dashboard/acceptedOrderHistory",
     *          "cause": "MissingServletRequestParameterException",
     *          "detail": "Required Integer parameter 'currencyPairId' is not present"
     *      }
     *
     * */

    /**
     * @apiDefine InvalidParamError
     * @apiError (400) {String} errorCode error code
     * @apiError (400) {String} url request URL
     * @apiError (400) {String} cause name of root exception
     * @apiError (400) {String} details detail of root exception
     * @apiErrorExample {json} Invalid Parameter:
     * HTTP/1.1 400 Bad Request
     *      {
     *              "errorCode": "INVALID_PARAM_VALUE",
     *              "url": "http://127.0.0.1:8080/api/dashboard/myStatementData",
     *              "cause": "MethodArgumentTypeMismatchException",
     *              "detail": "For input string: \"42oiuy80\""
     *      }
     *
     * */

    /**
     * @apiDefine CurrencyPairNotFoundError
     * @apiError (404) {String} errorCode error code
     * @apiError (404) {String} url request URL
     * @apiError (404) {String} cause name of root exception
     * @apiError (404) {String} details detail of root exception
     * @apiErrorExample {json} Currency Pair Not Found:
     * HTTP/1.1 404 Not Found
     *      {
     *          "errorCode": "CURRENCY_PAIR_NOT_FOUND",
     *          "url": "http://127.0.0.1:8080/api/dashboard/ordersForPairStatistics",
     *          "cause": "CurrencyPairNotFoundException",
     *          "detail": "Currency pair not found"
     *      }
     *
     * */


    /**
     * @api {get} /api/dashboard/generalInfo Get general information
     * @apiName getGeneralInfo
     * @apiGroup Dashboard
     * @apiUse TokenHeader
     * @apiPermission User
     * @apiDescription Retrieves list of basic information: commissions, currency pairs, merchants, limits
     * @apiSuccess (200) {Array} data Array of currency pairs
     * @apiUse CurrencyPair
     * @apiSuccess (200) {Number} currencyPair.minRateSell Min rate for sell orders
     * @apiSuccess (200) {Number} currencyPair.maxRateSell Max rate for sell orders
     * @apiSuccess (200) {Number} currencyPair.minRateBuy Min rate for buy orders
     * @apiSuccess (200) {Number} currencyPair.maxRateBuy Max rate for buy orders
     * @apiSuccess (200) {Number} currencyPair.minAmountSell Min amount for sell orders
     * @apiSuccess (200) {Number} currencyPair.maxAmountSell Max amount for sell orders
     * @apiSuccess (200) {Number} currencyPair.minAmountBuy Min amount for buy orders
     * @apiSuccess (200) {Number} currencyPair.maxAmountBuy Max amount for buy orders
     * @apiSuccess {Array} merchants List of available merchants
     * @apiSuccess {Object} merchant Container object
     * @apiSuccess {Integer} merchant.merchantId merchant id
     * @apiSuccess {Integer} merchant.currencyId currency id
     * @apiSuccess {String} merchant.name merchant name
     * @apiSuccess {String} merchant.processType merchant process type (CRYPTO, MERCHANT, INVOICE)
     * @apiSuccess {Number} merchant.minInputSum minimal sum of input payment
     * @apiSuccess {Number} merchant.minOutputSum minimal sum of output payment
     * @apiSuccess {Number} merchant.inputCommission commission rate for refill operations
     * @apiSuccess {Number} merchant.outputCommission commission rate for withdraw operations
     * @apiSuccess {Number} merchant.minFixedCommission minimal commission amount
     * @apiSuccess {Boolean} merchant.additionalTagForWithdrawAddressIsUsed if additional tag is needed for output
     * @apiSuccess {String} merchant.additionalFieldName name of additional tag
     * @apiSuccess {Boolean} merchant.generateAdditionalRefillAddressAvailable for cryptos - if it is possible to generate new address
     * @apiSuccess {Boolean} data.withdrawCommissionDependsOnDestinationTag - if withdraw commission is computed dynamically against dst tag value
     * @apiSuccess {Array} merchant.merchantImageList List of merchant images
     * @apiSuccess {Object} merchantImage Merchant image
     * @apiSuccess {String} merchant.merchantImage.merchantId merchant id
     * @apiSuccess {String} merchant.merchantImage.currencyId currency id
     * @apiSuccess {String} merchant.merchantImage.image_name image name
     * @apiSuccess {String} merchant.merchantImage.image_path - path for image on server
     * @apiSuccess {String} merchant.merchantImage.id - merchant image id
     * @apiSuccess {Boolean} merchant.withdrawBlocked if refill is blocked for merchant
     * @apiSuccess {Boolean} merchant.refillBlocked if withdraw is blocked for merchant
     * @apiSuccess {Object} commissions Container object for commission rates
     * @apiSuccess {Number} commissions.inputCommission commission for input operations
     * @apiSuccess {Number} commissions.outputCommission commission for output operations
     * @apiSuccess {Number} commissions.sellCommission sell commission
     * @apiSuccess {Number} commissions.buyCommission buy commission
     * @apiSuccess {Array} transferMerchants List of available merchants
     * @apiSuccess {Object} transferMerchant Container object
     * @apiSuccess {Integer} transferMerchant.merchantId merchant id
     * @apiSuccess {String} transferMerchant.name merchant name
     * @apiSuccess {Boolean} transferMerchant.isVoucher defines if transfer is by voucher
     * @apiSuccess {Boolean} transferMerchant.recipientUserIsNeeded defines if transfer is for a certain user
     * @apiSuccess {Array} transferMerchant.blockedForCurrencies IDs of currencies where the transfer is blocked
     * @apiSuccess {Array} transferLimits Result
     * @apiSuccess {Object} transferLimit Container object
     * @apiSuccess {Integer} transferLimit.currencyId currency id
     * @apiSuccess {Number} transferLimit.transferMinLimit min limit for transfer
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "currencyPairs": [
     * {
     * "id": 1,
     * "name": "BTC/USD",
     * "currency1": {
     * "id": 4,
     * "name": "BTC"
     * },
     * "currency2": {
     * "id": 2,
     * "name": "USD"
     * },
     * "minRateSell": 0,
     * "maxRateSell": 99999999999,
     * "minRateBuy": 0,
     * "maxRateBuy": 99999999999,
     * "minAmountSell": 0.5,
     * "maxAmountSell": 2,
     * "minAmountBuy": 0,
     * "maxAmountBuy": 0
     * }
     * ],
     * "merchants": [
     * {
     * "merchantId": 1,
     * "currencyId": 1,
     * "name": "Yandex kassa",
     * "processType": "MERCHANT",
     * "minInputSum": 200,
     * "minOutputSum": 200,
     * "minTransferSum": 200,
     * "inputCommission": 0,
     * "outputCommission": 0,
     * "transferCommission": 0,
     * "minFixedCommission": 0,
     * "listMerchantImage": [
     * {
     * "id": 1,
     * "imagePath": "/client/img/merchants/visa.png"
     * }
     * ],
     * "withdrawBlocked": true,
     * "refillBlocked": true,
     * "transferBlocked": true
     * }
     * ],
     * "commissions": {
     * "inputCommission": 0,
     * "outputCommission": 1,
     * "sellCommission": 0,
     * "buyCommission": 0,
     * "transferCommission": 0
     * },
     * "transferMerchants": [
     * {
     * "merchantId": 30,
     * "name": "SimpleTransfer",
     * "isVoucher": false,
     * "recipientUserIsNeeded": true,
     * "blockedForCurrencies": []
     * }
     * ],
     * "transferLimits": [
     * {
     * "currencyId": 1,
     * "transferMinLimit": 200
     * }
     * ]
     * }
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InternalServerError
     */
    /*@RequestMapping(value = "/generalInfo", method = GET)
    public GeneralInfoDto getGeneralInfo(@RequestParam(required = false) Integer currencyId) {
        GeneralInfoDto result = new GeneralInfoDto();
        result.setCommissions(orderService.getAllCommissions());
        result.setCurrencyPairs(currencyService.findCurrencyPairsWithLimitsForUser().stream().filter(p -> p.getType() == CurrencyPairType.MAIN).collect(Collectors.toList()));
        List<Integer> currencyIds = currencyId == null ? Collections.EMPTY_LIST : Collections.singletonList(currencyId);
        result.setTransferLimits(currencyService.retrieveMinTransferLimits(currencyIds));
        result.setMerchants(merchantService.findNonTransferMerchantCurrencies(currencyId));
        result.setTransferMerchants(merchantService.findTransferMerchants());
        return result;
    }*/

    /**
     * @api {get} /api/dashboard/currencyPairs Get available currency pairs
     * @apiName getCurrencyPairs
     * @apiGroup Dashboard
     * @apiUse TokenHeader
     * @apiPermission User
     * @apiDescription Retrieves list of available currency pairs
     * @apiSuccess (200) {Array} data Array of currency pairs
     * @apiUse CurrencyPair
     * @apiSuccess (200) {Number} minRateSell Min rate for sell orders
     * @apiSuccess (200) {Number} maxRateSell Max rate for sell orders
     * @apiSuccess (200) {Number} minRateBuy Min rate for buy orders
     * @apiSuccess (200) {Number} maxRateBuy Max rate for buy orders
     * @apiSuccess (200) {Number} currencyPair.minAmountSell Min amount for sell orders
     * @apiSuccess (200) {Number} currencyPair.maxAmountSell Max amount for sell orders
     * @apiSuccess (200) {Number} currencyPair.minAmountBuy Min amount for buy orders
     * @apiSuccess (200) {Number} currencyPair.maxAmountBuy Max amount for buy orders
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "id": 1,
     * "name": "BTC/USD",
     * "currency1": {
     * "id": 4,
     * "name": "BTC",
     * "description": null,
     * "minWithdrawSum": null
     * },
     * "currency2": {
     * "id": 2,
     * "name": "USD",
     * "description": null,
     * "minWithdrawSum": null
     * }
     * }
     * "minRateSell": 500,
     * "maxRateSell": 5000,
     * "minRateBuy": 700,
     * "maxRateBuy": 99999999999,
     * "minAmountSell": 0.5,
     * "maxAmountSell": 2,
     * "minAmountBuy": 0,
     * "maxAmountBuy": 0
     * <p>
     * ]
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InternalServerError
     */
   /* @RequestMapping(value = "/currencyPairs", method = GET)
    public List<CurrencyPairWithLimitsDto> getCurrencyPairs() {
        return currencyService.findCurrencyPairsWithLimitsForUser().stream().filter(p -> p.getType() == CurrencyPairType.MAIN).collect(Collectors.toList());

    }*/


    /**
     * @api {get} /api/dashboard/candleChart/:currencyPairId/:intervalType/:intervalValue Get data for candle chart
     * @apiName getCandleChartData
     * @apiGroup Dashboard
     * @apiUse TokenHeader
     * @apiParam {Integer} currencyPairId id of currency pair
     * @apiParam {String} intervalType type of interval (valid values: "HOUR", "DAY", "MONTH", "YEAR")
     * @apiParam {Integer} intervalValue value of interval
     * @apiParamExample Request Example:
     * /api/dashboard/candleChart?currencyPairId=5&intervalType=DAY&intervalValue=7
     * @apiPermission User
     * @apiDescription Registers user
     * @apiSuccess (200) {Array} chartData Request result
     * @apiSuccess (200) {Object} data Candle chart data item
     * @apiSuccess (200) {Object} data.beginPeriod beginning of period as Java8 LocalDateTime
     * @apiSuccess (200) {Object} data.endPeriod end of period as Java8 LocalDateTime
     * @apiSuccess (200) {Number} data.openRate open rate
     * @apiSuccess (200) {Number} data.closeRate close rate
     * @apiSuccess (200) {Number} data.lowRate low rate
     * @apiSuccess (200) {Number} data.highRate high rate
     * @apiSuccess (200) {Number} data.baseVolume base amount of order
     * @apiSuccess (200) {Number} data.beginDate same as beginPeriod, different format
     * @apiSuccess (200) {Number} data.endDate same as endPeriod, different format
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "openRate":0.1,
     * "closeRate":0.1,
     * "lowRate":0.1,
     * "highRate":0.1,
     * "baseVolume":0,
     * "beginDate":1472132318000,
     * "endDate":1472132378000
     * },
     * {
     * "openRate":0.1,
     * "closeRate":0.1,
     * "lowRate":0.1,
     * "highRate":0.1,
     * "baseVolume":0,
     * "beginDate":1472132378000,
     * "endDate":1472132438000
     * }
     * ]
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse MissingParamError
     * @apiUse CurrencyPairNotFoundError
     * @apiUse InternalServerError
     */

    /*@RequestMapping(value = "/candleChart", method = GET)
    public List<CandleChartItemReducedDto> getCandleChartData(@RequestParam(value = "currencyPairId") Integer currencyPairId,
                                                              @RequestParam(value = "intervalType") IntervalType intervalType,
                                                              @RequestParam(value = "intervalValue") Integer intervalValue) {
        CurrencyPair currencyPair = currencyService.findCurrencyPairById(currencyPairId);
        BackDealInterval interval = new BackDealInterval(intervalValue, intervalType);
        List<CandleChartItemReducedDto> result = orderService.getDataForCandleChart(currencyPair, interval).stream()
                .map(CandleChartItemReducedDto::new)
                .collect(Collectors.toList());
        return result;


    }*/


    /**
     * @api {get} /api/dashboard/currencyPairStatistics Currency pair statistics
     * @apiName getCurrencyPairStatisticsForAllCurrencies
     * @apiGroup Dashboard
     * @apiUse TokenHeader
     * @apiPermission User
     * @apiDescription Retrieves current statistics (states) for all currency pairs
     * @apiSuccess {Array} orderStatistics Result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Number} data.currencyPairName currency pair name
     * @apiSuccess {Number} data.lastOrderRate rate of last order
     * @apiSuccess {Number} data.predLastOrderRate rate of order preceding last
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "currencyPairName": "BTC/USD",
     * "lastOrderRate": 600,
     * "predLastOrderRate": 590
     * },
     * {
     * "currencyPairName": "BTC/CNY",
     * "lastOrderRate": 3844.42,
     * "predLastOrderRate": 3862.02
     * },
     * ]
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InternalServerError
     */
   /* @RequestMapping(value = "/currencyPairStatistics", method = GET)
    public List<ExOrderStatisticsShortByPairsApiDto> getCurrencyPairStatistics(HttpServletRequest request) {
        return orderService.getOrdersStatisticByPairsSessionless(localeResolver.resolveLocale(request)).stream()
                .map(dto -> new ExOrderStatisticsShortByPairsApiDto(dto, localeResolver.resolveLocale(request))).collect(Collectors.toList());

    }
*/

    /**
     * @api {get} /api/dashboard/ordersForPairStatistics/:currencyPairId/:intervalType/:intervalValue Statistics for orders of current currency pair
     * @apiName getNewCurrencyPairData
     * @apiGroup Dashboard
     * @apiUse TokenHeader
     * @apiPermission User
     * @apiDescription data with statistics for orders for certain currency pair and time interval
     * @apiParam {Integer} currencyPairId id of currency pair
     * @apiParam {String} intervalType type of interval (valid values: "HOUR", "DAY", "MONTH", "YEAR")
     * @apiParam {Integer} intervalValue value of interval
     * @apiParamExample Request Example:
     * /api/dashboard/ordersForPairStatistics?currencyPairId=5&intervalType=DAY&intervalValue=7
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Number} data.firstOrderAmountBase Base amount of first order
     * @apiSuccess {Number} data.firstOrderRate Rate of first order
     * @apiSuccess {Number} data.lastOrderAmountBase Base amount of last order
     * @apiSuccess {Number} data.lastOrderRate Rate of last order
     * @apiSuccess {Number} data.minRate minimum rate
     * @apiSuccess {Number} data.maxRate maximum rate
     * @apiSuccess {Number} data.sumBase sum in base currency
     * @apiSuccess {Number} data.sumConvert sum in convert currency
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "firstOrderAmountBase": 1.5,
     * "firstOrderRate": 599,
     * "lastOrderAmountBase": 2,
     * "lastOrderRate": 600,
     * "minRate": 590,
     * "maxRate": 610,
     * "sumBase": 11.000002,
     * "sumConvert": 6593.0012
     * }
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse MissingParamError
     * @apiUse CurrencyPairNotFoundError
     * @apiUse InternalServerError
     */
   /* @RequestMapping(value = "/ordersForPairStatistics", method = GET, produces = APPLICATION_JSON_VALUE)
    public ExOrderStatisticsApiDto getNewCurrencyPairData(@RequestParam(value = "currencyPairId") Integer currencyPairId,
                                                          @RequestParam(value = "intervalType") IntervalType intervalType,
                                                          @RequestParam(value = "intervalValue") Integer intervalValue,
                                                          HttpServletRequest request) {

        CurrencyPair currencyPair = currencyService.findCurrencyPairById(currencyPairId);
        BackDealInterval interval = new BackDealInterval(intervalValue, intervalType);
        ExOrderStatisticsDto dto = orderService.getOrderStatistic(currencyPair, interval, localeResolver.resolveLocale(request));
        return new ExOrderStatisticsApiDto(dto, localeResolver.resolveLocale(request));
    }*/


    /**
     * @api {get} /api/dashboard/acceptedOrderHistory/:scope/:currencyPairId Accepted orders during last 24 hours
     * @apiName GetOrderHistory
     * @apiGroup Dashboard
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription returns list the data of accepted orders during last 24 hours
     * @apiParam {String} scope "ALL" to retrieve all accepted orders. Other value or empty to retrieve "my orders" only
     * @apiParam {Integer} currencyPairId id of currency pair
     * @apiParamExample Request Example:
     * /api/dashboard/acceptedOrderHistory?scope=ALL&currencyPairId=20
     * @apiSuccess {Array} acceptedOrders Result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.orderId Order ID
     * @apiSuccess {Long} data.dateAcceptionTime date and time of order acception
     * @apiSuccess {Number} data.rate exchange rate
     * @apiSuccess {Number} data.amountBase amount in base currency
     * @apiSuccess {String} data.operationType SELL or BUY
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "orderId": 18366,
     * "acceptionTime": 1473686556000,
     * "rate": 80,
     * "amountBase": 100,
     * "operationType": "BUY"
     * },
     * {
     * "orderId": 18351,
     * "acceptionTime": 1473686556000,
     * "rate": 93,
     * "amountBase": 25.15845623,
     * "operationType": "BUY"
     * }
     * ]
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse MissingParamError
     * @apiUse CurrencyPairNotFoundError
     * @apiUse InternalServerError
     */
  /*  @RequestMapping(value = "/acceptedOrderHistory", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<OrderAcceptedHistoryApiDto> getOrderHistory(@RequestParam(value = "scope") String scope,
                                                            @RequestParam(value = "currencyPairId") Integer currencyPairId,
                                                            HttpServletRequest request) {
        String email = "ALL".equals(scope.toUpperCase()) ? "" : getAuthenticatedUserEmail();
        CurrencyPair currencyPair = currencyService.findCurrencyPairById(currencyPairId);
        return orderService.getOrderAcceptedForPeriod(email, ORDER_HISTORY_INTERVAL, ORDER_HISTORY_LIMIT,
                currencyPair, localeResolver.resolveLocale(request)).stream()
                .map(dto -> new OrderAcceptedHistoryApiDto(dto, localeResolver.resolveLocale(request))).collect(Collectors.toList());

    }*/


    /**
     * @api {get} /api/dashboard/commission Current commissions for creating and accepting orders
     * @apiName getCommissions
     * @apiGroup Dashboard
     * @apiUse TokenHeader
     * @apiPermission User
     * @apiDescription returns current commissions for operation SELL and BUY
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Number} data.inputCommission commission for input operations
     * @apiSuccess {Number} data.outputCommission commission for output operations
     * @apiSuccess {Number} data.sellCommission sell commission
     * @apiSuccess {Number} data.buyCommission buy commission
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "inputCommission": 0.5,
     * "outputCommission": 0.5,
     * "sellCommission": 0.2,
     * "buyCommission": 0.2,
     * "transferCommission": 0
     * }
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InternalServerError
     */
   /* @RequestMapping(value = "/commission", method = GET, produces = APPLICATION_JSON_VALUE)
    public CommissionsDto getOrderCommissions() {
        return orderService.getAllCommissions();
    }

*/
    /**
     * @api {get} /api/dashboard/sellOrders/:currencyPairId List of SELL open orders
     * @apiName getSellOrdersList
     * @apiGroup Dashboard
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription returns list of SELL open orders
     * @apiParam {Integer} currencyPairId id of currency pair
     * @apiParamExample Request Example:
     * /api/dashboard/sellOrders?currencyPairId=5
     * @apiSuccess {Array} result Result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Number} data.id order id
     * @apiSuccess {Number} data.userId id of order owner
     * @apiSuccess {String} data.orderType order type (SELL)
     * @apiSuccess {Number} data.exrate exchange rate
     * @apiSuccess {Number} data.amountBase amount in base currency
     * @apiSuccess {Number} data.amountConvert amount in second currency
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "id": 18381,
     * "userId": 495,
     * "orderType": "SELL",
     * "exrate": 600,
     * "amountBase": 2,
     * "amountConvert": 1200
     * },
     * {
     * "id": 18383,
     * "userId": 495,
     * "orderType": "SELL",
     * "exrate": 600,
     * "amountBase": 2,
     * "amountConvert": 1200
     * }
     * ]
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse MissingParamError
     * @apiUse CurrencyPairNotFoundError
     * @apiUse InternalServerError
     */
  /*  @RequestMapping(value = "/sellOrders", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<OrderListApiDto> getSellOrdersList(@RequestParam(value = "currencyPairId") Integer currencyPairId, HttpServletRequest request) {
        CurrencyPair currencyPair = currencyService.findCurrencyPairById(currencyPairId);
        return orderService.getAllSellOrders(currencyPair, localeResolver.resolveLocale(request)).stream()
                .map(dto -> new OrderListApiDto(dto, localeResolver.resolveLocale(request))).collect(Collectors.toList());
    }

*/
    /**
     * @api {get} /api/dashboard/buyOrders/:currencyPairId List of BUY open orders
     * @apiName getBuyOrdersList
     * @apiGroup Dashboard
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription returns list of BUY open orders
     * @apiParam {Integer} currencyPairId id of currency pair
     * @apiParamExample Request Example:
     * /api/dashboard/buyOrders?currencyPairId=5
     * @apiSuccess {Array} result Result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Number} data.id order id
     * @apiSuccess {Number} data.userId id of order owner
     * @apiSuccess {String} data.orderType order type (BUY)
     * @apiSuccess {Number} data.exrate exchange rate
     * @apiSuccess {Number} data.amountBase amount in base currency
     * @apiSuccess {Number} data.amountConvert amount in second currency
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "id": 18381,
     * "userId": 495,
     * "orderType": "BUY",
     * "exrate": 600,
     * "amountBase": 2,
     * "amountConvert": 1200
     * },
     * {
     * "id": 18383,
     * "userId": 495,
     * "orderType": "BUY",
     * "exrate": 600,
     * "amountBase": 2,
     * "amountConvert": 1200
     * }
     * ]
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse MissingParamError
     * @apiUse CurrencyPairNotFoundError
     * @apiUse InternalServerError
     */
   /* @RequestMapping(value = "/buyOrders", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<OrderListApiDto> getBuyOrdersList(@RequestParam(value = "currencyPairId") Integer currencyPairId, HttpServletRequest request) {
        CurrencyPair currencyPair = currencyService.findCurrencyPairById(currencyPairId);
        return orderService.getAllBuyOrders(currencyPair, localeResolver.resolveLocale(request)).stream()
                .map(dto -> new OrderListApiDto(dto, localeResolver.resolveLocale(request))).collect(Collectors.toList());

    }
*/
    /**
     * @api {get} /api/dashboard/myWalletsData User wallets
     * @apiName getMyWalletsData
     * @apiGroup Dashboard
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription returns list of user's wallet to show in page "Balance"
     * @apiParam {Integer[]} currencyIds ids of currency pairs
     * @apiParamExample Request Example:
     * /api/dashboard/myWalletsData?currencyIds=2,9,7
     * @apiSuccess {Array} result Result
     * @apiSuccess {Integer} data.id wallet id
     * @apiSuccess {Integer} data.userId user id
     * @apiSuccess {Integer} data.currencyId currency id
     * @apiSuccess {String} data.currencyName Currency name
     * @apiSuccess {Number} data.activeBalance Total active balance (available for use)
     * @apiSuccess {Number} data.onConfirmation Amount on confirmation
     * @apiSuccess {Number} data.onConfirmationStage stage of confirmation
     * @apiSuccess {Number} data.onConfirmationCount Number of input transactions on confirmation
     * @apiSuccess {Number} data.reservedBalance Total amount of reserved costs in wallet
     * @apiSuccess {Number} data.reservedByOrders Costs reserved on orders
     * @apiSuccess {Number} data.reservedByMerchant Costs reserved on withdraw
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "userId": 495,
     * "currencyId": 1,
     * "currencyName": "RUB",
     * "activeBalance": 10193.80162,
     * "onConfirmation": 0,
     * "onConfirmationStage": 0,
     * "onConfirmationCount": 0,
     * "reservedBalance": 0,
     * "reservedByOrders": 0,
     * "reservedByMerchant": 0,
     * "id": 4277
     * },
     * ]
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InternalServerError
     */
  /*  @RequestMapping(value = "/myWalletsData", method = GET)
    public List<MyWalletsDetailedApiDto> getMyWalletsData(@RequestParam(required = false) Integer[] currencyIds, HttpServletRequest request) {
        List<Integer> currencyIdList;
        if (currencyIds == null || currencyIds.length == 0) {
            currencyIdList = Collections.EMPTY_LIST;
        } else {
            currencyIdList = Arrays.asList(currencyIds);
        }

        return walletService.getAllWalletsForUserDetailed(getAuthenticatedUserEmail(), currencyIdList, localeResolver.resolveLocale(request)).stream()
                .map(dto -> new MyWalletsDetailedApiDto(dto, localeResolver.resolveLocale(request))).collect(Collectors.toList());


    }
*/

    /**
     * @api {get} /api/dashboard/myWalletByCurrency/:currencyId Statistics for a user wallet
     * @apiName getMyWalletDataByCurrency
     * @apiGroup Dashboard
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription returns user wallet short stats
     * @apiParam {Integer} currencyId id of currency pair
     * @apiParamExample Request Example:
     * /api/dashboard/myWalletByCurrency?currencyId=6
     * @apiSuccess {Integer} id wallet id
     * @apiSuccess {Integer} userId user id
     * @apiSuccess {Integer} currencyId currency id
     * @apiSuccess {String} currencyName currency name
     * @apiSuccess {Number} activeBalance active balance
     * @apiSuccess {Number} reservedBalance reserved balance
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "userId": 495,
     * "currencyId": 6,
     * "currencyName": "EDRC",
     * "activeBalance": 10100,
     * "reservedBalance": 0,
     * "id": 4281
     * }
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse MissingParamError
     * @apiUse CurrencyPairNotFoundError
     * @apiUse InternalServerError
     */
   /* @RequestMapping(value = "/myWalletByCurrency", method = GET)
    public MyWalletsStatisticsApiDto getMyWalletDataByCurrency(@RequestParam Integer currencyId) {
        int userId = userService.getIdByEmail(getAuthenticatedUserEmail());
        int walletId = walletService.getWalletId(userId, currencyId);
        return walletService.getUserWalletShortStatistics(walletId);


    }*/


    /**
     * @api {get} /api/dashboard/myOrdersData User orders
     * @apiName getMyOrdersData
     * @apiGroup Dashboard
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription returns list of user's orders to show in pages "History" and "Orders"
     * @apiParam {Number} currencyPairId - (REQUIRED) currency pair id
     * @apiParam {Boolean} showAllPairs - (OPTIONAL) if true, statistics for all pairs is shown, ignoring currencyPairId
     * @apiParam {String} type - (REQUIRED) determines type of the order (BUY, SELL)
     * @apiParam {String[]} status - (REQUIRED) status of the order (OPENED, CLOSED, CANCELLED, DELETED)
     * @apiParam {Number} offset - (OPTIONAL) offset for pagination
     * @apiParam {Number} limit - (OPTIONAL) limit for pagination
     * @apiParamExample Request Example:
     * api/dashboard/myOrdersData?currencyPairId=21&showAllPairs=true&type=BUY&status=OPENED,CLOSED
     * @apiSuccess {Array} result Result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Number} data.id order id
     * @apiSuccess {Number} data.userId id of order owner
     * @apiSuccess {String} data.operationType BUY, SELL
     * @apiSuccess {Number} data.exExchangeRate exchange rate
     * @apiSuccess {Number} data.amountBase amount in base currency
     * @apiSuccess {Number} data.amountConvert amount in convertation currency
     * @apiSuccess {Integer} data.comissionId commission id
     * @apiSuccess {Number} data.commissionFixedAmount amount of commission
     * @apiSuccess {Number} data.amountWithCommission total amount
     * @apiSuccess {Integer} data.userAcceptorId id of acceptor user
     * @apiSuccess {Long} dto.dateCreation date and time of order creation
     * @apiSuccess {Long} dto.dateAcception date and time of order acception
     * @apiSuccess {String} dto.status OPENED, CLOSED, CANCELLED
     * @apiSuccess {Long} dto.dateStatusModification date and time of status modification
     * @apiSuccess {Number} dto.commissionAmountForAcceptor commission for acceptor
     * @apiSuccess {Number} dto.amountWithCommissionForAcceptor total amount for acceptor
     * @apiSuccess {String} dto.currencyPairName name of currency pair
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "id": 18383,
     * "userId": 495,
     * "operationType": "SELL",
     * "exExchangeRate": 600,
     * "amountBase": 2,
     * "amountConvert": 1200,
     * "comissionId": 8,
     * "commissionFixedAmount": 2.4,
     * "amountWithCommission": 1197.6,
     * "userAcceptorId": 0,
     * "dateCreation": 1473139073000,
     * "dateAcception": null,
     * "status": "OPENED",
     * "dateStatusModification": 1473139073000,
     * "commissionAmountForAcceptor": null,
     * "amountWithCommissionForAcceptor": null,
     * "currencyPairName": "BTC/USD",
     * },
     * ]
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse MissingParamError
     * @apiUse InternalServerError
     */
  /*  @RequestMapping(value = "/myOrdersData", method = GET, produces = "application/json; charset=UTF-8")
    public List<OrderWideListApiDto> getMyOrdersData(
            @RequestParam Integer currencyPairId,
            @RequestParam(required = false) Boolean showAllPairs,
            @RequestParam OperationType type,
            @RequestParam OrderStatus[] status,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit,
            HttpServletRequest request) {
        logger.debug(Arrays.asList(status));
        CurrencyPair currencyPair = showAllPairs == null || !showAllPairs ? currencyService.findCurrencyPairById(currencyPairId) : null;
        int offsetValue = offset == null ? 0 : offset;
        int limitValue = limit == null ? -1 : limit;
        return orderService.getMyOrdersWithState(getAuthenticatedUserEmail(), currencyPair,
                Arrays.asList(status), type, offsetValue, limitValue, localeResolver.resolveLocale(request)).stream()
                .map(dto -> new OrderWideListApiDto(dto, localeResolver.resolveLocale(request))).collect(Collectors.toList());

    }*/


    /**
     * @api {get} /api/dashboard/myStatementData/:walletId History by wallet
     * @apiName getMyAccountStatementData
     * @apiGroup Dashboard
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription returns list of user's wallet statement to show in pages "Balance" button "History"
     * @apiParam {String} walletId id of wallet
     * @apiParam {Number} offset - (OPTIONAL) offset for pagination
     * @apiParam {Number} limit - (OPTIONAL) limit for pagination
     * @apiParamExample Request Example:
     * /api/dashboard/myStatementData?limit=4&offset=0&walletId=4280
     * @apiSuccess {Array} result Result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Long} data.datetime date and time of transaction creation
     * @apiSuccess {Integer} data.transactionId transaction id
     * @apiSuccess {Number} data.activeBalanceBefore active balance before transaction
     * @apiSuccess {Number} data.reservedBalanceBefore reserved balance before transaction
     * @apiSuccess {String} data.operationType INPUT, OUTPUT, WALLET_INNER_TRANSFER
     * @apiSuccess {Number} data.amount amount
     * @apiSuccess {Number} data.commissionAmount commission amount
     * @apiSuccess {Number} data.activeBalanceAfter active balance after transaction
     * @apiSuccess {Number} data.reservedBalanceAfter reserved balance after transaction
     * @apiSuccess {String} data.sourceType IN/OUT, ORDER
     * @apiSuccess {String} data.sourceTypeId MERCHANT, ORDER
     * @apiSuccess {Integer} data.sourceId source id - if source is order - order id, null otherwise
     * @apiSuccess {String} data.transactionStatus CREATED, DELETED
     * @apiSuccess {Integer} data.walletId id of wallet
     * @apiSuccess {Integer} data.userId id of user
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "datetime": 1476249541000,
     * "transactionId": 296367,
     * "activeBalanceBefore": 9992,
     * "reservedBalanceBefore": 4,
     * "operationType": "INPUT",
     * "amount": 1,
     * "commissionAmount": 0,
     * "activeBalanceAfter": 9993,
     * "reservedBalanceAfter": 4,
     * "sourceType": "ORDER",
     * "sourceTypeId": "ORDER",
     * "sourceId": 38760,
     * "transactionStatus": "CREATED",
     * "walletId": 4280
     * "userId": 495
     * },
     * ]
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse MissingParamError
     * @apiUse InternalServerError
     */
  /*  @RequestMapping(value = "/myStatementData", method = GET, produces = "application/json; charset=UTF-8")
    public List<AccountStatementApiDto> getMyAccountStatementData(
            @RequestParam("walletId") Integer walletId,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit,
            HttpServletRequest request) throws JsonProcessingException {
        *//**//*
        int offsetValue = offset == null ? 0 : offset;
        int limitValue = limit == null ? -1 : limit;
        return transactionService.getAccountStatement(walletId, offsetValue, limitValue,
                localeResolver.resolveLocale(request)).stream().filter(dto -> dto.getTransactionId() != 0).map(dto -> new AccountStatementApiDto(dto,
                localeResolver.resolveLocale(request))).collect(Collectors.toList());
    }
*/
    /**
     * @api {get} /api/dashboard/myInputoutputData Input/output data
     * @apiName getMyInputoutputData
     * @apiGroup Dashboard
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription returns list of user's input/output orders to show in pages "History input/output"
     * @apiParam {Number} offset - (OPTIONAL) offset for pagination
     * @apiParam {Number} limit - (OPTIONAL) limit for pagination
     * @apiSuccess {Array} result Result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {String} data.datetime date and time of transaction creation
     * @apiSuccess {String} data.currencyName
     * @apiSuccess {Number} data.amount transaction amount
     * @apiSuccess {Number} data.commissionAmount commission amount
     * @apiSuccess {String} data.merchantName name of merchant
     * @apiSuccess {String} data.operationType INPUT, OUTPUT, WALLET_INNER_TRANSFER
     * @apiSuccess {Integer} data.transactionId transaction id
     * @apiSuccess {Integer} data.sourceId id of transaction source
     * @apiSuccess {String} data.transactionProvided localized message for transaction status
     * @apiSuccess {Integer} data.userId user id
     * @apiSuccess {String} data.bankAccount account of bank
     * @apiSuccess {String} data.invoiceStatus if present, indicates current status of input/output payment
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "datetime": 1494585059000,
     * "currencyName": "IDR",
     * "amount": 100305,
     * "commissionAmount": 0,
     * "merchantName": "Invoice",
     * "operationType": "Input",
     * "transactionId": 1775518,
     * "sourceId": 1775518,
     * "transactionProvided": "Completed",
     * "userId": 495,
     * "bankAccount": "1440099965557",
     * "invoiceStatus": "ACCEPTED_ADMIN"
     * <p>
     * },
     * {
     * "datetime": 1473260819000,
     * "currencyName": "EDR",
     * "amount": 9.95,
     * "commissionAmount": 0.05,
     * "merchantName": "E-DinarCoin",
     * "operationType": "Output",
     * "transactionId": 126598,
     * "transactionProvided": "Pending"
     * }
     * ]
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse MissingParamError
     * @apiUse InternalServerError
     */
 /*   @RequestMapping(value = "/myInputoutputData", method = GET, produces = "application/json; charset=UTF-8")
    public List<MyInputOutputHistoryApiDto> getMyInputoutputData(
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit,
            HttpServletRequest request) {


        int offsetValue = offset == null ? 0 : offset;
        int limitValue = limit == null ? -1 : limit;
        List<MyInputOutputHistoryApiDto> data = inputOutputService.getMyInputOutputHistory(getAuthenticatedUserEmail(),
                offsetValue, limitValue, localeResolver.resolveLocale(request)).stream()
                .map(dto -> new MyInputOutputHistoryApiDto(dto, messageSource, localeResolver.resolveLocale(request))).collect(Collectors.toList());
        return data;


    }*/

    /**
     * @api {get} /api/dashboard/transferLimits Limits for transfers
     * @apiName retrieveMinTransferLimits
     * @apiGroup Dashboard
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription returns list of transfer limits for currencies
     * @apiParamExample Request Example:
     * /api/dashboard/transferLimits
     * @apiSuccess {Array} result Result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.currencyId currency id
     * @apiSuccess {Number} data.transferMinLimit min limit for transfer
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "currencyId": 1,
     * "transferMinLimit": 200
     * },
     * {
     * "currencyId": 2,
     * "transferMinLimit": 5
     * }
     * ]
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InternalServerError
     */
 /*   @RequestMapping(value = "/transferLimits", method = GET)
    public List<TransferLimitDto> retrieveMinTransferLimits(@RequestParam(required = false) Integer[] currencyIds) {
        List<Integer> currencyIdList = currencyIds == null || currencyIds.length == 0 ? Collections.EMPTY_LIST : Arrays.asList(currencyIds);
        return currencyService.retrieveMinTransferLimits(currencyIdList);
    }

    @RequestMapping(value = "/test/currencyPairRates", method = GET, produces = "application/json; charset=UTF-8")
    public ResponseEntity<Void> retrieveCurrencyPairRates() {
        try {
            stockExchangeService.retrieveCurrencies();
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }*/

    /**
     * @api {get} /api/dashboard/stockExchangeStatistics Get stock exchange statistics
     * @apiName getStockExchangeStatistics
     * @apiGroup Dashboard
     * @apiUse TokenHeader
     * @apiParam {Array} pairs id of currency pair
     * @apiParamExample Request Example:
     * /api/dashboard/stockExchangeStatistics?pairs=1,2
     * @apiPermission User
     * @apiDescription Get statistics from other cryptocurrency exchanges
     * @apiSuccess (200) {Array} exchangeStats statistics for currency pair
     * @apiSuccess (200) {Object} data item of exchange stats - corresponds to single stock exchange
     * @apiSuccess (200) {String} data.stockExchange stock exchange name
     * @apiSuccess (200) {Number} data.last price of last deal
     * @apiSuccess (200) {Number} data.buy highest bid price
     * @apiSuccess (200) {Number} data.sell lowest ask price
     * @apiSuccess (200) {Number} data.low lowest price for last 24 hours
     * @apiSuccess (200) {Number} data.high highest price for last 24 hours
     * @apiSuccess (200) {Number} data.volume trade volume
     * @apiSuccess (200) {Number} data.timestamp time when data were retrieved
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "stockExchange": "xBTCe",
     * "last": 848,
     * "buy": 848.11,
     * "sell": 848,
     * "low": 797.002,
     * "high": 856.007,
     * "volume": 21525.02,
     * "timestamp": 1482404750000
     * },
     * {
     * "stockExchange": "BITFINEX",
     * "last": 865,
     * "buy": 865,
     * "sell": 865.08,
     * "low": 807.14,
     * "high": 874,
     * "volume": 19583.3610558,
     * "timestamp": 1482404750000
     * }
     * ]
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse CurrencyPairNotFoundError
     * @apiUse InternalServerError
     */
   /* @RequestMapping(value = "/stockExchangeStatistics", method = GET, produces = "application/json; charset=UTF-8")
    public List<StockExchangeStats> getStockExchangeStatistics(@RequestParam Integer currencyPairId) {
        return stockExchangeService.getStockExchangeStatistics(currencyPairId);
    }


    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ApiError mismatchArgumentsErrorHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.INVALID_PARAM_VALUE, req.getRequestURL(), exception);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ApiError missingServletRequestParameterHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.MISSING_REQUIRED_PARAM, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(CurrencyPairNotFoundException.class)
    @ResponseBody
    public ApiError CurrencyPairNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.CURRENCY_PAIR_NOT_FOUND, req.getRequestURL(), exception);
    }
*/

    /*@ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public Map<String, Object> NullPointerHandler(HttpServletRequest req, Exception exception) {
        Map<String, Object> result = new HashMap<>();
        result.put("stacktrace", Arrays.asList(exception.getStackTrace()));
        return result;
    }*/

   /* @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiError OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.INTERNAL_SERVER_ERROR, req.getRequestURL(), exception);
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return "";
        }
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
*/

}
