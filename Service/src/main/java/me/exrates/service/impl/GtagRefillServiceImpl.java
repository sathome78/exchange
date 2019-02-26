package me.exrates.service.impl;

import me.exrates.dao.GtagRefillRequests;
import me.exrates.dao.UserDao;
import me.exrates.service.GtagRefillService;
import me.exrates.service.GtagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GtagRefillServiceImpl implements GtagRefillService {

    @Autowired
    private GtagRefillRequests gtagRefillRequests;


    @Autowired
    private UserDao userDao;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Integer getUserRequests(String username) {
        Integer userIdByGa = userDao.getUserIdByGa(username);

        return gtagRefillRequests.getUserRequestsCount(userIdByGa);
    }

    @Override
    public void resetCount(String username) {
        Integer userIdByGa = userDao.getUserIdByGa(username);
        gtagRefillRequests.resetCount(userIdByGa);
    }
}
