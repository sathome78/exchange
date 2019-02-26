package me.exrates.service.impl;

import me.exrates.dao.GtagRefillRequests;
import me.exrates.service.GtagRefillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GtagRefillServiceImpl implements GtagRefillService {

    @Autowired
    private GtagRefillRequests gtagRefillRequests;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Integer getUserRequests(String username) {
        Integer count = gtagRefillRequests.getUserRequestsCount(username);
        gtagRefillRequests.resetCount(username);
        return count;
    }
}
