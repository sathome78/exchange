package me.exrates.dao;

public interface GtagRefillRequests {


    void updateUserRequestsCount(Integer username);

    Integer getUserRequestsCount(Integer username);

    void resetCount(Integer username);

    Integer getUserIdOfGtagRequests(Integer userName);

    void addFirstCount(Integer userName);

}
