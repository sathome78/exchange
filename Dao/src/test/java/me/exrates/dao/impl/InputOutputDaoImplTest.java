package me.exrates.dao.impl;

import me.exrates.dao.InputOutputDao;
import me.exrates.dao.configuration.TestConfiguration;
import me.exrates.model.dto.TransactionFilterDataDto;
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
@ContextConfiguration(classes = {TestConfiguration.class, InputOutputDaoImplTest.InnerConf.class})
public class InputOutputDaoImplTest {

    private static final String EMAIL = "shvets.k@gmail.com";
    private static final LocalDateTime START_DATE = LocalDateTime.now().minusMonths(6);
    private static final LocalDateTime END_DATE = LocalDateTime.now();
    private static final LocalDateTime FUTURE_START_DATE = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime FUTURE_END_DATE = LocalDateTime.now().plusDays(2);
    private static final List<Integer> OPERATION_TYPE_LIST = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

    @Autowired
    private InputOutputDao inputOutputDao;

    private TransactionFilterDataDto filter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        filter = TransactionFilterDataDto.builder()
                .email(EMAIL)
                .limit(0)
                .offset(0)
                .operationTypes(OPERATION_TYPE_LIST)
                .currencyId(0)
                .currencyName(StringUtils.EMPTY)
                .build();
    }

    @Test
    public void findMyInputOutputHistoryByOperationTypeTest_WithResult() {
        List<MyInputOutputHistoryDto> history = inputOutputDao.findMyInputOutputHistoryByOperationType(
                filter.toBuilder()
                        .dateFrom(START_DATE)
                        .dateTo(END_DATE)
                        .build(),
                Locale.ENGLISH);

        assertNotNull(history);
    }

    @Test
    public void findMyInputOutputHistoryByOperationTypeTest_WithoutResult() {
        List<MyInputOutputHistoryDto> history = inputOutputDao.findMyInputOutputHistoryByOperationType(
                filter.toBuilder()
                        .dateFrom(FUTURE_START_DATE)
                        .dateTo(FUTURE_END_DATE)
                        .build(),
                Locale.ENGLISH);

        assertNotNull(history);
    }

    @Configuration
    static class InnerConf {

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