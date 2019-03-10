package me.exrates.service.orders;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.OrderDao;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.vo.TransactionDescription;
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


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int createOrder(OrderCreateDto orderCreateDto, OrderActionEnum action) {
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
                ExOrder exOrder = new ExOrder(orderCreateDto);
                OrderBaseType orderBaseType = orderCreateDto.getOrderBaseType();
                if (orderBaseType == null) {
                    CurrencyPairType type = exOrder.getCurrencyPair().getPairType();
                    orderBaseType = type == CurrencyPairType.ICO ? OrderBaseType.ICO : OrderBaseType.LIMIT;
                    exOrder.setOrderBaseType(orderBaseType);
                }
                TransactionSourceType sourceType;
                switch (orderBaseType) {
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
                    exOrder.setId(createdOrderId);
                    WalletTransferStatus result = walletService.walletInnerTransfer(
                            outWalletId,
                            outAmount.negate(),
                            sourceType,
                            exOrder.getId(),
                            description);
                    if (result != WalletTransferStatus.SUCCESS) {
                        throw new OrderCreationException(result.toString());
                    }
                    setStatus(createdOrderId, OrderStatus.OPENED);
                    exOrder.setStatus(OrderStatus.OPENED);
                }
                eventPublisher.publishEvent(new CreateOrderEvent(exOrder));
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


}
