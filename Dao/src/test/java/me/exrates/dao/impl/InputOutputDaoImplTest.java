package me.exrates.dao.impl;

import me.exrates.dao.InputOutputDao;
import config.AbstractDatabaseContextTest;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {InputOutputDaoImplTest.InnerConf.class})
public class InputOutputDaoImplTest {

    private static final String EMAIL = "shvets.k@gmail.com";
    private static final LocalDateTime START_DATE = LocalDateTime.now().minusMonths(6);
    private static final LocalDateTime END_DATE = LocalDateTime.now();
    private static final LocalDateTime FUTURE_START_DATE = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime FUTURE_END_DATE = LocalDateTime.now().plusDays(2);
    private static final List<Integer> OPERATION_TYPE_LIST = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

    @Autowired
    private InputOutputDao inputOutputDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findMyInputOutputHistoryByOperationTypeTest_WithResult() {
        List<MyInputOutputHistoryDto> history = inputOutputDao.findMyInputOutputHistoryByOperationType(
                EMAIL,
                0,
                StringUtils.EMPTY,
                START_DATE,
                END_DATE,
                0,
                0,
                OPERATION_TYPE_LIST,
                Locale.ENGLISH);

        assertNotNull(history);
    }

    @Test
    public void findMyInputOutputHistoryByOperationTypeTest_WithoutResult() {
        List<MyInputOutputHistoryDto> history = inputOutputDao.findMyInputOutputHistoryByOperationType(
                EMAIL,
                0,
                StringUtils.EMPTY,
                FUTURE_START_DATE,
                FUTURE_END_DATE,
                0,
                0,
                OPERATION_TYPE_LIST,
                Locale.ENGLISH);

        assertNotNull(history);
    }

    @Configuration
    static class InnerConf extends LegacyAppContextConfig {

        @Bean
        public InputOutputDao inputOutputDao() {
            return new InputOutputDaoImpl();
        }

        @Bean
        public MessageSource messageSource() {
            return Mockito.mock(MessageSource.class);
        }

    }
}
