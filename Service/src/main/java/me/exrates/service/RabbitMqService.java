package me.exrates.service;


import me.exrates.model.ExOrder;
import me.exrates.model.chart.CandleDetailedDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.io.IOException;

public interface RabbitMqService {

    String REFILL_QUEUE = "refill";

//    void sendOrderInfo(InputCreateOrderDto inputOrder, String queueName);

    void sendTradeInfo(ExOrder order);

}
