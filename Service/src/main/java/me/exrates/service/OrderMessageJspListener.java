package me.exrates.service;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.service.handler.OrdersEventHandleService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@EnableRabbit
@Component
public class OrderMessageJspListener {


    @Autowired
    private OrdersEventHandleService ordersEventHandleService;


    /*
    * This method is triggered if rabbit exchange receives message ({order}) from counterpart application
     */
    @RabbitListener(queues = RabbitMqService.JSP_QUEUE)
    public void processOrder(InputCreateOrderDto order) {
        log.debug("Order Received: " + order);
        ordersEventHandleService.handleOrderEventOnMessage(order);
    }

    // uncomment for testing as this order will be sent from this application
//    @RabbitListener(queues = RabbitMqService.ANGULAR_QUEUE)
//    public void processOrderSelf(InputCreateOrderDto order) {
//        logger.debug("Order Received: " + order);
//    }
} 