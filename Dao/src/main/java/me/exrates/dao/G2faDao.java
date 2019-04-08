package me.exrates.dao;

public interface G2faDao {

    String getGoogleAuthSecretCodeByUser(Integer userId);

    void setGoogleAuthSecretCode(Integer userId);

    void setGoogleAuthSecretCode(Integer userId, String secretCode);

    void setEnable2faGoogleAuth(Integer userId, Boolean connection);

    boolean isGoogleAuthenticatorEnable(Integer userId);

    void updateGoogleAuthSecretCode(Integer userId, String secretCode, boolean enabled);
}
