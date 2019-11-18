package me.exrates.service.impl;

import me.exrates.configurations.CacheConfiguration;
import me.exrates.dao.CurrencyDao;
import me.exrates.dao.exception.notfound.CurrencyPairNotFoundException;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.CurrencyPair;
import me.exrates.model.User;
import me.exrates.model.dto.CurrencyPairLimitDto;
import me.exrates.model.dto.CurrencyReportInfoDto;
import me.exrates.model.dto.MerchantCurrencyScaleDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.api.BalanceDto;
import me.exrates.model.dto.api.RateDto;
import me.exrates.model.dto.mobileApiDto.TransferLimitDto;
import me.exrates.model.dto.mobileApiDto.dashboard.CurrencyPairWithLimitsDto;
import me.exrates.model.dto.openAPI.CurrencyPairInfoItem;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.Market;
import me.exrates.model.enums.MerchantProcessType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.UserCommentTopicEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.service.CurrencyService;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import me.exrates.service.api.ExchangeApi;
import me.exrates.service.exception.ScaleForAmountNotSetException;
import me.exrates.service.util.BigDecimalConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static me.exrates.configurations.CacheConfiguration.CURRENCY_BY_NAME_CACHE;
import static me.exrates.configurations.CacheConfiguration.CURRENCY_PAIRS_LIST_BY_TYPE_CACHE;
import static me.exrates.configurations.CacheConfiguration.CURRENCY_PAIR_BY_ID_CACHE;
import static me.exrates.configurations.CacheConfiguration.CURRENCY_PAIR_BY_NAME_CACHE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceTestConfig.class, CacheConfiguration.class})
public class CurrencyServiceImplTest {

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CurrencyDao currencyDao;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private ExchangeApi exchangeApi;

    @Autowired
    private BigDecimalConverter converter;

    @Autowired
    @Qualifier(CURRENCY_BY_NAME_CACHE)
    private Cache currencyByNameCache;

    @Autowired
    @Qualifier(CURRENCY_PAIR_BY_NAME_CACHE)
    private Cache currencyPairByNameCache;

    @Autowired
    @Qualifier(CURRENCY_PAIR_BY_ID_CACHE)
    private Cache currencyPairByIdCache;

    @Autowired
    @Qualifier(CURRENCY_PAIRS_LIST_BY_TYPE_CACHE)
    private Cache currencyPairsListByTypeCache;

    private List<UserCurrencyOperationPermissionDto> userCurrencyOperationPermissionDtoList;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("USER", "testemail@gmail.com",
                        AuthorityUtils.createAuthorityList("USER")));

        userCurrencyOperationPermissionDtoList = new ArrayList<>();
        UserCurrencyOperationPermissionDto entity = new UserCurrencyOperationPermissionDto();
        entity.setCurrencyName("FFF");
        userCurrencyOperationPermissionDtoList.add(entity);

        reset(currencyDao);
        reset(exchangeApi);
    }

    @Test
    public void getCurrencyName_Test() {
        when(currencyDao.getCurrencyName(anyInt())).thenReturn("name");

        assertEquals("name", currencyService.getCurrencyName(5));
        verify(currencyDao, times(1)).getCurrencyName(5);
    }

    @Test
    public void getAllActiveCurrencies_Test() {
        List<Currency> currencyList = Arrays.asList(new Currency());
        stub(currencyDao.getAllActiveCurrencies()).toReturn(currencyList);

        assertEquals(currencyList, currencyService.getAllActiveCurrencies());
        verify(currencyDao, times(1)).getAllActiveCurrencies();
    }

    @Test
    public void getAllCurrencies_Test() {
        List<Currency> currencyList = Arrays.asList(new Currency());
        stub(currencyDao.getAllCurrencies()).toReturn(currencyList);

        assertEquals(currencyList, currencyService.getAllCurrencies());
        verify(currencyDao, times(1)).getAllCurrencies();
    }

    @Test
    public void findByName_Test() {
        Currency currency = new Currency(8);
        when(currencyDao.findByName(anyString())).thenReturn(currency);

        assertEquals(currency, currencyService.findByName("test"));
        verify(currencyDao, times(1)).findByName("test");
    }

    @Test
    public void findById_Test() {
        Currency currency = new Currency(8);
        when(currencyDao.findById(anyInt())).thenReturn(currency);

        assertEquals(currency, currencyService.findById(7));
        verify(currencyDao, times(1)).findById(7);
    }

    @Test
    public void findAllCurrencies_Test() {
        List<Currency> currencyList = Arrays.asList(new Currency());
        when(currencyDao.findAllCurrencies()).thenReturn(currencyList);

        assertEquals(currencyList, currencyService.findAllCurrencies());
        verify(currencyDao, times(1)).findAllCurrencies();
    }

    @Test
    public void updateCurrencyLimit_Test() {
        when(userRoleService.getRealUserRoleIdByBusinessRoleList(anyString())).thenReturn(Arrays.asList(5, 6, 7));
        doNothing().when(currencyDao).updateCurrencyLimit(anyInt(), any(OperationType.class), anyListOf(Integer.TYPE), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class), any(Integer.class));
        currencyService.updateCurrencyLimit(6, OperationType.STORNO, "", new BigDecimal(5), new BigDecimal(5),  new BigDecimal(9), new BigDecimal(1), 8);

        verify(userRoleService, times(1)).getRealUserRoleIdByBusinessRoleList("");
        verify(currencyDao, times(1)).updateCurrencyLimit(6, OperationType.STORNO, Arrays.asList(5, 6, 7), new BigDecimal(5), new BigDecimal(5), new BigDecimal(9), new BigDecimal(1), 8);
    }

    @Test
    public void updateCurrencyLimit1_Test() {
        doNothing().when(currencyDao).updateCurrencyLimit(anyInt(), any(OperationType.class), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class), any(Integer.class));
        currencyService.updateCurrencyLimit(6, OperationType.STORNO, new BigDecimal(5), new BigDecimal(5), new BigDecimal(1), new BigDecimal(8), 8);

        verify(currencyDao, times(1)).updateCurrencyLimit(6, OperationType.STORNO, new BigDecimal(5), new BigDecimal(5), new BigDecimal(1), new BigDecimal(8), 8);
    }

    @Test
    public void retrieveCurrencyLimitsForRole_Test() {
        List<Integer> integerList = Arrays.asList(5, 6, 7, 9);
        List<CurrencyLimit> currencyLimits = Arrays.asList(new CurrencyLimit());

        when(userRoleService.getRealUserRoleIdByBusinessRoleList(anyString())).thenReturn(integerList);
        when(currencyDao.retrieveCurrencyLimitsForRoles(anyList(), any(OperationType.class))).thenReturn(currencyLimits);

        assertEquals(currencyLimits, currencyService.retrieveCurrencyLimitsForRole("test", OperationType.INPUT));

        verify(userRoleService, times(1)).getRealUserRoleIdByBusinessRoleList("test");
        verify(currencyDao, times(1)).retrieveCurrencyLimitsForRoles(integerList, OperationType.INPUT);
    }

    @Test
    public void retrieveMinLimitForRoleAndCurrency_Test() {
        when(currencyDao.retrieveMinLimitForRoleAndCurrency(any(UserRole.class), any(OperationType.class), anyInt())).thenReturn(new BigDecimal(10));

        assertEquals(new BigDecimal(10), currencyService.retrieveMinLimitForRoleAndCurrency(UserRole.USER, OperationType.BUY, 777));

        verify(currencyDao, times(1)).retrieveMinLimitForRoleAndCurrency(UserRole.USER, OperationType.BUY, 777);
    }

    @Test
    public void retrieveMaxDailyRequestForRoleAndCurrency_Test() {
        when(currencyDao.retrieveMaxDailyRequestForRoleAndCurrency(any(UserRole.class), any(OperationType.class), anyInt())).thenReturn(new BigDecimal(10));

        assertEquals(new BigDecimal(10), currencyService.retrieveMaxDailyRequestForRoleAndCurrency(UserRole.USER, OperationType.BUY, 777));

        verify(currencyDao, times(1)).retrieveMaxDailyRequestForRoleAndCurrency(UserRole.USER, OperationType.BUY, 777);
    }

    @Test
    public void getAllCurrencyPairs_Test() {
        CurrencyPair currencyPair = new CurrencyPair("New Curr");
        when(currencyDao.getAllCurrencyPairs(anyObject())).thenReturn(Arrays.asList(currencyPair));

        assertEquals(Arrays.asList(currencyPair), currencyService.getAllCurrencyPairs(CurrencyPairType.ICO));

        verify(currencyDao, times(1)).getAllCurrencyPairs(CurrencyPairType.ICO);
    }

    @Test
    public void getAllCurrencyPairsWithHidden_Test() {
        when(currencyDao.getAllCurrencyPairsWithHidden(any(CurrencyPairType.class))).thenReturn(Arrays.asList(new CurrencyPair("Some Curr")));

        assertEquals(Arrays.asList(new CurrencyPair("Some Curr")), currencyService.getAllCurrencyPairsWithHidden(CurrencyPairType.MAIN));

        verify(currencyDao, times(1)).getAllCurrencyPairsWithHidden(CurrencyPairType.MAIN);
    }

    @Test
    public void getAllCurrencyPairsInAlphabeticOrder_Test() {
        List<CurrencyPair> result = Arrays.asList(
                new CurrencyPair("zz"),
                new CurrencyPair("dfF"),
                new CurrencyPair("2"),
                new CurrencyPair("1"));

        when(currencyDao.getAllCurrencyPairs(any(CurrencyPairType.class))).thenReturn(result);
        result.sort(Comparator.comparing(CurrencyPair::getName));

        assertEquals(result.get(0), currencyService.getAllCurrencyPairsInAlphabeticOrder(CurrencyPairType.MAIN).get(0));
        assertEquals(result.get(1), currencyService.getAllCurrencyPairsInAlphabeticOrder(CurrencyPairType.MAIN).get(1));
        assertEquals(result.get(2), currencyService.getAllCurrencyPairsInAlphabeticOrder(CurrencyPairType.MAIN).get(2));
        assertEquals(result.get(3), currencyService.getAllCurrencyPairsInAlphabeticOrder(CurrencyPairType.MAIN).get(3));

        verify(currencyDao, times(1)).getAllCurrencyPairs(CurrencyPairType.MAIN);
    }

    @Test
    public void getAllCurrencyPairsWithHiddenInAlphabeticOrder() {
        List<CurrencyPair> result = Arrays.asList(
                new CurrencyPair("zz"),
                new CurrencyPair("dfF"),
                new CurrencyPair("2"),
                new CurrencyPair("1"));

        when(currencyDao.getAllCurrencyPairsWithHidden(any(CurrencyPairType.class))).thenReturn(result);
        result.sort(Comparator.comparing(CurrencyPair::getName));

        assertEquals(result.get(0), currencyService.getAllCurrencyPairsWithHiddenInAlphabeticOrder(CurrencyPairType.MAIN).get(0));
        assertEquals(result.get(1), currencyService.getAllCurrencyPairsWithHiddenInAlphabeticOrder(CurrencyPairType.MAIN).get(1));
        assertEquals(result.get(2), currencyService.getAllCurrencyPairsWithHiddenInAlphabeticOrder(CurrencyPairType.MAIN).get(2));
        assertEquals(result.get(3), currencyService.getAllCurrencyPairsWithHiddenInAlphabeticOrder(CurrencyPairType.MAIN).get(3));

        verify(currencyDao, times(4)).getAllCurrencyPairsWithHidden(CurrencyPairType.MAIN);
    }

    @Test
    public void findCurrencyPairById_WhenOk() {
        when(currencyDao.findCurrencyPairById(anyInt())).thenReturn(new CurrencyPair("Curr"));
        assertEquals(new CurrencyPair("Curr"), currencyService.findCurrencyPairById(7));

        verify(currencyDao, times(1)).findCurrencyPairById(7);
    }

    @Test(expected = CurrencyPairNotFoundException.class)
    public void findCurrencyPairById_WhenException() {
        when(currencyDao.findCurrencyPairById(anyInt())).thenThrow(EmptyResultDataAccessException.class);
        currencyService.findCurrencyPairById(7);
    }

    @Test
    public void amountToString_Test() {
        assertEquals("7.00000000", currencyService.amountToString(new BigDecimal(7), "EDR"));
    }

    @Test
    public void resolvePrecision_Test() {
        assertEquals(8, currencyService.resolvePrecision("EDR"));
        assertEquals(2, currencyService.resolvePrecision("EDR2"));
    }

    @Test
    public void resolvePrecisionByOperationType_WhenEDC_OUTPUT_PRECISION() {
        Currency currency = new Currency(7);
        currency.setName("EDR");
        when(currencyDao.findByName(anyString())).thenReturn(currency);

        assertEquals(3, currencyService.resolvePrecisionByOperationType("EDR", OperationType.OUTPUT));
        verify(currencyDao, times(1)).findByName(anyString());
    }

    @Test
    public void resolvePrecisionByOperationType_WhenCRYPTO_PRECISION() {
        Currency currency = new Currency(7);
        currency.setName("EDR");
        when(currencyDao.findByName(anyString())).thenReturn(currency);

        assertEquals(8, currencyService.resolvePrecisionByOperationType("EDR", OperationType.STORNO));
        verify(currencyDao, times(1)).findByName(anyString());
    }

    @Test
    public void resolvePrecisionByOperationType_WhenDEFAULT_PRECISION() {
        Currency currency = new Currency(7);
        currency.setName("EDR2");
        when(currencyDao.findByName(anyString())).thenReturn(currency);

        assertEquals(2, currencyService.resolvePrecisionByOperationType("rfrEDR", OperationType.STORNO));
        verify(currencyDao, times(1)).findByName(anyString());
    }

    @Test
    public void retrieveMinTransferLimits_Test() {
        List<TransferLimitDto> transferLimitDtos = Arrays.asList(new TransferLimitDto());

        when(currencyDao.retrieveMinTransferLimits(anyList(), anyInt())).thenReturn(transferLimitDtos);

        assertEquals(transferLimitDtos, currencyService.retrieveMinTransferLimits(Arrays.asList(5, 6, 7)));

        verify(currencyDao, times(1)).retrieveMinTransferLimits(Arrays.asList(5, 6, 7), 4);
    }

    @Test
    public void findWithOperationPermissionByUserAndDirection_Test() {
        when(currencyDao.findCurrencyOperationPermittedByUserAndDirection(anyInt(), anyString())).thenReturn(userCurrencyOperationPermissionDtoList);

        assertEquals(userCurrencyOperationPermissionDtoList, currencyService.findWithOperationPermissionByUserAndDirection(90, InvoiceOperationDirection.REFILL));
        verify(currencyDao, times(1)).findCurrencyOperationPermittedByUserAndDirection(90, "REFILL");
    }

    @Test
    public void getCurrencyOperationPermittedForRefill_Test() {
        when(userService.getIdByEmail(anyString())).thenReturn(99);
        when(currencyDao.findCurrencyOperationPermittedByUserAndDirection(anyInt(), anyString())).thenReturn(userCurrencyOperationPermissionDtoList);

        assertEquals(userCurrencyOperationPermissionDtoList, currencyService.getCurrencyOperationPermittedForRefill("email@email.com"));

        verify(currencyDao, times(1)).findCurrencyOperationPermittedByUserAndDirection(99, "REFILL");

    }

    @Test
    public void getAllCurrencyOperationPermittedForRefill_Test() {
        when(userService.getIdByEmail(anyString())).thenReturn(99);
        when(currencyDao.findAllCurrencyOperationPermittedByUserAndDirection(anyInt(), anyString())).thenReturn(userCurrencyOperationPermissionDtoList);

        assertEquals(userCurrencyOperationPermissionDtoList, currencyService.getAllCurrencyOperationPermittedForRefill("email@email.com"));

        verify(currencyDao, times(1)).findAllCurrencyOperationPermittedByUserAndDirection(99, "REFILL");
    }

    @Test
    public void getCurrencyOperationPermittedForWithdraw_Test() {
        when(userService.getIdByEmail(anyString())).thenReturn(99);
        when(currencyDao.findCurrencyOperationPermittedByUserAndDirection(anyInt(), anyString())).thenReturn(userCurrencyOperationPermissionDtoList);
        assertEquals(userCurrencyOperationPermissionDtoList, currencyService.getCurrencyOperationPermittedForWithdraw("email@email.com"));

        verify(currencyDao, times(1)).findCurrencyOperationPermittedByUserAndDirection(99, "WITHDRAW");
    }

    @Test
    public void getAllCurrencyOperationPermittedForWithdraw_Test() {
        when(userService.getIdByEmail(anyString())).thenReturn(99);
        when(currencyDao.findAllCurrencyOperationPermittedByUserAndDirection(anyInt(), anyString())).thenReturn(userCurrencyOperationPermissionDtoList);

        assertEquals(userCurrencyOperationPermissionDtoList, currencyService.getAllCurrencyOperationPermittedForWithdraw("email@email.com"));

        verify(currencyDao, times(1)).findAllCurrencyOperationPermittedByUserAndDirection(99, "WITHDRAW");
    }

    @Test
    public void getCurrencyPermittedNameList_WhenParameterString() {
        Set<String> set = new HashSet<>();
        set.add("FFF");

        when(userService.getIdByEmail(anyString())).thenReturn(90);
        when(currencyDao.findCurrencyOperationPermittedByUserList(anyInt())).thenReturn(userCurrencyOperationPermissionDtoList);

        assertEquals(set, currencyService.getCurrencyPermittedNameList("email@email.com"));

        verify(currencyDao, times(1)).findCurrencyOperationPermittedByUserList(90);

    }

    @Test
    public void getCurrencyPermittedOperationList_Test() {
        when(currencyDao.findCurrencyOperationPermittedByUserList(anyInt())).thenReturn(userCurrencyOperationPermissionDtoList);

        assertEquals(userCurrencyOperationPermissionDtoList, currencyService.getCurrencyPermittedOperationList(37));

        verify(currencyDao, times(1)).findCurrencyOperationPermittedByUserList(37);

    }

    @Test
    public void getCurrencyPermittedNameList_WhenParameterInteger() {
        Set<String> set = new HashSet<>();
        set.add("FFF");

        when(currencyDao.findCurrencyOperationPermittedByUserList(anyInt())).thenReturn(userCurrencyOperationPermissionDtoList);

        assertEquals(set, currencyService.getCurrencyPermittedNameList(25));

        verify(currencyDao, times(1)).findCurrencyOperationPermittedByUserList(25);
    }

    @Test
    public void getWarningForCurrency_Test() {
        List<String> list = Arrays.asList("list", "string");
        when(currencyDao.getWarningForCurrency(anyInt(), any(UserCommentTopicEnum.class))).thenReturn(list);

        assertEquals(list, currencyService.getWarningForCurrency(77, UserCommentTopicEnum.WITHDRAW_CURRENCY_WARNING));

        verify(currencyDao, times(1)).getWarningForCurrency(77, UserCommentTopicEnum.WITHDRAW_CURRENCY_WARNING);
    }

    @Test
    public void getWarningsByTopic_Test() {
        when(currencyDao.getWarningsByTopic(any(UserCommentTopicEnum.class))).thenReturn(Arrays.asList("list", "string"));

        assertEquals(Arrays.asList("list", "string"), currencyService.getWarningsByTopic(UserCommentTopicEnum.GENERAL));

        verify(currencyDao, times(1)).getWarningsByTopic(UserCommentTopicEnum.GENERAL);
    }

    @Test
    public void getWarningForMerchant_Test() {
        when(currencyDao.getWarningForMerchant(anyInt(), any(UserCommentTopicEnum.class))).thenReturn(Arrays.asList("list", "string"));

        assertEquals(Arrays.asList("list", "string"), currencyService.getWarningForMerchant(80, UserCommentTopicEnum.GENERAL));

        verify(currencyDao, times(1)).getWarningForMerchant(80, UserCommentTopicEnum.GENERAL);
    }

    @Test
    public void getById_Test() {
        when(currencyDao.findById(anyInt())).thenReturn(new Currency(10));

        assertEquals(new Currency(10), currencyService.getById(40));

        verify(currencyDao, times(1)).findById(40);
    }

    @Test
    public void findLimitForRoleByCurrencyPairAndType_Test() {
        CurrencyPairLimitDto currencyPairLimitDto = new CurrencyPairLimitDto();
        currencyPairLimitDto.setCurrencyPairName("String");

        when(currencyDao.findCurrencyPairLimitForRoleByPairAndType(anyInt(), anyInt(), anyInt())).thenReturn(currencyPairLimitDto);

        assertEquals(currencyPairLimitDto, currencyService.findLimitForRoleByCurrencyPairAndType(49, OperationType.SELL));

        verify(currencyDao, times(1)).findCurrencyPairLimitForRoleByPairAndType(49, 4, 1);
    }

    @Test
    public void findLimitForRoleByCurrencyPairAndTypeAndUser_Test() {
        CurrencyPairLimitDto currencyPairLimitDto = new CurrencyPairLimitDto();
        currencyPairLimitDto.setCurrencyPairName("String");
        User user = new User();
        user.setRole(UserRole.ADMIN_USER);

        when(currencyDao.findCurrencyPairLimitForRoleByPairAndType(anyInt(), anyInt(), anyInt())).thenReturn(currencyPairLimitDto);

        assertEquals(currencyPairLimitDto, currencyService.findLimitForRoleByCurrencyPairAndTypeAndUser(49, OperationType.SELL, user));

        verify(currencyDao, times(1)).findCurrencyPairLimitForRoleByPairAndType(49, 3, 1);
    }

    @Test
    public void findAllCurrencyLimitsForRoleAndType_Test() {
        CurrencyPairLimitDto currencyPairLimitDto = new CurrencyPairLimitDto();
        currencyPairLimitDto.setCurrencyPairName("String");
        List<CurrencyPairLimitDto> currencyPairLimitDtos = Arrays.asList(currencyPairLimitDto);

        when(userRoleService.getRealUserRoleIdByBusinessRoleList(anyString())).thenReturn(Arrays.asList(5, 6, 7));
        when(currencyDao.findLimitsForRolesByType(anyList(), anyInt())).thenReturn(currencyPairLimitDtos);
        assertEquals(currencyPairLimitDtos, currencyService.findAllCurrencyLimitsForRoleAndType("User", OrderType.SELL));

        verify(userRoleService, times(1)).getRealUserRoleIdByBusinessRoleList("User");
        verify(currencyDao, times(1)).findLimitsForRolesByType(Arrays.asList(5, 6, 7), 1);
    }

    @Test
    public void updateCurrencyPairLimit_Test() {
        when(userRoleService.getRealUserRoleIdByBusinessRoleList(anyString())).thenReturn(Arrays.asList(5, 6, 7));
        doNothing().when(currencyDao).setCurrencyPairLimit(anyInt(), anyList(), anyInt(), any(BigDecimal.class),
                any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class));

        currencyService.updateCurrencyPairLimit(5, OrderType.BUY, "USER", new BigDecimal(3), new BigDecimal(7), new BigDecimal(5), new BigDecimal(18), new BigDecimal(0));

        verify(userRoleService, times(1)).getRealUserRoleIdByBusinessRoleList("USER");
        verify(currencyDao, times(1)).setCurrencyPairLimit(5, Arrays.asList(5, 6, 7), 2, new BigDecimal(3), new BigDecimal(7), new BigDecimal(5), new BigDecimal(18), new BigDecimal(0));
    }

    @Test
    public void findCurrencyPairsWithLimitsForUser_Test() {
        CurrencyPair currencyPair = new CurrencyPair(new Currency(3), new Currency(6));
        CurrencyPairWithLimitsDto currencyPairWithLimitsDto = new CurrencyPairWithLimitsDto(currencyPair,
                new BigDecimal(3), new BigDecimal(7), new BigDecimal(22), new BigDecimal(77),
                new BigDecimal(32), new BigDecimal(3), new BigDecimal(3), new BigDecimal(3));

        when(currencyDao.findAllCurrencyPairsWithLimits(anyInt())).thenReturn(Arrays.asList(currencyPairWithLimitsDto));

        assertEquals(Arrays.asList(currencyPairWithLimitsDto), currencyService.findCurrencyPairsWithLimitsForUser());

        verify(currencyDao, times(1)).findAllCurrencyPairsWithLimits(4);
    }

    @Test
    public void findAllCurrenciesWithHidden_Test() {
        when(currencyDao.findAllCurrenciesWithHidden()).thenReturn(Arrays.asList(new Currency(7)));

        assertEquals(Arrays.asList(new Currency(7)), currencyService.findAllCurrenciesWithHidden());

        verify(currencyDao, times(1)).findAllCurrenciesWithHidden();
    }

    @Test
    public void computeRandomizedAddition_WhenRandomAmountParamIsNotPresent() {
        assertEquals(new BigDecimal(0), currencyService.computeRandomizedAddition(0, OperationType.BUY));
    }

    @Test
    public void computeRandomizedAddition_WhenRandomAmountParamIsPresent() {
        assertNotEquals(new BigDecimal(0), currencyService.computeRandomizedAddition(10, OperationType.INPUT));
    }

    @Test
    public void isIco() {
        when(currencyDao.isCurrencyIco(anyInt())).thenReturn(true);

        assertEquals(true, currencyService.isIco(7));

        verify(currencyDao, times(1)).isCurrencyIco(7);
    }

    @Test
    public void getCurrencyScaleByCurrencyId_WhenOk() {
        MerchantCurrencyScaleDto merchantCurrencyScaleDto = new MerchantCurrencyScaleDto();
        merchantCurrencyScaleDto.setScaleForRefill(4);
        merchantCurrencyScaleDto.setScaleForWithdraw(6);

        when(currencyDao.findCurrencyScaleByCurrencyId(anyInt())).thenReturn(merchantCurrencyScaleDto);

        assertEquals(merchantCurrencyScaleDto, currencyService.getCurrencyScaleByCurrencyId(19));
        verify(currencyDao, times(1)).findCurrencyScaleByCurrencyId(19);

    }

    @Test(expected = ScaleForAmountNotSetException.class)
    public void getCurrencyScaleByCurrencyId_WhenScaleForRefillEqualsNull() {
        MerchantCurrencyScaleDto merchantCurrencyScaleDto = new MerchantCurrencyScaleDto();
        merchantCurrencyScaleDto.setScaleForWithdraw(6);

        when(currencyDao.findCurrencyScaleByCurrencyId(anyInt())).thenReturn(merchantCurrencyScaleDto);
        currencyService.getCurrencyScaleByCurrencyId(19);
    }

    @Test(expected = ScaleForAmountNotSetException.class)
    public void getCurrencyScaleByCurrencyId_WhenScaleForWithdrawEqualsNull() {
        MerchantCurrencyScaleDto merchantCurrencyScaleDto = new MerchantCurrencyScaleDto();
        merchantCurrencyScaleDto.setScaleForRefill(6);

        when(currencyDao.findCurrencyScaleByCurrencyId(anyInt())).thenReturn(merchantCurrencyScaleDto);
        currencyService.getCurrencyScaleByCurrencyId(19);
    }

    @Test
    public void getCurrencyPairByName_Test() {
        when(currencyDao.findCurrencyPairByName(anyString())).thenReturn(new CurrencyPair("Pair Name"));

        assertEquals(new CurrencyPair("Pair Name"), currencyService.getCurrencyPairByName("Name"));

        verify(currencyDao, times(1)).findCurrencyPairByName("Name");
    }

    @Test
    public void findCurrencyPairIdByName_WhenOk() {
        when(currencyDao.findOpenCurrencyPairIdByName(anyString())).thenReturn(Optional.of(5));

        assertEquals(Integer.valueOf(5), currencyService.findCurrencyPairIdByName("Name"));

        verify(currencyDao, times(1)).findOpenCurrencyPairIdByName("Name");
    }

    @Test(expected = CurrencyPairNotFoundException.class)
    public void findCurrencyPairIdByName_WhenCurrencyPairNotFoundException() {
        when(currencyDao.findOpenCurrencyPairIdByName(anyString())).thenReturn(Optional.empty());
        currencyService.findCurrencyPairIdByName("Name");
    }

    @Test
    public void findAllCurrenciesByProcessType_Test() {
        when(currencyDao.findAllCurrenciesByProcessType(any(MerchantProcessType.class)))
                .thenReturn(Arrays.asList(new Currency(28)));

        assertEquals(Arrays.asList(new Currency(28)),
                currencyService.findAllCurrenciesByProcessType(MerchantProcessType.MERCHANT));

        verify(currencyDao, times(1))
                .findAllCurrenciesByProcessType(MerchantProcessType.MERCHANT);
    }

    @Test
    public void findPermitedCurrencyPairs_Test() {
        when(currencyDao.findPermitedCurrencyPairs(any(CurrencyPairType.class)))
                .thenReturn(Arrays.asList(new CurrencyPair("Pair Name")));

        assertEquals(Arrays.asList(new CurrencyPair("Pair Name")),
                currencyService.findPermitedCurrencyPairs(CurrencyPairType.ICO));

        verify(currencyDao, times(1))
                .findPermitedCurrencyPairs(CurrencyPairType.ICO);

    }

    @Test
    public void getNotHiddenCurrencyPairByName_Test() {
        when(currencyDao.getNotHiddenCurrencyPairByName(anyString())).thenReturn(new CurrencyPair("Pair Name"));

        assertEquals(new CurrencyPair("Pair Name"), currencyService.getNotHiddenCurrencyPairByName("Pair Name"));

        verify(currencyDao, times(1)).getNotHiddenCurrencyPairByName("Pair Name");
    }

    @Test
    public void findActiveCurrencyPairs_Test() {
        List<CurrencyPairInfoItem> currencyPairInfoItems = Arrays.asList(new CurrencyPairInfoItem("String"));
        when(currencyDao.findActiveCurrencyPairs()).thenReturn(currencyPairInfoItems);

        assertEquals(currencyPairInfoItems, currencyService.findActiveCurrencyPairs());

        verify(currencyDao, times(1)).findActiveCurrencyPairs();
    }

    @Test
    public void findAllCurrency_Test() {
        List<Currency> currencyList = Arrays.asList(new Currency(37));
        when(currencyDao.findAllCurrency()).thenReturn(currencyList);

        assertEquals(currencyList, currencyService.findAllCurrency());

        verify(currencyDao, times(1)).findAllCurrency();
    }

    @Test
    public void updateVisibilityCurrencyById_Test() {
        when(currencyDao.updateVisibilityCurrencyById(anyInt())).thenReturn(true);

        assertEquals(true, currencyService.updateVisibilityCurrencyById(34));

        verify(currencyDao, times(1)).updateVisibilityCurrencyById(34);
    }

    @Test
    public void findAllCurrencyPair_Test() {
        List<CurrencyPair> currencyPairs = Arrays.asList(new CurrencyPair("Name"));

        when(currencyDao.findAllCurrencyPair()).thenReturn(currencyPairs);

        assertEquals(currencyPairs, currencyService.findAllCurrencyPair());

        verify(currencyDao, times(1)).findAllCurrencyPair();
    }

    @Test
    public void updateVisibilityCurrencyPairById_Test() {
        when(currencyDao.updateVisibilityCurrencyPairById(anyInt())).thenReturn(false);

        assertEquals(false, currencyService.updateVisibilityCurrencyPairById(536));

        verify(currencyDao, times(1)).updateVisibilityCurrencyPairById(536);
    }

    @Test
    public void updateAccessToDirectLinkCurrencyPairById_Test() {
        when(currencyDao.updateAccessToDirectLinkCurrencyPairById(anyInt())).thenReturn(false);

        assertEquals(false, currencyService.updateAccessToDirectLinkCurrencyPairById(536));

        verify(currencyDao, times(1)).updateAccessToDirectLinkCurrencyPairById(536);
    }

    @Test
    public void getStatsByCoin_Test() {
        List<CurrencyReportInfoDto> currencyReportInfoDtos = Arrays.asList(new CurrencyReportInfoDto());
        when(currencyDao.getStatsByCoin(anyInt())).thenReturn(currencyReportInfoDtos);

        assertEquals(currencyReportInfoDtos, currencyService.getStatsByCoin(536));

        verify(currencyDao, times(1)).getStatsByCoin(536);
    }

    @Test
    public void setPropertyCalculateLimitToUsd_Test() {
        when(userRoleService.getRealUserRoleIdByBusinessRoleList(anyString())).thenReturn(Arrays.asList(4, 5, 6));
        when(currencyDao.setPropertyCalculateLimitToUsd(anyInt(), any(OperationType.class), anyList(), anyBoolean())).thenReturn(true);

        assertEquals(true, currencyService.setPropertyCalculateLimitToUsd(77, OperationType.STORNO, "String", false));

        verify(currencyDao, times(1)).setPropertyCalculateLimitToUsd(77, OperationType.STORNO, Arrays.asList(4, 5, 6), false);
    }

    @Test
    public void updateWithdrawLimits_WhenCurrencyLimitsIsEmpty() {
        when(currencyDao.getAllCurrencyLimits()).thenReturn(Collections.EMPTY_LIST);

        currencyService.updateWithdrawLimits();

        verify(currencyDao, times(1)).getAllCurrencyLimits();
    }

    @Test
    public void updateWithdrawLimits_WhenRatesIsEmpty() {
        when(currencyDao.getAllCurrencyLimits()).thenReturn(Arrays.asList(new CurrencyLimit()));
        when(exchangeApi.getRates()).thenReturn(new HashMap<>());

        currencyService.updateWithdrawLimits();

        verify(currencyDao, times(1)).getAllCurrencyLimits();
        verify(exchangeApi, times(1)).getRates();
    }

    @Test
    public void updateWithdrawLimits_WhenRatesIsNotEmpty() {
        Currency currency = new Currency(4);
        currency.setName("Name");
        CurrencyLimit currencyLimit = new CurrencyLimit();
        currencyLimit.setCurrency(currency);
        currencyLimit.setRecalculateToUsd(true);
        currencyLimit.setMinSumUsdRate(new BigDecimal(5));
        currencyLimit.setMinSum(new BigDecimal(5));

        when(currencyDao.getAllCurrencyLimits()).thenReturn(Arrays.asList(currencyLimit));
        when(exchangeApi.getRates()).thenReturn(new HashMap<String, RateDto>() {{
            put("Name", RateDto.builder()
                    .usdRate(BigDecimal.valueOf(3))
                    .btcRate(BigDecimal.valueOf(6))
                    .build());
        }});
        doNothing().when(currencyDao).updateWithdrawLimits(anyList());

        currencyService.updateWithdrawLimits();

        verify(currencyDao, times(1)).getAllCurrencyLimits();
        verify(exchangeApi, times(1)).getRates();
        verify(currencyDao, times(1)).updateWithdrawLimits(Arrays.asList(currencyLimit));
    }

    @Test
    public void updateWithdrawLimits_WhenRateDtoIsNullOrUsdRateIsZero() {
        Currency currency = new Currency(4);
        currency.setName("Name");

        Currency currency2 = new Currency(4);
        currency2.setName("Name2");

        CurrencyLimit currencyLimit = new CurrencyLimit();
        currencyLimit.setCurrency(currency);
        currencyLimit.setRecalculateToUsd(true);
        currencyLimit.setMinSumUsdRate(new BigDecimal(5));
        currencyLimit.setMinSum(new BigDecimal(5));

        CurrencyLimit currencyLimit2 = new CurrencyLimit();
        currencyLimit2.setCurrency(currency2);

        when(currencyDao.getAllCurrencyLimits()).thenReturn(Arrays.asList(currencyLimit, currencyLimit2));
        when(exchangeApi.getRates()).thenReturn(new HashMap<String, RateDto>() {{
            put("Name", RateDto.builder()
                    .usdRate(BigDecimal.valueOf(0))
                    .btcRate(BigDecimal.valueOf(6))
                    .build());
        }});
        doNothing().when(currencyDao).updateWithdrawLimits(anyList());

        currencyService.updateWithdrawLimits();

        verify(currencyDao, times(1)).getAllCurrencyLimits();
        verify(exchangeApi, times(1)).getRates();
        verify(currencyDao, times(1)).updateWithdrawLimits(Arrays.asList(currencyLimit, currencyLimit2));
    }

    @Test
    public void updateWithdrawLimits_WhenRecalculateToUsdIsFalse() {
        Currency currency = new Currency(4);
        currency.setName("Name");
        CurrencyLimit currencyLimit = new CurrencyLimit();
        currencyLimit.setCurrency(currency);
        currencyLimit.setRecalculateToUsd(false);
        currencyLimit.setMinSumUsdRate(new BigDecimal(5));
        currencyLimit.setMinSum(new BigDecimal(5));

        when(currencyDao.getAllCurrencyLimits()).thenReturn(Arrays.asList(currencyLimit));
        when(exchangeApi.getRates()).thenReturn(new HashMap<String, RateDto>() {{
            put("Name", RateDto.builder()
                    .usdRate(BigDecimal.valueOf(3))
                    .btcRate(BigDecimal.valueOf(6))
                    .build());
        }});
        doNothing().when(currencyDao).updateWithdrawLimits(anyList());

        currencyService.updateWithdrawLimits();

        verify(currencyDao, times(1)).getAllCurrencyLimits();
        verify(exchangeApi, times(1)).getRates();
        verify(currencyDao, times(1)).updateWithdrawLimits(Arrays.asList(currencyLimit));
    }

    @Test
    public void getCurrencies_Test() {
        List<Currency> currencyList = Arrays.asList(new Currency(90));
        when(currencyDao.getCurrencies(any(MerchantProcessType[].class))).thenReturn(currencyList);

        assertEquals(currencyList,
                currencyService.getCurrencies(new MerchantProcessType[]{MerchantProcessType.CRYPTO}));

        verify(currencyDao, times(1)).getCurrencies(new MerchantProcessType[]{MerchantProcessType.CRYPTO});
    }

    @Test
    public void getPairsByFirstPartName_Test() {
        when(currencyDao.findAllCurrenciesByFirstPartName(anyString()))
                .thenReturn(Arrays.asList(new CurrencyPair("Pair")));

        assertEquals(Arrays.asList(new CurrencyPair("Pair")),
                currencyService.getPairsByFirstPartName("Pair"));

        verify(currencyDao, times(1)).findAllCurrenciesByFirstPartName("Pair");
    }

    @Test
    public void getPairsBySecondPartName_Test() {
        when(currencyDao.findAllCurrenciesBySecondPartName(anyString()))
                .thenReturn(Arrays.asList(new CurrencyPair("Pair")));

        assertEquals(Arrays.asList(new CurrencyPair("Pair")),
                currencyService.getPairsBySecondPartName("Pair"));

        verify(currencyDao, times(1)).findAllCurrenciesBySecondPartName("Pair");
    }

    @Test
    public void isCurrencyPairHidden_Test(){
        when(currencyDao.isCurrencyPairHidden(anyInt())).thenReturn(true);

        assertEquals(true, currencyService.isCurrencyPairHidden(51));

        verify(currencyDao, times(1)).isCurrencyPairHidden(51);
    }

    @Test
    public void addCurrencyForIco(){
        doNothing().when(currencyDao).addCurrency(anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyBoolean());

       currencyService.addCurrencyForIco("name", "description");

        verify(currencyDao, times(1)).addCurrency("name", "description",
                "no_bean", "/client/img/merchants/ico.png", true, true);
    }

    @Test(expected = RuntimeException.class)
    public void addCurrencyPairForIco_WhenRuntimeException(){
        Currency currency = new Currency(8);
        when(currencyDao.findByName(anyString())).thenReturn(currency);
        when(currencyDao.findCurrencyPairByName(anyString())).thenReturn(new CurrencyPair("CurrencyPair"));

        currencyService.addCurrencyPairForIco("firstCurrencyName", "secondCurrencyName");
    }

    @Test
    public void addCurrencyPairForIco_WhenCurrencyPairNotFoundException(){
        Currency currency = new Currency(9);
        when(currencyDao.findByName(anyString())).thenReturn(currency);
        when(currencyDao.findCurrencyPairByName(anyString())).thenThrow(CurrencyPairNotFoundException.class);
        doNothing().when(currencyDao).addCurrencyPair(any(Currency.class), any(Currency.class), anyString(),
                any(CurrencyPairType.class), any(Market.class), anyString(), anyBoolean());

        currencyService.addCurrencyPairForIco("firstCurrencyName2", "secondCurrencyName2");

        verify(currencyDao, times(1)).findByName("firstCurrencyName2");
        verify(currencyDao, times(1)).findByName("secondCurrencyName2");
        verify(currencyDao, times(1)).findCurrencyPairByName("firstCurrencyName2/secondCurrencyName2");
        verify(currencyDao, times(1)).addCurrencyPair(currency, currency, "firstCurrencyName2/secondCurrencyName2",
                CurrencyPairType.ICO, Market.ICO,"firstCurrencyName2/secondCurrencyName2", true);
    }

    @Test
    public void addCurrencyPairForIco_WhenValueRetrievalException(){
        Currency currency = new Currency(8);
        when(currencyDao.findByName(anyString())).thenReturn(currency);
        when(currencyDao.findCurrencyPairByName(anyString())).thenThrow(Cache.ValueRetrievalException.class);
        doNothing().when(currencyDao).addCurrencyPair(any(Currency.class), any(Currency.class), anyString(),
                any(CurrencyPairType.class), any(Market.class), anyString(), anyBoolean());

        currencyService.addCurrencyPairForIco("firstCurrencyName3", "secondCurrencyName3");

        verify(currencyDao, times(1)).findByName("firstCurrencyName3");
        verify(currencyDao, times(1)).findByName("secondCurrencyName3");
        verify(currencyDao, times(1)).findCurrencyPairByName("firstCurrencyName3/secondCurrencyName3");
        verify(currencyDao, times(1)).addCurrencyPair(currency, currency, "firstCurrencyName3/secondCurrencyName3",
                CurrencyPairType.ICO, Market.ICO,"firstCurrencyName3/secondCurrencyName3", true);
    }

    @Test
    public void updateCurrencyExchangeRates_WhenRatesIsEmpty(){
        currencyService.updateCurrencyExchangeRates(Collections.emptyList());
    }

    @Test
    public void updateCurrencyExchangeRates_WhenOk(){
        doNothing().when(currencyDao).updateCurrencyExchangeRates(anyListOf(RateDto.class));

        currencyService.updateCurrencyExchangeRates(Arrays.asList(RateDto.zeroRate("name1"), RateDto.zeroRate("name2")));

        verify(currencyDao, times(1)).updateCurrencyExchangeRates(Arrays.asList(RateDto.zeroRate("name1"), RateDto.zeroRate("name2")));
    }

    @Test
    public void getCurrencyRates(){
        when(currencyDao.getCurrencyRates()).thenReturn(Arrays.asList(RateDto.zeroRate("name1"), RateDto.zeroRate("name2")));

        assertEquals(Arrays.asList(RateDto.zeroRate("name1"), RateDto.zeroRate("name2")),
                currencyService.getCurrencyRates());

        verify(currencyDao, times(1)).getCurrencyRates();
    }

    @Test
    public void updateCurrencyBalances_WhenBalancesIsEmpty(){
        currencyService.updateCurrencyBalances(Collections.emptyList());
    }

    @Test
    public void updateCurrencyBalances_WhenOk(){
        doNothing().when(currencyDao).updateCurrencyBalances(anyListOf(BalanceDto.class));

        currencyService.updateCurrencyBalances(Arrays.asList(BalanceDto.zeroBalance("name1"), BalanceDto.zeroBalance("name2")));

        verify(currencyDao, times(1)).updateCurrencyBalances(Arrays.asList(BalanceDto.zeroBalance("name1"), BalanceDto.zeroBalance("name2")));
    }

    @Test
    public void getCurrencyBalances(){
        when(currencyDao.getCurrencyBalances()).thenReturn(Arrays.asList(BalanceDto.zeroBalance("name1"), BalanceDto.zeroBalance("name2")));

        assertEquals(Arrays.asList(BalanceDto.zeroBalance("name1"), BalanceDto.zeroBalance("name2")),
                currencyService.getCurrencyBalances());

        verify(currencyDao, times(1)).getCurrencyBalances();
    }

}
