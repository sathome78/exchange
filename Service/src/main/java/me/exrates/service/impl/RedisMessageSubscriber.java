package me.exrates.service.impl;

import com.antkorwin.xsync.XSync;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.chart.CandleDetailedDto;
import me.exrates.service.stomp.StompMessenger;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;


@Log4j2
public class RedisMessageSubscriber implements MessageListener {

    private final StompMessenger stompMessenger;
    private ObjectMapper objectMapper;
    private XSync<String> xSync;

    public RedisMessageSubscriber(StompMessenger stompMessenger) {
        this.stompMessenger = stompMessenger;
        this.objectMapper = new ObjectMapper();
        this.xSync = new XSync<>();
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            final CandleDetailedDto dto = objectMapper.readValue(message.toString(), CandleDetailedDto.class);
            String key = dto.getPairName().concat(dto.getBackDealInterval());
            xSync.execute(key, () -> {
                stompMessenger.sendLastCandle(dto);
            });
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

}
