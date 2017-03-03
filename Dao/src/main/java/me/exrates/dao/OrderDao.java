package me.exrates.dao;

import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.PagingData;
import me.exrates.model.dto.*;
import me.exrates.model.dto.mobileApiDto.dashboard.CommissionsDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.UserRole;
import me.exrates.model.vo.BackDealInterval;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface OrderDao {

    int createOrder(ExOrder order);

    ExOrder getOrderById(int orderid);

    boolean setStatus(int orderId, OrderStatus status);

    boolean updateOrder(ExOrder exOrder);

    List<OrderListDto> getOrdersBuyForCurrencyPair(CurrencyPair currencyPair);

    List<OrderListDto> getOrdersSellForCurrencyPair(CurrencyPair currencyPair);

    List<Map<String, Object>> getDataForAreaChart(CurrencyPair currencyPair, BackDealInterval backDealInterval);

    List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval backDealInterval);

    ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval);

    List<ExOrderStatisticsShortByPairsDto> getOrderStatisticByPairs();

    List<CoinmarketApiDto> getCoinmarketData(String currencyPairName);

    OrderInfoDto getOrderInfo(int orderId, Locale locale);

    Object deleteOrderByAdmin(int orderId);

    int searchOrderByAdmin(Integer currencyPair, Integer orderType, String orderDate, BigDecimal orderRate, BigDecimal orderVolume);

    Object deleteOrderForPartialAccept(int orderId);

    List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(String email, BackDealInterval backDealInterval, Integer limit, CurrencyPair currencyPair);

    OrderCommissionsDto getCommissionForOrder(UserRole userRole);

    CommissionsDto getAllCommissions(UserRole userRole);

    List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, OrderStatus status,
                                                OperationType operationType,
                                                String scope, Integer offset, Integer limit, Locale locale);

    List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, List<OrderStatus> statuses,
                                                OperationType operationType,
                                                String scope, Integer offset, Integer limit, Locale locale);

    OrderCreateDto getMyOrderById(int orderId);

    WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                    OperationType operationType, UserRole userRole);

    boolean lockOrdersListForAcception(List<Integer> ordersList);

    PagingData<List<OrderBasicInfoDto>> searchOrders(Integer currencyPair, Integer orderId, Integer orderType, String orderDateFrom, String orderDateTo,
                                                     BigDecimal orderRateFrom, BigDecimal orderRateTo, BigDecimal orderVolumeFrom,
                                                     BigDecimal orderVolumeTo, String creatorEmail, String acceptorEmail, Locale locale,
                                                     int offset, int limit, String orderColumnName, String orderDirection);

    List<ExOrder> selectTopOrders(Integer currencyPairId, BigDecimal exrate,
                                  OperationType orderType);
}
