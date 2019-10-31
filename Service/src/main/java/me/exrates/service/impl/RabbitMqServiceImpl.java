package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.ExOrder;
import me.exrates.service.RabbitMqService;
import me.exrates.service.chart.OrderDataDto;
import me.exrates.service.exception.RabbitMqException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

@EnableScheduling
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

//    @Scheduled(initialDelay = 0, fixedDelay = 5000)
//    @Override
//    public void generateNewTrade() {
//        initData();
//    }

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

//    private void initData() {
//        Random r = new Random();
//
//        final OrderDataDto orderDataDto = OrderDataDto.builder()
//                .pairName("BTC/USD")
//                .exrate(BigDecimal.valueOf(9000.0 + (12000.0 - 9000.0) * r.nextDouble()))
//                .amountBase(BigDecimal.valueOf(0.0 + (100.0 - 0.0) * r.nextDouble()))
//                .amountConvert(BigDecimal.valueOf(0.0 + (100.0 - 0.0) * r.nextDouble()))
//                .tradeDate(LocalDateTime.now())
//                .build();
//
//        log.info("Start sending trade data to chart service");
//        try {
//            rabbitTemplate.convertAndSend(chartQueue, orderDataDto);
//
//            log.info("End sending trade data to chart service");
//        } catch (AmqpException ex) {
//            throw new RabbitMqException("Failed to send data via rabbit queue");
//        }
//    }
}