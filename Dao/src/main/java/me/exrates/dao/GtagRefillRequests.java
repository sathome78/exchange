package me.exrates.dao;

public interface GtagRefillRequests {


    void updateUserRequestsCount(Integer username);

    Integer getUserRequestsCount(Integer userId);

    void resetCount(Integer username);

    Integer getUserIdOfGtagRequests(Integer userName);

    void addFirstCount(Integer userName);

}
