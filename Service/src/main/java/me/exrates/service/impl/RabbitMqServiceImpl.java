package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.ExOrder;
import me.exrates.service.RabbitMqService;
import me.exrates.service.chart.OrderDataDto;
import me.exrates.service.exception.RabbitMqException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@EnableRabbit
@PropertySource(value = {"classpath:/rabbit.properties"})
@Log4j2
@Service
public class RabbitMqServiceImpl implements RabbitMqService {

    private final RabbitTemplate rabbitTemplate;

    private String chartQueue;

    @Autowired
    public RabbitMqServiceImpl(RabbitTemplate rabbitTemplate,
                               @Value("${rabbit.chart.queue}") String chartQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.chartQueue = chartQueue;
    }

    @Override
    public void sendOrderInfo(ExOrder order) {
        log.info("Start sending order data to chart service");
        try {
            rabbitTemplate.convertAndSend(chartQueue, new OrderDataDto(order));

            log.info("End sending order data to chart service");
        } catch (AmqpException ex) {
            throw new RabbitMqException("Failed to send order data via rabbit queue");
        }
    }
}