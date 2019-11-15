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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@EnableRabbit
@PropertySource(value = {"classpath:/rabbit.properties"})
@Log4j2
@Service
public class RabbitMqServiceImpl implements RabbitMqService {

    private final RabbitTemplate rabbitTemplate;

    private String chartQueue;
    private String externalQueue;

    @Autowired
    public RabbitMqServiceImpl(RabbitTemplate rabbitTemplate,
                               @Value("${rabbit.chart.queue}") String chartQueue,
                               @Value("${rabbit.external.queue}") String externalQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.chartQueue = chartQueue;
        this.externalQueue = externalQueue;
    }

    @Override
    public void sendOrderInfoToChartService(ExOrder order) {
//        log.info("Start sending order data to chart service");
//        try {
//            rabbitTemplate.convertAndSend(chartQueue, new OrderDataDto(order));
//
//            log.info("End sending order data to chart service");
//        } catch (AmqpException ex) {
//            throw new RabbitMqException("Failed to send order data via rabbit queue to chart service");
//        }
    }

    @Override
    public void sendOrderInfoToExternalService(ExOrder order) {
//        log.info("Start sending order data to external service");
//        try {
//            rabbitTemplate.convertAndSend(externalQueue, new OrderDataDto(order));
//
//            log.info("End sending order data to external service");
//        } catch (AmqpException ex) {
//            throw new RabbitMqException("Failed to send data via rabbit queue to external service");
//        }
    }

    //todo: test
    @Scheduled(initialDelay = 0, fixedDelay = 100)
    @Override
    public void generateNewTrade() {
        initData("BTC/USD", 4000.0, 12000.0);
        initData("BTC/USDT", 4500.0, 12000.0);
        initData("ETH/USD", 100.0, 200.0);
        initData("ETH/USDT", 110.0, 210.0);
    }

    private void initData(String currencyPairName, double min, double max) {
        Random r = new Random();

        final OrderDataDto orderDataDto = OrderDataDto.builder()
                .currencyPairName(currencyPairName)
                .exrate(BigDecimal.valueOf(min + (max - min) * r.nextDouble()))
                .amountBase(BigDecimal.valueOf(0.0 + (100.0 - 0.0) * r.nextDouble()))
                .amountConvert(BigDecimal.valueOf(0.0 + (100.0 - 0.0) * r.nextDouble()))
                .tradeDate(LocalDateTime.now())
                .build();

        log.info("Start sending trade data to chart service");
        try {
            rabbitTemplate.convertAndSend(chartQueue, orderDataDto);

            log.info("End sending trade data to chart service");
        } catch (AmqpException ex) {
            throw new RabbitMqException("Failed to send data via rabbit queue");
        }
    }
}