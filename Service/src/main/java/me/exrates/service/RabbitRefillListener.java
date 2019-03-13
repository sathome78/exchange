package me.exrates.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.vo.WalletOperationMsDto;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Log4j2
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
            refillService.processRefillRequest(objectMapper.readValue(walletOperationMsDto, WalletOperationMsDto.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}