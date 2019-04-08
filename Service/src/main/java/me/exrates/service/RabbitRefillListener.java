package me.exrates.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.vo.WalletOperationMsDto;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;


@Log4j2(topic = "rabbit_refill")
@EnableRabbit
@Component
@RequiredArgsConstructor
@Conditional(MicroserviceConditional.class)
public class RabbitRefillListener {

    private final RefillService refillService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMqService.REFILL_QUEUE)
    public void processRefillEvent(String walletOperationMsDto) {
        try {
            log.info("Receivied: " + walletOperationMsDto);
            refillService.processRefillRequest(objectMapper.readValue(walletOperationMsDto, WalletOperationMsDto.class));
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }
}