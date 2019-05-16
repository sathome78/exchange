package me.exrates.service;


import me.exrates.model.dto.MerchantOperationDto;

public interface RabbitService {

    String REFILL_QUEUE = "refill";
    String MERCHANTS_QUEUE = "merchants";

    void send(String route, Object data);

    void sendAcceptMerchantEvent(MerchantOperationDto dto);
}