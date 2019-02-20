package me.exrates.controller.openAPI;

import me.exrates.model.dto.CallbackURL;
import me.exrates.model.dto.mobileApiDto.dashboard.CommissionsDto;
import me.exrates.model.dto.openAPI.OrderParamsDto;
import me.exrates.model.enums.OrderType;

import java.math.BigDecimal;

public class TestUtils {

    private TestUtils() {
    }

    public static OrderParamsDto getTestOrderCreate() {
        OrderParamsDto orderParamsDto = new OrderParamsDto();
        orderParamsDto.setCurrencyPair("btc_usd");
        orderParamsDto.setAmount(new BigDecimal(1));
        orderParamsDto.setOrderType(OrderType.BUY);
        orderParamsDto.setPrice(new BigDecimal(1));
        return orderParamsDto;
    }

    public static CallbackURL getTestCallbackUrl() {
        CallbackURL url = new CallbackURL();
        url.setCallbackURL("localhost:8080/mycallback");
        url.setPairId(1);
        return url;
    }

    public static CommissionsDto getComissionsDto () {
        CommissionsDto dto = new CommissionsDto();
        dto.setBuyCommission(BigDecimal.ONE);
        dto.setSellCommission(BigDecimal.ZERO);
        dto.setInputCommission(BigDecimal.ZERO);
        dto.setOutputCommission(BigDecimal.ZERO);
        dto.setTransferCommission(BigDecimal.ZERO);
        return dto;
    }
}
