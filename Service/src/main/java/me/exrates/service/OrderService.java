package me.exrates.service;

import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminOrderFilterData;
import me.exrates.model.dto.mobileApiDto.dashboard.CommissionsDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.model.vo.CacheData;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public interface OrderService {

  List<ExOrderStatisticsShortByPairsDto> getOrdersStatisticByPairsSessionless(Locale locale);

  OrderCreateDto prepareNewOrder(CurrencyPair activeCurrencyPair, OperationType orderType, String userEmail, BigDecimal amount, BigDecimal rate);

  OrderValidationDto validateOrder(OrderCreateDto orderCreateDto);

  /**
   * Returns the ID of the newly created and saved in DB order
   * Generates transaction of transferring money from active balance to reserved balance the corresponding wallet
   *
   * @param order OrderCreateDto, that passed from frontend and that will be converted to entity ExOrder to save in DB
   * @return generated ID of the newly created order, or 0 if order was not be created
   */
  int createOrder(OrderCreateDto order, OrderActionEnum action);

  Optional<String> autoAccept(OrderCreateDto orderCreateDto, Locale locale);

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
  public OrderCreateDto getMyOrderById(int orderId);

  /**
   * Returns entity ExOrder by its ID
   *
   * @param orderId
   * @return entity ExOrder for found order, or null if order not found
   */
  ExOrder getOrderById(int orderId);

  /**
   * Sets new status for existing order with given ID.
   *
   * @param orderId
   * @param status
   * @return
   */
  boolean setStatus(int orderId, OrderStatus status);

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
  void acceptOrdersList(int userAcceptorId, List<Integer> ordersList, Locale locale);

  /**
   * Accepts the order
   * and generates set of transactions for creator-user and acceptor-user
   * and modifies wallets for users and company
   * If there were errors while accept, errors will be thrown:
   * - NotEnoughUserWalletMoneyException
   * - TransactionPersistException
   * - OrderAcceptionException
   *
   * @param userId  is ID of acceptor-user
   * @param orderId is ID of order that must be accepted
   * @param locale  is current locale. Used to generate messages
   */
  void acceptOrder(int userId, int orderId, Locale locale);
  
  void acceptOrderByAdmin(String acceptorEmail, Integer orderId, Locale locale);
  
  /**
   * Cancels the order and set status "CANCELLED"
   * Only order with status "OPENED" can be cancelled
   * This method for cancel order by creator-user
   *
   * @param exOrder is the entity ExOrder of order that must be cancelled
   * @return "true" if the order can be cancelled and has been cancelled successfully, "false" in other cases
   */
  boolean cancellOrder(ExOrder exOrder, Locale locale);

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

  /**
   * Returns detailed info about the order, including info from related transactions
   *
   * @param orderId is ID the order
   * @return OrderInfoDto containing detailed info about the order
   */
  OrderInfoDto getOrderInfo(int orderId, Locale locale);

  Object deleteOrderByAdmin(int orderId);

  Object deleteOrderForPartialAccept(int orderId);

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

  @Transactional
  List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval interval, LocalDateTime startTime);

  /**
   * Returns statistics of orders by currency pairs.
   * Statistics contains last and pred last rates for each currency pair
   *
   * @return statistics of orders by currency pairs
   * @author ValkSam
   */
  List<ExOrderStatisticsShortByPairsDto> getOrdersStatisticByPairs(CacheData cacheData, Locale locale);

  /**
   * Returns data for candle chart for <i>currencyPair</i> for for period: from current moment to <i></>interval</i> back
   *
   * @param currencyPair
   * @param interval
   * @return data for candle chart
   * @author ValkSam
   */
  List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval interval);

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
   * @return list of Buy orders
   */
  List<OrderListDto> getAllBuyOrders(CacheData cacheData, CurrencyPair currencyPair, Locale locale);

  /**
   * Returns list of Sell orders of status open, exclude the orders of current user
   *
   * @param currencyPair
   * @return list of Sell orders
   */
  List<OrderListDto> getAllSellOrders(CacheData cacheData, CurrencyPair currencyPair, Locale locale);

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

  List<OrderWideListDto> getUsersOrdersWithStateForAdmin(String email, CurrencyPair currencyPair, OrderStatus status,
                                                         OperationType operationType,
                                                         Integer offset, Integer limit, Locale locale);

  List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, OrderStatus status,
                                              OperationType operationType,
                                              Integer offset, Integer limit, Locale locale);

  List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, List<OrderStatus> statuses,
                                              OperationType operationType,
                                              Integer offset, Integer limit, Locale locale);

  List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(String email,
                                                          BackDealInterval backDealInterval,
                                                          Integer limit, CurrencyPair currencyPair, Locale locale);

  List<OrderListDto> getAllBuyOrders(CurrencyPair currencyPair, Locale locale);

  List<OrderListDto> getAllSellOrders(CurrencyPair currencyPair, Locale locale);

  List<UserSummaryOrdersByCurrencyPairsDto> getUserSummaryOrdersByCurrencyPairList(Integer requesterUserId, String startDate, String endDate, List<Integer> roles);
}
