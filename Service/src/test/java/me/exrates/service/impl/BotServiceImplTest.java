package me.exrates.service.impl;

import me.exrates.dao.BotDao;
import me.exrates.model.*;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.PriceGrowthDirection;
import me.exrates.service.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.Scheduler;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BotServiceImplTest {

    @Mock
    private OrderServiceImpl orderService;
    @Mock
    private UserService userService;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private SendMailService sendMailService;

    @Mock
    private ReferralService referralService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private BotDao botDao;

    @Mock
    private Scheduler botOrderCreationScheduler;

    private BotTrader botTrader;

    private final String TEST_USER_EMAIL = "talalai@talala.com";

    private BotTradingSettings testSettings;

    private CurrencyPair currencyPair;


    @InjectMocks
    private BotServiceImpl botService = new BotServiceImpl();


    @Before
    public void setUp() {

        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn(" ");

        currencyPair = new CurrencyPair();
        currencyPair.setId(1);
        currencyPair.setName("BTC/USD");
        Currency currency1 = new Currency();
        currency1.setName("BTC");
        currency1.setId(4);
        currency1.setDescription("Bitcoin");
        currencyPair.setCurrency1(currency1);

        Currency currency2 = new Currency();
        currency2.setName("USD");
        currency2.setId(1);
        currency2.setDescription("US Dollar");
        currencyPair.setCurrency2(currency2);

        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(currencyPair);

        botTrader = new BotTrader();
        botTrader.setId(1);
        botTrader.setUserId(25);
        botTrader.setEnabled(true);
        botTrader.setAcceptDelayInMillis(3);
        when(botDao.retrieveBotTrader()).thenReturn(Optional.of(botTrader));

        when(userService.getEmailById(botTrader.getUserId())).thenReturn(TEST_USER_EMAIL);

        BotLaunchSettings botLaunchSettings = new BotLaunchSettings();
        botLaunchSettings.setUserOrderPriceConsidered(true);
        botLaunchSettings.setCurrencyPairName(currencyPair.getName());
        botLaunchSettings.setCreateTimeoutInSeconds(1);
        botLaunchSettings.setLaunchIntervalInMinutes(1);
        botLaunchSettings.setEnabledForPair(true);
        botLaunchSettings.setQuantityPerSequence(3);
        botLaunchSettings.setId(1);
        botLaunchSettings.setBotId(botTrader.getId());

        testSettings = new BotTradingSettings();
        testSettings.setId(10);
        testSettings.setBotLaunchSettings(botLaunchSettings);
        testSettings.setMaxAmount(new BigDecimal(500));
        testSettings.setMinAmount(new BigDecimal(100));
        testSettings.setMinPrice(new BigDecimal(1200));
        testSettings.setMaxPrice(new BigDecimal(1500));
        testSettings.setMinUserPrice(new BigDecimal(1250));
        testSettings.setMaxUserPrice(new BigDecimal(1450));
        testSettings.setPriceStep(new BigDecimal(10));
        testSettings.setDirection(PriceGrowthDirection.UP);

        when(botDao.retrieveBotTradingSettingsForCurrencyPairAndOrderType(botTrader.getId(), 1, OrderType.SELL))
                .thenReturn(Optional.of(testSettings));
            when(orderService.prepareNewOrder(any(), any(), anyString(), any(), any(), any())).thenCallRealMethod();
        when(orderService.createOrder(any(), eq(OrderActionEnum.CREATE))).thenReturn(111);

    }


    @Test
    public void runOrderCreationSequenceRegularTest() {
        BigDecimal startPrice = new BigDecimal(1300);
        when(orderService.getLastOrderPriceByCurrencyPairAndOperationType(currencyPair, OperationType.valueOf(OrderType.SELL.name())))
                .thenReturn(Optional.of(startPrice));
        int ordersPerSequence = testSettings.getBotLaunchSettings().getQuantityPerSequence();
        long totalTimeout = ordersPerSequence * testSettings.getBotLaunchSettings().getCreateTimeoutInSeconds() * 1000;
        BigDecimal endPrice = startPrice.add(testSettings.getPriceStep().multiply(new BigDecimal(ordersPerSequence)));

        ArgumentCaptor<BigDecimal> rateCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        botService.runOrderCreation(1, OrderType.SELL);
        verify(orderService, timeout(totalTimeout).times(ordersPerSequence))
                .prepareNewOrder(any(), any(), anyString(), any(), rateCaptor.capture(), any());
        verify(botDao, never()).updatePriceGrowthDirection(eq(testSettings.getId()), any(PriceGrowthDirection.class));
        List<BigDecimal> allRates = rateCaptor.getAllValues();
        assertEquals(ordersPerSequence, allRates.size());
        assertEquals(endPrice, allRates.get(ordersPerSequence - 1));
    }

    @Test
    public void runOrderCreationSequenceTest_NoLastPrice() {
        BigDecimal startPrice = testSettings.getBotLaunchSettings().isUserOrderPriceConsidered() ? testSettings.getMinUserPrice() : testSettings.getMinPrice();
        when(orderService.getLastOrderPriceByCurrencyPairAndOperationType(currencyPair, OperationType.valueOf(OrderType.SELL.name())))
                .thenReturn(Optional.empty());
        ArgumentCaptor<BigDecimal> rateCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        int ordersPerSequence = testSettings.getBotLaunchSettings().getQuantityPerSequence();
        long totalTimeout = ordersPerSequence * testSettings.getBotLaunchSettings().getCreateTimeoutInSeconds() * 1000;

        BigDecimal endPrice = startPrice.add(testSettings.getPriceStep().multiply(new BigDecimal(ordersPerSequence)));
        botService.runOrderCreation(1, OrderType.SELL);
        verify(orderService, timeout(totalTimeout).times(ordersPerSequence))
                .prepareNewOrder(any(), any(), anyString(), any(), rateCaptor.capture(), any());
        verify(botDao, never()).updatePriceGrowthDirection(eq(testSettings.getId()), any(PriceGrowthDirection.class));
        List<BigDecimal> allRates = rateCaptor.getAllValues();
        assertEquals(ordersPerSequence, allRates.size());
        assertEquals(endPrice, allRates.get(ordersPerSequence - 1));
    }

    @Test
    public void runOrderCreationSequenceTest_ChangeDirection() {
        BigDecimal startPrice = new BigDecimal(1430);
        when(orderService.getLastOrderPriceByCurrencyPairAndOperationType(currencyPair, OperationType.valueOf(OrderType.SELL.name())))
                .thenReturn(Optional.of(startPrice));
        ArgumentCaptor<BigDecimal> rateCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        int ordersPerSequence = testSettings.getBotLaunchSettings().getQuantityPerSequence();
        long totalTimeout = ordersPerSequence * testSettings.getBotLaunchSettings().getCreateTimeoutInSeconds() * 1000;
        botService.runOrderCreation(1, OrderType.SELL);
        verify(orderService, timeout(totalTimeout).times(ordersPerSequence))
                .prepareNewOrder(any(), any(), anyString(), any(), rateCaptor.capture(), any());
        verify(botDao).updatePriceGrowthDirection(testSettings.getId(), PriceGrowthDirection.DOWN);
        List<BigDecimal> allRates = rateCaptor.getAllValues();
        assertEquals(ordersPerSequence, allRates.size());
        assertEquals(new BigDecimal(1440), allRates.get(ordersPerSequence - 1));
    }


}
