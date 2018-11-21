package me.exrates.ngcontroller.service.impl;

import me.exrates.ngcontroller.dao.RefillPendingRequestDAO;
import me.exrates.ngcontroller.model.RefillPendingRequestDto;
import me.exrates.ngcontroller.service.RefillPendingRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RefillPendingRequestServiceImpl implements RefillPendingRequestService {

    @Autowired
    RefillPendingRequestDAO refillPendingRequestDAO;

    @Override
    public List<RefillPendingRequestDto> getPendingRefillRequests(long userId) {
        return refillPendingRequestDAO.getPendingRefillRequests(userId);
    }
}
