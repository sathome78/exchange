package me.exrates.ngcontroller.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.config.RabbitConfig;
import me.exrates.ngcontroller.model.InputCreateOrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageListener {

    static final Logger logger = LoggerFactory.getLogger(OrderMessageListener.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrderMessageListener(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitConfig.ANGULAR_QUEUE)
    public void processOrder(InputCreateOrderDto order) {
        try {
            String orderJson = objectMapper.writeValueAsString(order);
            Message message = MessageBuilder
                    .withBody(orderJson.getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();
            this.messagingTemplate.convertAndSend("/topic/rabbit", message);
        } catch (JsonProcessingException e) {
            logger.error("Failed to redirect to rabbit topic", e);
        }
        logger.debug("Order Received: " + order);
    }

    @RabbitListener(queues = RabbitConfig.JSP_QUEUE)
    public void processOrder2(InputCreateOrderDto order) {
        logger.debug("Order Received: " + order);
    }
}