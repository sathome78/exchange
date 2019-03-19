package me.exrates.controller.openAPI;

import com.google.common.base.Strings;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.CallbackURL;
import me.exrates.model.dto.ExOrderDto;
import me.exrates.model.dto.OrderCreationResultDto;
import me.exrates.model.dto.openAPI.OpenOrderDto;
import me.exrates.model.dto.openAPI.OrderCreationResultOpenApiDto;
import me.exrates.model.dto.openAPI.OrderParamsDto;
import me.exrates.model.enums.OrderType;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.exception.CallBackUrlAlreadyExistException;
import me.exrates.dao.exception.notfound.OrderNotFoundException;
import me.exrates.service.exception.UserOperationAccessException;
import me.exrates.service.userOperation.UserOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static me.exrates.service.util.OpenApiUtils.transformCurrencyPair;

@RestController
@RequestMapping(value = "/openapi/v1/orders",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OpenApiOrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final UserOperationService userOperationService;
    private final MessageSource messageSource;

    @Autowired
    public OpenApiOrderController(OrderService orderService,
                                  UserService userService,
                                  UserOperationService userOperationService,
                                  MessageSource messageSource) {
        this.orderService = orderService;
        this.userService = userService;
        this.userOperationService = userOperationService;
        this.messageSource = messageSource;
    }

    /**
     * @api {post} /openapi/v1/orders/create Create order
     * @apiName Creates order
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiUse APIJson
     * @apiPermission NonPublicAuth
     * @apiDescription Creates Order
     *
     * @apiParam {String} currency_pair Name of currency pair (e.g. btc_usd)
     * @apiParam {String} order_type Type of order (BUY or SELL)
     * @apiParam {Number} amount Amount in base currency
     * @apiParam {Number} price Exchange rate
     *
     * @apiParamExample Request Example:
     * /openapi/v1/orders/create
     * {
     *      "currencyPair": "btc_usd",
     *      "orderType": "BUY",
     *      "amount": 2.3,
     *      "price": 1.0
     * }
     *
     * @apiSuccess {Object} orderCreationResult Order creation result information
     * @apiSuccess {Integer} orderCreationResult.created_order_id Id of created order (not shown in case of partial accept)
     * @apiSuccess {Integer} orderCreationResult.auto_accepted_quantity Number of orders accepted automatically (not shown if no orders were auto-accepted)
     * @apiSuccess {Number} orderCreationResult.partially_accepted_amount Amount that was accepted partially (shown only in case of partial accept)
     *
     * @apiError AuthenticationNotAvailableException
     * @apiError InvalidCurrencyPairFormatException
     * @apiError OrderParamsWrongException
     * @apiError NotFoundException
     * @apiError ProcessingException
     */
    @PreAuthorize("hasAuthority('TRADE')")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<OrderCreationResultOpenApiDto> createOrder(@RequestBody @Valid OrderParamsDto orderParamsDto) {
        String currencyPairName = transformCurrencyPair(orderParamsDto.getCurrencyPair());
        String principal = userService.getUserEmailFromSecurityContext();
        final int userId = userService.getIdByEmail(principal);

        Locale locale = new Locale(userService.getPreferedLang(userId));

        boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userId, UserOperationAuthority.TRADING);
        if (!accessToOperationForUser) {
            throw new UserOperationAccessException(messageSource.getMessage("merchant.operationNotAvailable", null, locale));
        }
        OrderCreationResultDto resultDto = orderService.prepareAndCreateOrderRest(currencyPairName, orderParamsDto.getOrderType().getOperationType(),
                orderParamsDto.getAmount(), orderParamsDto.getPrice(), principal);
        return new ResponseEntity<>(new OrderCreationResultOpenApiDto(resultDto), HttpStatus.CREATED);
    }

    /**
     * @api {get} /openapi/v1/orders/accept/{orderId} Accept order by id
     * @apiName Accept order
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiUse APIJson
     * @apiPermission NonPublicAuth
     * @apiDescription Accepts order
     *
     * @apiParam {Integer} order id
     *
     * @apiParamExample Request Example:
     * /openapi/v1/orders/accept/1233
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * @apiErrorExample {json} Error-Response:
     * HTTP/1.1 200 OK
     * {
     *      "errorCode": "ACCEPTING_ORDER_ERROR",
     *      "url" : String,
     *      "detail" : String
     * }
     *
     * @apiError AuthenticationNotAvailableException
     * @apiError NotFoundException
     * @apiError ProcessingException
     **/
    @PreAuthorize("hasAuthority('TRADE') and hasAuthority('ACCEPT_BY_ID')")
    @GetMapping(value = "/accept/{orderId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> acceptOrder(@PathVariable Integer orderId) {
        String userEmail = userService.getUserEmailFromSecurityContext();
        orderService.acceptOrder(userEmail, orderId);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {get} /openapi/v1/orders/{orderId} Find order by id
     * @apiName Get order by id
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiPermission NonPublicAuth
     * @apiDescription Accepts order
     *
     * @apiParam {Integer} order_id Id of requested order
     *
     * @apiParamExample Request Example:
     * /openapi/v1/orders/123
     *
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.id Order id
     * @apiSuccess {Integer} data.currencyPairId Currency pair id
     * @apiSuccess {String} data.operationType type of order operation (BUY or SELL)
     * @apiSuccess {Number} data.exRate Rate
     * @apiSuccess {Number} data.amountBase Amount to process
     * @apiSuccess {Number} data.amountConvert Base amount multiply by exRate
     * @apiSuccess {Number} data.commission Commission's amount
     * @apiSuccess {Integer} data.userAcceptorId User-acceptor id
     * @apiSuccess {LocalDateTime} data.created When order was created
     * @apiSuccess {LocalDateTime} data.accepted When order was accepted
     * @apiSuccess {Integer} data.userAcceptorId User-acceptor id
     * @apiSuccess {String} data.status type of order status (INPROCESS, OPENED, CLOSED, CANCELLED, DELETED, DRAFT, SPLIT_CLOSED)
     * @apiSuccess {Integer} data.sourceId Source id
     * @apiSuccess {Number} data.stop  Stop price
     * @apiSuccess {String} data.baseType type of order status (LIMIT, STOP_LIMIT, ICO)
     * @apiSuccess {Number} data.partiallyAcceptedAmount  Partially accepted amount
     *
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     *      "id": 402298,
     *      "currencyPairId": 1,
     *      "operationType": "BUY",
     *      "exRate": 2000.009900479,
     *      "amountBase": 1,
     *      "amountConvert": 2000.009900479,
     *      "commission": 4.000019801,
     *      "userAcceptorId": 0,
     *      "created": "2018-12-22 00:49:11",
     *      "accepted": null",
     *      "status": "OPENED",
     *      "baseType": "LIMIT",
     *      "stop": null,
     *      "partiallyAcceptedAmount": null
     * }
     *
     * @apiError AuthenticationNotAvailableException
     * @apiError NotFoundException
     **/
    // "Order with id: 40229 not found among created or accepted orders"
    @PreAuthorize("hasAuthority('TRADE')")
    @GetMapping(value = "/{orderId}")
    public ResponseEntity<ExOrderDto> getById(@PathVariable Integer orderId) {
        Integer userId = userService.getIdByEmail(userService.getUserEmailFromSecurityContext());

        Optional<ExOrder> exOrder = Optional.ofNullable(orderService.getOrderById(orderId, userId));
        ExOrder order = exOrder.orElseThrow(() -> new OrderNotFoundException("Order with id: " + orderId
                + " not found among created or accepted orders"));
        return ResponseEntity.ok(ExOrderDto.valueOf(order));
    }

    /**
     * @api {delete} /openapi/v1/orders/{orderId} Cancel order by id
     * @apiName Cancel order by order id
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiPermission NonPublicAuth
     * @apiDescription Cancel order by order id
     *
     * @apiParam {Integer} order_id Id of order to be cancelled
     *
     * @apiParamExample Request Example:
     * /openapi/v1/orders/123
     *
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     *
     * @apiError AuthenticationNotAvailableException
     * @apiError IncorrectCurrentUserException
     * @apiError NotFoundException
     * @apiError ProcessingException
     */
    @PreAuthorize("hasAuthority('TRADE')")
    @DeleteMapping(value = "/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Integer orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {post} /openapi/v1/orders/callback/add Add callback
     * @apiName add callback
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiUse APIJson
     * @apiPermission NonPublicAuth
     * @apiDescription Add callback
     *
     * @apiParamExample Request Example:
     * /openapi/v1/orders/callback/add
     * <p>
     * {
     *      "callbackURL": String,
     *      "pairId": Integer
     * }
     *
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     *      "status": true
     * }
     *
     * @apiError AuthenticationNotAvailableException
     * @apiError CallBackUrlAlreadyExistException
     * @apiError NotFoundException
     */
    @PreAuthorize("hasAuthority('TRADE')")
    @PostMapping(value = "/callback/add", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Object> addCallback(@RequestBody CallbackURL callbackUrl) throws CallBackUrlAlreadyExistException {
        Map<String, Object> responseBody = new HashMap<>();
        String userEmail = userService.getUserEmailFromSecurityContext();
        int userId = userService.getIdByEmail(userEmail);
        if (Strings.isNullOrEmpty(callbackUrl.getCallbackURL())) {
            responseBody.put("status", "false");
            responseBody.put("error", " Callback url is null or empty");
            return responseBody;
        }
        int affectedRowCount = userService.setCallbackURL(userId, callbackUrl);
        responseBody.put("status", affectedRowCount != 0);
        return responseBody;
    }

    /**
     * @api {put} /openapi/v1/orders/callback/add Update callback
     * @apiName update callback
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiUse APIJson
     * @apiPermission NonPublicAuth
     * @apiDescription Update callback
     *
     * @apiParamExample Request Example:
     * /openapi/v1/orders/callback/update
     * {
     *      "callbackURL": String,
     *      "pairId": Integer
     * }
     *
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     *      "status": true
     * }
     *
     * @apiErrorExample {json} Error-Response:
     * HTTP/1.1 200 OK
     * {
     *      "status": "false",
     *      "error" : " Callback url is null or empty"
     * }
     *
     * @apiError AuthenticationNotAvailableException
     * @apiError NotFoundException
     */
    @PreAuthorize("hasAuthority('TRADE')")
    @PutMapping(value = "/callback/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Object> updateCallback(@RequestBody CallbackURL callbackUrl) {
        Map<String, Object> responseBody = new HashMap<>();
        int userId = userService.getIdByEmail(userService.getUserEmailFromSecurityContext());
        if (Strings.isNullOrEmpty(callbackUrl.getCallbackURL()) && Objects.nonNull(callbackUrl.getPairId())) {
            responseBody.put("status", "false");
            responseBody.put("error", " Callback url is null or empty");
            return responseBody;
        }
        int affectedRowCount = userService.updateCallbackURL(userId, callbackUrl);
        responseBody.put("status", affectedRowCount != 0);
        return responseBody;
    }

    /**
     * @api {get} /openapi/v1/orders/open/{order_type}?currency_pair Open orders
     * @apiName Open orders
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiPermission NonPublicAuth
     * @apiDescription Buy or sell open orders ordered by price (SELL ascending, BUY descending)
     *
     * @apiParam {String} order_type Type of order (BUY or SELL)
     * @apiParam {String} currency_pair Name of currency pair
     *
     * @apiParamExample Request Example:
     * /openapi/v1/orders/open/SELL?btc_usd
     *
     * @apiSuccess {Array} openOrder Open Order Result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.id Order id
     * @apiSuccess {String} data.order_type type of order (BUY or SELL)
     * @apiSuccess {Number} data.amount Amount in base currency
     * @apiSuccess {Number} data.price Exchange rate
     *
     * @apiError InvalidCurrencyPairFormatException
     * @apiError NotFoundException
     */
    @GetMapping(value = "/open/{order_type}")
    public List<OpenOrderDto> openOrders(@PathVariable("order_type") OrderType orderType,
                                         @RequestParam("currency_pair") String currencyPair) {
        String currencyPairName = transformCurrencyPair(currencyPair);
        return orderService.getOpenOrders(currencyPairName, orderType);
    }
}