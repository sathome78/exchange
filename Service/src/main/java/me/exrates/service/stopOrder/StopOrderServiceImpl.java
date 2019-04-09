package me.exrates.service.stopOrder;


import lombok.extern.log4j.Log4j2;
import me.exrates.dao.StopOrderDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.PagingData;
import me.exrates.model.StopOrder;
import me.exrates.model.dto.OrderBasicInfoDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderInfoDto;
import me.exrates.model.dto.StopOrderSummaryDto;
import me.exrates.model.dto.WalletsForOrderCancelDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminStopOrderFilterData;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.TransactionDescription;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.events.AcceptOrderEvent;
import me.exrates.service.exception.IncorrectCurrentUserException;
import me.exrates.service.exception.StopOrderNoConditionException;
import me.exrates.service.exception.process.NotCreatableOrderException;
import me.exrates.service.exception.process.OrderAcceptionException;
import me.exrates.service.exception.process.OrderCancellingException;
import me.exrates.service.util.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.isNull;

/**
 * Created by maks on 20.04.2017.
 */

@Service
@Log4j2
public class StopOrderServiceImpl implements StopOrderService {

    @Autowired
    private StopOrderDao stopOrderDao;
    @Autowired
    private OrderService orderService;
    @Autowired
    private StopOrdersHolder stopOrdersHolder;
    @Autowired
    private RatesHolder ratesHolder;
    @Autowired
    private TransactionDescription transactionDescription;
    @Autowired
    private WalletService walletService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserService userService;
    @Autowired
    private CurrencyService currencyService;

    private final static int THREADS_NUMBER = 10;
    private final static ExecutorService checkExecutors = Executors.newFixedThreadPool(THREADS_NUMBER);
    private ConcurrentMap<Integer, Object> buyLocks = new ConcurrentHashMap<>();
    private final static ExecutorService ordersExecutors = Executors.newFixedThreadPool(THREADS_NUMBER);
    private ConcurrentMap<Integer, Object> sellLocks = new ConcurrentHashMap<>();


    @Transactional
    @Override
    public String create(OrderCreateDto orderCreateDto, OrderActionEnum actionEnum, Locale locale) {
        int orderId = orderService.createOrder(orderCreateDto, actionEnum);
        if (orderId <= 0) {
            throw new NotCreatableOrderException(messageSource.getMessage("dberror.text", null, locale));
        }
        ExOrder exOrder = new ExOrder(orderCreateDto);
        exOrder.setId(orderId);
        this.onStopOrderCreate(exOrder);
        return "{\"result\":\"" + messageSource.getMessage("createdstoporder.text", null, locale) + "\"}";
    }


    @Transactional
    @Override
    public Integer createOrder(ExOrder exOrder) {
        StopOrder order = new StopOrder(exOrder);
        return stopOrderDao.create(order);
    }


    @Override
    public void proceedStopOrders(int pairId, NavigableSet<StopOrderSummaryDto> orders) {
        orders.forEach(p -> {
            try {
                ordersExecutors.execute(() -> proceedStopOrderAndRemove(p.getOrderId()));
            } catch (Exception e) {
                log.error("error processing stop order {}", e);
            }
        });
    }

    @Transactional
    @Override
    public void proceedStopOrderAndRemove(int stopOrderId) {
        OrderCreateDto stopOrder = null;
        stopOrder = getOrderById(stopOrderId, true);
        if (stopOrder == null || !stopOrder.getStatus().equals(OrderStatus.OPENED)) {
            throw new StopOrderNoConditionException(String.format(" order %s not found in db or illegal status ", stopOrderId));
        }
        stopOrdersHolder.delete(stopOrder.getCurrencyPair().getId(),
                new StopOrderSummaryDto(stopOrderId, stopOrder.getStop(), stopOrder.getOperationType()));
        try {
            this.proceedStopOrder(new ExOrder(stopOrder));
        } catch (OrderCancellingException e) {
            log.error("order not acceptable, error processing stop-order  {}", e);
        } catch (Exception e) {
            log.error("error processing stop-order  {}", e);
            stopOrdersHolder.addOrder(new ExOrder(stopOrder));
        }
    }


    @Transactional
    public void proceedStopOrder(ExOrder exOrder) {
        OrderCreateDto newOrder = orderService.prepareNewOrder(currencyService.findCurrencyPairById(
                exOrder.getCurrencyPair().getId()), exOrder.getOperationType(),
                userService.getEmailById(exOrder.getUserId()), exOrder.getAmountBase(), exOrder.getExRate(), OrderBaseType.LIMIT);
        if (newOrder == null) {
            throw new RuntimeException("error preparing new order");
        }
        cancelCostsReserveForStopOrder(exOrder, Locale.ENGLISH, OrderActionEnum.ACCEPT);
        stopOrderDao.setStatus(exOrder.getId(), OrderStatus.CLOSED);
        Integer orderId = orderService.createOrderByStopOrder(newOrder, OrderActionEnum.CREATE, Locale.ENGLISH);
        if (orderId != null) {
            stopOrderDao.setStatusAndChildOrderId(exOrder.getId(), orderId, OrderStatus.CLOSED);
        }
    }

    @Transactional
    public void cancelCostsReserveForStopOrder(ExOrder dto, Locale locale, OrderActionEnum actionEnum) {
        WalletsForOrderCancelDto walletsForOrderCancelDto = walletService.getWalletForStopOrderByStopOrderIdAndOperationTypeAndBlock(
                dto.getId(), dto.getOperationType(), dto.getCurrencyPairId());
        OrderStatus currentStatus = OrderStatus.convert(walletsForOrderCancelDto.getOrderStatusId());
        if (currentStatus != OrderStatus.OPENED) {
            throw new OrderCancellingException(messageSource.getMessage("order.cannotcancel", null, locale));
        }
        WalletTransferStatus transferResult = walletService.walletInnerTransfer(
                walletsForOrderCancelDto.getWalletId(),
                walletsForOrderCancelDto.getReservedAmount(),
                TransactionSourceType.STOP_ORDER,
                walletsForOrderCancelDto.getOrderId(),
                transactionDescription.get(dto.getStatus(), actionEnum));
        if (transferResult != WalletTransferStatus.SUCCESS) {
            throw new OrderCancellingException(transferResult.toString());
        }
    }

    @Override
    public List<StopOrder> getActiveStopOrdersByCurrencyPairsId(List<Integer> pairIds) {
        return stopOrderDao.getOrdersBypairId(pairIds, OrderStatus.OPENED);
    }

    @Override
    @Transactional
    public boolean cancelOrder(int orderId, Locale locale) {
        OrderCreateDto orderCreateDto = this.getOrderById(orderId, true);
        if (orderCreateDto == null) {
            throw new OrderCancellingException(messageSource.getMessage("orderinfo.searcherror", null, locale));
        }
        if (isNull(locale)) {
            final String currentUserEmail = getUserEmailFromSecurityContext();

            final int userId = orderCreateDto.getUserId();

            final String creatorEmail = userService.getEmailById(userId);
            if (!currentUserEmail.equals(creatorEmail)) {
                throw new IncorrectCurrentUserException(String.format("Creator email: %s and currentUser email: %s are different", creatorEmail, currentUserEmail));
            }
            locale = userService.getUserLocaleForMobile(currentUserEmail);
        }

        if (orderCreateDto.getStatus() != OrderStatus.OPENED) {
            throw new OrderCancellingException(messageSource.getMessage("order.cannotcancel.allreadycancelled", null, locale));
        }
        ExOrder exOrder = new ExOrder(orderCreateDto);
        boolean res;
        cancelCostsReserveForStopOrder(exOrder, locale, OrderActionEnum.CANCEL);
        res = this.setStatus(exOrder.getId(), OrderStatus.CANCELLED);
        if (res) {
            try {
                stopOrdersHolder.delete(exOrder.getCurrencyPairId(),
                        new StopOrderSummaryDto(exOrder.getId(), exOrder.getStop(), exOrder.getOperationType()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }


    @Transactional
    @Override
    public boolean setStatus(int orderId, OrderStatus status) {
        return stopOrderDao.setStatus(orderId, status);
    }

    @Override
    public OrderCreateDto getOrderById(Integer orderId, boolean forUpdate) {
        return stopOrderDao.getOrderById(orderId, forUpdate);
    }

    @Override
    @TransactionalEventListener
    public void onLimitOrderAccept(AcceptOrderEvent event) {
        log.debug("orderAccepted");
        ExOrder exOrder = (ExOrder) event.getSource();
        ratesHolder.onRateChange(exOrder.getCurrencyPairId(), exOrder.getOperationType(), exOrder.getExRate());
        checkExecutors.execute(() -> checkOrders(exOrder, OperationType.BUY));
        checkExecutors.execute(() -> checkOrders(exOrder, OperationType.SELL));
    }


    /*check stop orders on order accepted and rates changed*/
    private void checkOrders(ExOrder exOrder, OperationType operationType) {
        try {
            NavigableSet<StopOrderSummaryDto> result;
            switch (operationType) {
                case SELL: {
                    synchronized (getLock(exOrder.getCurrencyPairId(), operationType)) {
                        result = stopOrdersHolder.getSellOrdersForPairAndStopRate(exOrder.getCurrencyPairId(), exOrder.getExRate());
                        log.debug("proc order result {}", result.size());
                        if (!result.isEmpty()) {
                            this.proceedStopOrders(exOrder.getCurrencyPairId(), result);
                        }
                        break;
                    }
                }
                case BUY: {
                    synchronized (getLock(exOrder.getCurrencyPairId(), operationType)) {
                        result = stopOrdersHolder.getBuyOrdersForPairAndStopRate(exOrder.getCurrencyPairId(), exOrder.getExRate());
                        log.debug("buy order result {}", result.size());
                        if (!result.isEmpty()) {
                            this.proceedStopOrders(exOrder.getCurrencyPairId(), result);
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("cant check stop orders {}", e);
        }
    }

    private Object getLock(Integer currencyId, OperationType operationType) {
        switch (operationType) {
            case BUY: {
                buyLocks.putIfAbsent(currencyId, new Object());
                return buyLocks.get(currencyId);
            }
            case SELL: {
                sellLocks.putIfAbsent(currencyId, new Object());
                return sellLocks.get(currencyId);
            }
            default: {
                throw new RuntimeException("Operatione not supported " + operationType);
            }
        }

    }


    /*try to accept order after create*/
    @Override
    public void onStopOrderCreate(ExOrder exOrder) {
        log.debug("stop order created {}", exOrder.getId());
        try {
            BigDecimal currentRate = ratesHolder.getCurrentRate(exOrder.getCurrencyPairId(), exOrder.getOperationType());
            log.debug("current rate {}, stop {}", currentRate, exOrder.getStop());
            switch (exOrder.getOperationType()) {
                case SELL: {
                    if (currentRate != null && exOrder.getStop().compareTo(currentRate) >= 0) {
                        log.error("try to proceed sell stop order {}", exOrder.getId());
                        this.proceedStopOrder(exOrder);
                    } else {
                        log.error("add buy order to holder {}", exOrder.getId());
                        stopOrdersHolder.addOrder(exOrder);
                    }
                }
                break;
                case BUY: {
                    if (currentRate != null && exOrder.getStop().compareTo(currentRate) <= 0) {
                        log.error("try to proceed buy stop order {}", exOrder.getId());
                        this.proceedStopOrder(exOrder);
                    } else {
                        log.error("add buy order to holder {}", exOrder.getId());
                        stopOrdersHolder.addOrder(exOrder);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            log.error("cant check stop orders on order create {}", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderWideListDto> getMyOrdersWithState(CacheData cacheData,
                                                       String email, CurrencyPair currencyPair, OrderStatus status,
                                                       OperationType operationType,
                                                       String scope, Integer offset, Integer limit, Locale locale) {
        List<OrderWideListDto> result = stopOrderDao.getMyOrdersWithState(email, currencyPair, status, operationType, scope, offset, limit, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<OrderWideListDto>() {{
                add(new OrderWideListDto(false));
            }};
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderWideListDto> getUsersStopOrdersWithStateForAdmin(String email, CurrencyPair currencyPair, OrderStatus status,
                                                                      OperationType operationType,
                                                                      Integer offset, Integer limit, Locale locale) {
        return stopOrderDao.getMyOrdersWithState(email, currencyPair, status, operationType, null, offset, limit, locale);
    }

    @Override
    @Transactional
    public DataTable<List<OrderBasicInfoDto>> searchOrdersByAdmin(AdminStopOrderFilterData adminOrderFilterData, DataTableParams dataTableParams, Locale locale) {

        PagingData<List<OrderBasicInfoDto>> searchResult = stopOrderDao.searchOrders(adminOrderFilterData, dataTableParams, locale);
        DataTable<List<OrderBasicInfoDto>> output = new DataTable<>();
        output.setData(searchResult.getData());
        output.setRecordsTotal(searchResult.getTotal());
        output.setRecordsFiltered(searchResult.getFiltered());
        return output;
    }

    @Transactional
    @Override
    public OrderInfoDto getStopOrderInfo(int orderId, Locale locale) {
        return stopOrderDao.getStopOrderInfo(orderId, locale);
    }

    @Override
    public Object deleteOrderByAdmin(int id, Locale locale) {
        return this.cancelOrder(id, locale);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Integer> getAllOpenedStopOrdersByUserId(Integer userId) {
        return stopOrderDao.getAllOpenedStopOrdersByUserId(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Integer> getOpenedStopOrdersByCurrencyPair(Integer userId, String currencyPair) {
        return stopOrderDao.getOpenedStopOrdersByCurrencyPair(userId, currencyPair);
    }

    @PreDestroy
    private void shutdown() {
        checkExecutors.shutdown();
        ordersExecutors.shutdown();
    }

    private String getUserEmailFromSecurityContext() {
        return userService.getUserEmailFromSecurityContext();
    }
}
