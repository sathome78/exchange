package config;

public interface DatabaseConfig {

    String getUrl();

    String getDriverClassName();

    String getUser();

    String getPassword();

    String getSchemaName ();
}
