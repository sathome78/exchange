package me.exrates.dao;

import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.PagingData;
import me.exrates.model.dto.CurrencyPairTurnoverReportDto;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.OrderBasicInfoDto;
import me.exrates.model.dto.OrderCommissionsDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderInfoDto;
import me.exrates.model.dto.OrderReportInfoDto;
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
import me.exrates.model.enums.OrderTableEnum;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.model.vo.OrderRoleInfoForDelete;

import java.math.BigDecimal;
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

    List<OrderListDto> getOrdersBuyForCurrencyPair(CurrencyPair currencyPair, UserRole filterRole);

    void postAcceptedOrderToDB(ExOrder exOrder);

    List<OrderListDto> getOrdersSellForCurrencyPair(CurrencyPair currencyPair, UserRole filterRole);

    ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval);

    OrderInfoDto getOrderInfo(int orderId, Locale locale);

    List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(String email, BackDealInterval backDealInterval, Integer limit, CurrencyPair currencyPair);

    OrderCommissionsDto getCommissionForOrder(UserRole userRole);

    CommissionsDto getAllCommissions(UserRole userRole);

    List<OrderWideListDto> getMyOrdersWithState(Integer userId, CurrencyPair currencyPair, OrderStatus status,
                                                OperationType operationType, String scope, Integer offset,
                                                Integer limit, Locale locale, UserRole userRole);

    int getUnfilteredOrdersCount(int id, CurrencyPair currencyPair, OrderStatus statuses, OperationType operationType,
                                 String scope, int offset, int limit, UserRole userRole);

    List<OrderWideListDto> getMyOrdersWithState(Integer userId, CurrencyPair currencyPair, String currencyName,
                                                OrderStatus orderStatus, String scope, Integer limit, Integer offset,
                                                Boolean hideCanceled, String sortByCreated,
                                                LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo, Locale locale, UserRole userRole);

    WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                    OperationType operationType, UserRole userRole);

    boolean lockOrdersListForAcception(List<Integer> ordersList);

    PagingData<List<OrderBasicInfoDto>> searchOrders(AdminOrderFilterData adminOrderFilterData, DataTableParams dataTableParams, Locale locale);

    List<OrderReportInfoDto> getOrdersForReport(AdminOrderFilterData adminOrderFilterData);

    List<ExOrder> selectTopOrders(Integer currencyPairId, BigDecimal exrate, OperationType orderType, boolean sameRoleOnly, Integer userAcceptorRoleId, OrderBaseType orderBaseType);

    OrderRoleInfoForDelete getOrderRoleInfo(int orderId);

    List<OrderBookItem> getOrderBookItemsForType(Integer currencyPairId, OrderType orderType);

    List<OrderBookItem> getOrderBookItems(Integer currencyPairId);

    List<OpenOrderDto> getOpenOrders(Integer currencyPairId, OrderType orderType);

    List<TradeHistoryDto> getTradeHistory(Integer currencyPairId, LocalDateTime fromDate, LocalDateTime toDate, Integer limit, String direction);

    List<UserOrdersDto> getUserOpenOrders(Integer userId, Integer currencyPairId);

    List<UserOrdersDto> getUserOrdersByStatus(Integer userId, Integer currencyPairId, OrderStatus status, int limit, int offset, UserRole role);

    List<UserTradeHistoryDto> getUserTradeHistoryByCurrencyPair(Integer userId, Integer currencyPairId, LocalDateTime fromDate, LocalDateTime toDate, Integer limit, UserRole userRole);

    List<ExOrder> getAllOpenedOrdersByUserId(Integer userId);

    List<ExOrder> getOpenedOrdersByCurrencyPair(Integer userId, String currencyPair);

    List<TransactionDto> getOrderTransactions(Integer userId, Integer orderId);

    List<CurrencyPairTurnoverReportDto> getCurrencyPairTurnoverByPeriodAndRoles(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> roles);

    List<UserSummaryOrdersDto> getUserBuyOrdersDataByPeriodAndRoles(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles, int requesterId);

    List<UserSummaryOrdersDto> getUserSellOrdersDataByPeriodAndRoles(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles, int requesterId);

    List<UserOrdersDto> getUserOrders(Integer userId, Integer currencyPairId, int queryLimit, int queryOffset);

    Integer getMyOrdersWithStateCount(Integer userId, CurrencyPair currencyPair, String currencyName, OrderStatus orderStatus,
                                      String scope, Boolean hideCanceled, LocalDateTime dateTimeFrom,
                                      LocalDateTime dateTimeTo, UserRole userRole);

    Optional<BigDecimal> getLastOrderPriceByCurrencyPair(int currencyPairId);

    List<OrderListDto> findAllByOrderTypeAndCurrencyId(Integer currencyId, OrderType... orderType);

    List<ExOrderStatisticsShortByPairsDto> getRatesDataForCache(Integer currencyPairId);

    ExOrderStatisticsShortByPairsDto getBeforeLastRateForCache(Integer currencyPairId);

    List<ExOrderStatisticsShortByPairsDto> getAllDataForCache(Integer currencyPairId);

    List<ExOrder> findAllMarketOrderCandidates(Integer currencyId, OperationType operationType);

    Integer deleteClosedExorders();

    OrderTableEnum getOrderTable(int id);
}
