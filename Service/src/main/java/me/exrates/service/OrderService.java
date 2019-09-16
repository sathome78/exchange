package me.exrates.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.CurrencyPairWithRestriction;
import me.exrates.model.ExOrder;
import me.exrates.model.User;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.AdminOrderInfoDto;
import me.exrates.model.dto.CallBackLogDto;
import me.exrates.model.dto.CoinmarketApiDto;
import me.exrates.model.dto.CurrencyPairTurnoverReportDto;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.dto.OrderBasicInfoDto;
import me.exrates.model.dto.OrderBookWrapperDto;
import me.exrates.model.dto.OrderCommissionsDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderCreationResultDto;
import me.exrates.model.dto.OrderInfoDto;
import me.exrates.model.dto.OrderReportInfoDto;
import me.exrates.model.dto.OrderValidationDto;
import me.exrates.model.dto.OrdersListWrapper;
import me.exrates.model.dto.RefreshStatisticDto;
import me.exrates.model.dto.ReportDto;
import me.exrates.model.dto.UserSummaryOrdersByCurrencyPairsDto;
import me.exrates.model.dto.UserSummaryOrdersDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
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
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.PrecissionsEnum;
import me.exrates.model.enums.RefreshObjectsEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.ngModel.ResponseInfoCurrencyPairDto;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.model.vo.CacheData;
import me.exrates.service.util.BiTuple;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface OrderService {


    List<ExOrderStatisticsShortByPairsDto> getOrdersStatisticByPairsEx(RefreshObjectsEnum refreshObjectsEnum);

    List<ExOrderStatisticsShortByPairsDto> getStatForSomeCurrencies(Set<Integer> pairsIds);

    OrderCreateDto prepareNewOrder(CurrencyPairWithRestriction activeCurrencyPair, OperationType orderType, String userEmail, BigDecimal amount, BigDecimal rate, OrderBaseType baseType);

    OrderCreateDto prepareNewOrder(CurrencyPairWithRestriction activeCurrencyPair, OperationType orderType, String userEmail, BigDecimal amount, BigDecimal rate, Integer sourceId, OrderBaseType baseType);

    OrderValidationDto validateOrder(OrderCreateDto orderCreateDto, boolean fromDemo, User user);

    OrderValidationDto validateOrder(OrderCreateDto orderCreateDto);

    @Transactional
    String createOrder(OrderCreateDto orderCreateDto, OrderActionEnum action, Locale locale);

    @Transactional
    Integer createOrderByStopOrder(OrderCreateDto orderCreateDto, OrderActionEnum action, Locale locale);

    /**
     * Returns the ID of the newly created and saved in DB order
     * Generates transaction of transferring money from active balance to reserved balance the corresponding wallet
     *
     * @param order OrderCreateDto, that passed from frontend and that will be converted to entity ExOrder to save in DB
     * @return generated ID of the newly created order, or 0 if order was not be created
     */
    int createOrder(OrderCreateDto order, OrderActionEnum action, List<ExOrder> eventsList, boolean sendEvent);

    int createOrder(OrderCreateDto orderCreateDto, OrderActionEnum action);

    @Transactional
    void postBotOrderToDb(OrderCreateDto orderCreateDto);

    @Transactional
    OrderCreateDto prepareOrderRest(OrderCreationParamsDto orderCreationParamsDto, String userEmail, Locale locale, OrderBaseType orderBaseType);

    @Transactional
    OrderCreationResultDto createPreparedOrderRest(OrderCreateDto orderCreateDto, Locale locale);

    @Transactional
    OrderCreationResultDto prepareAndCreateOrderRest(String currencyPairName, OperationType orderType,
                                                     BigDecimal amount, BigDecimal exrate, String userEmail);


    @Transactional
    String createMarketOrder(OrderCreateDto orderCreateDto);

    OrderCreateDto prepareMarketOrder(InputCreateOrderDto inputOrder);

    Optional<String> autoAccept(OrderCreateDto orderCreateDto, Locale locale);

    @Transactional(rollbackFor = Exception.class)
    Optional<OrderCreationResultDto> autoAcceptMarketOrders(OrderCreateDto orderCreateDto, Locale locale);

    Optional<OrderCreationResultDto> autoAcceptOrders(OrderCreateDto orderCreateDto, Locale locale);

    /**
     * TODO ADD JAVADOC
     */
    List<OrderWideListDto> getMyOrdersWithState(
            CacheData cacheData,
            String email, CurrencyPair currencyPair, OrderStatus status,
            OperationType operationType,
            String scope, Integer offset, Integer limit, Locale locale);

    /**
     * TODO ADD JAVADOC
     */
    OrderCreateDto getMyOrderById(int orderId);

    /**
     * Returns entity ExOrder by its ID
     *
     * @param orderId
     * @return entity ExOrder for found order, or null if order not found
     */
    ExOrder getOrderById(int orderId);

    /**
     * Returns entity ExOrder by its ID and userId
     *
     * @param orderId
     * @param userId
     * @return entity ExOrder for found order, or null if order not found
     */
    ExOrder getOrderById(int orderId, int userId);

    /**
     * Sets new status for existing order with given ID.
     *
     * @param orderId
     * @param status
     * @return
     */
    boolean setStatus(int orderId, OrderStatus status);

    @Transactional
    void acceptOrder(String userEmail, Integer orderId);

    /**
     * Accepts the list of orders
     * The method <b>acceptOrdersList</b> is used to accept each orders from <b>ordersList</b>
     * Before the method <b>acceptOrdersList</b will be called, this method tries to lock the <b>ordersList</b>
     * If not success lock, the OrderAcceptionException will be thrown.
     *
     * @param userAcceptorId is ID of acceptor-user
     * @param ordersList     is list the ID of order that must be accepted
     * @param locale         is current locale. Used to generate messages
     */
    void acceptOrdersList(int userAcceptorId, List<Integer> ordersList, Locale locale, List<ExOrder> eventsList, boolean partialAccept);

    void acceptOrderByAdmin(String acceptorEmail, Integer orderId, Locale locale);

    void acceptManyOrdersByAdmin(String acceptorEmail, List<Integer> orderIds, Locale locale);


    @Transactional
    boolean cancelOrder(Integer orderId);

    boolean cancelOpenOrdersByCurrencyPair(String currencyPair);

    @Transactional
    boolean cancelAllOpenOrders();

    /**
     * Cancels the order and set status "CANCELLED"
     * Only order with status "OPENED" can be cancelled
     * This method for cancel order by creator-user
     *
     * @param exOrder is the entity ExOrder of order that must be cancelled
     * @return "true" if the order can be cancelled and has been cancelled successfully, "false" in other cases
     */
    boolean cancelOrder(ExOrder exOrder, Locale locale, List<ExOrder> acceptEventsList, boolean forPartialAccept);

    /**
     * Updates order's fields:
     * - user_acceptor_id
     * - status_id
     * - date_acception
     * Used while accepting process the order
     *
     * @param exOrder
     * @return "true" if order was updated successfully, or "false" if not
     */
    boolean updateOrder(ExOrder exOrder);

    /**
     * Returns data for CoinMarketCap API
     *
     * @param currencyPairName
     * @param backDealInterval
     * @return list the CoinmarketApiDto, which consists info about currency pairs according to API
     */
    List<CoinmarketApiDto> getCoinmarketData(String currencyPairName, BackDealInterval backDealInterval);

    List<CoinmarketApiDto> getCoinmarketDataForActivePairs(String currencyPairName, BackDealInterval backDealInterval);

    List<CoinmarketApiDto> getDailyCoinmarketData(String currencyPairName);

    List<CoinmarketApiDto> getHourlyCoinmarketData(String currencyPairName);

    /**
     * Returns detailed info about the order, including info from related transactions
     *
     * @param orderId is ID the order
     * @return OrderInfoDto containing detailed info about the order
     */
    OrderInfoDto getOrderInfo(int orderId, Locale locale);

    @Transactional
    AdminOrderInfoDto getAdminOrderInfo(int orderId, Locale locale);

    void deleteManyOrdersByAdmin(List<Integer> orderIds);

    Object deleteOrderByAdmin(int orderId);

    Object deleteOrderForPartialAccept(int orderId, List<ExOrder> acceptEventsList);

    /**
     * Searches order by its params:
     *
     * @param currencyPair
     * @param orderType
     * @param orderDate
     * @param orderRate
     * @param orderVolume
     * @return ID the found order, or -1 if order with the parameters has not be found
     */
    Integer searchOrderByAdmin(Integer currencyPair, String orderType, String orderDate, BigDecimal orderRate, BigDecimal orderVolume);

    List<BackDealInterval> getIntervals();

    List<ChartTimeFrame> getChartTimeFrames();

    /**
     * Returns object that contains data with statistics of orders for currencyPair.
     * Statistics formed by data for certain period: from current moment to <i></>backDealInterval</i> back
     *
     * @param currencyPair
     * @param backDealInterval is the length of interval
     * @return statistics of orders for currencyPair
     * @author ValkSam
     */
    ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval, Locale locale);

    /**
     * Returns data for area type chart for <i>currencyPair</i> for for period: from current moment to <i></>interval</i> back
     *
     * @param currencyPair
     * @param interval
     * @return data for area chart
     * @author ValkSam
     */
    List<Map<String, Object>> getDataForAreaChart(CurrencyPair currencyPair, BackDealInterval interval);

    /**
     * Returns data for the history of accepted orders
     *
     * @param backDealInterval
     * @param limit
     * @param locale
     * @return
     */
    List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(CacheData cacheData, String email, BackDealInterval backDealInterval, Integer limit, CurrencyPair currencyPair, Locale locale);

    @Transactional
    List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriodEx(String email,
                                                              BackDealInterval backDealInterval,
                                                              Integer limit, CurrencyPair currencyPair, Locale locale);

    /**
     * Returns SELL and BUY commissions for orders
     *
     * @return
     */
    OrderCommissionsDto getCommissionForOrder();

    CommissionsDto getAllCommissions();

    /**
     * Returns list of Buy orders of status open
     *
     * @param currencyPair
     * @param orderRoleFilterEnabled
     * @return list of Buy orders
     */
    List<OrderListDto> getAllBuyOrders(CacheData cacheData, CurrencyPair currencyPair, Locale locale, Boolean orderRoleFilterEnabled);

    @Transactional(readOnly = true)
    List<OrderListDto> getAllBuyOrdersEx(CurrencyPair currencyPair, Locale locale, UserRole userRole);

    @Transactional(readOnly = true)
    List<OrderListDto> getAllSellOrdersEx(CurrencyPair currencyPair, Locale locale, UserRole userRole);

    /**
     * Returns list of Sell orders of status open, exclude the orders of current user
     *
     * @param currencyPair
     * @param orderRoleFilterEnabled
     * @return list of Sell orders
     */
    List<OrderListDto> getAllSellOrders(CacheData cacheData, CurrencyPair currencyPair, Locale locale, Boolean orderRoleFilterEnabled);

    List<OrdersListWrapper> getOpenOrdersForWs(String pairName);

    /**
     * Returns data of
     * - userId by email,
     * - wallet by currency id
     * - commission by operation type
     * Used for creation order with corresponding parameters
     *
     * @param email
     * @param currency
     * @param operationType
     * @return
     */
    WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                    OperationType operationType);

    DataTable<List<OrderBasicInfoDto>> searchOrdersByAdmin(AdminOrderFilterData adminOrderFilterData, DataTableParams dataTableParams, Locale locale);

    List<OrderReportInfoDto> getOrdersForReport(AdminOrderFilterData adminOrderFilterData);

    List<OrderWideListDto> getUsersOrdersWithStateForAdmin(int id, CurrencyPair currencyPair, OrderStatus status,
                                                           OperationType operationType, Integer offset, Integer limit,
                                                           Locale locale);

    int getUsersOrdersWithStateForAdminCount(int id, CurrencyPair currencyPair, OrderStatus orderStatus,
                                             OperationType operationType, int offset, int limit);

    List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, OrderStatus status,
                                                OperationType operationType, String scope,
                                                Integer offset, Integer limit, Locale locale);

    List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(String email,
                                                            BackDealInterval backDealInterval,
                                                            Integer limit, CurrencyPair currencyPair, Locale locale);

    List<OrderListDto> getAllBuyOrders(CurrencyPair currencyPair, Locale locale);

    List<OrderListDto> getAllSellOrders(CurrencyPair currencyPair, Locale locale);

    List<UserSummaryOrdersByCurrencyPairsDto> getUserSummaryOrdersByCurrencyPairList(Integer requesterUserId, String startDate, String endDate, List<Integer> roles);

    BiTuple getTradesForRefresh(Integer pairId, String email, RefreshObjectsEnum refreshObjectEnum);

    @Transactional(readOnly = true)
    String getAllAndMyTradesForInit(int pairId, Principal principal) throws JsonProcessingException;

    Optional<BigDecimal> getLastOrderPriceByCurrencyPairAndOperationType(CurrencyPair currencyPair, OperationType operationType);

    String getOrdersForRefresh(Integer pairId, OperationType operationType, UserRole userRole);

    String getAllCurrenciesStatForRefresh(RefreshObjectsEnum refreshObjectsEnum);

    String getAllCurrenciesStatForRefreshForAllPairs();

    RefreshStatisticDto getSomeCurrencyStatForRefresh(Set<Integer> currencyId);

    ResponseInfoCurrencyPairDto getStatForPair(String pairName);

    Map<OrderType, List<OrderBookItem>> getOrderBook(String currencyPairName, @Nullable OrderType orderType);

    List<TradeHistoryDto> getTradeHistory(String currencyPairName, LocalDate fromDate, LocalDate toDate, Integer limit, String direction);

    List<UserOrdersDto> getUserOpenOrders(@Nullable String currencyPairName);

    @Transactional(readOnly = true)
    List<UserOrdersDto> getUserOpenOrders(@Nullable Integer currencyPairId, Integer userId);

    List<UserOrdersDto> getUserClosedOrders(String currencyPairName, Integer limit, Integer offset);

    List<UserOrdersDto> getUserCanceledOrders(String currencyPairName, Integer limit, Integer offset);

    List<OpenOrderDto> getOpenOrders(String currencyPairName, OrderType orderType);

    List<UserTradeHistoryDto> getUserTradeHistoryByCurrencyPair(String currencyPairName, LocalDate fromDate, LocalDate toDate, Integer limit);

    List<TransactionDto> getOrderTransactions(Integer orderId);

    List<CurrencyPairTurnoverReportDto> getCurrencyPairTurnoverByPeriodAndRoles(LocalDateTime startTime,
                                                                                LocalDateTime endTime,
                                                                                List<UserRole> roles);

    Map<String, List<UserSummaryOrdersDto>> getUserSummaryOrdersData(LocalDateTime startTime,
                                                                     LocalDateTime endTime,
                                                                     List<UserRole> userRoles,
                                                                     int requesterId);

    void logCallBackData(CallBackLogDto callBackLogDto);

    List<UserOrdersDto> getAllUserOrders(@Null String currencyPairName,
                                         @Null Integer limit,
                                         @Null Integer offset);

    List<OrderWideListDto> getMyOpenOrdersWithState(String pairName, String userEmail);

    List<OrderWideListDto> getMyOpenOrdersWithState(String pairName, int userId);

    Pair<Integer, List<OrderWideListDto>> getMyOrdersWithStateMap(Integer userId, CurrencyPair currencyPair, String currencyName,
                                                                  OrderStatus orderStatus, String scope, Integer limit,
                                                                  Integer offset, Boolean hideCanceled, String sortByCreated,
                                                                  LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo, Boolean limited,
                                                                  Locale locale);

    @Transactional
    boolean cancelOrders(Collection<Integer> orderIds);

    List<OrderWideListDto> getOrdersForExcel(Integer userId, CurrencyPair currencyPair, String currencyName,
                                             OrderStatus orderStatus, String scope, Integer limit,
                                             Integer offset, Boolean hideCanceled, String sortByCreated,
                                             LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo, Locale locale);

    ReportDto getOrderExcelFile(List<OrderWideListDto> orders) throws Exception;

    ReportDto getTransactionExcelFile(List<MyInputOutputHistoryDto> transactions) throws Exception;

    List<ExOrderStatisticsShortByPairsDto> getAllCurrenciesMarkersForAllPairsModel();

    Optional<BigDecimal> getLastOrderPriceByCurrencyPair(CurrencyPair currencyPair);

    List<OrdersListWrapper> getMyOpenOrdersForWs(String currencyPairName, String name);

    OrderBookWrapperDto findAllOrderBookItems(OrderType orderType, Integer currencyId, int precision);

    Map<PrecissionsEnum, String> findAllOrderBookItemsForAllPrecissions(OrderType orderType, Integer currencyId, List<PrecissionsEnum> precissionsList);

    List<ExOrderStatisticsShortByPairsDto> getRatesDataForCache(Integer currencyPairId);

    List<ExOrderStatisticsShortByPairsDto> getAllDataForCache(Integer currencyPairId);

    ExOrderStatisticsShortByPairsDto getBeforeLastRateForCache(Integer currencyPairId);
}
