package me.exrates.dao.configuration;

public interface DatabaseConfig {

    String getUrl();

    String getDriverClassName();

    String getUser();

    String getPassword();
}
