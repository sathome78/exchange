package me.exrates.controller.openAPI;

import me.exrates.controller.openAPI.config.WebAppTestConfig;
import me.exrates.dao.exception.notfound.UserNotFoundException;
import me.exrates.model.User;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.exceptions.OpenApiException;
import me.exrates.security.config.OpenApiSecurityConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import static me.exrates.service.util.OpenApiUtils.transformCurrencyPair;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppTestConfig.class, OpenApiSecurityConfig.class})
@WebAppConfiguration
public class OpenApiUserInfoOldControllerTest extends OpenApiCommonTest {

    @Test
    public void userBalances_successTest() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/balances")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(walletService, times(1)).getBalancesForUser();
    }

    @Test
    public void userOpenOrders_successTest() throws Exception {
        String cp = "btc_usd";

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/orders/open")
                .queryParam("currency_pair", cp)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).getUserOpenOrders(transformCurrencyPair(cp));
    }

    @Test
    public void userOpenOrders_noPairsTest() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/orders/open")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).getUserOpenOrders(null);
    }

    @Test(expected = NestedServletException.class)
    public void userOpenOrders_transformCurrencyPairNameErrorTest() throws Exception {
        String cp = "btc__usd";

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/orders/open")
                .queryParam("currency_pair", cp)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void userClosedOrders_successTest() throws Exception {
        String cp = "btc_usd";

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/orders/closed")
                .queryParam("currency_pair", cp)
                .queryParam("limit", 1)
                .queryParam("offset", 1)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).getUserClosedOrders(transformCurrencyPair(cp), 1, 1);
    }

    @Test
    public void userClosedOrders_nullArgsTest() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/orders/closed")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).getUserClosedOrders(null, null, null);
    }

    @Test(expected = NestedServletException.class)
    public void userClosedOrders_transformCurrencyPairNameErrorTest() throws Exception {
        String cp = "btc__usd";

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/orders/closed")
                .queryParam("currency_pair", cp)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void userCanceledOrders_successTest() throws Exception {
        String cp = "btc_usd";

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/orders/canceled")
                .queryParam("currency_pair", cp)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).getUserCanceledOrders(transformCurrencyPair(cp), null, null);
    }

    @Test
    public void userCanceledOrders_noPairTest() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/orders/canceled")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).getUserCanceledOrders(null, null, null);
    }

    @Test(expected = NestedServletException.class)
    public void userCanceledOrders_transformCurrencyPairNameErrorTest() throws Exception {
        String cp = "btc__usd";

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/orders/canceled")
                .queryParam("currency_pair", cp)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getCommissions_successTest() throws Exception {
        when(orderService.getAllCommissions()).thenReturn(TestUtils.getComissionsDto());

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/commissions")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).getAllCommissions();
    }

    @Test
    public void getUserTradeHistoryByCurrencyPair_successTest() throws Exception {
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

        verify(orderService, times(1)).getUserTradeHistoryByCurrencyPair(transformCurrencyPair(cp), datesFrom, datesTo, limit);
    }

    @Test
    public void getUserTradeHistoryByCurrencyPair_wrongDateRangeTest() throws Exception {
        String cp = "btc_usd";
        LocalDate datesFrom = LocalDate.now();
        LocalDate datesTo = LocalDate.now().minusDays(1);
        Integer limit = 1;

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/history/{currency_pair}/trades")
                .queryParam("from_date", datesFrom.toString())
                .queryParam("to_date", datesTo.toString())
                .queryParam("limit", limit)
                .build()
                .expand(cp);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        } catch (Exception ex) {
            assertTrue(((NestedServletException) ex).getRootCause() instanceof OpenApiException);
            OpenApiException exception = (OpenApiException) ((NestedServletException) ex).getRootCause();
            assertEquals(ErrorApiTitles.API_REQUEST_ERROR_DATES, exception.getTitle());
            assertEquals("From date is after to date", exception.getMessage());
        }
    }

    @Test
    public void getUserTradeHistoryByCurrencyPair_wrongLimitTest() throws Exception {
        String cp = "btc_usd";
        LocalDate datesFrom = LocalDate.now().minusDays(1);
        LocalDate datesTo = LocalDate.now();
        Integer limit = -1;

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/history/{currency_pair}/trades")
                .queryParam("from_date", datesFrom.toString())
                .queryParam("to_date", datesTo.toString())
                .queryParam("limit", limit)
                .build()
                .expand(cp);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        } catch (Exception ex) {
            assertTrue(((NestedServletException) ex).getRootCause() instanceof OpenApiException);
            OpenApiException exception = (OpenApiException) ((NestedServletException) ex).getRootCause();
            assertEquals(ErrorApiTitles.API_REQUEST_ERROR_LIMIT, exception.getTitle());
            assertEquals("Limit value equals or less than zero", exception.getMessage());
        }
    }

    @Test
    public void getUserTradeHistoryByCurrencyPair_wrongParametersTest() throws Exception {
        String cp = "b";

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/history/{currency_pair}/trades")
                .build()
                .expand(cp);

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void getOrderTransactions_successTest() throws Exception {
        Integer orderId = 1;

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/history/{order_id}/transactions")
                .build()
                .expand(orderId);

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).getOrderTransactions(orderId);
    }

    @Test
    public void checkEmailExistence_existTest() throws Exception {
        String email = "test@test.com";

        when(userService.findByEmail(email)).thenReturn(new User());

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/info/email/exists")
                .queryParam("email", email)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status", is(true)));
    }

    @Test
    public void checkEmailExistence_notExistTest() throws Exception {
        String email = "test@test.com";

        when(userService.findByEmail(email)).thenThrow(new UserNotFoundException(String.format("User: %s not found", email)));

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/user/info/email/exists")
                .queryParam("email", email)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(uriComponents.toUri().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status", is(false)));
    }
}