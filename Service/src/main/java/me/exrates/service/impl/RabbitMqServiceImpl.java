package me.exrates.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.service.RabbitMqService;
import me.exrates.service.exception.RabbitMqException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqServiceImpl implements RabbitMqService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqServiceImpl.class);

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    public RabbitMqServiceImpl(ObjectMapper objectMapper,
                               RabbitTemplate rabbitTemplate) {
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendOrderInfo(InputCreateOrderDto inputOrder, String queueName) {
        try {
            String orderJson = objectMapper.writeValueAsString(inputOrder);
            Message message = MessageBuilder
                    .withBody(orderJson.getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();
            try {
                this.rabbitTemplate.convertAndSend(queueName, message);
            } catch (AmqpException e) {
                String msg = "Failed to send data via rabbit queue";
                logger.error(msg + " " + orderJson, e);
                throw new RabbitMqException(msg);
            }
        } catch (JsonProcessingException e) {
            logger.error("Failed to send order to old instance", e);
        }

    }
}