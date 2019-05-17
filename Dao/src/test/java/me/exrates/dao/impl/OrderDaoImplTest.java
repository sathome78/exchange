package me.exrates.dao.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.dao.OrderDao;
import me.exrates.dao.WalletDao;
import config.AbstractDatabaseContextTest;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OrderStatus;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {OrderDaoImplTest.InnerConf.class})
public class OrderDaoImplTest {

    @Autowired
    private OrderDao orderDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getMyOrdersWithStateTest_Success() {
        List<OrderWideListDto> ordersHistory = orderDao.getMyOrdersWithState(
                13,
                null,
                StringUtils.EMPTY,
                OrderStatus.CLOSED,
                StringUtils.EMPTY,
                15,
                0,
                false,
                "DESC",
                null,
                null,
                Locale.ENGLISH
        );

        assertNotNull(ordersHistory);
        // todo
//        assertFalse(ordersHistory.isEmpty());
//        assertEquals(15, ordersHistory.size());
    }

    @Configuration
    static class InnerConf extends LegacyAppContextConfig {

        @Bean
        public OrderDao orderDao() {
            return new OrderDaoImpl();
        }

        @Bean
        public NamedParameterJdbcTemplate masterTemplate() {
            return Mockito.mock(NamedParameterJdbcTemplate.class);
        }

        @Bean
        public NamedParameterJdbcTemplate slaveForReportsTemplate() {
            return Mockito.mock(NamedParameterJdbcTemplate.class);
        }

        @Bean
        public CommissionDao commissionDao() {
            return Mockito.mock(CommissionDao.class);
        }

        @Bean
        public WalletDao walletDao() {
            return Mockito.mock(WalletDao.class);
        }
    }
}
