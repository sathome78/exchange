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
import org.springframework.stereotype.Service;

import java.util.Objects;

import static java.util.Objects.isNull;

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

//    @Override
//    public void sendOrderInfo(InputCreateOrderDto inputOrder, String queueName) {
//        try {
//            String orderJson = mapper.writeValueAsString(inputOrder);
//            log.info("Sending order to demo-server {}", orderJson);
//
//            try {
//                byte[] bytes = (byte[]) this.rabbitTemplate.convertSendAndReceive(queueName, orderJson);
//                String result = new String(bytes);
//                log.info("Return from demo-server {}", result);
//            } catch (AmqpException e) {
//                String msg = "Failed to send data via rabbit queue";
//                log.error(msg + " " + orderJson, e);
//                throw new RabbitMqException(msg);
//            }
//        } catch (JsonProcessingException e) {
//            log.error("Failed to send order to old instance", e);
//        }
//    }

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
}