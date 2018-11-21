package me.exrates.ngcontroller.service;

import me.exrates.ngcontroller.model.RefillPendingRequestDto;

import java.util.List;

public interface RefillPendingRequestService {
    List<RefillPendingRequestDto> getPendingRefillRequests(long userId);
}
