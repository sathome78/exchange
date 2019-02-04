package me.exrates.service;


import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.dto.RabbitResponse;

public interface RabbitMqService {

    String ANGULAR_QUEUE = "angular-queue";
    String JSP_QUEUE = "jsp-queue";

    RabbitResponse sendOrderInfo(InputCreateOrderDto inputOrder, String queueName);
}
