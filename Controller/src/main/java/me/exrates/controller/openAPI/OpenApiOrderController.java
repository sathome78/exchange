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
import me.exrates.service.exception.invoice.InsufficientCostsInWalletException;
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
            throw new OpenApiException(ErrorApiTitles.API_UNAVAILABLE_CURRENCY_PAIR, e.getMessage());
        } catch (OrderParamsWrongException e) {
            throw new OpenApiException(ErrorApiTitles.API_INVALID_ORDER_CREATION_PARAMS, e.getMessage());
        } catch (InsufficientCostsInWalletException ex) {
            throw new OpenApiException(ErrorApiTitles.API_INSUFFICIENT_FUNDS_ERROR, ex.getMessage());
        } catch (Exception e) {
            throw new OpenApiException(ErrorApiTitles.API_CREATE_ORDER_ERROR, e.getMessage());
        }
    }

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
            throw new OpenApiException(ErrorApiTitles.API_UNAVAILABLE_CURRENCY_PAIR, e.getMessage());
        } catch (OrderParamsWrongException e) {
            throw new OpenApiException(ErrorApiTitles.API_INVALID_ORDER_CREATION_PARAMS, e.getMessage());
        } catch (Exception e) {
            throw new OpenApiException(ErrorApiTitles.API_CREATE_ORDER_ERROR, e.getMessage());
        }
    }

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

    @PreAuthorize("hasAuthority('TRADE')")
    @GetMapping(value = "/{orderId}")
    public ResponseEntity<ExOrderDto> getById(@PathVariable Integer orderId) {
        int userId = userService.getIdByEmail(userService.getUserEmailFromSecurityContext());
        Optional<ExOrder> exOrder = Optional.ofNullable(orderService.getOrderById(orderId, userId));
        ExOrder order = exOrder.orElseThrow(() -> new OpenApiException(ErrorApiTitles.API_ORDER_NOT_FOUND, "Order with id: " + orderId
                + " not found among created or accepted orders"));
        return ResponseEntity.ok(ExOrderDto.valueOf(order));
    }

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

    @GetMapping(value = "/open/{order_type}")
    public List<OpenOrderDto> openOrders(@PathVariable("order_type") OrderType orderType,
                                         @RequestParam("currency_pair") String currencyPair) {
        String currencyPairName = openApiCommonService.validateUserAndCurrencyPair(currencyPair);
        return orderService.getOpenOrders(currencyPairName, orderType);
    }

}