package me.exrates.service;

import me.exrates.model.kyc.WorldCheck;

public interface WorldCheckService {
    WorldCheck getWorldCheck(int userId);
    void approveWorldCheck(int userId, String admin);
    void rejectWorldCheck(int userId, String admin);
}
