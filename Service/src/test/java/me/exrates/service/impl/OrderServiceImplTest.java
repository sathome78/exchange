package me.exrates.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.exrates.dao.OrderDao;
import me.exrates.model.dto.OrderFilterDataDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OrderStatus;
import me.exrates.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
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
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class OrderServiceImplTest {

    @Mock
    private OrderDao orderDao;

    @InjectMocks
    private OrderService orderService = new OrderServiceImpl();

    private OrderFilterDataDto filter;
    private Locale locale;

    @Before
    public void setUp() {
        locale = Locale.ENGLISH;

        LocalDateTime now = LocalDateTime.now();
        filter = OrderFilterDataDto.builder()
                .userId(1)
                .currencyPair(null)
                .currencyName(StringUtils.EMPTY)
                .status(OrderStatus.CLOSED)
                .scope(StringUtils.EMPTY)
                .offset(0)
                .limit(15)
                .hideCanceled(false)
                .sortedColumns(Collections.emptyMap())
                .dateFrom(now.minusDays(1))
                .dateTo(now)
                .build();
    }

    @Test
    public void getMyOrdersWithStateMap_foundOneRecordTest() {
        log.debug("getMyOrdersWithStateMap_foundOneRecordTest() - start");

        doReturn(1).when(orderDao).getMyOrdersWithStateCount(filter);

        doReturn(Collections.singletonList(new OrderWideListDto())).when(orderDao).getMyOrdersWithState(filter, locale);

        Pair<Integer, List<OrderWideListDto>> pair = orderService.getMyOrdersWithStateMap(filter, locale);

        verify(orderDao, atLeastOnce()).getMyOrdersWithStateCount(any(OrderFilterDataDto.class));

        verify(orderDao, atLeastOnce()).getMyOrdersWithState(any(OrderFilterDataDto.class), any(Locale.class));

        assertNotNull("Pair could not be null", pair);
        assertEquals("Number of records could be equals", 1, (int) pair.getLeft());
        assertFalse("List could not be empty", pair.getRight().isEmpty());
        assertEquals("The size of list could be equal to one", 1, pair.getRight().size());

        log.debug("getMyOrdersWithStateMap_foundOneRecordTest() - end");
    }

    @Test
    public void getMyOrdersWithStateMap_notFoundRecordsTest() {
        log.debug("getMyOrdersWithStateMap_notFoundRecordsTest() - start");

        doReturn(0).when(orderDao).getMyOrdersWithStateCount(filter);

        Pair<Integer, List<OrderWideListDto>> pair = orderService.getMyOrdersWithStateMap(filter, locale);

        verify(orderDao, atLeastOnce()).getMyOrdersWithStateCount(any(OrderFilterDataDto.class));

        verify(orderDao, never()).getMyOrdersWithState(any(OrderFilterDataDto.class), any(Locale.class));

        assertNotNull("Pair could not be null", pair);
        assertEquals("Number of records could be equals", 0, (int) pair.getLeft());
        assertTrue("List could be empty", pair.getRight().isEmpty());

        log.debug("getMyOrdersWithStateMap_notFoundRecordsTest() - end");
    }
}
