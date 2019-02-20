package me.exrates.controller.openAPI;

import me.exrates.controller.openAPI.config.WebAppTestConfig;
import me.exrates.model.enums.IntervalType;
import me.exrates.model.enums.OrderType;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.security.config.OpenApiSecurityConfig;
import me.exrates.service.util.OpenApiUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;

import static me.exrates.service.util.OpenApiUtils.transformCurrencyPair;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppTestConfig.class, OpenApiSecurityConfig.class})
@WebAppConfiguration
public class OpenApiPublicOldControllerTest extends OpenApiCommonTest {


    @Test
    public void getDailyTickerNoCp() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/ticker")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(orderService, times(1))
                .getDailyCoinmarketData(null);
    }

    @Test
    public void getDailyTicker() throws Exception {
        String cpName = "btc_usd";
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/ticker")
                .queryParam("currency_pair", cpName)
                .build();
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(orderService, times(1))
                .getDailyCoinmarketData(OpenApiUtils.transformCurrencyPair(cpName));
        verify(currencyService, times(1))
                .findCurrencyPairIdByName(OpenApiUtils.transformCurrencyPair(cpName));
    }

    @Test
    public void getOrderBook() throws Exception {
        String cpName = "btc_usd";
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/orderbook/{currency_pair}")
                .build()
                .expand(cpName);
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(orderService, times(1))
                .getOrderBook(OpenApiUtils.transformCurrencyPair(cpName), null);
    }

    @Test
    public void getOrderBookWithType() throws Exception {
        String cpName = "btc_usd";
        OrderType orderType = OrderType.SELL;
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/orderbook/{currency_pair}")
                .queryParam("order_type", orderType.toString())
                .build()
                .expand(cpName);
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(orderService, times(1))
                .getOrderBook(OpenApiUtils.transformCurrencyPair(cpName), orderType);
    }

    @Test
    public void getOrderBookExpectError() throws Exception {
        OrderType orderType = OrderType.SELL;
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/orderbook/")
                .queryParam("order_type", orderType.toString())
                .build();
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void getOrderBookWrongPairExpectError() throws Exception {
        String cpName = "btc/usd";
        OrderType orderType = OrderType.SELL;
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/orderbook/{currency_pair")
                .queryParam("order_type", orderType.toString())
                .build()
                .expand(cpName);
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void getTradeHistory() throws Exception {
        String cpName = "btc_usd";
        LocalDate datesFrom = LocalDate.now().minusDays(1);
        LocalDate datesTo = LocalDate.now();
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/history/{currency_pair}")
                .queryParam("from_date", datesFrom.toString())
                .queryParam("to_date", datesTo.toString())
                .build()
                .expand(cpName);
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(orderService, times(1))
                .getTradeHistory(OpenApiUtils.transformCurrencyPair(cpName), datesFrom, datesTo, 50, "ASC");
    }

    @Test
    public void findActiveCurrencyPairs() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/openapi/v1/public/currency_pairs")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(currencyService, times(1))
                .findActiveCurrencyPairs();
        verifyNoMoreInteractions(orderService);
    }

    @Test
    public void getCandleChartData() throws Exception {
        String cpName = "btc_usd";
        IntervalType intervalType = IntervalType.HOUR;
        Integer intervalValue = 1;
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/{currency_pair}/candle_chart")
                .queryParam("interval_type", intervalType)
                .queryParam("interval_value", intervalValue)
                .build()
                .expand(cpName);
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(currencyService, times(1))
                .getCurrencyPairByName(transformCurrencyPair(cpName));
        verify(orderService, times(1))
                .getDataForCandleChart(anyObject(), eq(new BackDealInterval(intervalValue, intervalType)));

    }
}
