package me.exrates.controller.openAPI;

import me.exrates.controller.openAPI.config.WebAppTestConfig;
import me.exrates.model.enums.OrderType;
import me.exrates.security.config.OpenApiSecurityConfig;
import me.exrates.service.util.OpenApiUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static me.exrates.controller.openAPI.TestUtils.getFakeOrderCreationResultDto;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppTestConfig.class, OpenApiSecurityConfig.class})
@WebAppConfiguration
public class OpenApiOrderControllerTest extends OpenApiCommonTest {


    @Test
    public void createOrder() throws Exception {
        when(orderService.prepareAndCreateOrderRest(anyString(), anyObject(), anyObject(), any(), anyString()))
                .thenReturn(getFakeOrderCreationResultDto());
        mockMvc.perform(MockMvcRequestBuilders.post("/openapi/v1/orders/create")
                                              .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                              .content(objectMapper.writeValueAsString(TestUtils.getTestOrderCreate())))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        verify(orderService, times(1))
                .prepareAndCreateOrderRest(anyString(), anyObject(), anyObject(), any(), anyString());
        verifyNoMoreInteractions(orderService);
    }

    @Test
    public void createOrderInvalidPriceAndAmount() throws Exception {
        String cp = "btc-usd";
        when(orderService.prepareAndCreateOrderRest(anyString(), anyObject(), anyObject(), any(), anyString()))
                .thenReturn(getFakeOrderCreationResultDto());
        mockMvc.perform(MockMvcRequestBuilders.post("/openapi/v1/orders/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(TestUtils.getCustomTestOrderCreate(null, null, OrderType.BUY, cp))))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void acceptOrder() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/openapi/v1/orders/accept/1234")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1))
                .acceptOrder(anyString(), anyInt());
    }

    @Test
    public void cancelOrder() throws Exception {
        when(orderService.cancelOrder(anyInt()))
                .thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.delete("/openapi/v1/orders/123")
                                              .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1))
                .cancelOrder(anyInt());
        verifyNoMoreInteractions(orderService);
    }

    @Test
    public void addCallback() throws Exception {
        when(userService.getIdByEmail(anyString()))
                .thenReturn(1);
        when(userService.setCallbackURL(anyInt(), any()))
                .thenReturn(1);
        mockMvc.perform(MockMvcRequestBuilders.post("/openapi/v1/orders/callback/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(TestUtils.getTestCallbackUrl())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status", is(true)))
                .andExpect(jsonPath("$.error").doesNotExist());

        verify(userService, times(1))
                .getIdByEmail(anyString());
        verify(userService, times(1))
                .setCallbackURL(anyInt(), any());
    }

    @Test
    public void updateCallback() throws Exception {
        when(userService.getIdByEmail(anyString()))
                .thenReturn(2);
        when(userService.updateCallbackURL(anyInt(), any()))
                .thenReturn(1);
        mockMvc.perform(MockMvcRequestBuilders.put("/openapi/v1/orders//callback/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(TestUtils.getTestCallbackUrl())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status", is(true)))
                .andExpect(jsonPath("$.error").doesNotExist());

        verify(userService, times(1))
                .updateCallbackURL(anyInt(), any());
    }

    @Test
    public void openOrders() throws Exception {
        String cpName = "btc_usd";
        OrderType orderType = OrderType.SELL;
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/orders/open/{order_type}")
                .queryParam("currency_pair",cpName)
                .build()
                .expand(orderType);

        String expected = "/openapi/v1/orders/open/SELL?currency_pair=btc_usd";
        assertEquals(expected, uriComponents.toUriString());
        mockMvc.perform(getOpenApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, "", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1))
                .getOpenOrders(OpenApiUtils.transformCurrencyPair(cpName), orderType);
    }
}