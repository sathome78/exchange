package me.exrates.service.impl;

import me.exrates.dao.GtagRefillRequests;
import me.exrates.dao.UserDao;
import me.exrates.service.GtagRefillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GtagRefillServiceImpl implements GtagRefillService {

    @Autowired
    private GtagRefillRequests gtagRefillRequests;


    @Autowired
    private UserDao userDao;

    public Integer getUserRequests(String email) {
        Integer userIdByGa = userDao.findByEmail(email).getId();
        return gtagRefillRequests.getUserRequestsCount(userIdByGa);
    }

    @Override
    public void resetCount(String username) {
        Integer userIdByGa = userDao.findByEmail(username).getId();
        gtagRefillRequests.resetCount(userIdByGa);
    }
}
