package me.exrates.ngcontroller.service;

import me.exrates.model.User;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.ngcontroller.mobel.InputCreateOrderDto;

public interface NgOrderService {
    OrderCreateDto prepareOrder(InputCreateOrderDto inputOrder);

    boolean processUpdateOrder(User user, InputCreateOrderDto inputOrder);

    boolean processUpdateStopOrder(User user, InputCreateOrderDto inputOrder);

    WalletsAndCommissionsForOrderCreationDto getWalletAndCommision();
}
