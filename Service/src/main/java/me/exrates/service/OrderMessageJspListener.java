package me.exrates.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.dto.OrderCreateSummaryDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.service.exception.RabbitMqException;
import me.exrates.service.exception.RabbitMqException;
import me.exrates.service.handler.OrdersEventHandleService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static me.exrates.model.enums.OrderBaseType.convert;

@Log4j2
@EnableRabbit
@Component
public class OrderMessageJspListener {


    @Autowired
    private OrdersEventHandleService ordersEventHandleService;

    @Autowired
    private OrderServiceDemoListener orderServiceDemoListener;

    @Autowired
    private ObjectMapper objectMapper;

    /*
     * This method is triggered if rabbit exchange receives message ({order}) from counterpart application
     */
    @RabbitListener(queues = RabbitMqService.JSP_QUEUE)
    public String processOrder(String orderJson) {
        log.info("Starting processing order {}", orderJson);
        InputCreateOrderDto order;
        try {
            order = objectMapper.readValue(orderJson, InputCreateOrderDto.class);
            log.info("Received order from demo {}", order);
        } catch (IOException e) {
            log.error("Failed read orderJson {}, e {}", orderJson, e);
            throw new RabbitMqException("Failed read orderJson " + orderJson + "e {}" + e.getLocalizedMessage());
        }
        OperationType orderType = OperationType.valueOf(order.getOrderType());
        OrderBaseType baseType = convert(order.getBaseType());

        OrderCreateSummaryDto orderCreateSummaryDto = orderServiceDemoListener.newOrderToSell(orderType, order.getUserId(),
                order.getAmount(), order.getRate(), baseType, order.getCurrencyPairId(), order.getStop());

        log.info("Creating OrderCreateSummaryDto {}", orderCreateSummaryDto);
        String orderToDB = orderServiceDemoListener.recordOrderToDB(order, orderCreateSummaryDto.getOrderCreateDto());
        log.info("Recorder to DB {}", orderToDB);
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