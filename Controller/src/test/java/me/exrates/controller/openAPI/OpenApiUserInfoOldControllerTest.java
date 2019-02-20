package me.exrates.controller.openAPI;

import me.exrates.controller.openAPI.config.WebAppTestConfig;
import me.exrates.security.config.OpenApiSecurityConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;

import static me.exrates.service.util.OpenApiUtils.transformCurrencyPair;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppTestConfig.class, OpenApiSecurityConfig.class})
@WebAppConfiguration
public class OpenApiUserInfoOldControllerTest extends OpenApiCommonTest{


    @Test
    public void userBalances() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/balances")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(walletService, times(1))
                .getBalancesForUser();
    }

    @Test
    public void userOpenOrders() throws Exception {
        String cp = "btc_usd";
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/orders/open")
                .queryParam("currency_pair", cp)
                .build();
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(orderService, times(1))
                .getUserOpenOrders(transformCurrencyPair(cp));
    }

    @Test
    public void userOpenOrdersNoPairs() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/orders/open")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(orderService, times(1))
                .getUserOpenOrders(null);
    }

    @Test
    public void userClosedOrders() throws Exception {
        String cp = "btc_usd";
        Integer limit = 1;
        Integer offset = 1;
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/orders/closed")
                .queryParam("currency_pair", cp)
                .queryParam("limit", 1)
                .queryParam("offset", 1)
                .build();
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(orderService, times(1))
                .getUserClosedOrders(transformCurrencyPair(cp), 1, 1);
    }

    @Test
    public void userClosedOrdersNullArgs() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/orders/closed")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(orderService, times(1))
                .getUserClosedOrders(null, null, null);
    }

    @Test
    public void userCanceledOrders() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/orders/canceled")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(orderService, times(1))
                .getUserCanceledOrders(null, null, null);
}

    @Test
    public void getCommissions() throws Exception {
        when(orderService.getAllCommissions())
                .thenReturn(TestUtils.getComissionsDto());
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/commissions")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(orderService, times(1))
                .getAllCommissions();
    }

    @Test
    public void getUserTradeHistoryByCurrencyPair() throws Exception {
        String cp = "btc_usd";
        LocalDate datesFrom = LocalDate.now().minusDays(1);
        LocalDate datesTo = LocalDate.now();
        Integer limit = 1;
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/history/{currency_pair}/trades")
                .queryParam("from_date", datesFrom.toString())
                .queryParam("to_date", datesTo.toString())
                .queryParam("limit", limit)
                .build()
                .expand(cp);
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(orderService, times(1))
                .getUserTradeHistoryByCurrencyPair(transformCurrencyPair(cp), datesFrom, datesTo, limit);
    }

    @Test
    public void getUserTradeHistoryByCurrencyPairWrongParameters() throws Exception {
        String cp = "b";
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/history/{currency_pair}/trades")
                .build()
                .expand(cp);
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void getOrderTransactions() throws Exception {
        Integer orderId = 1;
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/history/{order_id}/transactions")
                .build()
                .expand(orderId);
        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(orderService, times(1))
                .getOrderTransactions(orderId);
    }
}