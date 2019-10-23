package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.tradingview.ExternalOrderDto;
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
import org.springframework.stereotype.Service;

import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@PropertySource(value = {"classpath:/rabbit.properties"})
@Log4j2
@Service
public class RabbitMqServiceImpl implements RabbitMqService {

    private final CurrencyService currencyService;
    private final RabbitTemplate rabbitTemplate;

    private String chartQueue;
    private String tvTradesQueue;
    private String tvOrdersQueue;

    @Autowired
    public RabbitMqServiceImpl(CurrencyService currencyService,
                               RabbitTemplate rabbitTemplate,
                               @Value("${rabbit.chart.queue}") String chartQueue,
                               @Value("${rabbit.tradingview.trades-queue}") String tvTradesQueue,
                               @Value("${rabbit.tradingview.orders-queue}") String tvOrdersQueue) {
        this.currencyService = currencyService;
        this.rabbitTemplate = rabbitTemplate;
        this.chartQueue = chartQueue;
        this.tvTradesQueue = tvTradesQueue;
        this.tvOrdersQueue = tvOrdersQueue;
    }

    @Override
    public void sendTradeInfo(ExOrder order) {
        if (!Objects.equals(order.getStatus(), OrderStatus.CLOSED) || isNull(order.getDateAcception())) {
            return;
        }
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

    @Override
    public void sendTradeToTradingView(ExOrder order) {
        if (!Objects.equals(order.getStatus(), OrderStatus.CLOSED) || isNull(order.getDateAcception())) {
            return;
        }
        CurrencyPair currencyPair = currencyService.findCurrencyPairById(order.getCurrencyPairId());

        final ExternalOrderDto externalOrderDto = new ExternalOrderDto(order);
        externalOrderDto.setPairName(currencyPair.getName());

        log.info("Start sending accepted order to external service");
        try {
            rabbitTemplate.convertAndSend(tvTradesQueue, externalOrderDto);

            log.info("End sending accepted order to external service");
        } catch (AmqpException ex) {
            throw new RabbitMqException("Failed to send data via rabbit queue");
        }
    }

    @Override
    public void sendOrderToTradingView(ExOrder order) {
        if (Objects.equals(order.getStatus(), OrderStatus.CLOSED) || nonNull(order.getDateAcception())) {
            return;
        }
        CurrencyPair currencyPair = currencyService.findCurrencyPairById(order.getCurrencyPairId());

        final ExternalOrderDto externalOrderDto = new ExternalOrderDto(order);
        externalOrderDto.setPairName(currencyPair.getName());

        log.info("Start sending created order to external service");
        try {
            rabbitTemplate.convertAndSend(tvOrdersQueue, externalOrderDto);

            log.info("End sending created order to external service");
        } catch (AmqpException ex) {
            throw new RabbitMqException("Failed to send data via rabbit queue");
        }
    }
}