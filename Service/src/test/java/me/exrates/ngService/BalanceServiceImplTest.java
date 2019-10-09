package me.exrates.ngService;

import lombok.extern.slf4j.Slf4j;
import me.exrates.dao.CurrencyDao;
import me.exrates.dao.UserDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.dto.BalanceFilterDataDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.enums.CurrencyType;
import me.exrates.model.enums.TradeMarket;
import me.exrates.model.ngUtil.PagedResult;
import me.exrates.ngService.impl.BalanceServiceImpl;
import me.exrates.service.cache.ExchangeRatesHolder;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class BalanceServiceImplTest {

    private static final String BTC = "BTC";
    private static final String TEST_BTC = "TBTC";
    private static final String ETH = "ETH";
    private static final String RUB = "RUB";

    private static final CurrencyType TYPE = CurrencyType.CRYPTO;

    private static final String EMAIL = "sample@test.com";

    @Mock
    private NgWalletService ngWalletService;
    @Mock
    private ExchangeRatesHolder exchangeRatesHolder;

    @Mock
    private UserDao userDao;

    @Mock
    private WalletDao walletDao;

    @Mock
    private CurrencyDao currencyDao;

    private BalanceService balanceService;

    private BalanceFilterDataDto filter;
    private Locale locale;

    @Before
    public void setUp() {
        doReturn(Collections.singletonMap(1, "1")).when(exchangeRatesHolder).getRatesForMarket(TradeMarket.BTC);
        doReturn(Collections.singletonMap(2, "10")).when(exchangeRatesHolder).getRatesForMarket(TradeMarket.USD);
        doReturn(BigDecimal.valueOf(0.01)).when(exchangeRatesHolder).getBtcUsdRate();

        balanceService = new BalanceServiceImpl(null, null, ngWalletService,
                null, null, exchangeRatesHolder, null, userDao, walletDao, currencyDao);

        locale = Locale.ENGLISH;

        filter = BalanceFilterDataDto.builder()
                .limit(20)
                .offset(0)
                .excludeZero(true)
                .currencyName(BTC)
                .currencyId(1)
                .currencyType(TYPE)
                .email(EMAIL)
                .build();
    }

    @Test
    public void getWalletsDetails_emptyDetailsListTest() {
        log.debug("getWalletsDetails_emptyDetailsListTest() - start");

        doReturn(Collections.emptyList()).when(ngWalletService).getAllWalletsForUserDetailed(EMAIL, locale, TYPE);

        PagedResult<MyWalletsDetailedDto> pagedResult = balanceService.getWalletsDetails(filter);
        verify(ngWalletService, atLeastOnce()).getAllWalletsForUserDetailed(anyString(), any(Locale.class), any(CurrencyType.class));

        assertNotNull("Result could not be null", pagedResult);
        assertEquals("Result count could be equals to 0", 0, pagedResult.getCount());
        assertTrue("Result items list could be empty", pagedResult.getItems().isEmpty());

        log.debug("getWalletsDetails_emptyDetailsListTest() - end");
    }

    @Test
    public void getWalletsDetails_detailsListWithOneResultZeroBalancesTest() {
        log.debug("getWalletsDetails_detailsListWithOneResultZeroBalancesTest() - start");

        List<MyWalletsDetailedDto> result = new ArrayList<>();

        MyWalletsDetailedDto detailedDto1 = new MyWalletsDetailedDto();
        detailedDto1.setCurrencyId(1);
        detailedDto1.setCurrencyName(BTC);
        detailedDto1.setActiveBalance("0");
        detailedDto1.setReservedBalance("0");
        detailedDto1.setReservedByOrders("0");
        detailedDto1.setReservedByMerchant("0");
        detailedDto1.setOnConfirmation("0");
        result.add(detailedDto1);

        doReturn(result).when(ngWalletService).getAllWalletsForUserDetailed(EMAIL, locale, TYPE);

        PagedResult<MyWalletsDetailedDto> pagedResult = balanceService.getWalletsDetails(filter);

        verify(ngWalletService, atLeastOnce()).getAllWalletsForUserDetailed(anyString(), any(Locale.class), any(CurrencyType.class));

        assertNotNull("Result could not be null", pagedResult);
        assertEquals("Result count could be equals to 0", 0, pagedResult.getCount());
        assertTrue("Result items list could be empty", pagedResult.getItems().isEmpty());

        log.debug("getWalletsDetails_detailsListWithOneResultZeroBalancesTest() - start");
    }

    @Test
    public void getWalletsDetails_detailsListWithTwoResultsOneResultRubTest() {
        log.debug("getWalletsDetails_detailsListWithTwoResultsOneResultRubTest() - start");

        List<MyWalletsDetailedDto> result = new ArrayList<>();

        MyWalletsDetailedDto detailedDto1 = new MyWalletsDetailedDto();
        detailedDto1.setCurrencyId(3);
        detailedDto1.setCurrencyName(RUB);
        detailedDto1.setActiveBalance("0");
        detailedDto1.setReservedByOrders("0");
        detailedDto1.setReservedByMerchant("0");
        detailedDto1.setOnConfirmation("0");
        detailedDto1.setReservedBalance("0");
        result.add(detailedDto1);

        MyWalletsDetailedDto detailedDto2 = new MyWalletsDetailedDto();
        detailedDto2.setCurrencyId(1);
        detailedDto2.setCurrencyName(BTC);
        detailedDto2.setActiveBalance("10");
        detailedDto2.setReservedByOrders("0");
        detailedDto2.setReservedByMerchant("0");
        detailedDto2.setOnConfirmation("1");
        detailedDto2.setReservedBalance("1");
        result.add(detailedDto2);

        doReturn(result).when(ngWalletService).getAllWalletsForUserDetailed(EMAIL, locale, TYPE);

        PagedResult<MyWalletsDetailedDto> pagedResult = balanceService.getWalletsDetails(filter);

        verify(ngWalletService, atLeastOnce()).getAllWalletsForUserDetailed(anyString(), any(Locale.class), any(CurrencyType.class));

        assertNotNull("Result could not be null", pagedResult);
        assertEquals("Result count could be equals to 1", 1, pagedResult.getCount());
        assertFalse("Result items list could not be empty", pagedResult.getItems().isEmpty());
        assertEquals("Result items list size could be equal to 1", 1, pagedResult.getItems().size());

        log.debug("getWalletsDetails_detailsListWithTwoResultsOneResultRubTest() - start");
    }

    @Test
    public void getWalletsDetails_detailsListWithTwoResultsCurrencyIdMoreThanZeroTest() {
        log.debug("getWalletsDetails_detailsListWithTwoResultsCurrencyIdMoreThanZeroTest() - start");

        List<MyWalletsDetailedDto> result = new ArrayList<>();

        MyWalletsDetailedDto detailedDto1 = new MyWalletsDetailedDto();
        detailedDto1.setCurrencyId(4);
        detailedDto1.setCurrencyName(ETH);
        detailedDto1.setActiveBalance("20");
        detailedDto1.setReservedByOrders("0");
        detailedDto1.setReservedByMerchant("0");
        detailedDto1.setOnConfirmation("5");
        detailedDto1.setReservedBalance("5");
        result.add(detailedDto1);

        MyWalletsDetailedDto detailedDto2 = new MyWalletsDetailedDto();
        detailedDto2.setCurrencyId(1);
        detailedDto2.setCurrencyName(BTC);
        detailedDto2.setActiveBalance("10");
        detailedDto2.setReservedByOrders("0");
        detailedDto2.setReservedByMerchant("0");
        detailedDto2.setOnConfirmation("1");
        detailedDto2.setReservedBalance("1");
        result.add(detailedDto2);

        doReturn(result).when(ngWalletService).getAllWalletsForUserDetailed(EMAIL, locale, TYPE);

        PagedResult<MyWalletsDetailedDto> pagedResult = balanceService.getWalletsDetails(filter);

        verify(ngWalletService, atLeastOnce()).getAllWalletsForUserDetailed(anyString(), any(Locale.class), any(CurrencyType.class));

        assertNotNull("Result could not be null", pagedResult);
        assertEquals("Result count could be equals to 1", 1, pagedResult.getCount());
        assertFalse("Result items list could not be empty", pagedResult.getItems().isEmpty());
        assertEquals("Result items list size could be equal to 1", 1, pagedResult.getItems().size());

        log.debug("getWalletsDetails_detailsListWithTwoResultsCurrencyIdMoreThanZeroTest() - start");
    }

    @Test
    public void getWalletsDetails_detailsListWithTwoResultsCurrencyIdEqualsZeroAndCurrencyNameNotBlankTest() {
        log.debug("getWalletsDetails_detailsListWithTwoResultsCurrencyIdEqualsZeroAndCurrencyNameNotBlankTest() - start");

        List<MyWalletsDetailedDto> result = new ArrayList<>();

        MyWalletsDetailedDto detailedDto1 = new MyWalletsDetailedDto();
        detailedDto1.setCurrencyId(4);
        detailedDto1.setCurrencyName(TEST_BTC);
        detailedDto1.setActiveBalance("20");
        detailedDto1.setReservedByOrders("0");
        detailedDto1.setReservedByMerchant("0");
        detailedDto1.setOnConfirmation("5");
        detailedDto1.setReservedBalance("5");
        result.add(detailedDto1);

        MyWalletsDetailedDto detailedDto2 = new MyWalletsDetailedDto();
        detailedDto2.setCurrencyId(1);
        detailedDto2.setCurrencyName(BTC);
        detailedDto2.setActiveBalance("10");
        detailedDto2.setReservedByOrders("0");
        detailedDto2.setReservedByMerchant("0");
        detailedDto2.setOnConfirmation("1");
        detailedDto2.setReservedBalance("1");
        result.add(detailedDto2);

        doReturn(result).when(ngWalletService).getAllWalletsForUserDetailed(EMAIL, locale, TYPE);

        PagedResult<MyWalletsDetailedDto> pagedResult = balanceService.getWalletsDetails(filter.toBuilder()
                .currencyId(0)
                .build());

        verify(ngWalletService, atLeastOnce()).getAllWalletsForUserDetailed(anyString(), any(Locale.class), any(CurrencyType.class));

        assertNotNull("Result could not be null", pagedResult);
        assertEquals("Result count could be equals to 2", 2, pagedResult.getCount());
        assertFalse("Result items list could not be empty", pagedResult.getItems().isEmpty());
        assertEquals("Result items list size could be equal to 2", 2, pagedResult.getItems().size());

        log.debug("getWalletsDetails_detailsListWithTwoResultsCurrencyIdEqualsZeroAndCurrencyNameNotBlankTest() - start");
    }

    @Test
    public void getWalletsDetails_detailsListWithTwoResultsCurrencyIdEqualsZeroAndCurrencyNameBlankTest() {
        log.debug("getWalletsDetails_detailsListWithTwoResultsCurrencyIdEqualsZeroAndCurrencyNameBlankTest() - start");

        List<MyWalletsDetailedDto> result = new ArrayList<>();

        MyWalletsDetailedDto detailedDto1 = new MyWalletsDetailedDto();
        detailedDto1.setCurrencyId(4);
        detailedDto1.setCurrencyName(TEST_BTC);
        detailedDto1.setActiveBalance("20");
        detailedDto1.setReservedByOrders("0");
        detailedDto1.setReservedByMerchant("0");
        detailedDto1.setOnConfirmation("5");
        detailedDto1.setReservedBalance("5");
        result.add(detailedDto1);

        MyWalletsDetailedDto detailedDto2 = new MyWalletsDetailedDto();
        detailedDto2.setCurrencyId(1);
        detailedDto2.setCurrencyName(BTC);
        detailedDto2.setActiveBalance("10");
        detailedDto2.setReservedByOrders("0");
        detailedDto2.setReservedByMerchant("0");
        detailedDto2.setOnConfirmation("1");
        detailedDto2.setReservedBalance("1");
        result.add(detailedDto2);

        MyWalletsDetailedDto detailedDto3 = new MyWalletsDetailedDto();
        detailedDto3.setCurrencyId(2);
        detailedDto3.setCurrencyName(ETH);
        detailedDto3.setActiveBalance("15");
        detailedDto3.setReservedByOrders("0");
        detailedDto3.setReservedByMerchant("0");
        detailedDto3.setOnConfirmation("15");
        detailedDto3.setReservedBalance("15");
        result.add(detailedDto3);

        doReturn(result).when(ngWalletService).getAllWalletsForUserDetailed(EMAIL, locale, TYPE);

        PagedResult<MyWalletsDetailedDto> pagedResult = balanceService.getWalletsDetails(filter.toBuilder()
                .currencyId(0)
                .currencyName(StringUtils.EMPTY)
                .build());

        verify(ngWalletService, atLeastOnce()).getAllWalletsForUserDetailed(anyString(), any(Locale.class), any(CurrencyType.class));

        assertNotNull("Result could not be null", pagedResult);
        assertEquals("Result count could be equals to 3", 3, pagedResult.getCount());
        assertFalse("Result items list could not be empty", pagedResult.getItems().isEmpty());
        assertEquals("Result items list size could be equal to 3", 3, pagedResult.getItems().size());

        log.debug("getWalletsDetails_detailsListWithTwoResultsCurrencyIdEqualsZeroAndCurrencyNameBlankTest() - start");
    }

    @Test(expected = Exception.class)
    public void getWalletsDetails_errorTest() {
        log.debug("getWalletsDetails_errorTest() - start");

        doThrow(new Exception("Something happened wrong")).when(ngWalletService).getAllWalletsForUserDetailed(EMAIL, locale, TYPE);

        balanceService.getWalletsDetails(filter);

        verify(ngWalletService, atLeastOnce()).getAllWalletsForUserDetailed(anyString(), any(Locale.class), any(CurrencyType.class));

        log.debug("getWalletsDetails_errorTest() - start");
    }
}