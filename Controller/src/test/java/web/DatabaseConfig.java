package web;

public interface DatabaseConfig {
    String getSchema();

    String getUrl();

    String getDriverClassName();

    String getUser();

    String getPassword();
}
