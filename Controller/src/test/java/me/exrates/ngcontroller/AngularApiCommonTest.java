package me.exrates.ngcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import me.exrates.model.ChatMessage;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.User;
import me.exrates.model.dto.OrderBookWrapperDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.UserStatus;
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
}
