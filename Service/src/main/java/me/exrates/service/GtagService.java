package me.exrates.service;

public interface GtagService {

    void sendGtagEvents(String coinsCount, String tiker, String gaTag);

    void sendTradeEvent(int userID);
}
