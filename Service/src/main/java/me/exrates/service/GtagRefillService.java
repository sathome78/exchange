package me.exrates.service;

public interface GtagRefillService {


    Integer getUserRequests(String username);

    void resetCount(String principalEmail);

}
