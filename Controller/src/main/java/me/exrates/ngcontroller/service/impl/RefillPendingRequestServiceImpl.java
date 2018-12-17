package me.exrates.ngcontroller.service.impl;

import me.exrates.ngcontroller.dao.RefillPendingRequestDAO;
import me.exrates.ngcontroller.model.RefillPendingRequestDto;
import me.exrates.ngcontroller.service.RefillPendingRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.attribute.IntegerSyntax;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class RefillPendingRequestServiceImpl implements RefillPendingRequestService {

    @Autowired
    private RefillPendingRequestDAO refillPendingRequestDAO;

    @Override
    public List<RefillPendingRequestDto> getPendingRefillRequests(long userId) {
        /*example of statuses list
        * if statuseslist == null then this type of operation will not be queried*/
        List<Integer> refillStatuses = Arrays.asList(1,2,3,4,5,7,13,14,15,16,17);
        List<Integer> withdrawStatuses = Arrays.asList(1,2,3,4,5,6,11,13,14,15);
        List<Integer> transgerStatuses = Collections.singletonList(4);
        /*------------------------------------------------------------*/
        return refillPendingRequestDAO.getPendingRefillRequests(userId, withdrawStatuses, refillStatuses, transgerStatuses);
    }
}
