package me.exrates.dao;

import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.PagingData;
import me.exrates.model.dto.CandleChartItemDto;
import me.exrates.model.dto.CoinmarketApiDto;
import me.exrates.model.dto.CurrencyPairTurnoverReportDto;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.OrderBasicInfoDto;
import me.exrates.model.dto.OrderCommissionsDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderInfoDto;
import me.exrates.model.dto.OrderReportInfoDto;
import me.exrates.model.dto.StatisticForMarket;
import me.exrates.model.dto.UserSummaryOrdersByCurrencyPairsDto;
import me.exrates.model.dto.UserSummaryOrdersDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminOrderFilterData;
import me.exrates.model.dto.mobileApiDto.dashboard.CommissionsDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.dto.openAPI.OpenOrderDto;
import me.exrates.model.dto.openAPI.OrderBookItem;
import me.exrates.model.dto.openAPI.TradeHistoryDto;
import me.exrates.model.dto.openAPI.TransactionDto;
import me.exrates.model.dto.openAPI.UserOrdersDto;
import me.exrates.model.dto.openAPI.UserTradeHistoryDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.model.vo.OrderRoleInfoForDelete;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public interface OrderDao {

    int createOrder(ExOrder order);

    List<OrderListDto> getMyOpenOrdersForCurrencyPair(CurrencyPair currencyPair, OrderType orderType, int userId);

    Optional<BigDecimal> getLastOrderPriceByCurrencyPairAndOperationType(int currencyPairId, int operationTypeId);

    Optional<BigDecimal> getLowestOpenOrderPriceByCurrencyPairAndOperationType(int currencyPairId, int operationTypeId);

    ExOrder getOrderById(int orderid);

    ExOrder getOrderById(int orderId, int userId);

    boolean setStatus(int orderId, OrderStatus status);

    boolean updateOrder(ExOrder exOrder);

    boolean updateOrder(int orderId, ExOrder order);

    List<OrderListDto> getOrdersBuyForCurrencyPair(CurrencyPair currencyPair, UserRole filterRole);

    void postAcceptedOrderToDB(ExOrder exOrder);

    List<OrderListDto> getOrdersSellForCurrencyPair(CurrencyPair currencyPair, UserRole filterRole);

    List<Map<String, Object>> getDataForAreaChart(CurrencyPair currencyPair, BackDealInterval backDealInterval);

    List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval backDealInterval);

    List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval backDealInterval, LocalDateTime endTime);

    List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, LocalDateTime startTime, LocalDateTime endTime, int resolutionValue, String resolutionType);

    ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval);

    List<ExOrderStatisticsShortByPairsDto> getOrderStatisticByPairs();

    List<ExOrderStatisticsShortByPairsDto> getOrderStatisticForSomePairs(List<Integer> pairsIds);

    List<CoinmarketApiDto> getCoinmarketData(String currencyPairName);

    OrderInfoDto getOrderInfo(int orderId, Locale locale);

    int searchOrderByAdmin(Integer currencyPair, Integer orderType, String orderDate, BigDecimal orderRate, BigDecimal orderVolume);

    List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(String email, BackDealInterval backDealInterval, Integer limit, CurrencyPair currencyPair);

    OrderCommissionsDto getCommissionForOrder(UserRole userRole);

    CommissionsDto getAllCommissions(UserRole userRole);

    List<OrderWideListDto> getMyOrdersWithState(Integer userId, CurrencyPair currencyPair, OrderStatus status,
                                                OperationType operationType,
                                                String scope, Integer offset, Integer limit, Locale locale);

    int getUnfilteredOrdersCount(int id, CurrencyPair currencyPair, List<OrderStatus> statuses, OperationType operationType,
                                 String scope, int offset, int limit, Locale locale);

    List<OrderWideListDto> getMyOrdersWithState(Integer userId, CurrencyPair currencyPair, List<OrderStatus> statuses,
                                                OperationType operationType,
                                                String scope, Integer offset, Integer limit, Locale locale);

    List<OrderWideListDto> getMyOrdersWithState(Integer userId, CurrencyPair currencyPair, String currencyName,
                                                OrderStatus orderStatus, String scope, Integer limit, Integer offset,
                                                Boolean hideCanceled, String sortByCreated,
                                                LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo, Locale locale);

    OrderCreateDto getMyOrderById(int orderId);

    WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                    OperationType operationType, UserRole userRole);

    boolean lockOrdersListForAcception(List<Integer> ordersList);

    PagingData<List<OrderBasicInfoDto>> searchOrders(AdminOrderFilterData adminOrderFilterData, DataTableParams dataTableParams, Locale locale);

    List<OrderReportInfoDto> getOrdersForReport(AdminOrderFilterData adminOrderFilterData);

    List<ExOrder> selectTopOrders(Integer currencyPairId, BigDecimal exrate, OperationType orderType, boolean sameRoleOnly, Integer userAcceptorRoleId, OrderBaseType orderBaseType);

    List<UserSummaryOrdersByCurrencyPairsDto> getUserSummaryOrdersByCurrencyPairList(Integer requesterUserId, String startDate, String endDate, List<Integer> roles);

    OrderRoleInfoForDelete getOrderRoleInfo(int orderId);

    List<OrderBookItem> getOrderBookItemsForType(Integer currencyPairId, OrderType orderType);

    List<OrderBookItem> getOrderBookItems(Integer currencyPairId);

    List<OpenOrderDto> getOpenOrders(Integer currencyPairId, OrderType orderType);

    List<TradeHistoryDto> getTradeHistory(Integer currencyPairId, LocalDateTime fromDate, LocalDateTime toDate, Integer limit, String direction);

    List<UserOrdersDto> getUserOpenOrders(Integer userId, Integer currencyPairId);

    List<UserOrdersDto> getUserOrdersByStatus(Integer userId, Integer currencyPairId, OrderStatus status, int limit, int offset);

    List<UserTradeHistoryDto> getUserTradeHistoryByCurrencyPair(Integer userId, Integer currencyPairId, LocalDateTime fromDate, LocalDateTime toDate, Integer limit);

    List<ExOrder> getAllOpenedOrdersByUserId(Integer userId);

    List<ExOrder> getOpenedOrdersByCurrencyPair(Integer userId, String currencyPair);

    List<TransactionDto> getOrderTransactions(Integer userId, Integer orderId);

    List<CurrencyPairTurnoverReportDto> getCurrencyPairTurnoverByPeriodAndRoles(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> roles);

    List<UserSummaryOrdersDto> getUserBuyOrdersDataByPeriodAndRoles(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles, int requesterId);

    List<UserSummaryOrdersDto> getUserSellOrdersDataByPeriodAndRoles(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles, int requesterId);

    List<UserOrdersDto> getUserOrders(Integer userId, Integer currencyPairId, int queryLimit, int queryOffset);

    Integer getMyOrdersWithStateCount(Integer userId, CurrencyPair currencyPair, String currencyName, OrderStatus orderStatus,
                                      String scope, Integer limit, Integer offset, Boolean hideCanceled, LocalDateTime dateTimeFrom,
                                      LocalDateTime dateTimeTo);

    List<OrderWideListDto> getAllOrders(Integer userId, OrderStatus status, CurrencyPair currencyPair, Locale locale,
                                        String scope, LocalDate dateFrom, LocalDate dateTo, boolean hideCanceled);

    List<StatisticForMarket> getOrderStatisticForNewMarkets();

    Optional<BigDecimal> getLastOrderPriceByCurrencyPair(int currencyPairId);

    List<OrderListDto> findAllByOrderTypeAndCurrencyId(Integer currencyId, OrderType... orderType);

    List<ExOrderStatisticsShortByPairsDto> getRatesDataForCache(Integer currencyPairId);

    ExOrderStatisticsShortByPairsDto getBeforeLastRateForCache(Integer currencyPairId);

    List<ExOrderStatisticsShortByPairsDto> getAllDataForCache(Integer currencyPairId);
}
