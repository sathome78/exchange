package me.exrates.dao.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.dao.TransactionDao;
import me.exrates.dao.UserDao;
import me.exrates.dao.WalletDao;
import config.AbstractDatabaseContextTest;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.enums.MerchantProcessType;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WalletDaoImplTest.InnerConf.class})
public class WalletDaoImplTest {

    private static final String EMAIL = "shvets.k@gmail.com";
    private static final List<Integer> WITHDRAW_STATUS_IDS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
    private static final List<MerchantProcessType> PROCESS_TYPES = Collections.singletonList(MerchantProcessType.CRYPTO);

    @Autowired
    private WalletDao walletDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAllWalletsForUserDetailedTest() {
        List<MyWalletsDetailedDto> allWalletsForUserDetailed = walletDao.getAllWalletsForUserDetailed(EMAIL, WITHDRAW_STATUS_IDS, Locale.ENGLISH, PROCESS_TYPES);

        assertNotNull(allWalletsForUserDetailed);
        assertFalse(allWalletsForUserDetailed.isEmpty());
    }

    @Configuration
    static class InnerConf extends LegacyAppContextConfig {

        @Bean
        public WalletDao walletDao() {
            return new WalletDaoImpl();
        }

        @Bean
        public TransactionDao transactionDao() {
            return Mockito.mock(TransactionDao.class);
        }

        @Bean
        public UserDao userDao() {
            return Mockito.mock(UserDao.class);
        }

        @Bean
        public CurrencyDao currencyDao() {
            return Mockito.mock(CurrencyDao.class);
        }

        @Bean
        public NamedParameterJdbcTemplate masterTemplate() {
            return Mockito.mock(NamedParameterJdbcTemplate.class);
        }
    }
}
