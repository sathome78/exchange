package me.exrates.service.impl;

import com.antkorwin.xsync.XSync;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.chart.CandleDetailedDto;
import me.exrates.service.stomp.StompMessenger;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class RedisMessageSubscriber implements MessageListener {

    private final StompMessenger stompMessenger;
    private final ObjectMapper objectMapper;
    private final XSync<String> xSync;

    public RedisMessageSubscriber(StompMessenger stompMessenger,
                                  ObjectMapper objectMapper) {
        this.stompMessenger = stompMessenger;
        this.objectMapper = objectMapper;
        this.xSync = new XSync<>();
    }

    @Override
    public void onMessage(final Message message, final byte[] pattern) {
        try {
            CandleDetailedDto dto = objectMapper.readValue(message.toString(), CandleDetailedDto.class);

            final String key = dto.getPairName().concat(dto.getResolution());

            xSync.execute(key, () -> stompMessenger.sendLastCandle(dto));
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
        }
    }
}