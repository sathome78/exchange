package me.exrates.controller.mobile;

import me.exrates.model.dto.OrderCreateDto;
import me.exrates.service.CommissionService;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * Created by OLEG on 29.08.2016.
 */

/**
 * ALL controleers oommented for security reasons
 */
@RestController
@RequestMapping(value = "/api/orders")
public class MobileOrderController {

    private static final Logger LOGGER = LogManager.getLogger("mobileAPI");

    private Map<UUID, OrderCreateDto> creationUnconfirmedOrders = new ConcurrentReferenceHashMap<>();

    @Autowired
    OrderService orderService;

    @Autowired
    CommissionService commissionService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    WalletService walletService;

    @Autowired
    UserService userService;

    @Autowired
    MessageSource messageSource;


    /**
     * @apiDefine InvalidParamError
     * @apiError (400) {String} errorCode error code
     * @apiError (400) {String} url request URL
     * @apiError (400) {String} cause name of root exception
     * @apiError (400) {String} details detail of root exception
     * @apiErrorExample {json} Invalid Parameter Value:
     * HTTP/1.1 400 Bad Request
     *      {
     *          "errorCode": "INVALID_PARAM_VALUE",
     *          "url": "http://127.0.0.1:8080/api/orders/submitOrderForCreation",
     *          "cause": "OrderParamsWrongException",
     *          "detail": "{balance_4=not enough money in your wallet for the transaction!, exrate_0=Complete the field,
     *          amount_2=The value must be in the following range: 0.000000001 - 10 000, amount_1=The value must be less than 10 000,
     *          exrate_3=The exchange rate must be greater than 0}"
     *      }
     *
     * */

    /**
     * @apiDefine WrongOrderKeyError
     * @apiError (404) {String} errorCode error code
     * @apiError (404) {String} url request URL
     * @apiError (404) {String} cause name of root exception
     * @apiError (404) {String} details detail of root exception
     * @apiErrorExample {json} Order Key Not Found:
     * HTTP/1.1 404 Not Found
     *      {
     *          "errorCode": "ORDER_KEY_NOT_FOUND",
     *          "url": "http://127.0.0.1:8080/api/orders/createOrder",
     *          "cause": "WrongOrderKeyException",
     *          "detail": "No order found by key"
     *      }
     *
     * */

    /**
     * @apiDefine NotEnoughMoneyError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Insufficient Costs:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *          "errorCode": "INSUFFICIENT_FUNDS",
     *          "url": "http://127.0.0.1:8080/api/orders/acceptOrders",
     *          "cause": "InsufficientCostsForAcceptionException",
     *          "detail": "Insufficient coins for accepting the order(s)"
     *      }
     *
     * */

    /**
     * @apiDefine AlreadyAcceptedOrderError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Order Already Accepted:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *            "errorCode": "ALREADY_ACCEPTED_ORDER",
     *            "url": "http://127.0.0.1:8080/api/orders/acceptOrders",
     *            "cause": "AlreadyAcceptedOrderException",
     *            "detail": "The order is accepted already"
     *      }
     *
     * */

    /**
     * @apiDefine OrderNotFoundError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Order Not Found:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *           "errorCode": "ORDER_NOT_FOUND",
     *           "url": "http://127.0.0.1:8080/api/orders/delete",
     *           "cause": "OrderNotFoundException",
     *           "detail": "Order not found with ID 30,000,000"
     *      }
     *
     * */


    /**
     * @api {post} /api/orders/submitOrderForCreation Submit order for creation
     * @apiName submitOrderForCreation
     * @apiGroup Orders
     * @apiUse TokenHeader
     * @apiParam {Integer} currencyPairId id of currency pair
     * @apiParam {String} orderType type of order (valid values: "SELL", "BUY")
     * @apiParam {Number} amount amount in base currency
     * @apiParam {Number} rate exchange rate
     * @apiParamExample {json} Request Example:
     * {
     * "currencyPairId":1,
     * "orderType":"SELL",
     * "amount": 2.0,
     * "rate": 600
     * }
     * @apiPermission User
     * @apiDescription Method accepts basic order params, which are validated (including check for necessary amount
     * in user's wallet); returns summary and key to be passed after confirmation to createOrder or cancelCreation method.
     * @apiSuccess (200) {Object} summary Submitted order summary
     * @apiSuccess (200) {String} summary.currencyPairName currency pair
     * @apiSuccess (200) {String} summary.operationTypeName order type (BUY, SELL)
     * @apiSuccess (200) {Number} summary.balance user base currency wallet balance
     * @apiSuccess (200) {Number} summary.amount amount in base currency
     * @apiSuccess (200) {Number} summary.exrate exchange rate
     * @apiSuccess (200) {Number} summary.total total amount of convert currency
     * @apiSuccess (200) {Number} summary.commission commission amount
     * @apiSuccess (200) {Number} summary.totalWithComission total amount of convert currency with commission
     * @apiSuccess (200) {String} key Key to be passed to createOrder method after confirmation
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "currencyPairId": 1,
     * "operationType": "SELL",
     * "balance": 9993,
     * "amount": 2,
     * "exrate": 590,
     * "total": 1180,
     * "commission": 2.36,
     * "totalWithComission": 1177.64,
     * "key": "8d096c36-530a-46dc-a790-340a8fe135c3"
     * }
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse InternalServerError
     */

   /* @RequestMapping(value = "/submitOrderForCreation", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public OrderSummaryDto submitOrderForCreation(@Valid @RequestBody OrderCreationParamsDto orderCreationParamsDto, HttpServletRequest request) {
        LOGGER.debug("Order creation params" + orderCreationParamsDto);
        CurrencyPair activeCurrencyPair = currencyService.findCurrencyPairById(orderCreationParamsDto.getCurrencyPairId());
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        OrderCreateDto orderCreateDto = orderService.prepareOrderRest(orderCreationParamsDto, userEmail, userLocale, activeCurrencyPair.getPairType().getOrderBaseType());
        UUID orderKey = UUID.randomUUID();
        creationUnconfirmedOrders.put(orderKey, orderCreateDto);
        return new OrderSummaryDto(orderCreateDto, orderKey.toString());

    }*/

    /**
     * @api {post} /api/orders/createOrder Create order
     * @apiName createOrder
     * @apiGroup Orders
     * @apiUse TokenHeader
     * @apiParam {String} orderKey Key string returned from submitOrderForCreation method
     * @apiPermission User
     * @apiDescription Method accepts order key returned by submitOrderForCreation, creates the order
     * and stores it in DB. If there are no available orders for auto-accept (or created order is larger than all of the accepted ones),
     * returns ID of created order. If one or more orders were accepted automatically, returns also their quantity.
     * If the opposite order was accepted partially, returns the full amount of that order and the accepted part.
     * @apiSuccess (201) {Integer} createdOrderId id of created order
     * @apiSuccess (201) {Integer} autoAcceptedQuantity number of orders accepted automatically
     * @apiSuccess (201) {Integer} partiallyAcceptedAmount amount that was accepted partially
     * @apiSuccess (201) {Integer} partiallyAcceptedOrderFullAmount full amount of partially accepted order
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 201 Created
     * {
     * "autoAcceptedQuantity": 3
     * "partiallyAcceptedAmount": 1.25,
     * "partiallyAcceptedOrderFullAmount": 7
     * }
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse WrongOrderKeyError
     * @apiUse NotEnoughMoneyError
     * @apiUse InternalServerError
     */
   /* @RequestMapping(value = "/createOrder", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<OrderCreationResultDto> createOrder(@RequestBody Map<String, String> body) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        try {
            OrderCreateDto orderCreateDto = removeOrderCreateDtoByKey(body);
            OrderCreationResultDto orderCreationResultDto = orderService.createPreparedOrderRest(orderCreateDto, userLocale);
            return new ResponseEntity<>(orderCreationResultDto, CREATED);
        } catch (NotEnoughUserWalletMoneyException e) {
            throw new NotEnoughUserWalletMoneyException(messageSource.getMessage("validation.orderNotEnoughMoney", null, userLocale));
        }
    }*/

    /**
     * @api {post} /api/orders/cancelCreation Cancel order
     * @apiName cancelOrderCreation
     * @apiGroup Orders
     * @apiUse TokenHeader
     * @apiParam {String} orderKey Key string returned from submitOrderForCreation method
     * @apiPermission User
     * @apiDescription Cancels order creation. Returns empty response with HTTP 200 status
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse WrongOrderKeyError
     * @apiUse InternalServerError
     */
 /*   @RequestMapping(value = "/cancelCreation", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> cancelOrderCreation(@RequestBody Map<String, String> body) {
        removeOrderCreateDtoByKey(body);
        return new ResponseEntity<>(OK);
    }


    private OrderCreateDto removeOrderCreateDtoByKey(Map<String, String> body) {
        if (!body.containsKey("orderKey")) {
            throw new OrderParamsWrongException("No key supplied!");
        }
        String orderKey = body.get("orderKey");
        UUID orderKeyUUID = UUID.fromString(orderKey);
        OrderCreateDto orderCreateDto = creationUnconfirmedOrders.remove(orderKeyUUID);
        if (orderCreateDto == null) {
            throw new WrongOrderKeyException("No order found by key");
        }
        return orderCreateDto;
    }*/


    /**
     * @api {post} /api/orders/acceptOrders Accept orders
     * @apiName acceptOrderList
     * @apiGroup Orders
     * @apiUse TokenHeader
     * @apiParam {Array} orderIdsList List of IDs of orders to be accepted
     * @apiParamExample {json} Request example
     * {
     * "orderIdsList": [18381, 18382, 18383]
     * }
     * @apiPermission User
     * @apiDescription Accept list of orders.
     * @apiSuccess (200) {String} result Result detail
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "result": "The 1 orders have been accepted successfully"
     * }
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse NotEnoughMoneyError
     * @apiUse AlreadyAcceptedOrderError
     * @apiUse InternalServerError
     */
   /* @RequestMapping(value = "/acceptOrders", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Integer acceptOrderList(@RequestBody Map<String, List<Integer>> body) {


        List<Integer> ordersList = body.get("orderIdsList");
        if (ordersList == null) {
            throw new OrderParamsWrongException("Field \"orderIdsList\" is missing!");
        }

        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            int userId = userService.getIdByEmail(userEmail);
            Locale userLocale = userService.getUserLocaleForMobile(userEmail);
            orderService.acceptOrdersList(userId, ordersList, userLocale);
        } catch (Exception e) {
            throw e;
        }

        return ordersList.size();

    }*/


    /**
     * @api {post} /api/orders/delete/:orderId Delete order
     * @apiName deleteOrder
     * @apiGroup Orders
     * @apiUse TokenHeader
     * @apiParam {Integer} orderId id of order to be deleted
     * @apiParamExample Request Example:
     * /api/orders/delete?orderId=18382
     * @apiPermission User
     * @apiDescription Method accepts order data returned from submitDeleteOrder method and returns notification of successful deletion
     * @apiSuccess (200) {String} result Result detail
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "result": "order was successfull deleted"
     * }
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse OrderNotFoundError
     * @apiUse InternalServerError
     */
  /*  @RequestMapping(value = "/delete/{orderId}", method = DELETE, produces = "application/json;charset=utf-8")
    public boolean deleteOrder(@PathVariable Integer orderId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);

        OrderCreateDto orderCreateDto = orderService.getMyOrderById(orderId);
        if (orderCreateDto == null) {
            throw new OrderNotFoundException(messageSource.getMessage("orders.getordererror", new Object[]{orderId}, userLocale));
        }
        if (!orderService.cancelOrder(new ExOrder(orderCreateDto), userLocale)) {
            throw new OrderCancellingException(messageSource.getMessage("myorders.deletefailed", null, userLocale));
        }
        return true;
    }*/


   /* @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ApiError httpMessageNotReadableExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(REQUEST_NOT_READABLE, req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, OrderParamsWrongException.class, MethodArgumentTypeMismatchException.class})
    public ApiError methodArgumentNotValidExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(INVALID_PARAM_VALUE, req.getRequestURL(), exception);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ApiError missingServletRequestParameterHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.MISSING_REQUIRED_PARAM, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(WrongOrderKeyException.class)
    public ApiError wrongOrderKeyExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ORDER_KEY_NOT_FOUND, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler({NotEnoughMoneyException.class, NotEnoughUserWalletMoneyException.class, InsufficientCostsForAcceptionException.class})
    public ApiError notEnoughMoneyExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(INSUFFICIENT_FUNDS, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(AlreadyAcceptedOrderException.class)
    public ApiError alreadyAcceptExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ALREADY_ACCEPTED_ORDER, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseBody
    public ApiError orderNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ORDER_NOT_FOUND, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(CurrencyPairNotFoundException.class)
    @ResponseBody
    public ApiError currencyPairNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.CURRENCY_PAIR_NOT_FOUND, req.getRequestURL(), exception);
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiError otherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.INTERNAL_SERVER_ERROR, req.getRequestURL(), exception);
    }*/


}
