package me.exrates.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.CallBackLogDao;
import me.exrates.dao.CommissionDao;
import me.exrates.dao.OrderDao;
import me.exrates.model.Commission;
import me.exrates.model.CompanyWallet;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.OrderWsDetailDto;
import me.exrates.model.PagingData;
import me.exrates.model.Transaction;
import me.exrates.model.User;
import me.exrates.model.UserRoleSettings;
import me.exrates.model.Wallet;
import me.exrates.model.chart.ChartResolution;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.AdminOrderInfoDto;
import me.exrates.model.dto.CallBackLogDto;
import me.exrates.model.dto.CandleChartItemDto;
import me.exrates.model.dto.CoinmarketApiDto;
import me.exrates.model.dto.CurrencyPairLimitDto;
import me.exrates.model.dto.CurrencyPairTurnoverReportDto;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.OrderBasicInfoDto;
import me.exrates.model.dto.OrderBookWrapperDto;
import me.exrates.model.dto.OrderCommissionsDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderCreationResultDto;
import me.exrates.model.dto.OrderDetailDto;
import me.exrates.model.dto.OrderFilterDataDto;
import me.exrates.model.dto.OrderInfoDto;
import me.exrates.model.dto.OrderReportInfoDto;
import me.exrates.model.dto.OrderValidationDto;
import me.exrates.model.dto.OrdersListWrapper;
import me.exrates.model.dto.SimpleOrderBookItem;
import me.exrates.model.dto.UserSummaryOrdersByCurrencyPairsDto;
import me.exrates.model.dto.UserSummaryOrdersDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.dto.WalletsForOrderAcceptionDto;
import me.exrates.model.dto.WalletsForOrderCancelDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminOrderFilterData;
import me.exrates.model.dto.mobileApiDto.OrderCreationParamsDto;
import me.exrates.model.dto.mobileApiDto.dashboard.CommissionsDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.dto.openAPI.OpenOrderDto;
import me.exrates.model.dto.openAPI.OrderBookItem;
import me.exrates.model.dto.openAPI.TradeHistoryDto;
import me.exrates.model.dto.openAPI.TransactionDto;
import me.exrates.model.dto.openAPI.UserOrdersDto;
import me.exrates.model.dto.openAPI.UserTradeHistoryDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.BusinessUserRoleEnum;
import me.exrates.model.enums.ChartPeriodsEnum;
import me.exrates.model.enums.ChartTimeFramesEnum;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderDeleteStatus;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.PrecissionsEnum;
import me.exrates.model.enums.ReferralTransactionStatusEnum;
import me.exrates.model.enums.RefreshObjectsEnum;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.TransactionStatus;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.OrderRoleInfoForDelete;
import me.exrates.model.vo.TransactionDescription;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.CurrencyService;
import me.exrates.service.NotificationService;
import me.exrates.service.OrderService;
import me.exrates.service.ReferralService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.cache.ChartsCacheManager;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.events.AcceptOrderEvent;
import me.exrates.service.events.EventsForDetailed.AcceptDetailOrderEvent;
import me.exrates.service.events.EventsForDetailed.AutoAcceptEventsList;
import me.exrates.service.events.CancelOrderEvent;
import me.exrates.service.events.CreateOrderEvent;
import me.exrates.service.events.EventsForDetailed.CancelDetailorderEvent;
import me.exrates.service.events.EventsForDetailed.CreateDetailOrderEvent;
import me.exrates.service.events.OrderEvent;
import me.exrates.service.events.PartiallyAcceptedOrder;
import me.exrates.service.exception.AlreadyAcceptedOrderException;
import me.exrates.service.exception.AttemptToAcceptBotOrderException;
import me.exrates.service.exception.IncorrectCurrentUserException;
import me.exrates.service.exception.InsufficientCostsForAcceptionException;
import me.exrates.service.exception.NotCreatableOrderException;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.OrderAcceptionException;
import me.exrates.service.exception.OrderCancellingException;
import me.exrates.service.exception.OrderCreationException;
import me.exrates.service.exception.OrderDeletingException;
import me.exrates.service.exception.WalletCreationException;
import me.exrates.service.exception.api.OrderParamsWrongException;
import me.exrates.service.impl.proxy.ServiceCacheableProxy;
import me.exrates.service.stopOrder.StopOrderService;
import me.exrates.service.util.BiTuple;
import me.exrates.service.util.Cache;
import me.exrates.service.vo.ProfileData;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static me.exrates.model.enums.OrderActionEnum.ACCEPT;
import static me.exrates.model.enums.OrderActionEnum.ACCEPTED;
import static me.exrates.model.enums.OrderActionEnum.CANCEL;
import static me.exrates.model.enums.OrderActionEnum.CREATE;
import static me.exrates.model.enums.OrderActionEnum.CREATE_SPLIT;
import static me.exrates.model.enums.OrderActionEnum.DELETE;
import static me.exrates.model.enums.OrderActionEnum.DELETE_SPLIT;
import static me.exrates.service.util.CollectionUtil.isEmpty;

@Log4j2
@Service
public class OrderServiceImpl implements OrderService {

    public static final String BUY = "BUY";
    public static final String SELL = "SELL";
    public static final String SCOPE = "ALL";
    private final static String CONTENT_DISPOSITION = "Content-Disposition";
    private final static String ATTACHMENT = "attachment; filename=";
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int ORDERS_QUERY_DEFAULT_LIMIT = 20;
    private static final Logger logger = LogManager.getLogger(OrderServiceImpl.class);

    private static final Logger txLogger = LogManager.getLogger("ordersTxLogger");

    private final List<BackDealInterval> intervals = Arrays.stream(ChartPeriodsEnum.values())
            .map(ChartPeriodsEnum::getBackDealInterval)
            .collect(Collectors.toList());

    private final List<ChartTimeFrame> timeFrames = Arrays.stream(ChartTimeFramesEnum.values())
            .map(ChartTimeFramesEnum::getTimeFrame)
            .collect(Collectors.toList());
    private final Object autoAcceptLock = new Object();
    private final Object restOrderCreationLock = new Object();
    @Autowired
    NotificationService notificationService;
    @Autowired
    ServiceCacheableProxy serviceCacheableProxy;
    @Autowired
    TransactionDescription transactionDescription;
    @Autowired
    StopOrderService stopOrderService;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private CallBackLogDao callBackDao;
    @Autowired
    private CommissionDao commissionDao;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserService userService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private CompanyWalletService companyWalletService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private ChartsCacheManager chartsCacheManager;
    @Autowired
    private ExchangeRatesHolder exchangeRatesHolder;
    @Autowired
    private StopOrderService stopOrderServiceImpl;

    @Override
    public List<BackDealInterval> getIntervals() {
        return intervals;
    }

    @Override
    public List<ChartTimeFrame> getChartTimeFrames() {
        return timeFrames;
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval, Locale locale) {
        ExOrderStatisticsDto result = orderDao.getOrderStatistic(currencyPair, backDealInterval);
        result = new ExOrderStatisticsDto(result);
        result.setPercentChange(BigDecimalProcessing.formatNonePoint(BigDecimalProcessing.doAction(
                result.getFirstOrderRate(), result.getLastOrderRate(), ActionType.PERCENT_GROWTH), 2));
        result.setFirstOrderAmountBase(BigDecimalProcessing.formatNonePoint(result.getFirstOrderAmountBase(), true));
        result.setFirstOrderRate(BigDecimalProcessing.formatNonePoint(result.getFirstOrderRate(), true));
        result.setLastOrderAmountBase(BigDecimalProcessing.formatNonePoint(result.getLastOrderAmountBase(), true));
        result.setLastOrderRate(BigDecimalProcessing.formatNonePoint(result.getLastOrderRate(), true));
        result.setMinRate(BigDecimalProcessing.formatNonePoint(result.getMinRate(), true));
        result.setMaxRate(BigDecimalProcessing.formatNonePoint(result.getMaxRate(), true));
        result.setSumBase(BigDecimalProcessing.formatNonePoint(result.getSumBase(), true));
        result.setSumConvert(BigDecimalProcessing.formatNonePoint(result.getSumConvert(), true));
        return result;
    }

    @Override
    public List<Map<String, Object>> getDataForAreaChart(CurrencyPair currencyPair, BackDealInterval interval) {
        logger.info("Begin 'getDataForAreaChart' method");
        return orderDao.getDataForAreaChart(currencyPair, interval);
    }


    @Override
    public List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval interval) {
        return orderDao.getDataForCandleChart(currencyPair, interval);
    }


    @Override
    public List<CandleChartItemDto> getCachedDataForCandle(CurrencyPair currencyPair, ChartTimeFrame timeFrame) {
        return chartsCacheManager.getData(currencyPair.getId(), timeFrame);
    }


    @Override
    public List<CandleChartItemDto> getLastDataForCandleChart(Integer currencyPairId,
                                                              LocalDateTime startTime, ChartResolution resolution) {


        return orderDao.getDataForCandleChart(currencyService.findCurrencyPairById(currencyPairId), startTime, LocalDateTime.now(),
                resolution.getTimeValue(), resolution.getTimeUnit().name());
    }


    @Override
    public List<CandleChartItemDto> getDataForCandleChart(int pairId, ChartTimeFrame timeFrame) {
        LocalDateTime endTime = LocalDateTime.now();
//    LocalDateTime lastHalfHour = endTime.truncatedTo(ChronoUnit.HOURS)
//            .plusMinutes(30 * (endTime.getMinute() / 30));
        LocalDateTime startTime = endTime.minus(timeFrame.getTimeValue(), timeFrame.getTimeUnit().getCorrespondingTimeUnit());
//    LocalDateTime firstHalfHour = startTime.truncatedTo(ChronoUnit.HOURS)
//            .plusMinutes(30 * (startTime.getMinute() / 30));

        return orderDao.getDataForCandleChart(currencyService.findCurrencyPairById(pairId),
                startTime, endTime, timeFrame.getResolution().getTimeValue(),
                timeFrame.getResolution().getTimeUnit().name());
    }


    @Override
    public List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval interval, LocalDateTime startTime) {
        LocalDateTime endTime = startTime.plus((long) interval.getIntervalValue(), interval.getIntervalType().getCorrespondingTimeUnit());
        return orderDao.getDataForCandleChart(currencyPair, interval, endTime);
    }


    @Override
    public List<ExOrderStatisticsShortByPairsDto> getOrdersStatisticByPairs(CacheData cacheData, Locale locale) {
        List<ExOrderStatisticsShortByPairsDto> result = orderDao.getOrderStatisticByPairs();
        result = result
                .stream()
                .map(ExOrderStatisticsShortByPairsDto::new)
                .collect(Collectors.toList());
        result.forEach(e -> {
            BigDecimal lastRate = new BigDecimal(e.getLastOrderRate());
            BigDecimal predLastRate = e.getPredLastOrderRate() == null ? lastRate : new BigDecimal(e.getPredLastOrderRate());
            e.setLastOrderRate(BigDecimalProcessing.formatLocaleFixedSignificant(lastRate, locale, 12));
            e.setPredLastOrderRate(BigDecimalProcessing.formatLocaleFixedSignificant(predLastRate, locale, 12));
            BigDecimal percentChange = BigDecimalProcessing.doAction(predLastRate, lastRate, ActionType.PERCENT_GROWTH);
            e.setPercentChange(BigDecimalProcessing.formatLocaleFixedDecimal(percentChange, locale, 2));
        });
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ExOrderStatisticsShortByPairsDto> getOrdersStatisticByPairsEx(RefreshObjectsEnum refreshObjectsEnum) {
        List<ExOrderStatisticsShortByPairsDto> dto = this.processStatistic(exchangeRatesHolder.getAllRates())
                .stream()
                .filter(statistic -> !statistic.isHidden())
                .collect(Collectors.toList());
        switch (refreshObjectsEnum) {
            case ICO_CURRENCIES_STATISTIC: {
                dto = dto
                        .stream()
                        .filter(p -> p.getType() == CurrencyPairType.ICO)
                        .collect(Collectors.toList());
                break;
            }
            case MAIN_CURRENCIES_STATISTIC: {
                dto = dto
                        .stream()
                        .filter(p -> p.getType() == CurrencyPairType.MAIN)
                        .collect(Collectors.toList());
                break;
            }
            default: {
            }
        }
        return dto;
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getStatForSomeCurrencies(List<Integer> pairsIds) {
        List<ExOrderStatisticsShortByPairsDto> dto = exchangeRatesHolder.getCurrenciesRates(pairsIds);
        Locale locale = Locale.ENGLISH;
        dto.forEach(e -> {
            BigDecimal lastRate = new BigDecimal(e.getLastOrderRate());
            BigDecimal predLastRate = e.getPredLastOrderRate() == null ? lastRate : new BigDecimal(e.getPredLastOrderRate());
            e.setLastOrderRate(BigDecimalProcessing.formatLocaleFixedSignificant(lastRate, locale, 12));
            e.setPredLastOrderRate(BigDecimalProcessing.formatLocaleFixedSignificant(predLastRate, locale, 12));
            BigDecimal percentChange = BigDecimalProcessing.doAction(predLastRate, lastRate, ActionType.PERCENT_GROWTH);
            e.setPercentChange(BigDecimalProcessing.formatLocaleFixedDecimal(percentChange, locale, 2));
        });
        return dto;
    }

    @Override
    public OrderCreateDto prepareNewOrder(CurrencyPair activeCurrencyPair, OperationType orderType, String userEmail, BigDecimal amount, BigDecimal rate, OrderBaseType baseType) {
        return prepareNewOrder(activeCurrencyPair, orderType, userEmail, amount, rate, null, baseType);
    }

    @Override
    public OrderCreateDto prepareNewOrder(CurrencyPair activeCurrencyPair, OperationType orderType, String userEmail, BigDecimal amount, BigDecimal rate, Integer sourceId, OrderBaseType baseType) {
        Currency spendCurrency = null;
        if (orderType == OperationType.SELL) {
            spendCurrency = activeCurrencyPair.getCurrency1();
        } else if (orderType == OperationType.BUY) {
            spendCurrency = activeCurrencyPair.getCurrency2();
        }
        WalletsAndCommissionsForOrderCreationDto walletsAndCommissions = getWalletAndCommission(userEmail, spendCurrency, orderType);
        /**/
        OrderCreateDto orderCreateDto = new OrderCreateDto();
        orderCreateDto.setOperationType(orderType);
        orderCreateDto.setCurrencyPair(activeCurrencyPair);
        orderCreateDto.setAmount(amount);
        orderCreateDto.setExchangeRate(rate);
        orderCreateDto.setUserId(walletsAndCommissions.getUserId());
        orderCreateDto.setCurrencyPair(activeCurrencyPair);
        orderCreateDto.setSourceId(sourceId);
        orderCreateDto.setOrderBaseType(baseType);
        /*todo get 0 comission values from db*/
        /*todo 0 comission for the edr pairs, temporary*/
        if (baseType == OrderBaseType.ICO || orderCreateDto.getCurrencyPair().getName().contains("EDR")) {
            walletsAndCommissions.setCommissionValue(BigDecimal.ZERO);
            walletsAndCommissions.setCommissionId(24);
        }
        if (orderType == OperationType.SELL) {
            orderCreateDto.setWalletIdCurrencyBase(walletsAndCommissions.getSpendWalletId());
            orderCreateDto.setCurrencyBaseBalance(walletsAndCommissions.getSpendWalletActiveBalance());
            orderCreateDto.setComissionForSellId(walletsAndCommissions.getCommissionId());
            orderCreateDto.setComissionForSellRate(walletsAndCommissions.getCommissionValue());
        } else if (orderType == OperationType.BUY) {
            orderCreateDto.setWalletIdCurrencyConvert(walletsAndCommissions.getSpendWalletId());
            orderCreateDto.setCurrencyConvertBalance(walletsAndCommissions.getSpendWalletActiveBalance());
            orderCreateDto.setComissionForBuyId(walletsAndCommissions.getCommissionId());
            orderCreateDto.setComissionForBuyRate(walletsAndCommissions.getCommissionValue());
        }
        /**/
        orderCreateDto.calculateAmounts();
        return orderCreateDto;
    }

    @Override
    public OrderValidationDto validateOrder(OrderCreateDto orderCreateDto, boolean fromDemo, User user) {
        OrderValidationDto orderValidationDto = new OrderValidationDto();
        Map<String, Object> errors = orderValidationDto.getErrors();
        Map<String, Object[]> errorParams = orderValidationDto.getErrorParams();
        if (orderCreateDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("amount_" + errors.size(), "order.fillfield");
        }
        if (orderCreateDto.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("exrate_" + errors.size(), "order.fillfield");
        }

        CurrencyPairLimitDto currencyPairLimit;
        if (!fromDemo) {
            currencyPairLimit = currencyService.findLimitForRoleByCurrencyPairAndType(orderCreateDto.getCurrencyPair().getId(),
                    orderCreateDto.getOperationType());
        } else {
            currencyPairLimit = currencyService.findLimitForRoleByCurrencyPairAndTypeAndUser(orderCreateDto.getCurrencyPair().getId(),
                    orderCreateDto.getOperationType(), user);
        }

        if (orderCreateDto.getOrderBaseType() != null && orderCreateDto.getOrderBaseType().equals(OrderBaseType.STOP_LIMIT)) {
            if (orderCreateDto.getStop() == null || orderCreateDto.getStop().compareTo(BigDecimal.ZERO) <= 0) {
                errors.put("stop_" + errors.size(), "order.fillfield");
            } else {
                if (orderCreateDto.getStop().compareTo(currencyPairLimit.getMinRate()) < 0) {
                    String key = "stop_" + errors.size();
                    errors.put(key, "order.minrate");
                    errorParams.put(key, new Object[]{currencyPairLimit.getMinRate()});
                }
                if (orderCreateDto.getStop().compareTo(currencyPairLimit.getMaxRate()) > 0) {
                    String key = "stop_" + errors.size();
                    errors.put(key, "order.maxrate");
                    errorParams.put(key, new Object[]{currencyPairLimit.getMaxRate()});
                }
            }
        }
        /*------------------*/
        if (orderCreateDto.getCurrencyPair().getPairType() == CurrencyPairType.ICO) {
            validateIcoOrder(errors, errorParams, orderCreateDto);
        }
        /*------------------*/
        if (orderCreateDto.getAmount() != null) {
            if (orderCreateDto.getAmount().compareTo(currencyPairLimit.getMaxAmount()) > 0) {
                String key1 = "amount_" + errors.size();
                errors.put(key1, "order.maxvalue");
                errorParams.put(key1, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMaxAmount(), false)});
                String key2 = "amount_" + errors.size();
                errors.put(key2, "order.valuerange");
                errorParams.put(key2, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMinAmount(), false),
                        BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMaxAmount(), false)});
            }
            if (orderCreateDto.getAmount().compareTo(currencyPairLimit.getMinAmount()) < 0) {
                String key1 = "amount_" + errors.size();
                errors.put(key1, "order.minvalue");
                errorParams.put(key1, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMinAmount(), false)});
                String key2 = "amount_" + errors.size();
                errors.put(key2, "order.valuerange");
                errorParams.put(key2, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMinAmount(), false),
                        BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMaxAmount(), false)});
            }
        }
        if (orderCreateDto.getExchangeRate() != null) {
            if (orderCreateDto.getExchangeRate().compareTo(BigDecimal.ZERO) < 1) {
                errors.put("exrate_" + errors.size(), "order.zerorate");
            }
            if (orderCreateDto.getExchangeRate().compareTo(currencyPairLimit.getMinRate()) < 0) {
                String key = "exrate_" + errors.size();
                errors.put(key, "order.minrate");
                errorParams.put(key, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMinRate(), false)});
            }
            if (orderCreateDto.getExchangeRate().compareTo(currencyPairLimit.getMaxRate()) > 0) {
                String key = "exrate_" + errors.size();
                errors.put(key, "order.maxrate");
                errorParams.put(key, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMaxRate(), false)});
            }

        }
        if ((orderCreateDto.getAmount() != null) && (orderCreateDto.getExchangeRate() != null)) {
            boolean ifEnoughMoney = orderCreateDto.getSpentWalletBalance().compareTo(BigDecimal.ZERO) > 0 && orderCreateDto.getSpentAmount().compareTo(orderCreateDto.getSpentWalletBalance()) <= 0;
            if (!ifEnoughMoney) {
                errors.put("balance_" + errors.size(), "validation.orderNotEnoughMoney");
            }
        }
        return orderValidationDto;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public OrderValidationDto validateOrder(OrderCreateDto orderCreateDto) {
        OrderValidationDto orderValidationDto = new OrderValidationDto();
        Map<String, Object> errors = orderValidationDto.getErrors();
        Map<String, Object[]> errorParams = orderValidationDto.getErrorParams();
        if (orderCreateDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("amount_" + errors.size(), "order.fillfield");
        }
        if (orderCreateDto.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("exrate_" + errors.size(), "order.fillfield");
        }

        CurrencyPairLimitDto currencyPairLimit = currencyService.findLimitForRoleByCurrencyPairAndType(orderCreateDto.getCurrencyPair().getId(),
                orderCreateDto.getOperationType());
        if (orderCreateDto.getOrderBaseType() != null && orderCreateDto.getOrderBaseType().equals(OrderBaseType.STOP_LIMIT)) {
            if (orderCreateDto.getStop() == null || orderCreateDto.getStop().compareTo(BigDecimal.ZERO) <= 0) {
                errors.put("stop_" + errors.size(), "order.fillfield");
            } else {
                if (orderCreateDto.getStop().compareTo(currencyPairLimit.getMinRate()) < 0) {
                    String key = "stop_" + errors.size();
                    errors.put(key, "order.minrate");
                    errorParams.put(key, new Object[]{currencyPairLimit.getMinRate()});
                }
                if (orderCreateDto.getStop().compareTo(currencyPairLimit.getMaxRate()) > 0) {
                    String key = "stop_" + errors.size();
                    errors.put(key, "order.maxrate");
                    errorParams.put(key, new Object[]{currencyPairLimit.getMaxRate()});
                }
            }
        }
        /*------------------*/
        if (orderCreateDto.getCurrencyPair().getPairType() == CurrencyPairType.ICO) {
            validateIcoOrder(errors, errorParams, orderCreateDto);
        }
        /*------------------*/
        if (orderCreateDto.getAmount() != null) {
            if (orderCreateDto.getAmount().compareTo(currencyPairLimit.getMaxAmount()) > 0) {
                String key1 = "amount_" + errors.size();
                errors.put(key1, "order.maxvalue");
                errorParams.put(key1, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMaxAmount(), false)});
                String key2 = "amount_" + errors.size();
                errors.put(key2, "order.valuerange");
                errorParams.put(key2, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMinAmount(), false),
                        BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMaxAmount(), false)});
            }
            if (orderCreateDto.getAmount().compareTo(currencyPairLimit.getMinAmount()) < 0) {
                String key1 = "amount_" + errors.size();
                errors.put(key1, "order.minvalue");
                errorParams.put(key1, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMinAmount(), false)});
                String key2 = "amount_" + errors.size();
                errors.put(key2, "order.valuerange");
                errorParams.put(key2, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMinAmount(), false),
                        BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMaxAmount(), false)});
            }
        }
        if (orderCreateDto.getExchangeRate() != null) {
            if (orderCreateDto.getExchangeRate().compareTo(BigDecimal.ZERO) < 1) {
                errors.put("exrate_" + errors.size(), "order.zerorate");
            }
            if (orderCreateDto.getExchangeRate().compareTo(currencyPairLimit.getMinRate()) < 0) {
                String key = "exrate_" + errors.size();
                errors.put(key, "order.minrate");
                errorParams.put(key, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMinRate(), false)});
            }
            if (orderCreateDto.getExchangeRate().compareTo(currencyPairLimit.getMaxRate()) > 0) {
                String key = "exrate_" + errors.size();
                errors.put(key, "order.maxrate");
                errorParams.put(key, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMaxRate(), false)});
            }

        }
        if ((orderCreateDto.getAmount() != null) && (orderCreateDto.getExchangeRate() != null)) {
            boolean ifEnoughMoney = orderCreateDto.getSpentWalletBalance().compareTo(BigDecimal.ZERO) > 0 && orderCreateDto.getSpentAmount().compareTo(orderCreateDto.getSpentWalletBalance()) <= 0;
            if (!ifEnoughMoney) {
                errors.put("balance_" + errors.size(), "validation.orderNotEnoughMoney");
            }
        }
        return orderValidationDto;
    }

    private void validateIcoOrder(Map<String, Object> errors, Map<String, Object[]> errorParams, OrderCreateDto orderCreateDto) {
        if (orderCreateDto.getOrderBaseType() != OrderBaseType.ICO) {
            throw new RuntimeException("unsupported type of order");
        }
        if (orderCreateDto.getOperationType() == OperationType.SELL) {
            SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream()
                    .filter(p -> p.getAuthority().equals(UserRole.ICO_MARKET_MAKER.name())).findAny().orElseThrow(() -> new RuntimeException("not allowed"));
        }
        if (orderCreateDto.getOperationType() == OperationType.BUY) {
            Optional<BigDecimal> lastRate = orderDao.getLowestOpenOrderPriceByCurrencyPairAndOperationType(orderCreateDto.getCurrencyPair().getId(), OperationType.SELL.type);
            if (!lastRate.isPresent() || orderCreateDto.getExchangeRate().compareTo(lastRate.get()) < 0) {
                errors.put("exrate_" + errors.size(), "order_ico.no_orders_for_rate");
            }
        }
    }

    @Override
    public String createOrder(OrderCreateDto orderCreateDto, OrderActionEnum action, Locale locale) {
        Optional<String> autoAcceptResult = this.autoAccept(orderCreateDto, locale);
        if (autoAcceptResult.isPresent()) {
            logger.debug(autoAcceptResult.get());
            return autoAcceptResult.get();
        }
        Integer orderId = this.createOrder(orderCreateDto, CREATE);
        if (orderId <= 0) {
            throw new NotCreatableOrderException(messageSource.getMessage("dberror.text", null, locale));
        }
        return "{\"result\":\"" + messageSource.getMessage("createdorder.text", null, locale) + "\"}";
    }

    @Override
    @Transactional
    public Integer createOrderByStopOrder(OrderCreateDto orderCreateDto, OrderActionEnum action, Locale locale) {
        Optional<OrderCreationResultDto> autoAcceptResult = this.autoAcceptOrders(orderCreateDto, locale);
        if (autoAcceptResult.isPresent()) {
            logger.debug(autoAcceptResult.get());
            return autoAcceptResult.get().getCreatedOrderId();
        }
        Integer orderId = this.createOrder(orderCreateDto, CREATE);
        if (orderId <= 0) {
            throw new NotCreatableOrderException(messageSource.getMessage("dberror.text", null, locale));
        }
        return orderId;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int createOrder(OrderCreateDto orderCreateDto, OrderActionEnum action) {
        return createOrder(orderCreateDto, action, null, false);
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int createOrder(OrderCreateDto orderCreateDto, OrderActionEnum action, List<OrderEvent> eventsList, boolean partialAccept) {
        logTransaction("createOrder" ,"begin", orderCreateDto.getCurrencyPair().getId(), -1, null);
        ProfileData profileData = new ProfileData(200);
        try {
            String description = transactionDescription.get(null, action);
            int createdOrderId;
            int outWalletId;
            BigDecimal outAmount;
            if (orderCreateDto.getOperationType() == OperationType.BUY) {
                outWalletId = orderCreateDto.getWalletIdCurrencyConvert();
                outAmount = orderCreateDto.getTotalWithComission();
            } else {
                outWalletId = orderCreateDto.getWalletIdCurrencyBase();
                outAmount = orderCreateDto.getAmount();
            }
            if (walletService.ifEnoughMoney(outWalletId, outAmount)) {
                profileData.setTime1();
                ExOrder exOrder = new ExOrder(orderCreateDto);
                OrderBaseType orderBaseType = orderCreateDto.getOrderBaseType();
                if (orderBaseType == null) {
                    CurrencyPairType type = exOrder.getCurrencyPair().getPairType();
                    orderBaseType = type == CurrencyPairType.ICO ? OrderBaseType.ICO : OrderBaseType.LIMIT;
                    exOrder.setOrderBaseType(orderBaseType);
                }
                TransactionSourceType sourceType;
                switch (orderBaseType) {
                    case STOP_LIMIT: {
                        createdOrderId = stopOrderService.createOrder(exOrder);
                        sourceType = TransactionSourceType.STOP_ORDER;
                        break;
                    }
                    case ICO: {
                        if (orderCreateDto.getOperationType() == OperationType.BUY) {
                            return 0;
                        }
                    }
                    default: {
                        createdOrderId = orderDao.createOrder(exOrder);
                        sourceType = TransactionSourceType.ORDER;
                    }
                }
                if (createdOrderId > 0) {
                    profileData.setTime2();
                    exOrder.setId(createdOrderId);
                    WalletTransferStatus result = walletService.walletInnerTransfer(
                            outWalletId,
                            outAmount.negate(),
                            sourceType,
                            exOrder.getId(),
                            description);
                    profileData.setTime3();
                    if (result != WalletTransferStatus.SUCCESS) {
                        throw new OrderCreationException(result.toString());
                    }
                    setStatus(createdOrderId, OrderStatus.OPENED, exOrder.getOrderBaseType());
                    exOrder.setStatus(OrderStatus.OPENED);
                    profileData.setTime4();
                }
                logTransaction("createOrder" ,"end", orderCreateDto.getCurrencyPair().getId(), createdOrderId, null);
                eventPublisher.publishEvent(new CreateOrderEvent(exOrder));
                if(!partialAccept) {
                    eventPublisher.publishEvent(new CreateDetailOrderEvent(exOrder, exOrder.getCurrencyPairId()));
                } else {
                    eventsList.add(new CreateOrderEvent(exOrder));
                }
                return createdOrderId;

            } else {
                //this exception will be caught in controller, populated  with message text  and thrown further
                throw new NotEnoughUserWalletMoneyException("");
            }
        } finally {
            profileData.checkAndLog("slow creation order: " + orderCreateDto + " profile: " + profileData);
        }
    }

    @Override
    @Transactional
    public void postBotOrderToDb(OrderCreateDto orderCreateDto) {
        ExOrder exOrder = new ExOrder(orderCreateDto);
        exOrder.setUserAcceptorId(orderCreateDto.getUserId());
        orderDao.postAcceptedOrderToDB(exOrder);
        eventPublisher.publishEvent(new AcceptOrderEvent(exOrder));
    }


    @Override
    @Transactional
    public OrderCreateDto prepareOrderRest(OrderCreationParamsDto orderCreationParamsDto, String userEmail, Locale locale, OrderBaseType orderBaseType) {
        CurrencyPair activeCurrencyPair = currencyService.findCurrencyPairById(orderCreationParamsDto.getCurrencyPairId());
        OrderCreateDto orderCreateDto = prepareNewOrder(activeCurrencyPair, orderCreationParamsDto.getOrderType(),
                userEmail, orderCreationParamsDto.getAmount(), orderCreationParamsDto.getRate(), orderBaseType);
        log.debug("Order prepared" + orderCreateDto);
        OrderValidationDto orderValidationDto = validateOrder(orderCreateDto, false, null);
        Map<String, Object> errors = orderValidationDto.getErrors();
        if (!errors.isEmpty()) {
            errors.replaceAll((key, value) -> messageSource.getMessage(value.toString(), orderValidationDto.getErrorParams().get(key), locale));
            throw new OrderParamsWrongException(errors.toString());
        }
        return orderCreateDto;
    }


    @Override
    @Transactional
    public OrderCreationResultDto createPreparedOrderRest(OrderCreateDto orderCreateDto, Locale locale) {
        Optional<OrderCreationResultDto> autoAcceptResult = autoAcceptOrders(orderCreateDto, locale);
        log.info("Auto accept result: " + autoAcceptResult);
        if (autoAcceptResult.isPresent()) {
            return autoAcceptResult.get();
        }
        OrderCreationResultDto orderCreationResultDto = new OrderCreationResultDto();

        Integer createdOrderId = createOrder(orderCreateDto, CREATE);
        if (createdOrderId <= 0) {
            throw new NotCreatableOrderException(messageSource.getMessage("dberror.text", null, locale));
        }
        orderCreationResultDto.setCreatedOrderId(createdOrderId);
        log.info("Order creation result result: " + autoAcceptResult);
        return orderCreationResultDto;
    }


    @Override
    @Transactional
    public OrderCreationResultDto prepareAndCreateOrderRest(String currencyPairName, OperationType orderType,
                                                            BigDecimal amount, BigDecimal exrate, String userEmail) {
        synchronized (restOrderCreationLock) {
            log.info(String.format("Start creating order: %s %s amount %s rate %s", currencyPairName, orderType.name(), amount, exrate));
            Locale locale = userService.getUserLocaleForMobile(userEmail);
            CurrencyPair currencyPair = currencyService.getCurrencyPairByName(currencyPairName);
            if (currencyPair.getPairType() != CurrencyPairType.MAIN) {
                throw new NotCreatableOrderException("This pair available only through website");
            }
            OrderCreateDto orderCreateDto = prepareOrderRest(new OrderCreationParamsDto(currencyPair.getId(), orderType, amount, exrate), userEmail, locale, OrderBaseType.LIMIT);
            return createPreparedOrderRest(orderCreateDto, locale);
        }
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Optional<String> autoAccept(OrderCreateDto orderCreateDto, Locale locale) {
        Optional<OrderCreationResultDto> autoAcceptResult = autoAcceptOrders(orderCreateDto, locale);
        if (!autoAcceptResult.isPresent()) {
            return Optional.empty();
        }
        OrderCreationResultDto orderCreationResultDto = autoAcceptResult.get();
        StringBuilder successMessage = new StringBuilder("{\"result\":\"");
        if (orderCreationResultDto.getAutoAcceptedQuantity() != null && orderCreationResultDto.getAutoAcceptedQuantity() > 0) {
            successMessage.append(messageSource.getMessage("order.acceptsuccess",
                    new Integer[]{orderCreationResultDto.getAutoAcceptedQuantity()}, locale)).append("; ");
        }
        if (orderCreationResultDto.getPartiallyAcceptedAmount() != null) {
            successMessage.append(messageSource.getMessage("orders.partialAccept.success", new Object[]{orderCreationResultDto.getPartiallyAcceptedAmount(),
                    orderCreationResultDto.getPartiallyAcceptedOrderFullAmount(), orderCreateDto.getCurrencyPair().getCurrency1().getName()}, locale));
        }
        if (orderCreationResultDto.getCreatedOrderId() != null) {
            successMessage.append(messageSource.getMessage("createdorder.text", null, locale));
        }
        successMessage.append("\"}");
        return Optional.of(successMessage.toString());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<OrderCreationResultDto> autoAcceptOrders(OrderCreateDto orderCreateDto, Locale locale) {
        logTransaction("autoAcceptOrders" ,"begin", orderCreateDto.getCurrencyPair().getId(), -1, null);
        synchronized (autoAcceptLock) {
            List<OrderEvent> acceptEventsList = new ArrayList<>();
            ProfileData profileData = new ProfileData(200);
            try {
                boolean acceptSameRoleOnly = userRoleService.isOrderAcceptionAllowedForUser(orderCreateDto.getUserId());
                List<ExOrder> acceptableOrders = orderDao.selectTopOrders(orderCreateDto.getCurrencyPair().getId(), orderCreateDto.getExchangeRate(),
                        OperationType.getOpposite(orderCreateDto.getOperationType()), acceptSameRoleOnly, userService.getUserRoleFromDB(orderCreateDto.getUserId()).getRole(), orderCreateDto.getOrderBaseType());
                profileData.setTime1();
                logger.debug("acceptableOrders - " + OperationType.getOpposite(orderCreateDto.getOperationType()) + " : " + acceptableOrders);
                if (acceptableOrders.isEmpty()) {
                    return Optional.empty();
                }
                BigDecimal cumulativeSum = BigDecimal.ZERO;
                List<ExOrder> ordersForAccept = new ArrayList<>();
                ExOrder orderForPartialAccept = null;
                for (ExOrder order : acceptableOrders) {
                    cumulativeSum = cumulativeSum.add(order.getAmountBase());
                    if (orderCreateDto.getAmount().compareTo(cumulativeSum) > 0) {
                        ordersForAccept.add(order);
                    } else if (orderCreateDto.getAmount().compareTo(cumulativeSum) == 0) {
                        ordersForAccept.add(order);
                        break;
                    } else {
                        orderForPartialAccept = order;
                        break;
                    }
                }
                OrderCreationResultDto orderCreationResultDto = new OrderCreationResultDto();

                if (ordersForAccept.size() > 0) {
                     acceptOrdersList(orderCreateDto.getUserId(), ordersForAccept
                            .stream()
                            .map(ExOrder::getId)
                            .collect(Collectors.toList()), locale, acceptEventsList, true);
                    orderCreationResultDto.setAutoAcceptedQuantity(ordersForAccept.size());
                }
                if (orderForPartialAccept != null) {
                    BigDecimal partialAcceptResult = acceptPartially(orderCreateDto, orderForPartialAccept, cumulativeSum, locale, acceptEventsList);
                    orderCreationResultDto.setPartiallyAcceptedAmount(partialAcceptResult);
                    orderCreationResultDto.setPartiallyAcceptedOrderFullAmount(orderForPartialAccept.getAmountBase());
                } else if (orderCreateDto.getAmount().compareTo(cumulativeSum) > 0 && orderCreateDto.getOrderBaseType() != OrderBaseType.ICO) {
                    User user = userService.getUserById(orderCreateDto.getUserId());
                    profileData.setTime2();
                    OrderCreateDto remainderNew = prepareNewOrder(
                            orderCreateDto.getCurrencyPair(),
                            orderCreateDto.getOperationType(),
                            user.getEmail(),
                            orderCreateDto.getAmount().subtract(cumulativeSum),
                            orderCreateDto.getExchangeRate(),
                            orderCreateDto.getOrderBaseType());
                    profileData.setTime3();
                    Integer createdOrderId = createOrder(remainderNew, CREATE, acceptEventsList, true);
                    profileData.setTime4();
                    orderCreationResultDto.setCreatedOrderId(createdOrderId);
                }
                logTransaction("autoAcceptOrders" ,"end", orderCreateDto.getCurrencyPair().getId(), -1, null);
                if (!acceptEventsList.isEmpty()) {
                    eventPublisher.publishEvent(new AutoAcceptEventsList(acceptEventsList, orderCreateDto.getCurrencyPair().getId()));
                }
                return Optional.of(orderCreationResultDto);
            } finally {
                profileData.checkAndLog("slow creation order: " + orderCreateDto + " profile: " + profileData);
            }
        }
    }

    private BigDecimal acceptPartially(OrderCreateDto newOrder, ExOrder orderForPartialAccept, BigDecimal cumulativeSum, Locale locale, List<OrderEvent> acceptEventsList) {
        logTransaction("acceptPartially" ,"begin", orderForPartialAccept.getCurrencyPairId(), orderForPartialAccept.getId(), null);
        deleteOrderForPartialAccept(orderForPartialAccept.getId(), acceptEventsList);
        BigDecimal amountForPartialAccept = newOrder.getAmount().subtract(cumulativeSum.subtract(orderForPartialAccept.getAmountBase()));
        OrderCreateDto accepted = prepareNewOrder(newOrder.getCurrencyPair(), orderForPartialAccept.getOperationType(),
                userService.getUserById(orderForPartialAccept.getUserId()).getEmail(), amountForPartialAccept,
                orderForPartialAccept.getExRate(), orderForPartialAccept.getId(), newOrder.getOrderBaseType());
        OrderCreateDto remainder = prepareNewOrder(newOrder.getCurrencyPair(), orderForPartialAccept.getOperationType(),
                userService.getUserById(orderForPartialAccept.getUserId()).getEmail(), orderForPartialAccept.getAmountBase().subtract(amountForPartialAccept),
                orderForPartialAccept.getExRate(), orderForPartialAccept.getId(), newOrder.getOrderBaseType());
        logTransaction("acceptPartially" ,"middle", orderForPartialAccept.getCurrencyPairId(), orderForPartialAccept.getId(), null);
        int acceptedId = createOrder(accepted, CREATE, acceptEventsList, true);
        createOrder(remainder, CREATE_SPLIT, acceptEventsList, true);
        acceptOrder(newOrder.getUserId(), acceptedId, locale, false, acceptEventsList, true);
        logTransaction("acceptPartially" ,"end", orderForPartialAccept.getCurrencyPairId(), acceptedId, null);
        eventPublisher.publishEvent(partiallyAcceptedOrder(orderForPartialAccept, amountForPartialAccept));

   /* TODO temporary disable
    notificationService.createLocalizedNotification(orderForPartialAccept.getUserId(), NotificationEvent.ORDER,
        "orders.partialAccept.title", "orders.partialAccept.yourOrder",
        new Object[]{orderForPartialAccept.getId(), amountForPartialAccept.toString(),
            orderForPartialAccept.getAmountBase().toString(), newOrder.getCurrencyPair().getCurrency1().getName()});*/
        return amountForPartialAccept;
    }


    private void logTransaction(String method, String part, Integer pairId, int partiallyAcceptedOrderId, List<UserOrdersDto> ordersDtos) {
        if (pairId == null) {
            pairId = 0;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String partOfLogging = String.format("time %s ; timestamp %s ; method %s ; part %s ; user %s ; pairId %d ; partialyAccepted or created Id %d ;",
                LocalDateTime.now(), System.currentTimeMillis(), method, part, auth == null ? "no user" : auth.getName(), pairId, partiallyAcceptedOrderId);
        String logMessage;
        try {
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                org.springframework.transaction.TransactionStatus status = TransactionAspectSupport.currentTransactionStatus();
                boolean isSyncActive = TransactionSynchronizationManager.isSynchronizationActive();
                boolean isRollbackOnly = status.isRollbackOnly();
                boolean isCompleted = status.isCompleted();
                boolean isNewTx = status.isNewTransaction();
                boolean hasSavepoint = status.hasSavepoint();
                logMessage = String.format("%s txName %s ; isSyncActive %s ; isRollbackOnly %s ; isCompleted %s ; isNewTx %s ; hasSavepoint %s ;",
                        partOfLogging, TransactionSynchronizationManager.getCurrentTransactionName(), isSyncActive, isRollbackOnly, isCompleted, isNewTx, hasSavepoint);
            } else {
                logMessage = String.format("%s no transaction active ; ", partOfLogging);
                if(ordersDtos != null) {
                    String ids = ordersDtos.stream().map(p->p.getId().toString()).collect(Collectors.joining( "," ) );
                    logMessage = logMessage.concat(ids);
                }
            }
        } catch (NoTransactionException e) {
            logMessage = String.format("%s error when complition log ", partOfLogging);
        }
        txLogger.info(logMessage);
    }

    private OrderEvent partiallyAcceptedOrder(ExOrder orderForPartialAccept, BigDecimal amountForPartialAccept) {
        orderForPartialAccept.setPartiallyAcceptedAmount(amountForPartialAccept);
        return new PartiallyAcceptedOrder(orderForPartialAccept);
    }

    @Override
    public List<OrderWideListDto> getMyOrdersWithState(CacheData cacheData,
                                                       String email, CurrencyPair currencyPair, OrderStatus status,
                                                       OperationType operationType,
                                                       String scope, Integer offset, Integer limit, Locale locale) {
        List<OrderWideListDto> result = orderDao.getMyOrdersWithState(userService.getIdByEmail(email), currencyPair, status, operationType, scope, offset, limit, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<OrderWideListDto>() {{
                add(new OrderWideListDto(false));
            }};
        }
        return result;
    }

    @Override
    public OrderCreateDto getMyOrderById(int orderId) {
        return orderDao.getMyOrderById(orderId);
    }

    @Transactional(readOnly = true)
    public ExOrder getOrderById(int orderId) {
        return orderDao.getOrderById(orderId);
    }

    @Override
    public ExOrder getOrderById(int orderId, int userId) {
        return orderDao.getOrderById(orderId, userId);
    }

    @Transactional
    public boolean setStatus(int orderId, OrderStatus status, OrderBaseType orderBaseType) {
        switch (orderBaseType) {
            case STOP_LIMIT: {
                return stopOrderService.setStatus(orderId, status);
            }
            default: {
                return this.setStatus(orderId, status);
            }
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public boolean setStatus(int orderId, OrderStatus status) {
        return orderDao.setStatus(orderId, status);
    }


    @Override
    @Transactional
    public void acceptOrder(String userEmail, Integer orderId) {
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        Integer userId = userService.getIdByEmail(userEmail);
        acceptOrdersList(userId, Collections.singletonList(orderId), locale, null, false);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void acceptOrdersList(int userAcceptorId, List<Integer> ordersList, Locale locale, List<OrderEvent> eventsList, boolean partialAccept) {
        if (orderDao.lockOrdersListForAcception(ordersList)) {
            for (Integer orderId : ordersList) {
                acceptOrder(userAcceptorId, orderId, locale, eventsList, partialAccept);
            }
        } else {
            throw new OrderAcceptionException(messageSource.getMessage("order.lockerror", null, locale));
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    void acceptOrder(int userAcceptorId, int orderId, Locale locale, List<OrderEvent> eventsList, boolean partialAccept) {
        acceptOrder(userAcceptorId, orderId, locale, true, eventsList, partialAccept);

    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void acceptOrderByAdmin(String acceptorEmail, Integer orderId, Locale locale) {
        Integer userId = userService.getIdByEmail(acceptorEmail);
        acceptOrdersList(userId, Collections.singletonList(orderId), locale, null, false);
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void acceptManyOrdersByAdmin(String acceptorEmail, List<Integer> orderIds, Locale locale) {
        Integer userId = userService.getIdByEmail(acceptorEmail);
        acceptOrdersList(userId, orderIds, locale, null, false);
    }


    private void acceptOrder(int userAcceptorId, int orderId, Locale locale, boolean sendNotification, List<OrderEvent> eventsList, boolean partialAccept) {
        try {
            ExOrder exOrder = this.getOrderById(orderId);

            checkAcceptPermissionForUser(userAcceptorId, exOrder.getUserId(), locale);

            WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = walletService.getWalletsForOrderByOrderIdAndBlock(exOrder.getId(), userAcceptorId);
            String descriptionForCreator = transactionDescription.get(OrderStatus.convert(walletsForOrderAcceptionDto.getOrderStatusId()), ACCEPTED);
            String descriptionForAcceptor = transactionDescription.get(OrderStatus.convert(walletsForOrderAcceptionDto.getOrderStatusId()), ACCEPT);
            /**/
            if (walletsForOrderAcceptionDto.getOrderStatusId() != 2) {
                throw new AlreadyAcceptedOrderException(messageSource.getMessage("order.alreadyacceptederror", null, locale));
            }
            /**/
            int createdWalletId;
            if (exOrder.getOperationType() == OperationType.BUY) {
                if (walletsForOrderAcceptionDto.getUserCreatorInWalletId() == 0) {
                    createdWalletId = walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyBase(), userService.getUserById(exOrder.getUserId()), new BigDecimal(0)));
                    if (createdWalletId == 0) {
                        throw new WalletCreationException(messageSource.getMessage("order.createwalleterror", new Object[]{exOrder.getUserId()}, locale));
                    }
                    walletsForOrderAcceptionDto.setUserCreatorInWalletId(createdWalletId);
                }
                if (walletsForOrderAcceptionDto.getUserAcceptorInWalletId() == 0) {
                    createdWalletId = walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyConvert(), userService.getUserById(userAcceptorId), new BigDecimal(0)));
                    if (createdWalletId == 0) {
                        throw new WalletCreationException(messageSource.getMessage("order.createwalleterror", new Object[]{userAcceptorId}, locale));
                    }
                    walletsForOrderAcceptionDto.setUserAcceptorInWalletId(createdWalletId);
                }
            }
            if (exOrder.getOperationType() == OperationType.SELL) {
                if (walletsForOrderAcceptionDto.getUserCreatorInWalletId() == 0) {
                    createdWalletId = walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyConvert(), userService.getUserById(exOrder.getUserId()), new BigDecimal(0)));
                    if (createdWalletId == 0) {
                        throw new WalletCreationException(messageSource.getMessage("order.createwalleterror", new Object[]{exOrder.getUserId()}, locale));
                    }
                    walletsForOrderAcceptionDto.setUserCreatorInWalletId(createdWalletId);
                }
                if (walletsForOrderAcceptionDto.getUserAcceptorInWalletId() == 0) {
                    createdWalletId = walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyBase(), userService.getUserById(userAcceptorId), new BigDecimal(0)));
                    if (createdWalletId == 0) {
                        throw new WalletCreationException(messageSource.getMessage("order.createwalleterror", new Object[]{userAcceptorId}, locale));
                    }
                    walletsForOrderAcceptionDto.setUserAcceptorInWalletId(createdWalletId);
                }
            }
            /**/
            /*calculate convert currency amount for creator - simply take stored amount from order*/
            BigDecimal amountWithComissionForCreator = getAmountWithComissionForCreator(exOrder);
            Commission comissionForCreator = new Commission();
            comissionForCreator.setId(exOrder.getComissionId());
            /*calculate convert currency amount for acceptor - calculate at the current commission rate*/
            OperationType operationTypeForAcceptor = exOrder.getOperationType() == OperationType.BUY ? OperationType.SELL : OperationType.BUY;
            Commission comissionForAcceptor;
            /*todo: zero comissions from db*/
            CurrencyPair cp = currencyService.findCurrencyPairById(exOrder.getCurrencyPairId());
            exOrder.setCurrencyPair(cp);
            if (exOrder.getOrderBaseType() == OrderBaseType.ICO || exOrder.getCurrencyPair().getName().contains("EDR")) {
                comissionForAcceptor = Commission.zeroComission();
            } else {
                comissionForAcceptor = commissionDao.getCommission(operationTypeForAcceptor, userService.getUserRoleFromDB(userAcceptorId));
            }
            /*-------------------------*/
            BigDecimal comissionRateForAcceptor = comissionForAcceptor.getValue();
            BigDecimal amountComissionForAcceptor = BigDecimalProcessing.doAction(exOrder.getAmountConvert(), comissionRateForAcceptor, ActionType.MULTIPLY_PERCENT);
            BigDecimal amountWithComissionForAcceptor;
            if (exOrder.getOperationType() == OperationType.BUY) {
                amountWithComissionForAcceptor = BigDecimalProcessing.doAction(exOrder.getAmountConvert(), amountComissionForAcceptor, ActionType.SUBTRACT);
            } else {
                amountWithComissionForAcceptor = BigDecimalProcessing.doAction(exOrder.getAmountConvert(), amountComissionForAcceptor, ActionType.ADD);
            }
            /*determine the IN and OUT amounts for creator and acceptor*/
            BigDecimal creatorForOutAmount = null;
            BigDecimal creatorForInAmount = null;
            BigDecimal acceptorForOutAmount = null;
            BigDecimal acceptorForInAmount = null;
            BigDecimal commissionForCreatorOutWallet = null;
            BigDecimal commissionForCreatorInWallet = null;
            BigDecimal commissionForAcceptorOutWallet = null;
            BigDecimal commissionForAcceptorInWallet = null;
            if (exOrder.getOperationType() == OperationType.BUY) {
                commissionForCreatorOutWallet = exOrder.getCommissionFixedAmount();
                commissionForCreatorInWallet = BigDecimal.ZERO;
                commissionForAcceptorOutWallet = BigDecimal.ZERO;
                commissionForAcceptorInWallet = amountComissionForAcceptor;
                /**/
                creatorForOutAmount = amountWithComissionForCreator;
                creatorForInAmount = exOrder.getAmountBase();
                acceptorForOutAmount = exOrder.getAmountBase();
                acceptorForInAmount = amountWithComissionForAcceptor;
            }
            if (exOrder.getOperationType() == OperationType.SELL) {
                commissionForCreatorOutWallet = BigDecimal.ZERO;
                commissionForCreatorInWallet = exOrder.getCommissionFixedAmount();
                commissionForAcceptorOutWallet = amountComissionForAcceptor;
                commissionForAcceptorInWallet = BigDecimal.ZERO;
                /**/
                creatorForOutAmount = exOrder.getAmountBase();
                creatorForInAmount = amountWithComissionForCreator;
                acceptorForOutAmount = amountWithComissionForAcceptor;
                acceptorForInAmount = exOrder.getAmountBase();
            }
            WalletOperationData walletOperationData;
            WalletTransferStatus walletTransferStatus;
            String exceptionMessage = "";
            /**/
            /*for creator OUT*/
            walletOperationData = new WalletOperationData();
            walletService.walletInnerTransfer(
                    walletsForOrderAcceptionDto.getUserCreatorOutWalletId(),
                    creatorForOutAmount,
                    TransactionSourceType.ORDER,
                    exOrder.getId(),
                    descriptionForCreator);
            walletOperationData.setOperationType(OperationType.OUTPUT);
            walletOperationData.setWalletId(walletsForOrderAcceptionDto.getUserCreatorOutWalletId());
            walletOperationData.setAmount(creatorForOutAmount);
            walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
            walletOperationData.setCommission(comissionForCreator);
            walletOperationData.setCommissionAmount(commissionForCreatorOutWallet);
            walletOperationData.setSourceType(TransactionSourceType.ORDER);
            walletOperationData.setSourceId(exOrder.getId());
            walletOperationData.setDescription(descriptionForCreator);
            walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
            if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                exceptionMessage = getWalletTransferExceptionMessage(walletTransferStatus, "order.notenoughreservedmoneyforcreator", locale);
                if (walletTransferStatus == WalletTransferStatus.CAUSED_NEGATIVE_BALANCE) {
                    throw new InsufficientCostsForAcceptionException(exceptionMessage);
                }
                throw new OrderAcceptionException(exceptionMessage);
            }
            /*for acceptor OUT*/
            walletOperationData = new WalletOperationData();
            walletOperationData.setOperationType(OperationType.OUTPUT);
            walletOperationData.setWalletId(walletsForOrderAcceptionDto.getUserAcceptorOutWalletId());
            walletOperationData.setAmount(acceptorForOutAmount);
            walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
            walletOperationData.setCommission(comissionForAcceptor);
            walletOperationData.setCommissionAmount(commissionForAcceptorOutWallet);
            walletOperationData.setSourceType(TransactionSourceType.ORDER);
            walletOperationData.setSourceId(exOrder.getId());
            walletOperationData.setDescription(descriptionForAcceptor);
            walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
            if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                exceptionMessage = getWalletTransferExceptionMessage(walletTransferStatus, "order.notenoughmoneyforacceptor", locale);
                if (walletTransferStatus == WalletTransferStatus.CAUSED_NEGATIVE_BALANCE) {
                    throw new InsufficientCostsForAcceptionException(exceptionMessage);
                }
                throw new OrderAcceptionException(exceptionMessage);
            }
            /*for creator IN*/
            walletOperationData = new WalletOperationData();
            walletOperationData.setOperationType(OperationType.INPUT);
            walletOperationData.setWalletId(walletsForOrderAcceptionDto.getUserCreatorInWalletId());
            walletOperationData.setAmount(creatorForInAmount);
            walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
            walletOperationData.setCommission(comissionForCreator);
            walletOperationData.setCommissionAmount(commissionForCreatorInWallet);
            walletOperationData.setSourceType(TransactionSourceType.ORDER);
            walletOperationData.setSourceId(exOrder.getId());
            walletOperationData.setDescription(descriptionForCreator);
            walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
            if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                exceptionMessage = getWalletTransferExceptionMessage(walletTransferStatus, "orders.acceptsaveerror", locale);
                throw new OrderAcceptionException(exceptionMessage);
            }

            /*for acceptor IN*/
            walletOperationData = new WalletOperationData();
            walletOperationData.setOperationType(OperationType.INPUT);
            walletOperationData.setWalletId(walletsForOrderAcceptionDto.getUserAcceptorInWalletId());
            walletOperationData.setAmount(acceptorForInAmount);
            walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
            walletOperationData.setCommission(comissionForAcceptor);
            walletOperationData.setCommissionAmount(commissionForAcceptorInWallet);
            walletOperationData.setSourceType(TransactionSourceType.ORDER);
            walletOperationData.setSourceId(exOrder.getId());
            walletOperationData.setDescription(descriptionForAcceptor);
            walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
            if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                exceptionMessage = getWalletTransferExceptionMessage(walletTransferStatus, "orders.acceptsaveerror", locale);
                throw new OrderAcceptionException(exceptionMessage);
            }
            /**/
            CompanyWallet companyWallet = new CompanyWallet();
            companyWallet.setId(walletsForOrderAcceptionDto.getCompanyWalletCurrencyConvert());
            companyWallet.setBalance(walletsForOrderAcceptionDto.getCompanyWalletCurrencyConvertBalance());
            companyWallet.setCommissionBalance(walletsForOrderAcceptionDto.getCompanyWalletCurrencyConvertCommissionBalance());
            companyWalletService.deposit(companyWallet, new BigDecimal(0), exOrder.getCommissionFixedAmount().add(amountComissionForAcceptor));
            /**/
            exOrder.setStatus(OrderStatus.CLOSED);
            exOrder.setDateAcception(LocalDateTime.now());
            exOrder.setUserAcceptorId(userAcceptorId);
            final Currency currency = currencyService.findCurrencyPairById(exOrder.getCurrencyPairId())
                    .getCurrency2();

            /** TODO: 6/7/16 Temporarily disable the referral program
             * referralService.processReferral(exOrder, exOrder.getCommissionFixedAmount(), currency.getId(), exOrder.getUserId()); //Processing referral for Order Creator
             * referralService.processReferral(exOrder, amountComissionForAcceptor, currency.getId(), exOrder.getUserAcceptorId()); //Processing referral for Order Acceptor
             */

            referralService.processReferral(exOrder, exOrder.getCommissionFixedAmount(), currency, exOrder.getUserId()); //Processing referral for Order Creator
            referralService.processReferral(exOrder, amountComissionForAcceptor, currency, exOrder.getUserAcceptorId()); //Processing referral for Order Acceptor

            if (!updateOrder(exOrder)) {
                throw new OrderAcceptionException(messageSource.getMessage("orders.acceptsaveerror", null, locale));
            }
      /*if (sendNotification) {
        notificationService.createLocalizedNotification(exOrder.getUserId(), NotificationEvent.ORDER, "acceptordersuccess.title",
            "acceptorder.message", new Object[]{exOrder.getId()});
      }*/

            /*  stopOrderService.onLimitOrderAccept(exOrder);*//*check stop-orders for process*/
            /*action for refresh orders*/
            eventPublisher.publishEvent(new AcceptOrderEvent(exOrder));
            if(!partialAccept) {
                eventPublisher.publishEvent(new AcceptDetailOrderEvent(exOrder, exOrder.getCurrencyPairId()));
            } else {
                eventsList.add(new AcceptOrderEvent(exOrder));
            }
        } catch (Exception e) {
            logger.error("Error while accepting order with id = " + orderId + " exception: " + e.getLocalizedMessage());
            throw e;
        }
    }


    private void checkAcceptPermissionForUser(Integer acceptorId, Integer creatorId, Locale locale) {
        UserRole acceptorRole = userService.getUserRoleFromDB(acceptorId);
        UserRole creatorRole = userService.getUserRoleFromDB(creatorId);

        UserRoleSettings creatorSettings = userRoleService.retrieveSettingsForRole(creatorRole.getRole());
        if (creatorSettings.isBotAcceptionAllowedOnly() && acceptorRole != UserRole.BOT_TRADER) {
            throw new AttemptToAcceptBotOrderException(messageSource.getMessage("orders.acceptsaveerror", null, locale));
        }
        if (userRoleService.isOrderAcceptionAllowedForUser(acceptorId)) {
            if (acceptorRole != creatorRole) {
                throw new OrderAcceptionException(messageSource.getMessage("order.accept.wrongRole", new Object[]{creatorRole.name()}, locale));
            }

        }


    }

    private String getWalletTransferExceptionMessage(WalletTransferStatus status, String negativeBalanceMessageCode, Locale locale) {
        String message = "";
        switch (status) {
            case CAUSED_NEGATIVE_BALANCE:
                message = messageSource.getMessage(negativeBalanceMessageCode, null, locale);
                break;
            case CORRESPONDING_COMPANY_WALLET_NOT_FOUND:
                message = messageSource.getMessage("orders.companyWalletNotFound", null, locale);
                break;
            case WALLET_NOT_FOUND:
                message = messageSource.getMessage("orders.walletNotFound", null, locale);
                break;
            case WALLET_UPDATE_ERROR:
                message = messageSource.getMessage("orders.walletUpdateError", null, locale);
                break;
            case TRANSACTION_CREATION_ERROR:
                message = messageSource.getMessage("transaction.createerror", null, locale);
                break;
            default:
                message = messageSource.getMessage("orders.acceptsaveerror", null, locale);

        }
        return message;
    }


    private BigDecimal getAmountWithComissionForCreator(ExOrder exOrder) {
        if (exOrder.getOperationType() == OperationType.SELL) {
            return BigDecimalProcessing.doAction(exOrder.getAmountConvert(), exOrder.getCommissionFixedAmount(), ActionType.SUBTRACT);
        } else {
            return BigDecimalProcessing.doAction(exOrder.getAmountConvert(), exOrder.getCommissionFixedAmount(), ActionType.ADD);
        }
    }

    @Transactional
    @Override
    public boolean cancelOrder(Integer orderId) {
        ExOrder exOrder = getOrderById(orderId);
        if (isNull(exOrder)) {
            return false;
        }
        return cancelOrder(exOrder);
    }

    @Transactional
    @Override
    public boolean cancelOpenOrdersByCurrencyPair(String currencyPair) {
        final Integer userId = userService.getIdByEmail(getUserEmailFromSecurityContext());

        List<ExOrder> openedOrders = orderDao.getOpenedOrdersByCurrencyPair(userId, currencyPair);
        if (isEmpty(openedOrders)) {
            return false;
        }

        return openedOrders
                .stream()
                .allMatch(this::cancelOrder);
    }

    @Transactional
    @Override
    public boolean cancelAllOpenOrders() {
        final Integer userId = userService.getIdByEmail(getUserEmailFromSecurityContext());

        List<ExOrder> openedOrders = orderDao.getAllOpenedOrdersByUserId(userId);
        if (isEmpty(openedOrders)) {
            return false;
        }

        return openedOrders
                .stream()
                .allMatch(this::cancelOrder);
    }

    private boolean cancelOrder(ExOrder exOrder) {
        return cancelOrder(exOrder, null, null, false);
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public boolean cancelOrder(ExOrder exOrder, Locale locale, List<OrderEvent> acceptEventsList, boolean forPartialAccept) {
        if (isNull(locale)) {
            final String currentUserEmail = getUserEmailFromSecurityContext();

            final int userId = exOrder.getUserId();

            final String creatorEmail = userService.getEmailById(userId);
            if (!currentUserEmail.equals(creatorEmail)) {
                throw new IncorrectCurrentUserException(String.format("Creator email: %s and currentUser email: %s are different", creatorEmail, currentUserEmail));
            }
            locale = userService.getUserLocaleForMobile(currentUserEmail);
        }
        WalletsForOrderCancelDto walletsForOrderCancelDto = walletService.getWalletForOrderByOrderIdAndOperationTypeAndBlock(
                exOrder.getId(),
                exOrder.getOperationType());

        OrderStatus currentStatus = OrderStatus.convert(walletsForOrderCancelDto.getOrderStatusId());
        if (currentStatus != OrderStatus.OPENED) {
            throw new OrderAcceptionException(messageSource.getMessage("order.cannotcancel", null, locale));
        }
        String description = transactionDescription.get(currentStatus, CANCEL);
        WalletTransferStatus transferResult = walletService.walletInnerTransfer(
                walletsForOrderCancelDto.getWalletId(),
                walletsForOrderCancelDto.getReservedAmount(),
                TransactionSourceType.ORDER,
                exOrder.getId(),
                description);
        if (transferResult != WalletTransferStatus.SUCCESS) {
            throw new OrderCancellingException(transferResult.toString());
        }
        boolean result = setStatus(exOrder.getId(), OrderStatus.CANCELLED);
        if (result) {
            exOrder.setStatus(OrderStatus.CANCELLED);
            eventPublisher.publishEvent(new CancelOrderEvent(exOrder, false));
            if(!forPartialAccept) {
                eventPublisher.publishEvent(new CancelDetailorderEvent(exOrder, false, true, exOrder.getCurrencyPairId()));
            } else {
                acceptEventsList.add(new CancelOrderEvent(exOrder, false));
            }
        }
        return result;
    }


    private String getStatusString(OrderStatus status, Locale ru) {
        String statusString = null;
        switch (status) {
            case INPROCESS:
                statusString = messageSource.getMessage("orderstatus.inprocess", null, ru);
                break;
            case OPENED:
                statusString = messageSource.getMessage("orderstatus.opened", null, ru);
                break;
            case CLOSED:
                statusString = messageSource.getMessage("orderstatus.closed", null, ru);
                break;
        }
        return statusString;
    }

    @Transactional(propagation = Propagation.NESTED)
    @Override
    public boolean updateOrder(ExOrder exOrder) {
        return orderDao.updateOrder(exOrder);
    }


    public List<CoinmarketApiDto> getCoinmarketData(String currencyPairName, BackDealInterval backDealInterval) {
        final List<CoinmarketApiDto> result = orderDao.getCoinmarketData(currencyPairName);
        List<CurrencyPair> currencyPairList = currencyService.getAllCurrencyPairs(CurrencyPairType.ALL);
        result.addAll(currencyPairList
                .stream()
                .filter(e -> (StringUtils.isEmpty(currencyPairName) || e.getName().equals(currencyPairName))
                        && result
                        .stream()
                        .noneMatch(r -> r.getCurrency_pair_name().equals(e.getName())))
                .map(CoinmarketApiDto::new)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public List<CoinmarketApiDto> getCoinmarketDataForActivePairs(String currencyPairName, BackDealInterval backDealInterval) {
        return orderDao.getCoinmarketData(currencyPairName);
    }

    @Override
    public List<CoinmarketApiDto> getDailyCoinmarketData(String currencyPairName) {
        return getCoinmarketDataForActivePairs(currencyPairName, new BackDealInterval("24 HOUR"));
    }

    @Override
    public List<CoinmarketApiDto> getHourlyCoinmarketData(String currencyPairName) {
        return getCoinmarketDataForActivePairs(currencyPairName, new BackDealInterval("1 HOUR"));
    }


    @Override
    public OrderInfoDto getOrderInfo(int orderId, Locale locale) {
        return orderDao.getOrderInfo(orderId, locale);
    }

    @Override
    public AdminOrderInfoDto getAdminOrderInfo(int orderId, Locale locale) {
        AdminOrderInfoDto dto = new AdminOrderInfoDto(this.getOrderInfo(orderId, locale));
        setIsOrderAcceptableAndNotifications(dto, locale);
        return dto;
    }

    private void setIsOrderAcceptableAndNotifications(AdminOrderInfoDto dto, Locale locale) {
        UserRole orderCreatorRole = userService.getUserRoleFromDB(dto.getOrderInfo().getOrderCreatorEmail());
        UserRole userRole = userService.getUserRoleFromSecurityContext();
        if (orderCreatorRole.getRole() == UserRole.TRADER.getRole() &&
                userRoleService.getRealUserRoleIdByBusinessRoleList(BusinessUserRoleEnum.ADMIN).contains(userRole.getRole())) {
            dto.setNotification(messageSource.getMessage("admin.orders.accept.warning", null, locale));
            dto.setAcceptable(true);
        } else if (userRole.getRole() == UserRole.TRADER.getRole() && orderCreatorRole.getRole() != UserRole.TRADER.getRole()) {
            dto.setAcceptable(false);
            dto.setNotification(messageSource.getMessage("admin.orders.cantaccept", null, locale));
        } else {
            dto.setAcceptable(true);
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void deleteManyOrdersByAdmin(List<Integer> orderIds) {
        orderIds.forEach(this::deleteOrderByAdmin);
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Integer deleteOrderByAdmin(int orderId) {
        OrderCreateDto order = orderDao.getMyOrderById(orderId);
        OrderRoleInfoForDelete orderRoleInfo = orderDao.getOrderRoleInfo(orderId);
        if (orderRoleInfo.mayDeleteWithoutProcessingTransactions()) {
            setStatus(orderId, OrderStatus.DELETED);
            return 1;
        }

        Object result = deleteOrder(orderId, OrderStatus.DELETED, DELETE, null, false);
        if (result instanceof OrderDeleteStatus) {
            if ((OrderDeleteStatus) result == OrderDeleteStatus.NOT_FOUND) {
                return 0;
            }
            throw new OrderDeletingException(((OrderDeleteStatus) result).toString());
        }
        /*notificationService.notifyUser(order.getUserId(), NotificationEvent.ORDER,
                "deleteOrder.notificationTitle", "deleteOrder.notificationMessage", new Object[]{order.getOrderId()});*/
        return (Integer) result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Integer deleteOrderForPartialAccept(int orderId, List<OrderEvent> acceptEventsList) {
        Object result = deleteOrder(orderId, OrderStatus.SPLIT_CLOSED, DELETE_SPLIT, acceptEventsList, true);
        if (result instanceof OrderDeleteStatus) {
            throw new OrderDeletingException(((OrderDeleteStatus) result).toString());
        }
        return (Integer) result;
    }


    @Override
    public Integer searchOrderByAdmin(Integer currencyPair, String orderType, String orderDate, BigDecimal orderRate, BigDecimal orderVolume) {
        Integer ot = OperationType.valueOf(orderType).getType();
        return orderDao.searchOrderByAdmin(currencyPair, ot, orderDate, orderRate, orderVolume);
    }

    @Override
    public List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(CacheData cacheData,
                                                                   String email,
                                                                   BackDealInterval backDealInterval,
                                                                   Integer limit, CurrencyPair currencyPair, Locale locale) {
        List<OrderAcceptedHistoryDto> result = orderDao.getOrderAcceptedForPeriod(email, backDealInterval, limit, currencyPair);
        result = result
                .stream()
                .map(OrderAcceptedHistoryDto::new)
                .collect(Collectors.toList());
        result.forEach(e -> {
            e.setRate(BigDecimalProcessing.formatLocale(e.getRate(), locale, true));
            e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
        });
        return result;
    }

    @Override
    public List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriodEx(String email,
                                                                     BackDealInterval backDealInterval,
                                                                     Integer limit, CurrencyPair currencyPair, Locale locale) {
        List<OrderAcceptedHistoryDto> result = orderDao.getOrderAcceptedForPeriod(email, backDealInterval, limit, currencyPair);
        result = result
                .stream()
                .map(OrderAcceptedHistoryDto::new)
                .collect(Collectors.toList());
        result.forEach(e -> {
            e.setRate(BigDecimalProcessing.formatLocale(e.getRate(), locale, true));
            e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
        });
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public OrderCommissionsDto getCommissionForOrder() {
        return orderDao.getCommissionForOrder(userService.getUserRoleFromSecurityContext());
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public CommissionsDto getAllCommissions() {
        UserRole userRole = userService.getUserRoleFromSecurityContext();
        return orderDao.getAllCommissions(userRole);
    }


    @Override
    public List<OrderListDto> getAllBuyOrders(CacheData cacheData,
                                              CurrencyPair currencyPair, Locale locale, Boolean orderRoleFilterEnabled) {
        Boolean evictEhCache = cacheData.getForceUpdate();
        UserRole filterRole = orderRoleFilterEnabled ? userService.getUserRoleFromSecurityContext() : null;
        List<OrderListDto> result = aggregateOrders(serviceCacheableProxy.getAllBuyOrders(currencyPair, filterRole, evictEhCache), OperationType.BUY, evictEhCache);
        result = new ArrayList<>(result);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<OrderListDto>() {{
                add(new OrderListDto(false));
            }};
        } else {
            result = result
                    .stream()
                    .map(OrderListDto::new).sorted((o1, o2) -> Double.valueOf(o2.getExrate()).compareTo(Double.valueOf(o1.getExrate())))
                    .collect(Collectors.toList());
            result.forEach(e -> {
                e.setExrate(BigDecimalProcessing.formatLocale(e.getExrate(), locale, 2));
                e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
                e.setAmountConvert(BigDecimalProcessing.formatLocale(e.getAmountConvert(), locale, true));
            });
        }
        return result;
    }


    @Override
    public List<OrderListDto> getAllBuyOrdersEx(CurrencyPair currencyPair, Locale locale, UserRole userRole) {
        List<OrderListDto> result = aggregateOrders(orderDao.getOrdersBuyForCurrencyPair(currencyPair, userRole), OperationType.BUY, true);
        result = new ArrayList<>(result);
        result = result
                .stream()
                .map(OrderListDto::new).sorted((o1, o2) -> Double.valueOf(o2.getExrate()).compareTo(Double.valueOf(o1.getExrate())))
                .collect(Collectors.toList());
        result.forEach(e -> {
            e.setExrate(BigDecimalProcessing.formatLocale(e.getExrate(), locale, 2));
            e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
            e.setAmountConvert(BigDecimalProcessing.formatLocale(e.getAmountConvert(), locale, true));
        });
        return result;
    }

    @Override
    public List<OrderListDto> getAllSellOrdersEx(CurrencyPair currencyPair, Locale locale, UserRole userRole) {
        List<OrderListDto> result = aggregateOrders(orderDao.getOrdersSellForCurrencyPair(currencyPair, userRole), OperationType.SELL, true);
        result = new ArrayList<>(result);
        result = result
                .stream()
                .map(OrderListDto::new).sorted((o1, o2) -> Double.valueOf(o1.getExrate()).compareTo(Double.valueOf(o2.getExrate())))
                .collect(Collectors.toList());
        result.forEach(e -> {
            e.setExrate(BigDecimalProcessing.formatLocale(e.getExrate(), locale, 2));
            e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
            e.setAmountConvert(BigDecimalProcessing.formatLocale(e.getAmountConvert(), locale, true));
        });
        return result;
    }


    @Override
    public List<OrderListDto> getAllSellOrders(CacheData cacheData,
                                               CurrencyPair currencyPair, Locale locale, Boolean orderRoleFilterEnabled) {
        Boolean evictEhCache = cacheData.getForceUpdate();
        UserRole filterRole = orderRoleFilterEnabled ? userService.getUserRoleFromSecurityContext() : null;
        List<OrderListDto> result = aggregateOrders(serviceCacheableProxy.getAllSellOrders(currencyPair, filterRole, evictEhCache), OperationType.SELL, evictEhCache);
        result = new ArrayList<>(result);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<OrderListDto>() {{
                add(new OrderListDto(false));
            }};
        } else {
            result = result
                    .stream()
                    .map(OrderListDto::new).sorted((o1, o2) -> Double.valueOf(o1.getExrate()).compareTo(Double.valueOf(o2.getExrate())))
                    .collect(Collectors.toList());
            result.forEach(e -> {
                e.setExrate(BigDecimalProcessing.formatLocale(e.getExrate(), locale, 2));
                e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
                e.setAmountConvert(BigDecimalProcessing.formatLocale(e.getAmountConvert(), locale, true));
            });
        }
        return result;
    }

    private List<OrderListDto> aggregateOrders(List<OrderListDto> historyDtos, OperationType operationType, boolean forceUpdate) {
        List<OrderListDto> resultList = new ArrayList<>();
        Map<String, List<OrderListDto>> map = historyDtos
                .stream()
                .collect(Collectors.groupingBy(OrderListDto::getExrate));
        map.forEach((k, v) -> {
            BigDecimal amountBase = new BigDecimal(0);
            BigDecimal amountConverted = new BigDecimal(0);
            StringJoiner ordersIds = new StringJoiner(" ");
            for (OrderListDto order : v) {
                amountBase = amountBase.add(new BigDecimal(order.getAmountBase()));
                amountConverted = amountConverted.add(new BigDecimal(order.getAmountConvert()));
                ordersIds.add(String.valueOf(order.getId()));
            }
            resultList.add(new OrderListDto(ordersIds.toString(), k, amountBase.toString(),
                    amountConverted.toString(), operationType, forceUpdate));
        });
        return resultList;
    }

    @Override
    public List<OrdersListWrapper> getOpenOrdersForWs(String pairName) {
        CurrencyPair cp = currencyService.getCurrencyPairByName(pairName);
        if (cp == null) {
            return null;
        }
        List<OrderWsDetailDto> dtoSell = orderDao.getOrdersSellForCurrencyPair(cp, null)
                .stream()
                .map(OrderWsDetailDto::new)
                .collect(Collectors.toList());
        List<OrderWsDetailDto> dtoBuy = orderDao.getOrdersBuyForCurrencyPair(cp, null)
                .stream()
                .map(OrderWsDetailDto::new)
                .collect(Collectors.toList());
        OrdersListWrapper sellOrders = new OrdersListWrapper(dtoSell, OperationType.SELL.name(), cp.getId());
        OrdersListWrapper buyOrders = new OrdersListWrapper(dtoBuy, OperationType.BUY.name(), cp.getId());
        return Arrays.asList(sellOrders, buyOrders);
    }

    @Transactional(readOnly = true)
    @Override
    public WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                           OperationType operationType) {
        UserRole userRole = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            userRole = userService.getUserRoleFromDB(email);
        } else {
            userRole = userService.getUserRoleFromSecurityContext();
        }
        return orderDao.getWalletAndCommission(email, currency, operationType, userRole);
    }

    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    @Override
    public DataTable<List<OrderBasicInfoDto>> searchOrdersByAdmin(AdminOrderFilterData adminOrderFilterData, DataTableParams dataTableParams, Locale locale) {

        PagingData<List<OrderBasicInfoDto>> searchResult = orderDao.searchOrders(adminOrderFilterData, dataTableParams, locale);
        DataTable<List<OrderBasicInfoDto>> output = new DataTable<>();
        output.setData(searchResult.getData());
        output.setRecordsTotal(searchResult.getTotal());
        output.setRecordsFiltered(searchResult.getFiltered());
        return output;
    }

    @Override
    public List<OrderReportInfoDto> getOrdersForReport(AdminOrderFilterData adminOrderFilterData) {
        adminOrderFilterData.initFilterItems();
        return orderDao.getOrdersForReport(adminOrderFilterData);
    }

    @Override
    public List<OrderWideListDto> getUsersOrdersWithStateForAdmin(String email, CurrencyPair currencyPair, OrderStatus status,
                                                                  OperationType operationType,
                                                                  Integer offset, Integer limit, Locale locale) {
        List<OrderWideListDto> result = orderDao.getMyOrdersWithState(userService.getIdByEmail(email), currencyPair, status, operationType, SCOPE, offset, limit, locale);

        return result;
    }

    @Override
    public List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, OrderStatus status,
                                                       OperationType operationType, String scope,
                                                       Integer offset, Integer limit, Locale locale) {
        return orderDao.getMyOrdersWithState(userService.getIdByEmail(email), currencyPair, status, operationType, scope, offset, limit, locale);
    }

    @Override
    public List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, List<OrderStatus> statuses,
                                                       OperationType operationType,
                                                       Integer offset, Integer limit, Locale locale) {
        return orderDao.getMyOrdersWithState(userService.getIdByEmail(email), currencyPair, statuses, operationType, null, offset, limit, locale);
    }


    @Override
    public List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(String email,
                                                                   BackDealInterval backDealInterval,
                                                                   Integer limit, CurrencyPair currencyPair, Locale locale) {
        List<OrderAcceptedHistoryDto> result = orderDao.getOrderAcceptedForPeriod(email, backDealInterval, limit, currencyPair);
        result.forEach(e -> {
            e.setRate(BigDecimalProcessing.formatLocale(e.getRate(), locale, true));
            e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
        });
        return result;
    }


    @Override
    public List<OrderListDto> getAllBuyOrders(CurrencyPair currencyPair, Locale locale) {
        List<OrderListDto> result = orderDao.getOrdersBuyForCurrencyPair(currencyPair, null);
        result.forEach(e -> {
            e.setExrate(BigDecimalProcessing.formatLocale(e.getExrate(), locale, 2));
            e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
            e.setAmountConvert(BigDecimalProcessing.formatLocale(e.getAmountConvert(), locale, true));
        });
        return result;
    }


    @Override
    public List<OrderListDto> getAllSellOrders(CurrencyPair currencyPair, Locale locale) {
        List<OrderListDto> result = orderDao.getOrdersSellForCurrencyPair(currencyPair, null);
        result.forEach(e -> {
            e.setExrate(BigDecimalProcessing.formatLocale(e.getExrate(), locale, 2));
            e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
            e.setAmountConvert(BigDecimalProcessing.formatLocale(e.getAmountConvert(), locale, true));
        });
        return result;
    }


    @Transactional
    Object deleteOrder(int orderId, OrderStatus newOrderStatus, OrderActionEnum action, List<OrderEvent> acceptEventsList, boolean forPartialAccept) {
        List<OrderDetailDto> list = walletService.getOrderRelatedDataAndBlock(orderId);
        if (list.isEmpty()) {
            return OrderDeleteStatus.NOT_FOUND;
        }
        int processedRows = 1;
        /**/
        OrderStatus currentOrderStatus = list.get(0).getOrderStatus();
        String description = transactionDescription.get(currentOrderStatus, action);
        /**/
        if (!setStatus(orderId, newOrderStatus)) {
            return OrderDeleteStatus.ORDER_UPDATE_ERROR;
        }
        /**/
        for (OrderDetailDto orderDetailDto : list) {
            if (currentOrderStatus == OrderStatus.CLOSED) {
                if (orderDetailDto.getCompanyCommission().compareTo(BigDecimal.ZERO) != 0) {
                    Integer companyWalletId = orderDetailDto.getCompanyWalletId();
                    if (companyWalletId != 0 && !companyWalletService.substractCommissionBalanceById(companyWalletId, orderDetailDto.getCompanyCommission())) {
                        return OrderDeleteStatus.COMPANY_WALLET_UPDATE_ERROR;
                    }
                }
                /**/
                WalletOperationData walletOperationData = new WalletOperationData();
                OperationType operationType = null;
                if (orderDetailDto.getTransactionType() == OperationType.OUTPUT) {
                    operationType = OperationType.INPUT;
                } else if (orderDetailDto.getTransactionType() == OperationType.INPUT) {
                    operationType = OperationType.OUTPUT;
                }
                if (operationType != null) {
                    walletOperationData.setOperationType(operationType);
                    walletOperationData.setWalletId(orderDetailDto.getUserWalletId());
                    walletOperationData.setAmount(orderDetailDto.getTransactionAmount());
                    walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
                    Commission commission = commissionDao.getDefaultCommission(OperationType.STORNO);
                    walletOperationData.setCommission(commission);
                    walletOperationData.setCommissionAmount(commission.getValue());
                    walletOperationData.setSourceType(TransactionSourceType.ORDER);
                    walletOperationData.setSourceId(orderId);
                    walletOperationData.setDescription(description);
                    WalletTransferStatus walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
                    if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                        return OrderDeleteStatus.TRANSACTION_CREATE_ERROR;
                    }
                }
                log.debug("rows before refs {}", processedRows);
                int processedRefRows = this.unprocessReferralTransactionByOrder(orderDetailDto.getOrderId(), description);
                processedRows = processedRefRows + processedRows;
                log.debug("rows after refs {}", processedRows);
                /**/
                if (!transactionService.setStatusById(
                        orderDetailDto.getTransactionId(),
                        TransactionStatus.DELETED.getStatus())) {
                    return OrderDeleteStatus.TRANSACTION_UPDATE_ERROR;
                }
                /**/
                processedRows++;
            } else if (currentOrderStatus == OrderStatus.OPENED) {
                WalletTransferStatus walletTransferStatus = walletService.walletInnerTransfer(
                        orderDetailDto.getOrderCreatorReservedWalletId(),
                        orderDetailDto.getOrderCreatorReservedAmount(),
                        TransactionSourceType.ORDER,
                        orderId,
                        description);
                if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                    return OrderDeleteStatus.TRANSACTION_CREATE_ERROR;
                }
                /**/
                if (!transactionService.setStatusById(
                        orderDetailDto.getTransactionId(),
                        TransactionStatus.DELETED.getStatus())) {
                    return OrderDeleteStatus.TRANSACTION_UPDATE_ERROR;
                }
            }
        }
        ExOrder exOrder = getOrderById(orderId);
        if (currentOrderStatus.equals(OrderStatus.OPENED)) {
            eventPublisher.publishEvent(new CancelOrderEvent(exOrder, true, true));
        }
        if(!forPartialAccept) {
            eventPublisher.publishEvent(new CancelDetailorderEvent(exOrder, false, true, exOrder.getCurrencyPairId()));
        } else {
            acceptEventsList.add(new CancelOrderEvent(exOrder, false, false));
        }
        return processedRows;
    }


    private int unprocessReferralTransactionByOrder(int orderId, String description) {
        List<Transaction> transactions = transactionService.getPayedRefTransactionsByOrderId(orderId);
        for (Transaction transaction : transactions) {
            WalletTransferStatus walletTransferStatus = null;
            try {
                WalletOperationData walletOperationData = new WalletOperationData();
                walletOperationData.setWalletId(transaction.getUserWallet().getId());
                walletOperationData.setAmount(transaction.getAmount());
                walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
                walletOperationData.setCommission(transaction.getCommission());
                walletOperationData.setCommissionAmount(transaction.getCommissionAmount());
                walletOperationData.setSourceType(TransactionSourceType.REFERRAL);
                walletOperationData.setSourceId(transaction.getSourceId());
                walletOperationData.setDescription(description);
                walletOperationData.setOperationType(OperationType.OUTPUT);
                walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
                referralService.setRefTransactionStatus(ReferralTransactionStatusEnum.DELETED, transaction.getSourceId());
                companyWalletService.substractCommissionBalanceById(transaction.getCompanyWallet().getId(), transaction.getAmount().negate());
            } catch (Exception e) {
                log.error("error unprocess ref transactions" + e);
            }
            log.debug("status " + walletTransferStatus);
            if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                throw new RuntimeException("can't unprocess referral transaction for order " + orderId);
            }
        }
        log.debug("end unprocess refs ");
        return transactions.size();
    }

    @Override
    public List<UserSummaryOrdersByCurrencyPairsDto> getUserSummaryOrdersByCurrencyPairList(Integer requesterUserId, String startDate, String endDate, List<Integer> roles) {
        return orderDao.getUserSummaryOrdersByCurrencyPairList(requesterUserId, startDate, endDate, roles);
    }


    @Override
    public String getOrdersForRefresh(Integer pairId, OperationType operationType, UserRole userRole) {
        CurrencyPair cp = currencyService.findCurrencyPairById(pairId);
        List<OrderListDto> dtos;
        switch (operationType) {
            case BUY: {
                dtos = getAllBuyOrdersEx(cp, Locale.ENGLISH, userRole);
                break;
            }
            case SELL: {
                dtos = getAllSellOrdersEx(cp, Locale.ENGLISH, userRole);
                break;
            }
            default:
                return null;
        }
        try {
            return objectMapper.writeValueAsString(Arrays.asList(new OrdersListWrapper(dtos, operationType.name(), pairId)));
        } catch (JsonProcessingException e) {
            log.error(e);
            return null;
        }
    }


    @Override
    public BiTuple<String, String> getTradesForRefresh(Integer pairId, String email, RefreshObjectsEnum refreshObjectEnum) {
        CurrencyPair cp = currencyService.findCurrencyPairById(pairId);
        List<OrderAcceptedHistoryDto> dtos = this.getOrderAcceptedForPeriodEx(email,
                new BackDealInterval("24 HOUR"),
                50,
                cp,
                Locale.ENGLISH);
        try {
            String responseForOldFront = new JSONArray() {{
                put(objectMapper.writeValueAsString(new OrdersListWrapper(dtos, refreshObjectEnum.name(), pairId)));
            }}.toString();
            String dtosOnly = objectMapper.writeValueAsString(dtos);
            return new BiTuple(dtosOnly, responseForOldFront);
        } catch (JsonProcessingException e) {
            log.error(e);
            return null;
        }
    }

    @Override
    public String getAllAndMyTradesForInit(int pairId, Principal principal) throws JsonProcessingException {
        CurrencyPair cp = currencyService.findCurrencyPairById(pairId);
        List<OrderAcceptedHistoryDto> dtos = this.getOrderAcceptedForPeriodEx(null,
                new BackDealInterval("24 HOUR"),
                50,
                cp,
                Locale.ENGLISH);
        JSONArray jsonArray = new JSONArray() {{
            put(objectMapper.writeValueAsString(new OrdersListWrapper(dtos, RefreshObjectsEnum.ALL_TRADES.name(), pairId)));
        }};
        if (principal != null) {
            List<OrderAcceptedHistoryDto> myDtos = this.getOrderAcceptedForPeriodEx(principal.getName(),
                    new BackDealInterval("24 HOUR"),
                    50,
                    cp,
                    Locale.ENGLISH);
            jsonArray.put(objectMapper.writeValueAsString(new OrdersListWrapper(myDtos, RefreshObjectsEnum.MY_TRADES.name(), pairId)));
        }
        return jsonArray.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BigDecimal> getLastOrderPriceByCurrencyPairAndOperationType(CurrencyPair currencyPair, OperationType operationType) {
        return orderDao.getLastOrderPriceByCurrencyPairAndOperationType(currencyPair.getId(), operationType.getType());
    }

    @Transactional(transactionManager = "slaveTxManager")
    @Override
    public String getChartData(Integer currencyPairId, final BackDealInterval backDealInterval) {
        CurrencyPair cp = currencyService.findCurrencyPairById(currencyPairId);
        List<CandleChartItemDto> rows = this.getDataForCandleChart(cp, backDealInterval);
        ArrayList<List> arrayListMain = new ArrayList<>();
        /*in first row return backDealInterval - to synchronize period menu with it*/
        arrayListMain.add(new ArrayList<Object>() {{
            add(backDealInterval);
        }});
        for (CandleChartItemDto candle : rows) {
            ArrayList<Object> arrayList = new ArrayList<>();
            /*values*/
            arrayList.add(candle.getBeginDate().toString());
            arrayList.add(candle.getEndDate().toString());
            arrayList.add(candle.getOpenRate());
            arrayList.add(candle.getCloseRate());
            arrayList.add(candle.getLowRate());
            arrayList.add(candle.getHighRate());
            arrayList.add(candle.getBaseVolume());
            arrayListMain.add(arrayList);
        }
        try {
            return objectMapper.writeValueAsString(new OrdersListWrapper(arrayListMain,
                    backDealInterval.getInterval(), currencyPairId));
        } catch (JsonProcessingException e) {
            log.error(e);
            return null;
        }
    }

    @Override
    public String getAllCurrenciesStatForRefresh(RefreshObjectsEnum refreshObjectsEnum) {
        OrdersListWrapper wrapper = new OrdersListWrapper(this.getOrdersStatisticByPairsEx(refreshObjectsEnum),
                refreshObjectsEnum.name());
        try {
            return new JSONArray() {{
                put(objectMapper.writeValueAsString(wrapper));
            }}.toString();
        } catch (JsonProcessingException e) {
            log.error(e);
            return null;
        }
    }

    @Override
    public String getAllCurrenciesStatForRefreshForAllPairs() {
        OrdersListWrapper wrapper = new OrdersListWrapper(this.processStatistic(exchangeRatesHolder.getAllRates()),
                RefreshObjectsEnum.CURRENCIES_STATISTIC.name());
        try {
            return new JSONArray() {{
                put(objectMapper.writeValueAsString(wrapper));
            }}.toString();
        } catch (JsonProcessingException e) {
            log.error(e);
            return null;
        }
    }

    @Override
    public Map<RefreshObjectsEnum, String> getSomeCurrencyStatForRefresh(List<Integer> currencyIds) {
        logger.debug("curencies for refresh size " + currencyIds.size());
        List<ExOrderStatisticsShortByPairsDto> dtos = this.getStatForSomeCurrencies(currencyIds);
        List<ExOrderStatisticsShortByPairsDto> icos = dtos
                .stream()
                .filter(p -> p.getType() == CurrencyPairType.ICO)
                .collect(Collectors.toList());
        List<ExOrderStatisticsShortByPairsDto> mains = dtos
                .stream()
                .filter(p -> p.getType() == CurrencyPairType.MAIN)
                .collect(Collectors.toList());
        Map<RefreshObjectsEnum, String> res = new HashMap<>();
        if (!icos.isEmpty()) {
            OrdersListWrapper wrapper = new OrdersListWrapper(icos, RefreshObjectsEnum.ICO_CURRENCY_STATISTIC.name());
            res.put(RefreshObjectsEnum.ICO_CURRENCY_STATISTIC, new JSONArray() {{
                try {
                    put(objectMapper.writeValueAsString(wrapper));
                } catch (JsonProcessingException e) {
                    logger.error(e);
                }
            }}.toString());
        }
        if (!mains.isEmpty()) {
            OrdersListWrapper wrapper = new OrdersListWrapper(mains, RefreshObjectsEnum.MAIN_CURRENCY_STATISTIC.name());
            res.put(RefreshObjectsEnum.MAIN_CURRENCY_STATISTIC, new JSONArray() {{
                try {
                    put(objectMapper.writeValueAsString(wrapper));
                } catch (JsonProcessingException e) {
                    log.error(e);
                }
            }}.toString());
        }
        return res;
    }

    private List<ExOrderStatisticsShortByPairsDto> processStatistic(List<ExOrderStatisticsShortByPairsDto> statisticList) {
        statisticList = Stream.of(
                statisticList
                        .stream()
                        .filter(p -> !new BigDecimal(p.getLastOrderRate()).equals(BigDecimal.ZERO)),
                statisticList
                        .stream()
                        .filter(p -> new BigDecimal(p.getLastOrderRate()).equals(BigDecimal.ZERO)))
                .flatMap(p -> p)
                .collect(Collectors.toList());
        setStatisitcValues(statisticList);
        return statisticList;
    }

    private void setStatisitcValues(List<ExOrderStatisticsShortByPairsDto> ordersList) {
        Locale locale = Locale.ENGLISH;
        ordersList.forEach(e -> {
            BigDecimal lastRate = new BigDecimal(e.getLastOrderRate());
            BigDecimal predLastRate = e.getPredLastOrderRate() == null ? lastRate : new BigDecimal(e.getPredLastOrderRate());
            e.setLastOrderRate(BigDecimalProcessing.formatLocaleFixedSignificant(lastRate, locale, 12));
            e.setPredLastOrderRate(BigDecimalProcessing.formatLocaleFixedSignificant(predLastRate, locale, 12));
            BigDecimal percentChange;
            if (predLastRate.compareTo(BigDecimal.ZERO) == 0) {
                percentChange = BigDecimal.ZERO;
            } else {
                percentChange = BigDecimalProcessing.doAction(predLastRate, lastRate, ActionType.PERCENT_GROWTH);
            }
            e.setPercentChange(BigDecimalProcessing.formatLocaleFixedDecimal(percentChange, locale, 2));
        });
    }


    @Override
    public Map<OrderType, List<OrderBookItem>> getOrderBook(String currencyPairName, @Nullable OrderType orderType) {
        Integer currencyPairId = currencyService.findCurrencyPairIdByName(currencyPairName);
        if (orderType != null) {
            return Collections.singletonMap(orderType, orderDao.getOrderBookItemsForType(currencyPairId, orderType));
        } else {
            Map<OrderType, List<OrderBookItem>> result = orderDao.getOrderBookItems(currencyPairId)
                    .stream()
                    .collect(Collectors.groupingBy(OrderBookItem::getOrderType));
            result.forEach((key, value) -> value.sort(Comparator.comparing(OrderBookItem::getRate, key.getBenefitRateComparator())));
            return result;
        }

    }

    @Transactional(readOnly = true)
    @Override
    public List<TradeHistoryDto> getTradeHistory(String currencyPairName,
                                                 @NotNull LocalDate fromDate,
                                                 @NotNull LocalDate toDate,
                                                 @Null Integer limit,
                                                 @Null String direction) {
        final Integer currencyPairId = currencyService.findCurrencyPairIdByName(currencyPairName);

        return orderDao.getTradeHistory(
                currencyPairId,
                LocalDateTime.of(fromDate, LocalTime.MIN),
                LocalDateTime.of(toDate, LocalTime.MAX),
                limit,
                direction);
    }

    @Override
    public List<UserOrdersDto> getUserOpenOrders(@Nullable String currencyPairName) {
        logTransaction("getUserOpenOrders", "begin", -1, -1, null);
        Integer userId = userService.getIdByEmail(getUserEmailFromSecurityContext());
        Integer currencyPairId = currencyPairName == null ? null : currencyService.findCurrencyPairIdByName(currencyPairName);
        List<UserOrdersDto> orders = orderDao.getUserOpenOrders(userId, currencyPairId);
        logTransaction("getUserOpenOrders", "end", currencyPairId, -1, orders);
        return orders;

    }

    @Override
    public List<UserOrdersDto> getUserClosedOrders(@Null String currencyPairName,
                                                   @Null Integer limit,
                                                   @Null Integer offset) {
        final Integer userId = userService.getIdByEmail(getUserEmailFromSecurityContext());

        Integer currencyPairId = isNull(currencyPairName) ? null : currencyService.findCurrencyPairIdByName(currencyPairName);
        int queryLimit = limit == null ? ORDERS_QUERY_DEFAULT_LIMIT : limit;
        int queryOffset = offset == null ? 0 : offset;

        return orderDao.getUserOrdersByStatus(userId, currencyPairId, OrderStatus.CLOSED, queryLimit, queryOffset);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserOrdersDto> getUserCanceledOrders(@Null String currencyPairName,
                                                     @Null Integer limit,
                                                     @Null Integer offset) {
        final Integer userId = userService.getIdByEmail(getUserEmailFromSecurityContext());

        Integer currencyPairId = isNull(currencyPairName) ? null : currencyService.findCurrencyPairIdByName(currencyPairName);
        int queryLimit = limit == null ? ORDERS_QUERY_DEFAULT_LIMIT : limit;
        int queryOffset = offset == null ? 0 : offset;

        return orderDao.getUserOrdersByStatus(userId, currencyPairId, OrderStatus.CANCELLED, queryLimit, queryOffset);
    }

    @Transactional(readOnly = true)
    public List<UserOrdersDto> getAllUserOrders(@Null String currencyPairName,
                                                @Null Integer limit,
                                                @Null Integer offset) {
        final int userId = userService.getIdByEmail(getUserEmailFromSecurityContext());

        Integer currencyPairId = isNull(currencyPairName) ? null : currencyService.findCurrencyPairIdByName(currencyPairName);
        int queryLimit = limit == null ? ORDERS_QUERY_DEFAULT_LIMIT : limit;
        int queryOffset = offset == null ? 0 : offset;
        return orderDao.getUserOrders(userId, currencyPairId, queryLimit, queryOffset);
    }

    private String getUserEmailFromSecurityContext() {
        return userService.getUserEmailFromSecurityContext();
    }

    @Override
    public List<OpenOrderDto> getOpenOrders(String currencyPairName, OrderType orderType) {
        Integer currencyPairId = currencyService.findCurrencyPairIdByName(currencyPairName);
        return orderDao.getOpenOrders(currencyPairId, orderType);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserTradeHistoryDto> getUserTradeHistoryByCurrencyPair(String currencyPairName,
                                                                       @NotNull LocalDate fromDate,
                                                                       @NotNull LocalDate toDate,
                                                                       @Null Integer limit) {
        final Integer currencyPairId = currencyService.findCurrencyPairIdByName(currencyPairName);
        final Integer userId = userService.getIdByEmail(getUserEmailFromSecurityContext());

        return orderDao.getUserTradeHistoryByCurrencyPair(
                userId,
                currencyPairId,
                LocalDateTime.of(fromDate, LocalTime.MIN),
                LocalDateTime.of(toDate, LocalTime.MAX),
                limit);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransactionDto> getOrderTransactions(Integer orderId) {
        final Integer userId = userService.getIdByEmail(getUserEmailFromSecurityContext());

        return orderDao.getOrderTransactions(userId, orderId);
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<CurrencyPairTurnoverReportDto> getCurrencyPairTurnoverByPeriodAndRoles(LocalDateTime startTime,
                                                                                       LocalDateTime endTime,
                                                                                       List<UserRole> roles) {
        return orderDao.getCurrencyPairTurnoverByPeriodAndRoles(startTime, endTime, roles);
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public Map<String, List<UserSummaryOrdersDto>> getUserSummaryOrdersData(LocalDateTime startTime,
                                                                            LocalDateTime endTime,
                                                                            List<UserRole> userRoles,
                                                                            int requesterId) {
        Map<String, List<UserSummaryOrdersDto>> summary = new HashMap<>();
        summary.put(BUY, orderDao.getUserBuyOrdersDataByPeriodAndRoles(startTime, endTime, userRoles, requesterId)
                .stream()
                .filter(userSummaryOrdersDto -> !userSummaryOrdersDto.isEmpty())
                .collect(Collectors.toList()));
        summary.put(SELL, orderDao.getUserSellOrdersDataByPeriodAndRoles(startTime, endTime, userRoles, requesterId)
                .stream()
                .filter(userSummaryOrdersDto -> !userSummaryOrdersDto.isEmpty())
                .collect(Collectors.toList()));
        return summary;
    }

    @Override
    public void logCallBackData(CallBackLogDto callBackLogDto) {
        callBackDao.logCallBackData(callBackLogDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Pair<Integer, List<OrderWideListDto>> getMyOrdersWithStateMap(OrderFilterDataDto filterDataDto, Locale locale) {

        int recordsCount = orderDao.getMyOrdersWithStateCount(filterDataDto);

        List<OrderWideListDto> orders = Collections.emptyList();
        if (recordsCount > 0) {
            orders = orderDao.getMyOrdersWithState(filterDataDto, locale);
        }
        return Pair.of(recordsCount, orders);
    }

    @Transactional
    @Override
    public boolean cancelOrders(Collection<Integer> orderIds) {
        return orderIds
                .stream()
                .allMatch(this::cancelOrder);
    }

    @Override
    public List<OrderWideListDto> getOrdersForExcel(Integer userId, CurrencyPair currencyPair, OrderStatus status, String scope, boolean hideCanceled, Locale locale, LocalDate dateFrom, LocalDate dateTo) {
        return orderDao.getAllOrders(userId, status, currencyPair, locale, scope, dateFrom, dateTo, hideCanceled);
    }

    @Override
    public void getExcelFile(List<OrderWideListDto> orders, OrderStatus orderStatus, HttpServletResponse response) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orders");

        Row headerRow = sheet.createRow(0);
        if (orderStatus == OrderStatus.OPENED) {
            headerRow.createCell(0).setCellValue("Order id");
            headerRow.createCell(1).setCellValue("Date created");
            headerRow.createCell(2).setCellValue("Market");
            headerRow.createCell(3).setCellValue("Type");
            headerRow.createCell(4).setCellValue("Amount");
            headerRow.createCell(5).setCellValue("Price");
            headerRow.createCell(6).setCellValue("Commission perсent");
            headerRow.createCell(7).setCellValue("Commission value");
            headerRow.createCell(8).setCellValue("In total");
        } else if (orderStatus == OrderStatus.CLOSED) {
            headerRow.createCell(0).setCellValue("Date");
            headerRow.createCell(1).setCellValue("Pair");
            headerRow.createCell(2).setCellValue("Order");
            headerRow.createCell(3).setCellValue("Type");
            headerRow.createCell(4).setCellValue("Price");
            headerRow.createCell(5).setCellValue("Amount");
            headerRow.createCell(6).setCellValue("Commission perсent");
            headerRow.createCell(7).setCellValue("Commission value");
            headerRow.createCell(8).setCellValue("In total");
        } else {
            throw new RuntimeException("Not supported");
        }

        int index = 1;

        try {
            for (OrderWideListDto order : orders) {
                Row row = sheet.createRow(index++);
                if (orderStatus == OrderStatus.OPENED) {
                    row.createCell(0, CellType.STRING).setCellValue(order.getId());
                    row.createCell(1, CellType.STRING).setCellValue(order.getDateCreation().toString());
                    row.createCell(2, CellType.STRING).setCellValue(order.getCurrencyPairName());
                    row.createCell(3, CellType.STRING).setCellValue(order.getOrderBaseType().toString());
                    row.createCell(4, CellType.STRING).setCellValue(order.getAmountBase());
                    row.createCell(5, CellType.STRING).setCellValue(order.getExExchangeRate());
                    row.createCell(6, CellType.STRING).setCellValue(order.getCommissionFixedAmount());
                    row.createCell(7, CellType.STRING).setCellValue(order.getCommissionValue());
                    row.createCell(8, CellType.STRING).setCellValue(order.getAmountWithCommission());
                } else {
                    String acceptDate = order.getDateAcception() != null ? order.getDateAcception().toString() : null;
                    row.createCell(0, CellType.STRING).setCellValue(acceptDate);
                    row.createCell(1, CellType.STRING).setCellValue(order.getCurrencyPairName());
                    row.createCell(2, CellType.STRING).setCellValue(order.getOrderBaseType().toString());
                    row.createCell(3, CellType.STRING).setCellValue(order.getOperationType());
                    row.createCell(4, CellType.STRING).setCellValue(order.getExExchangeRate());
                    row.createCell(5, CellType.STRING).setCellValue(order.getAmountBase());
                    row.createCell(6, CellType.STRING).setCellValue(order.getCommissionFixedAmount());
                    row.createCell(7, CellType.STRING).setCellValue(order.getCommissionValue());
                    row.createCell(8, CellType.STRING).setCellValue(order.getAmountWithCommission());
                }
            }

            StringBuilder fileName = new StringBuilder("Orders_")
                    .append(new SimpleDateFormat("yyyy_MM_dd").format(new Date()))
                    .append(".xlsx");

            response.setContentType("application/ms-excel");
            response.setHeader(CONTENT_DISPOSITION, ATTACHMENT + fileName.toString());

            OutputStream outStream = response.getOutputStream();
            workbook.write(outStream);
            outStream.flush();
            workbook.close();
        } catch (IOException e) {
            logger.error("Error creating excel file, e - {}", e.getMessage());
        }
    }

    @Override
    public void getTransactionExcelFile(List<MyInputOutputHistoryDto> transactions, HttpServletResponse response) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Status");
        headerRow.createCell(1).setCellValue("Date");
        headerRow.createCell(2).setCellValue("Currency");
        headerRow.createCell(3).setCellValue("Amount");
        headerRow.createCell(4).setCellValue("Type");
        headerRow.createCell(5).setCellValue("Address");
        headerRow.createCell(6).setCellValue("Status");

        try {
            int index = 1;
            if (transactions.isEmpty()) {
                Row row = sheet.createRow(index);
                row.createCell(0, CellType.STRING).setCellValue("");
                row.createCell(1, CellType.STRING).setCellValue("");
                row.createCell(2, CellType.STRING).setCellValue("");
                row.createCell(3, CellType.STRING).setCellValue("");
                row.createCell(4, CellType.STRING).setCellValue("");
                row.createCell(5, CellType.STRING).setCellValue("");
                row.createCell(6, CellType.STRING).setCellValue("");
            } else {
                for (MyInputOutputHistoryDto dto : transactions) {
                    Row row = sheet.createRow(index++);
                    row.createCell(0, CellType.STRING).setCellValue(getValue(dto.getStatus()));
                    row.createCell(1, CellType.STRING).setCellValue(getValue(dto.getDatetime()));
                    row.createCell(2, CellType.STRING).setCellValue(getValue(dto.getCurrencyName()));
                    row.createCell(3, CellType.STRING).setCellValue(getValue(dto.getAmount()));
                    row.createCell(4, CellType.STRING).setCellValue(getValue(dto.getSourceType()));
                    row.createCell(5, CellType.STRING).setCellValue(getValue(dto.getTransactionHash()));
                    row.createCell(6, CellType.STRING).setCellValue(getValue(dto.getStatus()));
                }
            }

            StringBuilder fileName = new StringBuilder("Transactions_")
                    .append(new SimpleDateFormat("MM_dd_yyyy").format(new Date()))
                    .append(".xlsx");

            response.setContentType("application/ms-excel");
            response.setHeader(CONTENT_DISPOSITION, ATTACHMENT + fileName.toString());

            OutputStream outStream = response.getOutputStream();
            workbook.write(outStream);
            outStream.flush();
            workbook.close();
        } catch (IOException e) {
            logger.error("Error creating excel file, e - {}", e.getMessage());
        }
    }

    private String getValue(Object value) {
        if (value == null) {
            return "";
        } else if (value instanceof LocalDate) {
            LocalDate date = (LocalDate) value;
            return DATE_TIME_FORMATTER.format(date);
        } else if (value instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime) value;
            return DATE_TIME_FORMATTER.format(localDateTime);
        }
        return String.valueOf(value);
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getAllCurrenciesMarkersForAllPairsModel() {
        return exchangeRatesHolder.getAllRates();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BigDecimal> getLastOrderPriceByCurrencyPair(CurrencyPair currencyPair) {
        return orderDao.getLastOrderPriceByCurrencyPair(currencyPair.getId());
    }

    @Override
    public List<OrdersListWrapper> getMyOpenOrdersForWs(String currencyPairName, String userName) {
        CurrencyPair cp = currencyService.getCurrencyPairByName(currencyPairName);
        Integer userId = userService.getIdByEmail(userName);
        if (cp == null) {
            return null;
        }
        List<OrderWsDetailDto> dtoSell = orderDao.getMyOpenOrdersForCurrencyPair(cp, OrderType.SELL, userId)
                .stream()
                .map(OrderWsDetailDto::new)
                .collect(Collectors.toList());
        List<OrderWsDetailDto> dtoBuy = orderDao.getMyOpenOrdersForCurrencyPair(cp, OrderType.BUY, userId)
                .stream()
                .map(OrderWsDetailDto::new)
                .collect(Collectors.toList());
        OrdersListWrapper sellOrders = new OrdersListWrapper(dtoSell, OperationType.SELL.name(), cp.getId());
        OrdersListWrapper buyOrders = new OrdersListWrapper(dtoBuy, OperationType.BUY.name(), cp.getId());
        return Arrays.asList(sellOrders, buyOrders);
    }

    @Override
    public OrderBookWrapperDto findAllOrderBookItems(OrderType orderType, Integer currencyId, int precision) {
        final MathContext context = new MathContext(8, RoundingMode.HALF_EVEN);
        List<OrderListDto> rawItems = orderDao.findAllByOrderTypeAndCurrencyId(currencyId, orderType)
                .stream()
                .peek(n -> n.setExrate(new BigDecimal(n.getExrate()).round(context).toPlainString()))
                .collect(Collectors.toList());
        List<SimpleOrderBookItem> simpleOrderBookItems = aggregateItems(orderType, rawItems, currencyId, precision);
        OrderBookWrapperDto dto = OrderBookWrapperDto
                .builder()
                .orderType(orderType)
                .orderBookItems(simpleOrderBookItems)
                .total(getWrapperTotal(simpleOrderBookItems))
                .build();
        ExOrderStatisticsShortByPairsDto statistic = exchangeRatesHolder.getOne(currencyId);
        if (nonNull(statistic)) {
            final BigDecimal lastOrderRate = new BigDecimal(statistic.getLastOrderRate());
            final BigDecimal predLastOrderRate = new BigDecimal(statistic.getPredLastOrderRate());

            dto.setLastExrate(safeFormatBigDecimal(lastOrderRate));
            dto.setPreLastExrate(safeFormatBigDecimal(predLastOrderRate));
            dto.setPositive(safeCompareBigDecimals(lastOrderRate, predLastOrderRate));
        }
        return dto;
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getDailyCoinmarketDataForCache(String currencyPairName) {
        return orderDao.getDailyCoinmarketDataForCache(currencyPairName);
    }

    @Override
    public Map<PrecissionsEnum, String> findAllOrderBookItemsForAllPrecissions(OrderType orderType, Integer currencyId, List<PrecissionsEnum> precissionsList) {
        final MathContext context = new MathContext(8, RoundingMode.HALF_EVEN);
        List<OrderListDto> rawItems = orderDao.findAllByOrderTypeAndCurrencyId(currencyId, orderType)
                .stream()
                .peek(n -> n.setExrate(new BigDecimal(n.getExrate()).round(context).toPlainString()))
                .collect(Collectors.toList());
        ExOrderStatisticsShortByPairsDto statistic = exchangeRatesHolder.getOne(currencyId);
        Map<PrecissionsEnum, String> result = new HashMap<>();
        precissionsList.forEach(p -> {
            try {
                List<SimpleOrderBookItem> simpleOrderBookItems = aggregateItems(orderType, rawItems, currencyId, p.getValue());
                OrderBookWrapperDto dto = OrderBookWrapperDto
                        .builder()
                        .orderType(orderType)
                        .orderBookItems(simpleOrderBookItems)
                        .total(getWrapperTotal(simpleOrderBookItems))
                        .build();
                if (nonNull(statistic)) {
                    final BigDecimal lastOrderRate = new BigDecimal(statistic.getLastOrderRate());
                    final BigDecimal predLastOrderRate = new BigDecimal(statistic.getPredLastOrderRate());

                    dto.setLastExrate(safeFormatBigDecimal(lastOrderRate));
                    dto.setPreLastExrate(safeFormatBigDecimal(predLastOrderRate));
                    dto.setPositive(safeCompareBigDecimals(lastOrderRate, predLastOrderRate));
                }
                result.put(p, objectMapper.writeValueAsString(Arrays.asList(dto)));
            } catch (Exception e) {
                log.error(e);
            }
        });
        return result;
    }

    private boolean safeCompareBigDecimals(BigDecimal last, BigDecimal beforeLast) {
        if (last == null) {
            return false;
        } else if (beforeLast == null) {
            return true;
        } else {
            return last.compareTo(beforeLast) > 0;
        }
    }

    private BigDecimal getWrapperTotal(List<SimpleOrderBookItem> items) {
        Optional<SimpleOrderBookItem> max = items
                .stream()
                .max(Comparator.comparing(SimpleOrderBookItem::getTotal));
        BigDecimal total = BigDecimal.ZERO;
        if (max.isPresent()) {
            total = max.get().getTotal();
        }
        return total;
    }

    private List<SimpleOrderBookItem> aggregateItems(OrderType orderType, List<OrderListDto> rawItems,
                                                     int currencyPairId, int precision) {
        MathContext mathContext = new MathContext(precision, RoundingMode.HALF_DOWN);
        Map<BigDecimal, List<OrderListDto>> groupByExrate = rawItems
                .stream()
                .collect(Collectors.groupingBy(item -> new BigDecimal(item.getExrate()).round(mathContext), Collectors.toList()));
        List<SimpleOrderBookItem> items = Lists.newArrayList();
        groupByExrate.forEach((key, value) -> items.add(SimpleOrderBookItem
                .builder()
                .exrate(new BigDecimal(key.toString()))
                .currencyPairId(currencyPairId)
                .orderType(orderType)
                .amount(getAmount(value))
                .build()));

        if (!items.isEmpty()) {
            if (orderType == OrderType.SELL) {
                items.sort(Comparator.comparing(SimpleOrderBookItem::getExrate));
            } else {
                items.sort((o1, o2) -> o2.getExrate().compareTo(o1.getExrate()));
            }
        }
        List<SimpleOrderBookItem> preparedItems = items.stream().limit(8).collect(Collectors.toList());
        setSumAmount(preparedItems);
        setTotal(preparedItems);
        return preparedItems;
    }

    private BigDecimal getAmount(List<OrderListDto> list) {
        BigDecimal amount = BigDecimal.ZERO;
        for (OrderListDto item : list) {
            amount = amount.add(new BigDecimal(item.getAmountBase()));
        }
        return amount;
    }

    private void setSumAmount(List<SimpleOrderBookItem> items) {
        for (int i = 0; i < items.size(); i++) {
            if (i == 0) {
                items.get(i).setSumAmount(items.get(i).getAmount());
            } else {
                BigDecimal add =
                        BigDecimalProcessing.doAction(items.get(i).getAmount(), items.get(i - 1).getSumAmount(), ActionType.ADD);
                items.get(i).setSumAmount(add);
            }
        }
    }

    private void setTotal(List<SimpleOrderBookItem> preparedItems) {
        for (int i = 0; i < preparedItems.size(); i++) {
            SimpleOrderBookItem item = preparedItems.get(i);
            if (i == 0) {
                BigDecimal total = BigDecimalProcessing.doAction(item.getAmount(), item.getExrate(), ActionType.MULTIPLY);
                item.setTotal(total);
                continue;
            }

            BigDecimal totalItem = BigDecimalProcessing.doAction(item.getAmount(), item.getExrate(), ActionType.MULTIPLY);
            BigDecimal prevTotal = item.getAmount().add(preparedItems.get(i - 1).getTotal());
            BigDecimal total = BigDecimalProcessing.doAction(prevTotal, totalItem, ActionType.ADD);
            item.setTotal(total);
        }
    }

    private String safeFormatBigDecimal(BigDecimal value) {
        if (value == null) {
            value = BigDecimal.ZERO;
        }
        value = BigDecimalProcessing.normalize(value);
        return BigDecimalProcessing.formatSpacePoint(value, false).replace(" ", "");
    }
}