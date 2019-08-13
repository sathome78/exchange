package me.exrates.service.vo;

public interface PersonalOrderRefreshDelayHandler {
    void onEvent(int userId, String currencyPairName);
}
