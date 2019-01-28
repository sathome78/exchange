package me.exrates.service;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.dto.OrderCreateSummaryDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.service.handler.OrdersEventHandleService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static me.exrates.model.enums.OrderBaseType.*;

@Log4j2
@EnableRabbit
@Component
public class OrderMessageJspListener {


    @Autowired
    private OrdersEventHandleService ordersEventHandleService;

    @Autowired
    private OrderServiceDemoListener orderServiceDemoListener;


    /*
     * This method is triggered if rabbit exchange receives message ({order}) from counterpart application
     */
    @RabbitListener(queues = RabbitMqService.JSP_QUEUE)
    public String processOrder(InputCreateOrderDto order) {
        log.info("Order from demo server received: " + order);
        OrderCreateSummaryDto orderCreateSummaryDto = orderServiceDemoListener.newOrderToSell(OperationType.valueOf(order.getOrderType()), order.getUserId(), order.getAmount(), order.getRate(), convert(order.getBaseType()), order.getCurrencyPair(), order.getStop());
        orderServiceDemoListener.recordOrderToDB(order,orderCreateSummaryDto.getOrderCreateDto());
        ordersEventHandleService.handleOrderEventOnMessage(order);
        log.info("Order saved: " + order);
        return "success";
    }

    // uncomment for testing as this order will be sent from this application
//    @RabbitListener(queues = RabbitMqService.ANGULAR_QUEUE)
//    public void processOrderSelf(InputCreateOrderDto order) {
//        logger.debug("Order Received: " + order);
//    }
} 