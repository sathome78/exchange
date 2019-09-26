package me.exrates.dao;

import java.util.Map;

public interface SettingsEmailRepository {
    Map<String, String> getAllEmailSenders();

    boolean addNewHost(String host, String emailSender);

    String getEmailSenderByHost(String host);
}
