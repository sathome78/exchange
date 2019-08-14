package me.exrates.service.impl;

import com.antkorwin.xsync.XSync;
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
    private XSync<String> xSync;

    @Autowired
    public RabbitMqServiceImpl(StompMessenger stompMessenger) {
        this.stompMessenger = stompMessenger;
        xSync = new XSync<>();
    }

    @RabbitListener(queues = "${rabbit.candles.topic}")
    public void processRefillEvent(CandleDetailedDto dto) {
        String key = dto.getPairName().concat(dto.getBackDealInterval().getInterval());
        xSync.execute(key, () -> {
            try {
                stompMessenger.sendLastCandle(dto);
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        });
    }
}
