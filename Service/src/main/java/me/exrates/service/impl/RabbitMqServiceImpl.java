package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.chart.CandleDetailedDto;
import me.exrates.service.RabbitMqService;
import me.exrates.service.stomp.StompMessenger;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@EnableRabbit
@Service
public class RabbitMqServiceImpl implements RabbitMqService {

    private final StompMessenger stompMessenger;

    @Autowired
    public RabbitMqServiceImpl(StompMessenger stompMessenger) {
        this.stompMessenger = stompMessenger;
    }

    @RabbitListener(queues = "${rabbit.candles.topic}")
    public void processRefillEvent(CandleDetailedDto dto) {
        try {
            /*todo synchronize here by pair and interval*/
            stompMessenger.sendLastCandle(dto);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }
}
