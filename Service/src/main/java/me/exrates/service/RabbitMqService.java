package me.exrates.service;

import me.exrates.model.dto.InputCreateOrderDto;

public interface RabbitMqService {

    String ANGULAR_QUEUE = "angular-queue";
    String JSP_QUEUE = "jsp-queue";

    void sendOrderInfo(InputCreateOrderDto inputOrder, String queueName);
}