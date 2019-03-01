package me.exrates.service;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.InputCreateOrderDto;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static me.exrates.model.enums.OrderBaseType.convert;

@Log4j2
@EnableRabbit
@Component
public class RefillJspListener {


    @RabbitListener(queues = RabbitMqService.REFILL_QUEUE)
    public void processOrderSelf(String message) {
        System.out.println(message);
    }
}