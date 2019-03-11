package me.exrates.service.orders;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.OrderDao;
import me.exrates.model.ExOrder;
import me.exrates.model.User;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderCreationResultDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.newOrders.Order;
import me.exrates.model.vo.TransactionDescription;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.events.CreateOrderEvent;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.OrderCreationException;
import me.exrates.service.vo.ProfileData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;
import static me.exrates.model.enums.OrderActionEnum.CREATE;

@Service
@Log4j2(topic = "orders_process")
public class OrdersProcessServiceImpl implements OrdersProcessService {

    @Autowired
    private WalletService walletService;
    @Autowired
    private TransactionDescription transactionDescription;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserService userService;
    @Autowired
    private HazelcastInstance hz;


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int createOrder(OrderCreateDto orderCreateDto, OrderActionEnum action) {
        orderCreateDto.setStatus(OrderStatus.OPENED);
        String description = transactionDescription.get(null, action);
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
            ExOrder exOrder = new ExOrder(orderCreateDto);
            Order order = new Order(orderCreateDto);

            TransactionSourceType sourceType = TransactionSourceType.ORDER;
            /*save order to hazelcast and mysql*/
            int createdOrderId = orderDao.createOrder(order);/*mysql*/

            if (createdOrderId > 0) {
                order.setId(createdOrderId);
                WalletTransferStatus result = walletService.walletInnerTransfer(
                        outWalletId,
                        outAmount.negate(),
                        sourceType,
                        order.getId(),
                        description);
                if (result != WalletTransferStatus.SUCCESS) {
                    throw new OrderCreationException(result.toString());
                }
            }

            eventPublisher.publishEvent(new CreateOrderEvent(order));
            return createdOrderId;

        } else {
            //this exception will be caught in controller, populated  with message text  and thrown further
            throw new NotEnoughUserWalletMoneyException("");
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public boolean setStatus(int orderId, OrderStatus status) {
        return orderDao.setStatus(orderId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<OrderCreationResultDto> autoAcceptOrders(OrderCreateDto orderCreateDto, Locale locale) throws InterruptedException {
        /*get distributed lock for order type and pair from hazelcast*/
        ILock lock = hz.getLock(String.join("_","lock_", orderCreateDto.getOperationType().name(), orderCreateDto.getCurrencyPair().getName()));

        /*try to get lock, wait max 20 secnds for aquire*/
        lock.tryLock(20, TimeUnit.SECONDS);

        try {
            /*get datarelated to user*/
            boolean acceptSameRoleOnly = userRoleService.isOrderAcceptionAllowedForUser(orderCreateDto.getUserId());

            /*get list of orders ready to accept form hazelcast*/
            List<Order> acceptableOrders = Collections.emptyList();/* get orders from hazelcast here */
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
            for (Order order : acceptableOrders) {
                    cumulativeSum = cumulativeSum.add(order.getAmountAvailable());
                    if (orderCreateDto.getAmount().compareTo(cumulativeSum) > 0) {
                        ordersForAccept.add(order);
                    } else if (orderCreateDto.getAmount().compareTo(cumulativeSum) == 0) {
                        ordersForAccept.add(order);
                        break;
                    } else {
                        sumForPartialAccept = order.getAmountBase().subtract(cumulativeSum.subtract(orderCreateDto.getAmount()));/*check it!*/
                        orderForPartialAccept = order;
                        break;
                    }
            }

            OrderCreationResultDto orderCreationResultDto = new OrderCreationResultDto();

            /*accept orders suiatble for full accept*/
            if (ordersForAccept.size() > 0) {
                acceptOrdersList(orderCreateDto.getUserId(), ordersForAccept.stream().map(ExOrder::getId).collect(toList()), locale);
                orderCreationResultDto.setAutoAcceptedQuantity(ordersForAccept.size());
            }

            /*accept orders for partially accept*/
            if (orderForPartialAccept != null) {
                BigDecimal partialAcceptResult = acceptPartially(orderCreateDto, orderForPartialAccept, cumulativeSum, locale);
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

    private void acceptPartially() {

    }



}
