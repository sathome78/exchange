package me.exrates.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.exceptions.RabbitMqException;
import me.exrates.service.RabbitMqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
    public String sendOrderInfo(InputCreateOrderDto inputOrder, String queueName) {
        String result = "fail";
        String processId = UUID.randomUUID().toString();

        try {
            String orderJson = objectMapper.writeValueAsString(inputOrder);
            logger.info("{} Send order to old version {}", processId, orderJson);
            try {
                logger.info("{} Going to send by rabbit", processId);
                byte[] receive = (byte[]) this.rabbitTemplate.convertSendAndReceive(queueName, orderJson);
                result = new String(receive);
            } catch (AmqpException e) {
                String msg = "Failed to send data via rabbit queue ";
                logger.error("{} {} {} {}", processId, msg, orderJson, e);
                throw new RabbitMqException(msg);
            }
        } catch (JsonProcessingException e) {
            logger.error("{} Failed to send order to old instance", processId, e);
        }
        logger.info("{} Result from old-server {}", processId, result);
        return result;
    }
}
