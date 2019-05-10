package me.exrates.service.logs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.model.dto.logging.LogsWrapper;
import me.exrates.service.exception.RabbitMqException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@PropertySource("classpath:/rabbit.properties")
@Component
public class UserLogsHandlerImpl implements UserLogsHandler {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbit.user_logs.queue}")
    private String queueName;

    public UserLogsHandlerImpl(ObjectMapper objectMapper,
                               RabbitTemplate rabbitTemplate) {
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }


    @Override
    public void onUserLogEvent(LogsWrapper logsWrapper) {
        CompletableFuture.runAsync(() -> sendToQueue(logsWrapper, queueName));
    }


    private void sendToQueue(LogsWrapper logsWrapper, String queueName) {
        try {
            String orderJson = objectMapper.writeValueAsString(logsWrapper);
            try {
                System.out.println("send to" + queueName + " " + logsWrapper);
                this.rabbitTemplate.convertAndSend(queueName, orderJson);
            } catch (AmqpException e) {
                String msg = "Failed to send data via rabbit queue";
                System.out.println(msg);
                throw new RabbitMqException(msg);
            }
        } catch (JsonProcessingException e) {
            /*ignore*/
        }
    }



}
