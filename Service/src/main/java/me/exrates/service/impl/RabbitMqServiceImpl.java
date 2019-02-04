package me.exrates.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.dto.RabbitResponse;
import me.exrates.model.exceptions.RabbitMqException;
import me.exrates.service.RabbitMqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
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
    public RabbitResponse sendOrderInfo(InputCreateOrderDto inputOrder, String queueName) {
        String processId = UUID.randomUUID().toString();
        Message messageBack = null;

        try {
            String orderJson = objectMapper.writeValueAsString(inputOrder);
            Message message = MessageBuilder
                    .withBody(orderJson.getBytes())
                    .setHeader("process-id", processId)
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();

            logger.info("{} Send order to old version {}", processId, orderJson);
            try {
                logger.info("{} Going to send by rabbit", processId);
                messageBack = this.rabbitTemplate.sendAndReceive(queueName, message);

            } catch (Exception e) {
                String msg = "Failed to send data via rabbit queue ";
                logger.error("{} {} {} {}", processId, msg, orderJson, e);
                throw new RabbitMqException(msg);
            }
        } catch (JsonProcessingException e) {
            logger.error("{} Failed to send order to old instance", processId, e);
        }

        String process = "undefined";
        String response = null;
        try {
            if (messageBack != null) {
                process = (String) messageBack.getMessageProperties().getHeaders().get("process-id");
            }
            response = new String(messageBack.getBody());
        } catch (Exception e) {
            logger.error("{} Don't wait response {}", processId, e);
            return new RabbitResponse(false, process, "Don't wait response");
        }
        logger.info("{} Result from old-server {}", process, response);

        return new RabbitResponse(response.equalsIgnoreCase("success"), process);
    }
}
