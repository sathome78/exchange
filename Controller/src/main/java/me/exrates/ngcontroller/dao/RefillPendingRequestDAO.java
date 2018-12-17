package me.exrates.ngcontroller.dao;

import me.exrates.ngcontroller.model.RefillPendingRequestDto;

import java.util.List;

public interface RefillPendingRequestDAO {

    List<RefillPendingRequestDto> getPendingRefillRequests(long userId, List<Integer> withdrawRequestStatuses,
                                                           List<Integer> refillRequestStatuses,
                                                           List<Integer> transferStatuses);
}
