package me.exrates.dao;

public interface IEOSubscribeRepository {

    boolean subscribeEmail(String email);

    boolean subscribeTelegram(String email);

    boolean isUserSubscribeForEmail(String email);

    boolean isUserSubscribeForTelegram(String email);

    boolean isUserSubscribe(String email);

    boolean updateSubscribeEmail(String email);

    boolean updateSubscribeTelegram(String email);
}
