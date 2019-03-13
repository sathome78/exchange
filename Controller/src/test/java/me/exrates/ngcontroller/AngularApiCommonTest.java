package me.exrates.ngcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import me.exrates.model.User;
import me.exrates.model.dto.BalanceFilterDataDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.enums.CurrencyType;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.ngModel.RefillPendingRequestDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
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
        return new RefillPendingRequestDto(777, "TEST_DATE", "TEST_CURRENCY", 100.0, 10.0, "TEST_SYSTEM", "TEST_STATUS", "TEST_OPERATION");
    }

    protected BalanceFilterDataDto getMockBalanceFilterDataDto() {
        return new BalanceFilterDataDto(25, 5, false, "XRP", 100, CurrencyType.CRYPTO, "test@gmail.com");
    }
}
