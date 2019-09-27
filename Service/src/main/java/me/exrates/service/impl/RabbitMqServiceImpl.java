package me.exrates.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.enums.OrderStatus;
import me.exrates.service.CurrencyService;
import me.exrates.service.RabbitMqService;
import me.exrates.service.chart.TradeDataDto;
import me.exrates.service.exception.RabbitMqException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static java.util.Objects.isNull;

@EnableScheduling
@PropertySource(value = {"classpath:/rabbit.properties"})
@Log4j2
@Service
public class RabbitMqServiceImpl implements RabbitMqService {

    private final CurrencyService currencyService;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper;

    private String chartQueue;

    @Autowired
    public RabbitMqServiceImpl(CurrencyService currencyService,
                               RabbitTemplate rabbitTemplate,
                               ObjectMapper mapper,
                               @Value("${rabbit.chart.queue}") String chartQueue) {
        this.currencyService = currencyService;
        this.rabbitTemplate = rabbitTemplate;
        this.mapper = mapper;
        this.chartQueue = chartQueue;
    }

//    @Scheduled(initialDelay = 0, fixedDelay = 5000)
//    @Override
//    public void generateNewTrade() {
//        initData();
//    }

    @Override
    public void sendTradeInfo(ExOrder order) {
        if (!Objects.equals(order.getStatus(), OrderStatus.CLOSED) || isNull(order.getDateAcception())) {
            return;
        }

        final TradeDataDto tradeDataDto = new TradeDataDto(order);
        tradeDataDto.setCurrencyPairName(currencyService.findCurrencyPairById(order.getCurrencyPairId()).getName());

        log.info("Start sending trade data to chart service");
        try {
            rabbitTemplate.convertAndSend(chartQueue, tradeDataDto);

            log.info("End sending trade data to chart service");
        } catch (AmqpException ex) {
            throw new RabbitMqException("Failed to send data via rabbit queue");
        }
    }

//    private void initData() {
//        Random r = new Random();
//
//        final TradeDataDto tradeDataDto = TradeDataDto.builder()
//                .pairName("BTC/USD")
//                .exrate(BigDecimal.valueOf(9000.0 + (12000.0 - 9000.0) * r.nextDouble()))
//                .amountBase(BigDecimal.valueOf(0.0 + (100.0 - 0.0) * r.nextDouble()))
//                .amountConvert(BigDecimal.valueOf(0.0 + (100.0 - 0.0) * r.nextDouble()))
//                .tradeDate(LocalDateTime.now())
//                .build();
//
//        log.info("Start sending trade data to chart service");
//        try {
//            rabbitTemplate.convertAndSend(chartQueue, tradeDataDto);
//
//            log.info("End sending trade data to chart service");
//        } catch (AmqpException ex) {
//            throw new RabbitMqException("Failed to send data via rabbit queue");
//        }
//    }
}