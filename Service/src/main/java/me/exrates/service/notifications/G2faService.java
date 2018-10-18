package me.exrates.service.notifications;

import java.io.UnsupportedEncodingException;

public interface G2faService {
    String generateQRUrl(String userEmail) throws UnsupportedEncodingException;

    String getGoogleAuthenticatorCode(Integer userId);

    void updateGoogleAuthenticatorSecretCodeForUser(Integer userId);

    boolean isGoogleAuthenticatorEnable(Integer userId);

    boolean checkGoogle2faVerifyCode(String verificationCode, Integer userId);

    void setEnable2faGoogleAuth(Integer userId, Boolean connection);
}
