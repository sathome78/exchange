package me.exrates.controller.openAPI;

import me.exrates.controller.openAPI.config.WebAppTestConfig;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.CallbackURL;
import me.exrates.model.dto.openAPI.OrderParamsDto;
import me.exrates.model.enums.OrderType;
import me.exrates.security.config.OpenApiSecurityConfig;
import me.exrates.service.util.OpenApiUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.NestedServletException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static me.exrates.controller.openAPI.TestUtils.getFakeOrderCreationResultDto;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppTestConfig.class, OpenApiSecurityConfig.class})
@WebAppConfiguration
public class OpenApiOrderControllerTest extends OpenApiCommonTest {

    @Test
    public void createOrder_successTest() throws Exception {
        when(orderService.prepareAndCreateOrderRest(anyString(), anyObject(), anyObject(), any(), anyString()))
                .thenReturn(getFakeOrderCreationResultDto());

        mockMvc.perform(MockMvcRequestBuilders.post("/openapi/v1/orders/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(TestUtils.getTestOrderCreate())))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        verify(orderService, times(1)).prepareAndCreateOrderRest(anyString(), anyObject(), anyObject(), any(), anyString());
    }

    @Test(expected = NestedServletException.class)
    public void createOrder_transformCurrencyPairNameErrorTest() throws Exception {
        OrderParamsDto testOrderCreate = TestUtils.getTestOrderCreate();
        testOrderCreate.setCurrencyPair("btc__usd");

        mockMvc.perform(MockMvcRequestBuilders.post("/openapi/v1/orders/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(testOrderCreate)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test(expected = NestedServletException.class)
    public void createOrder_accessClosedToOperationForUserTest() throws Exception {
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/openapi/v1/orders/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(TestUtils.getTestOrderCreate())))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void createOrder_invalidPriceAndAmountTest() throws Exception {
        String cp = "btc-usd";

        when(orderService.prepareAndCreateOrderRest(anyString(), anyObject(), anyObject(), any(), anyString())).thenReturn(getFakeOrderCreationResultDto());

        mockMvc.perform(MockMvcRequestBuilders.post("/openapi/v1/orders/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(TestUtils.getCustomTestOrderCreate(null, null, OrderType.BUY, cp))))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void acceptOrder_successTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/openapi/v1/orders/accept/1234")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).acceptOrder(anyString(), anyInt());
    }

    @Test
    public void cancelOrder_successTest() throws Exception {
        when(orderService.cancelOrder(anyInt())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/openapi/v1/orders/123")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).cancelOrder(anyInt());
    }

    @Test
    public void addCallback_successTest() throws Exception {
        when(userService.getIdByEmail(anyString())).thenReturn(1);
        when(userService.setCallbackURL(anyInt(), any())).thenReturn(1);

        mockMvc.perform(MockMvcRequestBuilders.post("/openapi/v1/orders/callback/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(TestUtils.getTestCallbackUrl())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status", is(true)))
                .andExpect(jsonPath("$.error").doesNotExist());

        verify(userService, times(1)).setCallbackURL(anyInt(), any(CallbackURL.class));
    }

    @Test
    public void addCallback_callbackUrlEmptyTest() throws Exception {
        CallbackURL testCallbackUrl = TestUtils.getTestCallbackUrl();
        testCallbackUrl.setCallbackURL(StringUtils.EMPTY);

        when(userService.getIdByEmail(anyString())).thenReturn(1);
        when(userService.setCallbackURL(anyInt(), any())).thenReturn(1);

        mockMvc.perform(MockMvcRequestBuilders.post("/openapi/v1/orders/callback/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(testCallbackUrl)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status", is("false")))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    public void updateCallback_successTest() throws Exception {
        when(userService.getIdByEmail(anyString())).thenReturn(2);
        when(userService.updateCallbackURL(anyInt(), any())).thenReturn(1);

        mockMvc.perform(MockMvcRequestBuilders.put("/openapi/v1/orders//callback/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(TestUtils.getTestCallbackUrl())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status", is(true)))
                .andExpect(jsonPath("$.error").doesNotExist());

        verify(userService, times(1)).updateCallbackURL(anyInt(), any(CallbackURL.class));
    }

    @Test
    public void updateCallback_callbackUrlEmptyTest() throws Exception {
        CallbackURL testCallbackUrl = TestUtils.getTestCallbackUrl();
        testCallbackUrl.setCallbackURL(StringUtils.EMPTY);

        when(userService.getIdByEmail(anyString())).thenReturn(2);
        when(userService.updateCallbackURL(anyInt(), any())).thenReturn(1);

        mockMvc.perform(MockMvcRequestBuilders.put("/openapi/v1/orders//callback/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(testCallbackUrl)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status", is("false")))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    public void openOrders_successTest() throws Exception {
        String cpName = "btc_usd";
        OrderType orderType = OrderType.SELL;

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/orders/open/{order_type}")
                .queryParam("currency_pair", cpName)
                .build()
                .expand(orderType);

        String expected = "/openapi/v1/orders/open/SELL?currency_pair=btc_usd";
        assertEquals(expected, uriComponents.toUriString());
        mockMvc.perform(getOpenApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, "", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).getOpenOrders(OpenApiUtils.transformCurrencyPair(cpName), orderType);
    }

    @Test(expected = NestedServletException.class)
    public void openOrders_transformCurrencyPairNameErrorTest() throws Exception {
        String cpName = "btc__usd";
        OrderType orderType = OrderType.SELL;

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path("/openapi/v1/orders/open/{order_type}")
                .queryParam("currency_pair", cpName)
                .build()
                .expand(orderType);

        mockMvc.perform(getOpenApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, "", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getById_successTest() throws Exception {
        when(userService.getIdByEmail(anyString())).thenReturn(1);
        when(orderService.getOrderById(anyInt(), anyInt())).thenReturn(new ExOrder());

        mockMvc.perform(MockMvcRequestBuilders.get("/openapi/v1/orders/123")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).getOrderById(anyInt(), anyInt());
    }

    @Test(expected = NestedServletException.class)
    public void getById_orderNotFoundTest() throws Exception {
        when(userService.getIdByEmail(anyString())).thenReturn(1);
        when(orderService.getOrderById(anyInt(), anyInt())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/openapi/v1/orders/123")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).getOrderById(anyInt(), anyInt());
    }
}