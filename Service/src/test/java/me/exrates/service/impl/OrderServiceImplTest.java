package me.exrates.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.exrates.dao.OrderDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OrderStatus;
import me.exrates.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class OrderServiceImplTest {

    @Mock
    private OrderDao orderDao;

    @InjectMocks
    private OrderService orderService = new OrderServiceImpl();

    @Test
    public void getMyOrdersWithStateMap_foundOneRecordTest() {
        log.debug("getMyOrdersWithStateMap_foundOneRecordTest() - start");

        LocalDateTime now = LocalDateTime.now();

        doReturn(1).when(orderDao).getMyOrdersWithStateCount(
                1,
                new CurrencyPair("BTC/USD"),
                StringUtils.EMPTY,
                OrderStatus.CLOSED,
                StringUtils.EMPTY,
                15,
                0,
                false,
                now.minusDays(1),
                now
        );

        doReturn(Collections.singletonList(new OrderWideListDto())).when(orderDao).getMyOrdersWithState(
                1,
                new CurrencyPair("BTC/USD"),
                StringUtils.EMPTY,
                OrderStatus.CLOSED,
                StringUtils.EMPTY,
                15,
                0,
                false,
                "DESC",
                now.minusDays(1),
                now,
                Locale.ENGLISH
        );

        Pair<Integer, List<OrderWideListDto>> pair = orderService.getMyOrdersWithStateMap(
                1,
                new CurrencyPair("BTC/USD"),
                StringUtils.EMPTY,
                OrderStatus.CLOSED,
                StringUtils.EMPTY,
                15,
                0,
                false,
                "DESC",
                now.minusDays(1),
                now,
                Locale.ENGLISH);

        verify(orderDao, atLeastOnce()).getMyOrdersWithStateCount(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(),
                any(LocalDateTime.class), any(LocalDateTime.class));

        verify(orderDao, atLeastOnce()).getMyOrdersWithState(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyString(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Locale.class));

        assertNotNull("Pair could not be null", pair);
        assertEquals("Number of records could be equals", 1, (int) pair.getLeft());
        assertFalse("List could not be empty", pair.getRight().isEmpty());
        assertEquals("The size of list could be equal to one", 1, pair.getRight().size());

        log.debug("getMyOrdersWithStateMap_foundOneRecordTest() - end");
    }

    @Test
    public void getMyOrdersWithStateMap_notFoundRecordsTest() {
        log.debug("getMyOrdersWithStateMap_notFoundRecordsTest() - start");

        LocalDateTime now = LocalDateTime.now();
        doReturn(0).when(orderDao).getMyOrdersWithStateCount(
                1,
                new CurrencyPair("BTC/USD"),
                StringUtils.EMPTY,
                OrderStatus.CLOSED,
                StringUtils.EMPTY,
                15,
                0,
                false,
                now.minusDays(1),
                now);

        Pair<Integer, List<OrderWideListDto>> pair = orderService.getMyOrdersWithStateMap(
                1,
                new CurrencyPair("BTC/USD"),
                StringUtils.EMPTY,
                OrderStatus.CLOSED,
                StringUtils.EMPTY,
                15,
                0,
                false,
                "DESC",
                now.minusDays(1),
                now,
                Locale.ENGLISH);

        verify(orderDao, atLeastOnce()).getMyOrdersWithStateCount(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(),
                any(LocalDateTime.class), any(LocalDateTime.class));

        assertNotNull("Pair could not be null", pair);
        assertEquals("Number of records could be equals", 0, (int) pair.getLeft());
        assertTrue("List could be empty", pair.getRight().isEmpty());

        log.debug("getMyOrdersWithStateMap_notFoundRecordsTest() - end");
    }
}
