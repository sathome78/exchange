package me.exrates.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.MerchantOperationDto;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RabbitServiceImpl implements RabbitService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitServiceImpl(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void send(String route, Object data){
        rabbitTemplate.convertAndSend(route, data);
    }

    @Override
    public void sendAcceptMerchantEvent(MerchantOperationDto dto) {
        try {
            log.info("sendAcceptMerchantEvent(): " + dto);
            send(MERCHANTS_QUEUE, dto);
        } catch (Exception e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private String toJson(Object dto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dto);
    }
}
