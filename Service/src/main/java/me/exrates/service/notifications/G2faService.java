package me.exrates.service.notifications;

import me.exrates.model.User;
import me.exrates.model.dto.Generic2faResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface G2faService {
    String generateQRUrl(String userEmail, String secretCode) throws UnsupportedEncodingException;

    String getGoogleAuthenticatorCode(Integer userId);

    void updateGoogleAuthenticatorSecretCodeForUser(Integer userId);

    boolean isGoogleAuthenticatorEnable(Integer userId);

    boolean isGoogleAuthenticatorEnable(String email);

    boolean checkGoogle2faVerifyCode(String verificationCode, Integer userId);

    void setEnable2faGoogleAuth(Integer userId, Boolean connection);

    Generic2faResponseDto getGoogleAuthenticatorCodeNg(Integer userId);

    void sendGoogleAuthPinConfirm(User user, HttpServletRequest request);

    boolean submitGoogleSecret(User user, Map<String, String> body);

    boolean disableGoogleAuth(User user, Map<String, String> body);
}
