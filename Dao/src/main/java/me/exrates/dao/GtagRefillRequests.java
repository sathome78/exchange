package me.exrates.dao;

public interface GtagRefillRequests {

    void updateUserRequestsCount(String username);

    Integer getUserRequestsCount(String username);

    void resetCount(String username);
}
