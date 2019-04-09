package me.exrates.controller.openAPI;

import com.google.common.base.Strings;
import me.exrates.model.ExOrder;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.CallbackURL;
import me.exrates.model.dto.ExOrderDto;
import me.exrates.model.dto.OrderCreationResultDto;
import me.exrates.model.dto.openAPI.OpenOrderDto;
import me.exrates.model.dto.openAPI.OrderCreationResultOpenApiDto;
import me.exrates.model.dto.openAPI.OrderCreationResultOpenApiDtoExtended;
import me.exrates.model.dto.openAPI.OrderParamsDto;
import me.exrates.model.enums.OrderType;
import me.exrates.model.exceptions.OpenApiException;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.exception.CallBackUrlAlreadyExistException;
import me.exrates.service.exception.IncorrectCurrentUserException;
import me.exrates.service.exception.api.OrderParamsWrongException;
import me.exrates.service.exception.process.CancelOrderException;
import me.exrates.service.exception.process.NotCreatableOrderException;
import me.exrates.service.exception.process.OrderAcceptionException;
import me.exrates.service.exception.process.OrderCancellingException;
import me.exrates.service.openapi.OpenApiCommonService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(value = "/openapi/v1/orders",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OpenApiOrderController {

    private final OpenApiCommonService openApiCommonService;
    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OpenApiOrderController(OpenApiCommonService openApiCommonService,
                                  OrderService orderService,
                                  UserService userService) {
        this.openApiCommonService = openApiCommonService;
        this.orderService = orderService;
        this.userService = userService;
    }

    /**
     * @api {post} /openapi/v1/orders/create Create order
     * @apiName Creates order
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiUse APIJson
     * @apiPermission NonPublicAuth
     * @apiDescription Creates Order
     * @apiParam {String} currency_pair Name of currency pair (e.g. btc_usd)
     * @apiParam {String} order_type Type of order (BUY or SELL)
     * @apiParam {Number} amount Amount in base currency
     * @apiParam {Number} price Exchange rate
     * @apiParamExample Request Example:
     * /openapi/v1/orders/create
     * {
     * "currencyPair": "btc_usd",
     * "orderType": "BUY",
     * "amount": 2.3,
     * "price": 1.0
     * }
     * @apiSuccess {Object} orderCreationResult Order creation result information
     * @apiSuccess {Integer} orderCreationResult.created_order_id Id of created order (not shown in case of partial accept)
     * @apiSuccess {Integer} orderCreationResult.auto_accepted_quantity Number of orders accepted automatically (not shown if no orders were auto-accepted)
     * @apiSuccess {Number} orderCreationResult.partially_accepted_amount Amount that was accepted partially (shown only in case of partial accept)
     * @apiError AuthenticationNotAvailableException
     * @apiError InvalidCurrencyPairFormatException
     * @apiError OrderParamsWrongException
     * @apiError NotFoundException
     * @apiError ProcessingException
     */
    @PreAuthorize("hasAuthority('TRADE')")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<OrderCreationResultOpenApiDto> createOrder(@RequestBody @Valid OrderParamsDto orderParamsDto) {
        String principal = userService.getUserEmailFromSecurityContext();
        String currencyPairName = openApiCommonService.validateUserAndCurrencyPair(orderParamsDto.getCurrencyPair());
        try {
            OrderCreationResultDto resultDto = orderService.prepareAndCreateOrderRest(currencyPairName, orderParamsDto.getOrderType().getOperationType(),
                    orderParamsDto.getAmount(), orderParamsDto.getPrice(), principal);
            return new ResponseEntity<>(new OrderCreationResultOpenApiDto(resultDto), HttpStatus.CREATED);
        } catch (NotCreatableOrderException e) {
            // "This pair available only through website"
            throw new OpenApiException(ErrorApiTitles.API_UNAVAILABLE_CURRENCY_PAIR, e.getMessage());
        } catch (OrderParamsWrongException e) {
            throw new OpenApiException(ErrorApiTitles.API_WRONG_ORDER_PARAMS, e.getMessage());
        } catch (Exception e) {
            throw new OpenApiException(ErrorApiTitles.API_CREATE_ORDER_ERROR, e.getMessage());
        }
    }


    /**
     * @api {post} /openapi/v1/orders/create/extended Create order
     * @apiName Creates order
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiPermission NonPublicAuth
     * @apiDescription Creates Order
     * @apiParam {String} currency_pair Name of currency pair (e.g. btc_usd)
     * @apiParam {String} order_type Type of order (BUY or SELL)
     * @apiParam {Number} amount Amount in base currency
     * @apiParam {Number} price Exchange rate
     * @apiParamExample Request Example:
     * /openapi/v1/orders/create
     * {
     * "currencyPair": "btc_usd",
     * "orderType": "BUY",
     * "amount": 2.3,
     * "price": 1.0
     * }
     * @apiSuccess {Object} orderCreationResult Order creation result information
     * @apiSuccess {Integer} orderCreationResult.created_order_id Id of created order (not shown in case of partial accept)
     * @apiSuccess {Integer} orderCreationResult.auto_accepted_quantity Number of orders accepted automatically (not shown if no orders were auto-accepted)
     * @apiSuccess {Number} orderCreationResult.partially_accepted_amount Amount that was accepted partially (shown only in case of partial accept)
     * @apiSuccess {Array} orderCreationResult.fully_accepted_orders_ids ids of orders that has been fully accepted
     * @apiSuccess {Integer} orderCreationResult.partially_accepted_order_id id of order that partially accepted and splitted as a result)
     * @apiSuccess {Integer} orderCreationResult.order_id_to_accept id of order that opened and accepted as a result of partially accept)
     * @apiSuccess {Integer} orderCreationResult.order_id_to_open id of order that opened and placed in common stack as a results of partially accept)
     */
    @PreAuthorize("hasAuthority('TRADE')")
    @PostMapping(value = "/create/extended", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<OrderCreationResultOpenApiDtoExtended> createOrderExtended(@RequestBody @Valid OrderParamsDto orderParamsDto) {
        String userEmail = userService.getUserEmailFromSecurityContext();
        String currencyPairName = openApiCommonService.validateUserAndCurrencyPair(orderParamsDto.getCurrencyPair());
        try {
            OrderCreationResultDto resultDto = orderService.prepareAndCreateOrderRest(currencyPairName, orderParamsDto.getOrderType().getOperationType(),
                    orderParamsDto.getAmount(), orderParamsDto.getPrice(), userEmail);
            return new ResponseEntity<>(new OrderCreationResultOpenApiDtoExtended(resultDto), HttpStatus.CREATED);
        } catch (NotCreatableOrderException e) {
            // "This pair available only through website"
            throw new OpenApiException(ErrorApiTitles.API_UNAVAILABLE_CURRENCY_PAIR, e.getMessage());
        } catch (OrderParamsWrongException e) {
            throw new OpenApiException(ErrorApiTitles.API_WRONG_ORDER_PARAMS, e.getMessage());
        } catch (Exception e) {
            throw new OpenApiException(ErrorApiTitles.API_CREATE_ORDER_ERROR, e.getMessage());
        }
    }

    /**
     * @api {get} /openapi/v1/orders/accept/{orderId} Accept order by id
     * @apiName Accept order
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiUse APIJson
     * @apiPermission NonPublicAuth
     * @apiDescription Accepts order
     * @apiParam {Integer} order id
     * @apiParamExample Request Example:
     * /openapi/v1/orders/accept/1233
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * @apiErrorExample {json} Error-Response:
     * HTTP/1.1 200 OK
     * {
     * "errorCode": "ACCEPTING_ORDER_ERROR",
     * "url" : String,
     * "detail" : String
     * }
     * @apiError AuthenticationNotAvailableException
     * @apiError NotFoundException
     * @apiError ProcessingException
     **/
    @PreAuthorize("hasAuthority('TRADE') and hasAuthority('ACCEPT_BY_ID')")
    @GetMapping(value = "/accept/{orderId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> acceptOrder(@PathVariable Integer orderId) {
        String userEmail = userService.getUserEmailFromSecurityContext();
        try {
            orderService.acceptOrder(userEmail, orderId);
            return ResponseEntity.ok().build();
        } catch (OrderAcceptionException e) {
            throw new OpenApiException(ErrorApiTitles.API_ACCEPT_ORDER_ERROR, e.getMessage());
        }
    }

    /**
     * @api {get} /openapi/v1/orders/{orderId} Find order by id
     * @apiName Get order by id
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiPermission NonPublicAuth
     * @apiDescription Accepts order
     * @apiParam {Integer} order_id Id of requested order
     * @apiParamExample Request Example:
     * /openapi/v1/orders/123
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
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "id": 402298,
     * "currencyPairId": 1,
     * "order_type": "BUY",
     * "price": 2000.009900479,
     * "amount": 1,
     * "amountConvert": 2000.009900479,
     * "commission": 4.000019801,
     * "userAcceptorId": 0,
     * "created": "2018-12-22 00:49:11",
     * "accepted": null",
     * "status": "OPENED",
     * "baseType": "LIMIT",
     * "stop": null,
     * "partiallyAcceptedAmount": null
     * }
     * @apiErrorExample {json}  Error-Response:
     * <p>
     * {
     * <p>
     * }
     **/
    // "Order with id: 40229 not found among created or accepted orders"
    @PreAuthorize("hasAuthority('TRADE')")
    @GetMapping(value = "/{orderId}")
    public ResponseEntity<ExOrderDto> getById(@PathVariable Integer orderId) {
        int userId = userService.getIdByEmail(userService.getUserEmailFromSecurityContext());
        Optional<ExOrder> exOrder = Optional.ofNullable(orderService.getOrderById(orderId, userId));
        ExOrder order = exOrder.orElseThrow(() -> new OpenApiException(ErrorApiTitles.API_ORDER_NOT_FOUND, "Order with id: " + orderId
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
     * @apiParam {Integer} order_id Id of order to be cancelled
     * @apiParamExample Request Example:
     * /openapi/v1/orders/123
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * @apiError AuthenticationNotAvailableException
     * @apiError IncorrectCurrentUserException
     * @apiError NotFoundException
     * @apiError ProcessingException
     */
    @PreAuthorize("hasAuthority('TRADE')")
    @DeleteMapping(value = "/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Integer orderId) {
        try {
            orderService.cancelOrder(orderId);
            return ResponseEntity.ok().build();
        } catch (IncorrectCurrentUserException e) {
            throw new OpenApiException(ErrorApiTitles.API_ORDER_CREATED_BY_ANOTHER_USER, e.getMessage());
        } catch (CancelOrderException | OrderCancellingException e) {
            throw new OpenApiException(ErrorApiTitles.API_ORDER_CANCEL_ERROR, e.getMessage());
        }
    }

    /**
     * @api {post} /openapi/v1/orders/callback/add Add callback
     * @apiName add callback
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiUse APIJson
     * @apiPermission NonPublicAuth
     * @apiDescription Add callback
     * @apiParamExample Request Example:
     * /openapi/v1/orders/callback/add
     * <p>
     * {
     * "callbackURL": String,
     * "pairId": Integer
     * }
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "status": true
     * }
     * @apiError AuthenticationNotAvailableException
     * @apiError CallBackUrlAlreadyExistException
     * @apiError NotFoundException
     */
    @PreAuthorize("hasAuthority('TRADE')")
    @PostMapping(value = "/callback/add", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Object> addCallback(@RequestBody CallbackURL callbackUrl) {
        Map<String, Object> responseBody = new HashMap<>();
        String userEmail = userService.getUserEmailFromSecurityContext();
        int userId = userService.getIdByEmail(userEmail);
        if (Strings.isNullOrEmpty(callbackUrl.getCallbackURL())) {
            responseBody.put("status", "false");
            responseBody.put("error", " Callback url is null or empty");
            return responseBody;
        }
        try {
            int affectedRowCount = userService.setCallbackURL(userId, callbackUrl);
            responseBody.put("status", affectedRowCount != 0);
            return responseBody;
        } catch (CallBackUrlAlreadyExistException e) {
            throw new OpenApiException(ErrorApiTitles.API_ORDER_ADD_CALLBACK_ERROR, e.getMessage());
        }
    }

    /**
     * @api {put} /openapi/v1/orders/callback/add Update callback
     * @apiName update callback
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiUse APIJson
     * @apiPermission NonPublicAuth
     * @apiDescription Update callback
     * @apiParamExample Request Example:
     * /openapi/v1/orders/callback/update
     * {
     * "callbackURL": String,
     * "pairId": Integer
     * }
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "status": true
     * }
     * @apiErrorExample {json} Error-Response:
     * HTTP/1.1 200 OK
     * {
     * "status": "false",
     * "error" : " Callback url is null or empty"
     * }
     * @apiError AuthenticationNotAvailableException
     * @apiError NotFoundException
     */
    @PreAuthorize("hasAuthority('TRADE')")
    @PutMapping(value = "/callback/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
     * @apiParam {String} order_type Type of order (BUY or SELL)
     * @apiParam {String} currency_pair Name of currency pair
     * @apiParamExample Request Example:
     * /openapi/v1/orders/open/SELL?btc_usd
     * @apiSuccess {Array} openOrder Open Order Result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.id Order id
     * @apiSuccess {String} data.order_type type of order (BUY or SELL)
     * @apiSuccess {Number} data.amount Amount in base currency
     * @apiSuccess {Number} data.price Exchange rate
     * @apiError InvalidCurrencyPairFormatException
     * @apiError NotFoundException
     */
    @GetMapping(value = "/open/{order_type}")
    public List<OpenOrderDto> openOrders(@PathVariable("order_type") OrderType orderType,
                                         @RequestParam("currency_pair") String currencyPair) {
        String currencyPairName = openApiCommonService.validateUserAndCurrencyPair(currencyPair);
        return orderService.getOpenOrders(currencyPairName, orderType);
    }

}