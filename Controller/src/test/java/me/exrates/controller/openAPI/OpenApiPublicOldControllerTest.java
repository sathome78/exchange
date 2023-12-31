package me.exrates.controller.openAPI;

import me.exrates.controller.openAPI.config.WebAppTestConfig;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.enums.IntervalType;
import me.exrates.model.enums.OrderType;
import me.exrates.model.exceptions.OpenApiException;
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
import org.springframework.web.util.NestedServletException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static me.exrates.service.util.OpenApiUtils.transformCurrencyPair;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppTestConfig.class, OpenApiSecurityConfig.class})
@WebAppConfiguration
public class OpenApiPublicOldControllerTest extends OpenApiCommonTest {

    @Test
    public void getDailyTickerNoCp_successTest() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/ticker")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).getDailyCoinmarketData(null);
    }

    @Test
    public void getDailyTicker_successTest() throws Exception {
        String cpName = "btc_usd";

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/ticker")
                .queryParam("currency_pair", cpName)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).getDailyCoinmarketData(OpenApiUtils.transformCurrencyPair(cpName));
        verify(currencyService, times(1)).findCurrencyPairIdByName(OpenApiUtils.transformCurrencyPair(cpName));
    }

    @Test(expected = NestedServletException.class)
    public void getDailyTicker_transformCurrencyPairNameErrorTest() throws Exception {
        String cpName = "btc__usd";

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/ticker")
                .queryParam("currency_pair", cpName)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getOrderBook_successTest() throws Exception {
        String cpName = "btc_usd";

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/orderbook/{currency_pair}")
                .build()
                .expand(cpName);

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).getOrderBook(OpenApiUtils.transformCurrencyPair(cpName), null);
    }

    @Test(expected = NestedServletException.class)
    public void getOrderBook_transformCurrencyPairNameErrorTest() throws Exception {
        String cpName = "btc__usd";

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/orderbook/{currency_pair}")
                .build()
                .expand(cpName);

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getOrderBook_withTypeTest() throws Exception {
        String cpName = "btc_usd";
        OrderType orderType = OrderType.SELL;

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/orderbook/{currency_pair}")
                .queryParam("order_type", orderType.toString())
                .build()
                .expand(cpName);

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).getOrderBook(OpenApiUtils.transformCurrencyPair(cpName), orderType);
    }

    @Test
    public void getOrderBook_expectErrorTest() throws Exception {
        OrderType orderType = OrderType.SELL;

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/orderbook/")
                .queryParam("order_type", orderType.toString())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void getOrderBook_wrongPairExpectErrorTest() throws Exception {
        String cpName = "btc/usd";
        OrderType orderType = OrderType.SELL;

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/orderbook/{currency_pair}")
                .queryParam("order_type", orderType.toString())
                .build()
                .expand(cpName);

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void getTradeHistory_successTest() throws Exception {
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

        verify(orderService, times(1)).getTradeHistory(OpenApiUtils.transformCurrencyPair(cpName), datesFrom, datesTo, 50, "ASC");
    }

    @Test
    public void getTradeHistory_wrongDateRangeTest() {
        String cpName = "btc_usd";
        LocalDate datesFrom = LocalDate.now();
        LocalDate datesTo = LocalDate.now().minusDays(1);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/history/{currency_pair}")
                .queryParam("from_date", datesFrom.toString())
                .queryParam("to_date", datesTo.toString())
                .build()
                .expand(cpName);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
            fail();
        } catch (Exception ex) {
            assertTrue(((NestedServletException) ex).getRootCause() instanceof OpenApiException);
            OpenApiException exception = (OpenApiException) ((NestedServletException) ex).getRootCause();
            assertEquals(ErrorApiTitles.API_REQUEST_ERROR_DATES, exception.getTitle());
            assertEquals("From date is after to date", exception.getMessage());
        }
    }

    @Test
    public void getTradeHistory_wrongLimitTest() throws Exception {
        String cpName = "btc_usd";
        LocalDate datesFrom = LocalDate.now().minusDays(1);
        LocalDate datesTo = LocalDate.now();

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/history/{currency_pair}")
                .queryParam("from_date", datesFrom.toString())
                .queryParam("to_date", datesTo.toString())
                .queryParam("limit", -1)
                .build()
                .expand(cpName);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        } catch (Exception ex) {
            assertTrue(((NestedServletException)ex).getRootCause() instanceof OpenApiException);
            OpenApiException exception = (OpenApiException) ((NestedServletException)ex).getRootCause();
            assertEquals(ErrorApiTitles.API_REQUEST_ERROR_LIMIT, exception.getTitle());
            assertEquals("Limit value equals or less than zero", exception.getMessage());
        }
    }

    @Test(expected = NestedServletException.class)
    public void getTradeHistory_transformCurrencyPairNameErrorTest() throws Exception {
        String cpName = "btc__usd";
        LocalDate datesFrom = LocalDate.now().minusDays(1);
        LocalDate datesTo = LocalDate.now();

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/public/history/{currency_pair}")
                .queryParam("from_date", datesFrom.toString())
                .queryParam("to_date", datesTo.toString())
                .build()
                .expand(cpName);

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void findActiveCurrencyPairs_successTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/openapi/v1/public/currency_pairs")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(currencyService, times(1)).findActiveCurrencyPairs();
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

        verify(currencyService, times(1)).getCurrencyPairByName(transformCurrencyPair(cpName));
        verify(orderService, times(1)).getDataForCandleChart(anyObject(), eq(new BackDealInterval(intervalValue, intervalType)));
    }
}