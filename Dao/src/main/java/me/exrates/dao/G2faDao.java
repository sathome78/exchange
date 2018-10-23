package me.exrates.dao;

import java.util.Set;

public interface G2faDao {
    String getGoogleAuthSecretCodeByUser(Integer userId);

    void setGoogleAuthSecretCode(Integer userId);

    void set2faGoogleAuthenticator(Integer userId);

    void setEnable2faGoogleAuth(Integer userId, Boolean connection);

    boolean isGoogleAuthenticatorEnable(Integer userId);

    Set<Integer> getUsersWithout2faGoogleAuth();
}
