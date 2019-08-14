package me.exrates.service;


import me.exrates.model.ExOrder;

public interface RabbitMqService {

    String REFILL_QUEUE = "refill";

//    void sendOrderInfo(InputCreateOrderDto inputOrder, String queueName);

    void sendTradeInfo(ExOrder order);
}