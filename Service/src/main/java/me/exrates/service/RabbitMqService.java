package me.exrates.service;


import me.exrates.model.ExOrder;

public interface RabbitMqService {

    void sendTradeInfo(ExOrder order);
}