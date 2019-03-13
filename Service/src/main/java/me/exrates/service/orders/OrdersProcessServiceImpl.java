package me.exrates.service.orders;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.map.AbstractEntryProcessor;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.transaction.TransactionContext;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.ExOrder;
import me.exrates.model.User;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderCreationResultDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.newOrders.Order;
import me.exrates.model.newOrders.Trade;
import me.exrates.model.newOrders.TransactionDto;
import me.exrates.model.newOrders.WalletDto;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.TransactionDescription;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import me.exrates.service.orders.events.CreateHzOrderEvent;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static me.exrates.model.enums.OrderActionEnum.CREATE;
import static com.hazelcast.query.Predicates.*;

@Service
@Log4j2(topic = "orders_process")
public class OrdersProcessServiceImpl implements OrdersProcessService {


    @Autowired
    private TransactionDescription transactionDescription;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserService userService;
    @Autowired
    private HazelcastInstance hz;


    @Override
    public TransactionDto createOrder(OrderCreateDto orderCreateDto, OrderActionEnum action) {
        orderCreateDto.setStatus(OrderStatus.OPENED);
        String description = transactionDescription.get(null, action);
        long outWalletId;
        BigDecimal outAmount;
        Integer currencyId;
        if (orderCreateDto.getOperationType() == OperationType.BUY) {
            outWalletId = orderCreateDto.getWalletIdCurrencyConvert();
            outAmount = orderCreateDto.getTotalWithComission();
            currencyId = orderCreateDto.getCurrencyPair().getCurrency1().getId();
        } else {
            outWalletId = orderCreateDto.getWalletIdCurrencyBase();
            outAmount = orderCreateDto.getAmount();
            currencyId = orderCreateDto.getCurrencyPair().getCurrency2().getId();
        }
        Order order = new Order(orderCreateDto);
        TransactionSourceType sourceType = TransactionSourceType.ORDER_HZ;
        /*save order to hazelcast */
        long createdOrderId  = hz.getAtomicLong("ordersIdCounter").incrementAndGet();
        order.setId(createdOrderId);
        /*begin transaction*/
        TransactionContext txCxt = hz.newTransactionContext();
        txCxt.beginTransaction();
        TransactionalMap<Long, Order> openOrdersMap = txCxt.getMap(HzCollectionNamesUtils.getNameForOrdersCollection(order.getCurrencyPairId()));
        TransactionalMap<Long, WalletDto> walletsMap = txCxt.getMap(HzCollectionNamesUtils.getNameForWalletsCollection(currencyId));
        WalletDto wallet = walletsMap.get(outWalletId);
        TransactionDto transaction;
        try {
            if (wallet.getActiveBalance().compareTo(outAmount) >= 0) {
                transaction = walletInnerTransfer(currencyId, outWalletId, outAmount.negate(), sourceType, order.getId(), description, txCxt);
                openOrdersMap.put(createdOrderId, order);
                /*commit tx*/
                txCxt.commitTransaction();
            } else {
                throw new NotEnoughUserWalletMoneyException("");
            }
        } catch (Throwable t) {
            /*rollback tx*/
            txCxt.rollbackTransaction();
            throw t;
        }
        eventPublisher.publishEvent(new CreateHzOrderEvent(order));
        return transaction;
    }

    static class OrdersSearch
            extends AbstractEntryProcessor<String, Order> {
        BigDecimal accum = BigDecimal.ZERO;

        @Override
        public Object process(Map.Entry< String, Order> entry) {

            BigDecimal acc = accum.add(entry.getValue().getAmountAvailable());

            employee.incSalary(10);
            entry.setValue(employee);
            return null;
        }
    }

    private Predicate getSearchSuitableOrdersPredicate(OrderCreateDto orderCreateDto, OrderType orderTypeToSearch, Integer userRole, boolean acceptSameRoleOnly) {
        Predicate typeId = equal("orderTypeId", orderTypeToSearch.getType());
        Predicate exrate;
        if (orderTypeToSearch.equals(OrderType.BUY)) {
            exrate = Predicates.lessEqual("exrate", orderCreateDto.getExchangeRate());
        } else {
            exrate = Predicates.greaterEqual("exrate", orderCreateDto.getExchangeRate());
        }
        Predicate currecyPair = equal("currencyPairId", orderCreateDto.getCurrencyPair().getId());
        Predicate status = equal("orderStatusId", OrderStatus.OPENED);
        Predicate baseType = equal("baseType", orderCreateDto.getOrderBaseType().name());
        Predicate predicate = and(typeId, exrate, currecyPair, status, baseType);
        if (acceptSameRoleOnly) {
            Predicate sameRole = equal("baseType", userRole);
            predicate = and(predicate, sameRole);
        }
        return predicate;
    }

    private static Comparator<Order> getComparatorForOrdersSort(OrderType orderType) {
        if (orderType == OrderType.BUY) {
            return Comparator
                    .comparing(Order::getExrate)
                    .reversed()
                    .thenComparing(Order::getDateOfCreation);
        } else {
            return Comparator
                    .comparing(Order::getExrate)
                    .thenComparing(Order::getDateOfCreation);
        }
    }

    private static Comparator<Map.Entry<Long,Order>> getComparatorForOrdersSort(OrderType orderType) {
        new Comparator<Map.Entry<Long, Order>>() {
            @Override
            public int compare(Map.Entry<Long, Order> o1, Map.Entry<Long, Order> o2) {
                return 0;
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        }

        return new Comparable<Map.Entry<Long, Order>>() {
            @Override
            public int compareTo(Map.Entry<Long, Order> o) {
                return 0;
            }
        };

        Comparator
                .comparing(Map.Entry<Long,Order>  )
                .reversed()
                .thenComparing(Order::getDateOfCreation);


        /*class ScoreComparator<K, V extends Comparable<V>>

// Let your class implement Comparator<T>, binding Map.Entry<K, V> to T
                implements Comparator<Map.Entry<K, V>> {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {

                // Call compareTo() on V, which is known to be a Comparable<V>
                return o1.getValue().compareTo(o2.getValue());
            }
        }
        if (orderType == OrderType.BUY) {
            return Comparator
                    .comparing(Order)
                    .reversed()
                    .thenComparing(Order::getDateOfCreation);
        } else {
            return Comparator
                    .comparing(Order::getExrate)
                    .thenComparing(Order::getDateOfCreation);
        }*/
    }



    private Optional<OrderCreationResultDto> autoAcceptOrders(OrderCreateDto orderCreateDto, Locale locale) throws InterruptedException {
        /*get distributed lock for order type and pair from hazelcast*/
        ILock lock = hz.getLock(String.join("_","lock_", orderCreateDto.getOperationType().name(), orderCreateDto.getCurrencyPair().getName()));
        /*try to get lock, wait max 20 secnds for aquire*/
        lock.tryLock(20, TimeUnit.SECONDS);
        try {
            /*get data related to user*/
            Integer userRole = null;
            boolean acceptSameRoleOnly = userRoleService.isOrderAcceptionAllowedForUser(orderCreateDto.getUserId());
            if (acceptSameRoleOnly) {
                userRole = userService.getUserRoleFromDB(orderCreateDto.getUserId()).getRole();
            }
            final OrderType orderTypeToSearch = OrderType.valueOf(OperationType.getOpposite(orderCreateDto.getOperationType()).name());
            /*get list of orders ready to accept form hazelcast*/

            IMap<Long, Order> ordersMap = hz.getMap(HzCollectionNamesUtils.getNameForOrdersCollection(orderCreateDto.getCurrencyPair().getId()));

            List<Order> acceptableOrders = orders.values(getSearchSuitableOrdersPredicate(orderCreateDto, orderTypeToSearch, userRole, acceptSameRoleOnly))
                    .stream()
                    .sorted(getComparatorForOrdersSort(orderTypeToSearch))
                    .collect(Collectors.toList());

            PagingPredicate pagingPredicate = new PagingPredicate(getSearchSuitableOrdersPredicate(orderCreateDto, orderTypeToSearch, userRole, acceptSameRoleOnly), 20);
            // Retrieve the first page
            Collection<Order> values = ordersMap.project()values( pagingPredicate );
            /*List<ExOrder> acceptableOrders = orderDao.selectTopOrders(orderCreateDto.getCurrencyPair().getId(), orderCreateDto.getExchangeRate(),
                       OperationType.getOpposite(orderCreateDto.getOperationType()), acceptSameRoleOnly, userService.getUserRoleFromDB(orderCreateDto.getUserId()).getRole(), orderCreateDto.getOrderBaseType());*/
            if (acceptableOrders.isEmpty()) {
                return Optional.empty();
            }
            /*iterate suitable orders and select them to accept*/
            BigDecimal cumulativeSum = BigDecimal.ZERO;
            List<Order> ordersForAccept = new ArrayList<>();
            Order orderForPartialAccept = null;
            BigDecimal sumForPartialAccept = null;
            TransactionContext txCxt = hz.newTransactionContext();
            txCxt.beginTransaction();
            TransactionalMap<Long, Order> orders = txCxt.getMap(HzCollectionNamesUtils.getNameForOrdersCollection(orderCreateDto.getCurrencyPair().getId()));
            for (Order order : acceptableOrders) {
                Order blockedOrder;
                try {
                     blockedOrder = orders.getForUpdate(order.getId());
                } catch (Exception e) {
                    continue;
                }
                cumulativeSum = cumulativeSum.add(blockedOrder.getAmountAvailable());
                if (orderCreateDto.getAmount().compareTo(cumulativeSum) > 0) {
                    ordersForAccept.add(blockedOrder);
                } else if (orderCreateDto.getAmount().compareTo(cumulativeSum) == 0) {
                    ordersForAccept.add(blockedOrder);
                    break;
                } else {
                    sumForPartialAccept = blockedOrder.getAmountAvailable().subtract(cumulativeSum.subtract(orderCreateDto.getAmount()));/*check it!*/
                    orderForPartialAccept = blockedOrder;
                    break;
                }
            }
            acceptableOrders = null;




            OrderCreationResultDto orderCreationResultDto = new OrderCreationResultDto();

            /*accept orders suiatble for full accept*/
            if (ordersForAccept.size() > 0) {
                acceptOrdersList(orderCreateDto.getUserId(), ordersForAccept.stream().map(ExOrder::getId).collect(toList()), locale);
                orderCreationResultDto.setAutoAcceptedQuantity(ordersForAccept.size());
            }

            /*accept orders for partially accept*/
            if (orderForPartialAccept != null) {
                acceptPartially(sumForPartialAccept, orderForPartialAccept, orderCreateDto);
                orderCreationResultDto.setPartiallyAcceptedAmount(partialAcceptResult);
                orderCreationResultDto.setPartiallyAcceptedOrderFullAmount(orderForPartialAccept.getAmountBase());
            }   /*create order for the rest of sum*/
                else if (orderCreateDto.getAmount().compareTo(cumulativeSum) > 0 && orderCreateDto.getOrderBaseType() != OrderBaseType.ICO) {
                User user = userService.getUserById(orderCreateDto.getUserId());
                OrderCreateDto remainderNew = prepareNewOrder(
                        orderCreateDto.getCurrencyPair(),
                        orderCreateDto.getOperationType(),
                        user.getEmail(),
                        orderCreateDto.getAmount().subtract(cumulativeSum),
                        orderCreateDto.getExchangeRate(),
                        orderCreateDto.getOrderBaseType());
                Integer createdOrderId = createOrder(remainderNew, CREATE);
                orderCreationResultDto.setCreatedOrderId(createdOrderId);
            }
            return Optional.of(orderCreationResultDto);
        } finally {
            lock.forceUnlock();
        }
    }

    private void acceptPartially(BigDecimal sumForPartialAccept, Order orderForPartialAccept, OrderCreateDto orderCreateDto) {
        orderForPartialAccept.setOrderStatusId(OrderStatus.PARTIALLY_ACCEPTED.getStatus());
        Trade trade = new Trade();
        trade.setAmountBase(sumForPartialAccept);
        trade.setAmountConvert();
        trade.se
    }


    private TransactionDto walletInnerTransfer(Integer currencyId, Long walletId , BigDecimal amount, TransactionSourceType sourceType, Long sourceId, String description, TransactionContext txCxt) {
        /*get maps*/
        TransactionalMap<Long, WalletDto> walletsMap = txCxt.getMap(HzCollectionNamesUtils.getNameForWalletsCollection(currencyId));
        TransactionalMap<Long, TransactionDto> transactionsMap = txCxt.getMap("transactions");
        /*block wallet for update*/
        WalletDto wallet = walletsMap.getForUpdate(walletId);
        /*count new balances*/
        BigDecimal newActiveBalance = BigDecimalProcessing.doAction(wallet.getActiveBalance(), amount, ActionType.ADD);
        BigDecimal newReservedBalance = BigDecimalProcessing.doAction(wallet.getReservedBalance(), amount, ActionType.SUBTRACT);
        if (newActiveBalance.compareTo(BigDecimal.ZERO) < 0 || newReservedBalance.compareTo(BigDecimal.ZERO) < 0) {
            String errorText = String.format("Negative balance: active %s, reserved %s ",
                    BigDecimalProcessing.formatNonePoint(newActiveBalance, false),
                    BigDecimalProcessing.formatNonePoint(newReservedBalance, false));
            log.error(errorText);
            throw new me.exrates.model.exceptions.NegativeBalanceException(errorText);
        }
        wallet.setActiveBalance(newActiveBalance);
        wallet.setReservedBalance(newReservedBalance);
        /*create transaction*/
        TransactionDto transaction = new TransactionDto();
        transaction.setOperationTypeId(OperationType.WALLET_INNER_TRANSFER.getType());
        transaction.setUserWalletId(wallet.getId());
        transaction.setCompanyWalletId(null);
        transaction.setAmount(amount);
        transaction.setCommissionAmount(BigDecimal.ZERO);
        transaction.setCommissionId(null);
        transaction.setCurrencyId(wallet.getCurrencyId());
        transaction.setActiveBalanceBefore(wallet.getActiveBalance());
        transaction.setReservedBalanceBefore(wallet.getReservedBalance());
        transaction.setCompanyBalanceBefore(null);
        transaction.setCompanyCommissionBalanceBefore(null);
        transaction.setSourceType(sourceType);
        transaction.setSourceId(sourceId);
        transaction.setDescription(description);
        /*put new balances to map*/
        walletsMap.set(walletId, wallet);
        long txId  = hz.getAtomicLong("txIdCounter").incrementAndGet();
        return transactionsMap.put(txId, transaction);
    }


}
