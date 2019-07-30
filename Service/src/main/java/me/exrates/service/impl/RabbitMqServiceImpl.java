package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.service.CurrencyService;
import me.exrates.service.RabbitMqService;
import me.exrates.service.chart.TradeDataDto;
import me.exrates.service.exception.RabbitMqException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class RabbitMqServiceImpl implements RabbitMqService {

    private final CurrencyService currencyService;
    private final RabbitTemplate rabbitTemplate;

    private String chartQueue;

    public RabbitMqServiceImpl(CurrencyService currencyService,
                               RabbitTemplate rabbitTemplate,
                               @Value("${rabbit.chart.queue}") String chartQueue) {
        this.currencyService = currencyService;
        this.rabbitTemplate = rabbitTemplate;
        this.chartQueue = chartQueue;
    }

    @Override
    public void sendTradeInfo(ExOrder order) {
        CurrencyPair currencyPair = currencyService.findCurrencyPairById(order.getCurrencyPairId());

        final TradeDataDto tradeDataDto = new TradeDataDto(order);
        tradeDataDto.setPairName(currencyPair.getName());

        log.info("Start sending trade data to chart service");
        try {
            rabbitTemplate.convertAndSend(chartQueue, tradeDataDto);

            log.info("End sending trade data to chart service");
        } catch (AmqpException ex) {
            throw new RabbitMqException("Failed to send data via rabbit queue");
        }
    }
}