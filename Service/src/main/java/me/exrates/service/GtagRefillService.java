package me.exrates.service;

public interface GtagRefillService {

    Integer getUserRequests(String email);

    void resetCount(String principalEmail);

}
