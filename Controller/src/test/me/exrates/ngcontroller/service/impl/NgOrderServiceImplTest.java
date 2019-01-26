package me.exrates.ngcontroller.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.dao.OrderDao;
import me.exrates.dao.StopOrderDao;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderType;
import me.exrates.ngcontroller.model.OrderBookWrapperDto;
import me.exrates.ngcontroller.service.NgOrderService;
import me.exrates.service.CurrencyService;
import me.exrates.service.DashboardService;
import me.exrates.service.OrderService;
import me.exrates.service.RabbitMqService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.cache.MarketRatesHolder;
import me.exrates.service.stopOrder.StopOrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = NgOrderServiceImplTest.TestConfig.class, loader = AnnotationConfigContextLoader.class)
public class NgOrderServiceImplTest {

    @Autowired
    private NgOrderService ngOrderService;

    @Autowired
    OrderDao orderDao;

    // precision 1 - lowest, 5 - highest
    @Test
    public void testHi() {
        when(orderDao.findAllByOrderTypeAndCurrencyId(anyObject(), anyInt())).thenReturn(getBuyList());

        OrderBookWrapperDto orderBookItems = ngOrderService.findAllOrderBookItems(OrderType.SELL, 3, 5);
    }

    private List<OrderListDto> getBuyList() {
        List<OrderListDto> items = new ArrayList<>();
        return items;
    }

    private OrderListDto getTestItem(String exrate, String amountBase, String amountConvert) {
        return new OrderListDto("", exrate, amountBase, amountConvert, OperationType.SELL, false);
    }


    @Configuration
    static class TestConfig {

        @Bean
        public CurrencyService currencyService() {
            return mock(CurrencyService.class);
        }

        @Bean
        public OrderService orderService() {
            return mock(OrderService.class);
        }

        @Bean
        public OrderDao orderDao() {
            return mock(OrderDao.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return mock(ObjectMapper.class);
        }

        @Bean
        public StopOrderDao stopOrderDao() {
            return mock(StopOrderDao.class);
        }

        @Bean
        public DashboardService dashboardService() {
            return mock(DashboardService.class);
        }

        @Bean
        public RabbitMqService rabbitMqService() {
            return mock(RabbitMqService.class);
        }

        @Bean
        public MarketRatesHolder marketRatesHolder() {
            return new MarketRatesHolder(orderDao(), rabbitMqService());
        }

        @Bean
        public SimpMessagingTemplate messagingTemplate() {
            return mock(SimpMessagingTemplate.class);
        }

        @Bean
        public StopOrderService stopOrderService() {
            return mock(StopOrderService.class);
        }

        @Bean
        public UserService userService() {
            return mock(UserService.class);
        }

        @Bean
        public WalletService walletService() {
            return mock(WalletService.class);
        }

        @Bean
        public NgOrderService ngOrderService() {
            return new NgOrderServiceImpl(currencyService(), orderService(), orderDao(), objectMapper(), stopOrderDao(),
                    dashboardService(), marketRatesHolder(), messagingTemplate(), stopOrderService(), userService(),
                    walletService());
        }

    }

}