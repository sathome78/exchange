package me.exrates.ngcontroller.service;

import me.exrates.model.dto.OrderCreateDto;
import me.exrates.ngcontroller.mobel.InputCreateOrderDto;

public interface NgOrderService {
    OrderCreateDto prepareOrder(InputCreateOrderDto inputOrder);

    boolean completeDeleteOrder(int idOrder);
}
