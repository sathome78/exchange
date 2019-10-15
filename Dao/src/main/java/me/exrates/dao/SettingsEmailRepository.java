package me.exrates.dao;

import me.exrates.model.EmailRule;

import java.util.List;

public interface SettingsEmailRepository {
    List<EmailRule> getAllEmailSenders();

    boolean addNewHost(String host, String emailSender);

    String getEmailSenderByHost(String host);

    boolean deleteEmailRule(String host);

    boolean updateEmailRule(String host, String emailSender);
}
