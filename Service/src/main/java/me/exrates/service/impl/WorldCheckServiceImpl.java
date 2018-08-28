package me.exrates.service.impl;

import me.exrates.dao.WorldCheckDao;
import me.exrates.model.kyc.WorldCheck;
import me.exrates.model.kyc.WorldCheckStatus;
import me.exrates.service.WorldCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorldCheckServiceImpl implements WorldCheckService {

    @Autowired
    private WorldCheckDao worldCheckDao;

    @Override
    @Transactional(readOnly = true)
    public WorldCheck getWorldCheck(int userId) {
        return worldCheckDao.getWorldCheck(userId);
    }

    @Override
    @Transactional
    public void approveWorldCheck(int userId, String admin) {
        if (worldCheckDao.getWorldCheck(userId) != null) {
            worldCheckDao.setStatus(userId, WorldCheckStatus.APPROVED, admin);
        } else {
            WorldCheck wc = new WorldCheck();
            wc.setStatus(WorldCheckStatus.APPROVED);
            wc.setAdmin(admin);
            worldCheckDao.save(userId, wc);
        }
    }

    @Override
    @Transactional
    public void rejectWorldCheck(int userId, String admin) {
        if (worldCheckDao.getWorldCheck(userId) != null) {
            worldCheckDao.setStatus(userId, WorldCheckStatus.REJECTED, admin);
        } else {
            WorldCheck wc = new WorldCheck();
            wc.setStatus(WorldCheckStatus.REJECTED);
            wc.setAdmin(admin);
            worldCheckDao.save(userId, wc);
        }
    }
}
