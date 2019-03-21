package me.exrates.ngcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import me.exrates.model.ChatMessage;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.User;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.dto.OrderBookWrapperDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.dto.kyc.responces.KycStatusResponseDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.ngModel.RefillPendingRequestDto;
import me.exrates.model.ngModel.ResponseInfoCurrencyPairDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;

public abstract class AngularApiCommonTest {

    ObjectMapper objectMapper = new ObjectMapper();

    private final String HEADER_SECURITY_TOKEN = "Exrates-Rest-Token";

    protected RequestBuilder getApiRequestBuilder(URI uri, HttpMethod method, HttpHeaders httpHeaders, String content, String contentType) {
        HttpHeaders headers = createHeaders();
        if (httpHeaders != null) {
            headers.putAll(httpHeaders);
        }
        if (method.equals(HttpMethod.GET)) {
            System.out.println(MockMvcRequestBuilders.get(uri).headers(headers).content(content).contentType(contentType));
            return MockMvcRequestBuilders.get(uri).headers(headers).content(content).contentType(contentType);
        } else if (method.equals(HttpMethod.POST)) {
            return MockMvcRequestBuilders.post(uri).headers(headers).content(content).contentType(contentType);
        } else if (method.equals(HttpMethod.PUT)) {
            return MockMvcRequestBuilders.put(uri).headers(headers).content(content).contentType(contentType);
        }
        throw new UnsupportedOperationException(String.format("Method: %s not supported", method.toString()));
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.put(HEADER_SECURITY_TOKEN, ImmutableList.of("Test-Token"));
        return headers;
    }

    protected User getMockUser() {
        User user = new User();
        user.setId(1);
        user.setNickname("TEST_NICKNAME");
        user.setEmail("TEST_EMAIL");
        user.setParentEmail("+380508008000");
        user.setStatus(UserStatus.REGISTERED);
        user.setPassword("TEST_PASSWORD");
        return user;
    }

    protected CurrencyPair getMockCurrencyPair() {
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(100);
        currencyPair.setName("TEST_NAME");
        currencyPair.setCurrency1(getMockCurrency("TEST_NAME"));
        currencyPair.setCurrency2(getMockCurrency("TEST_NAME"));
        currencyPair.setMarket("TEST_MARKET");
        currencyPair.setMarketName("TEST_MARKET_NAME");
        currencyPair.setPairType(CurrencyPairType.ALL);
        currencyPair.setHidden(Boolean.TRUE);
        currencyPair.setPermittedLink(Boolean.TRUE);
        return currencyPair;
    }

    protected ChatMessage getMockChatMessage() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(100L);
        chatMessage.setNickname("TEST_NICKNAME");
        chatMessage.setNickname("TEST_BODY");
        chatMessage.setTime(LocalDateTime.of(2019, 3, 15, 11, 5, 25));
        chatMessage.setUserId(111);
        return chatMessage;
    }

    protected OrderBookWrapperDto getMockOrderBookWrapperDto() {
        OrderBookWrapperDto dto = OrderBookWrapperDto.builder().build();
        dto.setOrderType(OrderType.SELL);
        dto.setLastExrate("TEST_LAST_EXRATE");
        dto.setPreLastExrate("TEST_PRE_LAST_EXRATE");
        dto.setPositive(Boolean.TRUE);
        dto.setTotal(BigDecimal.valueOf(25));
        dto.setOrderBookItems(Collections.emptyList());
        return dto;
    }

    protected ResponseInfoCurrencyPairDto getMockResponseInfoCurrencyPairDto() {
        ResponseInfoCurrencyPairDto dto = new ResponseInfoCurrencyPairDto();
        dto.setCurrencyRate("TEST_CURRENCY_RATE");
        dto.setPercentChange("TEST_PERCENT_CHANGE");
        dto.setChangedValue("TEST_CHANGED_VALUE");
        dto.setLastCurrencyRate("TEST_LAST_CURRENCY_RATE");
        dto.setVolume24h("TEST_VOLUME_24H");
        dto.setRateHigh("TEST_RATE_HIGH");
        dto.setRateLow("TEST_RATE_LOW");
        return dto;
    }

    protected ExOrderStatisticsShortByPairsDto getMockExOrderStatisticsShortByPairsDto() {
        ExOrderStatisticsShortByPairsDto dto = new ExOrderStatisticsShortByPairsDto();
        dto.setCurrencyPairId(100);
        dto.setCurrencyPairName("TEST_CURRENCY_PAIR_NAME");
        dto.setCurrencyPairPrecision(200);
        dto.setLastOrderRate("TEST_LAST_ORDER_RATE");
        dto.setPredLastOrderRate("TEST_PRED_LAST_ORDER_RATE");
        dto.setPercentChange("TEST_PERCENT_CHANGE");
        dto.setMarket("TEST_MARKET");
        dto.setPriceInUSD("TEST_PRICE_IN_USD");
        dto.setType(CurrencyPairType.MAIN);
        dto.setVolume("TEST_VOLUME");
        dto.setCurrencyVolume("TEST_CURRENCY_VOLUME");
        dto.setHigh24hr("TEST_HIGH_24H");
        dto.setLow24hr("TEST_LOW_24H");
        dto.setHidden(Boolean.TRUE);
        dto.setLastUpdateCache("TEST_LAST_UPDATE_CACHE");
        return dto;
    }

    protected OrderAcceptedHistoryDto getMockOrderAcceptedHistoryDto() {
        OrderAcceptedHistoryDto dto = new OrderAcceptedHistoryDto();
        dto.setOrderId(500);
        dto.setDateAcceptionTime("TEST_DATE_ACCEPTION_TIME");
        dto.setAcceptionTime(Timestamp.valueOf(LocalDateTime.of(2019, 3, 15, 15, 5, 55)));
        dto.setRate("TEST_RATE");
        dto.setAmountBase("TEST_AMOUNT_BASE");
        dto.setOperationType(OperationType.BUY);
        return dto;
    }

    protected Currency getMockCurrency(String name) {
        Currency currency = new Currency();
        currency.setId(100);
        currency.setName(name);
        currency.setDescription("TEST_DESCRIPTION");
        currency.setHidden(Boolean.TRUE);
        return currency;
    }

    protected MyWalletsDetailedDto getMockMyWalletsDetailedDto() {
        MyWalletsDetailedDto myWalletsDetailedDto = new MyWalletsDetailedDto();
        myWalletsDetailedDto.setId(100);
        myWalletsDetailedDto.setUserId(1);
        myWalletsDetailedDto.setCurrencyId(111);
        myWalletsDetailedDto.setCurrencyPrecision(222);
        myWalletsDetailedDto.setCurrencyName("TEST_CURRENCY_NAME");
        myWalletsDetailedDto.setCurrencyDescription("TEST_CURRENCY_DESCRIPTION");
        myWalletsDetailedDto.setActiveBalance("TEST_ACTIVE_BALANCE");
        myWalletsDetailedDto.setOnConfirmation("TEST_ON_CONFIRMATION");
        myWalletsDetailedDto.setOnConfirmationStage("TEST_ON_CONFIRMATION_STAGE");
        myWalletsDetailedDto.setOnConfirmationCount("TEST_ON_CONFIRMATION_COUNT");
        myWalletsDetailedDto.setReservedBalance("TEST_RESERVED_BALANCE");
        myWalletsDetailedDto.setReservedByOrders("TEST_RESERVED_BY_ORDERS");
        myWalletsDetailedDto.setReservedByMerchant("TEST_RESERVED_BY_MERCHANT");
        myWalletsDetailedDto.setBtcAmount("TEST_BTC_AMOUNT");
        myWalletsDetailedDto.setUsdAmount("TEST_USD_AMOUNT");
        myWalletsDetailedDto.setConfirmations(Collections.EMPTY_LIST);

        return myWalletsDetailedDto;
    }

    protected RefillPendingRequestDto getMockRefillPendingRequestDto() {
        return new RefillPendingRequestDto(777, "TEST_DATE", "TEST_CURRENCY", 100.0,
                10.0, "TEST_SYSTEM", "TEST_STATUS", "TEST_OPERATION");
    }

    protected MyWalletsStatisticsDto getMockMyWalletsStatisticsDto(String currencyName) {
        MyWalletsStatisticsDto myWalletsStatisticsDto = new MyWalletsStatisticsDto();
        myWalletsStatisticsDto.setNeedRefresh(Boolean.TRUE);
        myWalletsStatisticsDto.setPage(0);
        myWalletsStatisticsDto.setDescription("TEST_DESCRIPTION");
        myWalletsStatisticsDto.setCurrencyName(currencyName);
        myWalletsStatisticsDto.setActiveBalance("TEST_ACTIVE_BALANCE");
        myWalletsStatisticsDto.setTotalBalance("125");

        return myWalletsStatisticsDto;
    }

    protected ExOrderStatisticsShortByPairsDto getMockExOrderStatisticsShortByPairsDto(String currencyPairName) {
        ExOrderStatisticsShortByPairsDto exOrderStatisticsShortByPairsDto = new ExOrderStatisticsShortByPairsDto();
        exOrderStatisticsShortByPairsDto.setNeedRefresh(Boolean.TRUE);
        exOrderStatisticsShortByPairsDto.setPage(0);
        exOrderStatisticsShortByPairsDto.setCurrencyPairId(222);
        exOrderStatisticsShortByPairsDto.setCurrencyPairName(currencyPairName);
        exOrderStatisticsShortByPairsDto.setCurrencyPairPrecision(22);
        exOrderStatisticsShortByPairsDto.setLastOrderRate("22");
        exOrderStatisticsShortByPairsDto.setPredLastOrderRate("");
        exOrderStatisticsShortByPairsDto.setPercentChange("");
        exOrderStatisticsShortByPairsDto.setMarket("");
        exOrderStatisticsShortByPairsDto.setPriceInUSD("");
        exOrderStatisticsShortByPairsDto.setType(CurrencyPairType.MAIN);
        exOrderStatisticsShortByPairsDto.setVolume("");
        exOrderStatisticsShortByPairsDto.setCurrencyVolume("");
        exOrderStatisticsShortByPairsDto.setHigh24hr("");
        exOrderStatisticsShortByPairsDto.setLow24hr("");
        exOrderStatisticsShortByPairsDto.setLastUpdateCache("");

        return exOrderStatisticsShortByPairsDto;
    }

    protected MyInputOutputHistoryDto getMockMyInputOutputHistoryDto() {
        MyInputOutputHistoryDto myInputOutputHistoryDto = new MyInputOutputHistoryDto();
        myInputOutputHistoryDto.setDatetime(LocalDateTime.of(2019, 03, 13, 14, 00, 00));
        myInputOutputHistoryDto.setCurrencyName("TEST_CURRENCY_NAME");
        myInputOutputHistoryDto.setAmount("TEST_AMOUNT");
        myInputOutputHistoryDto.setCommissionAmount("TEST_COMMISSION_AMOUNT");
        myInputOutputHistoryDto.setMerchantName("TEST_MERCHANT_NAME");
        myInputOutputHistoryDto.setOperationType("TEST_OPERATION_TYPE");
        myInputOutputHistoryDto.setTransactionId(100);
        myInputOutputHistoryDto.setProvided(200);
        myInputOutputHistoryDto.setTransactionProvided("TEST_TRANSACTION_PROVIDED");
        myInputOutputHistoryDto.setId(300);
        myInputOutputHistoryDto.setDestination("TEST_DESCRIPTION");
        myInputOutputHistoryDto.setUserId(400);
        myInputOutputHistoryDto.setBankAccount("TEST_BANK_ACCOUNT");
        myInputOutputHistoryDto.setStatusUpdateDate(LocalDateTime.of(2019, 03, 14, 15, 00, 00));
        myInputOutputHistoryDto.setSummaryStatus("TEST_SUMMARY_STATUS");
        myInputOutputHistoryDto.setUserFullName("TEST_USER_FULL_NAME");
        myInputOutputHistoryDto.setRemark("TEST_REMARK");
        myInputOutputHistoryDto.setSourceId(TransactionSourceType.REFILL.toString());
        myInputOutputHistoryDto.setSourceType(TransactionSourceType.REFILL);
        myInputOutputHistoryDto.setConfirmation(700);
        myInputOutputHistoryDto.setNeededConfirmations(800);
        myInputOutputHistoryDto.setAdminHolderId(900);
        myInputOutputHistoryDto.setAuthorisedUserId(1000);
        myInputOutputHistoryDto.setButtons(Collections.EMPTY_LIST);
        myInputOutputHistoryDto.setTransactionHash("TEST_TRANSACTIONAL_HASH");
        myInputOutputHistoryDto.setMarket("TEST_MARKET");
        myInputOutputHistoryDto.setAccepted(Boolean.TRUE);

        return myInputOutputHistoryDto;
    }

    protected OrderCreateDto getMockOrderCreateDto() {
        OrderCreateDto orderCreateDto = new OrderCreateDto();
        orderCreateDto.setOrderId(111);
        orderCreateDto.setUserId(222);
        orderCreateDto.setStatus(OrderStatus.OPENED);
        orderCreateDto.setCurrencyPair(getMockCurrencyPair());
        orderCreateDto.setComissionForBuyId(10);
        orderCreateDto.setComissionForBuyRate(BigDecimal.valueOf(15));
        orderCreateDto.setComissionForSellId(333);
        orderCreateDto.setComissionForSellRate(BigDecimal.valueOf(20));
        orderCreateDto.setWalletIdCurrencyBase(444);
        orderCreateDto.setUserId(400);
        orderCreateDto.setCurrencyBaseBalance(BigDecimal.valueOf(200));
        orderCreateDto.setWalletIdCurrencyConvert(555);
        orderCreateDto.setCurrencyConvertBalance(BigDecimal.valueOf(25));

        return orderCreateDto;
    }

    protected InputCreateOrderDto getMockInputCreateOrderDto() {
        InputCreateOrderDto inputCreateOrderDto = new InputCreateOrderDto();
        inputCreateOrderDto.setOrderType("TEST_ORDER_type");
        inputCreateOrderDto.setOrderId(111);
        inputCreateOrderDto.setCurrencyPairId(999);
        inputCreateOrderDto.setAmount(BigDecimal.valueOf(15));
        inputCreateOrderDto.setRate(BigDecimal.valueOf(35));
        inputCreateOrderDto.setCommission(BigDecimal.valueOf(5));
        inputCreateOrderDto.setBaseType(getMockExOrder().getOrderBaseType().toString());
        inputCreateOrderDto.setTotal(BigDecimal.valueOf(100));
        inputCreateOrderDto.setStop(BigDecimal.valueOf(75));
        inputCreateOrderDto.setStatus("TEST_STATUS");
        inputCreateOrderDto.setUserId(400);
        inputCreateOrderDto.setUserId(666);
        inputCreateOrderDto.setCurrencyPair(getMockCurrencyPair());

        return inputCreateOrderDto;
    }

    protected WalletsAndCommissionsForOrderCreationDto getMockWalletsAndCommissionsForOrderCreationDto() {
        WalletsAndCommissionsForOrderCreationDto commissions = new WalletsAndCommissionsForOrderCreationDto();
        commissions.setSpendWalletId(777);
        commissions.setSpendWalletActiveBalance(BigDecimal.valueOf(258));
        commissions.setCommissionId(888);
        commissions.setCommissionValue(BigDecimal.valueOf(7));

        return commissions;
    }

    protected ExOrder getMockExOrder() {
        ExOrder exOrder = new ExOrder();
        exOrder.setId(1515);
        exOrder.setUserId(1000);
        exOrder.setCurrencyPairId(2222);
        exOrder.setOperationType(OperationType.BUY);
        exOrder.setExRate(BigDecimal.TEN);
        exOrder.setAmountBase(BigDecimal.TEN);
        exOrder.setAmountConvert(BigDecimal.TEN);
        exOrder.setComissionId(3232);
        exOrder.setCommissionFixedAmount(BigDecimal.TEN);
        exOrder.setUserAcceptorId(3333);
        exOrder.setDateCreation(LocalDateTime.of(2019, 3, 18, 15, 15, 15));
        exOrder.setDateAcception(LocalDateTime.of(2019, 3, 18, 15, 15, 15));
        exOrder.setStatus(OrderStatus.OPENED);
        exOrder.setCurrencyPair(getMockCurrencyPair());
        exOrder.setSourceId(3598);
        exOrder.setStop(BigDecimal.ONE);
        exOrder.setOrderBaseType(OrderBaseType.LIMIT);
        exOrder.setPartiallyAcceptedAmount(BigDecimal.TEN);
        exOrder.setEventTimestamp(1111L);

        return exOrder;
    }

    protected KycStatusResponseDto getMockKycStatusResponseDto() {
        String[] missingOptionalDocs = new String[5];

        KycStatusResponseDto dto = new KycStatusResponseDto();
        dto.setStatus("TEST_STATUS");
        dto.setErrorMsg("TEST_ERROR_MSG");
        dto.setMissingOptionalDocs(missingOptionalDocs);
        dto.setAnalysisResults(Collections.EMPTY_LIST);

        return dto;
    }
}
