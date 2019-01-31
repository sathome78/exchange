package me.exrates.ngcontroller.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.service.RabbitMqService;
import me.exrates.service.cache.MarketRatesHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@EnableRabbit
@Component
public class OrderMessageListener {

    static final Logger logger = LoggerFactory.getLogger(OrderMessageListener.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final MarketRatesHolder marketRatesHolder;

    @Autowired
    public OrderMessageListener(MarketRatesHolder marketRatesHolder,
                                SimpMessagingTemplate messagingTemplate,
                                ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
        this.marketRatesHolder = marketRatesHolder;
    }

    @RabbitListener(queues = RabbitMqService.ANGULAR_QUEUE)
    public String processOrder(String orderJson) {
        String processId = UUID.randomUUID().toString();
        InputCreateOrderDto orderDto = null;
        logger.info("{} Getting order from old-server", processId, orderJson);
        try {

            orderDto = objectMapper.readValue(orderJson, InputCreateOrderDto.class);
            logger.info("{} Reading order from old-server", processId, orderDto);

//            marketRatesHolder.setRateMarket(order.getCurrencyPairId(), order.getRate(), order.getAmount());

            String message = objectMapper.writeValueAsString(orderDto);

            this.messagingTemplate.convertAndSend("/topic/rabbit", message);
        } catch (JsonProcessingException e) {
            logger.error("{} Failed to redirect to rabbit topic", processId, e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("{} Order Received: {}", processId, orderDto);
        return "Processed";
    }

    // uncomment for testing as this order will be sent from this application
//    @RabbitListener(queues = RabbitMqService.JSP_QUEUE)
//    public void processOrder2(InputCreateOrderDto order) {
//        logger.debug("Order Received: " + order);
//    }
}