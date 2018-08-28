package me.exrates.dao;

import me.exrates.model.kyc.WorldCheck;
import me.exrates.model.kyc.WorldCheckStatus;

public interface WorldCheckDao {
    int save(int userId, WorldCheck worldCheck);
    WorldCheck getWorldCheck(int userId);
    void setStatus(int userId, WorldCheckStatus status, String admin);
}
