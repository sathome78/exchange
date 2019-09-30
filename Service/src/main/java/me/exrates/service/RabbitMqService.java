package me.exrates.service;


import me.exrates.model.ExOrder;

public interface RabbitMqService {

    String REFILL_QUEUE = "refill";

//    void generateNewTrade();

    void sendOrderInfo(ExOrder order);
}