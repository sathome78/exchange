package config;

import java.util.Properties;

public final class DatabaseConfigImpl implements DatabaseConfig {

    private final String DB_URL;
    private final String DB_DRIVER_CLASSNAME;
    private final String DB_USERNAME;
    private final String DB_PASSWORD;
    private final String DB_SCHEME;
    private final String DB_ROOT_SCHEME;

    DatabaseConfigImpl(DatabaseConfig config) {
        this.DB_URL = config.getUrl();
        this.DB_DRIVER_CLASSNAME = config.getDriverClassName();
        this.DB_USERNAME = config.getUser();
        this.DB_PASSWORD = config.getPassword();
        this.DB_SCHEME = config.getSchemaName();
        this.DB_ROOT_SCHEME = config.getRootSchemeName();
    }

    DatabaseConfigImpl(Properties properties, String schemaName) {
        this.DB_URL = properties.getProperty("db.master.url");
        this.DB_DRIVER_CLASSNAME = properties.getProperty("db.master.classname");
        this.DB_USERNAME = properties.getProperty("db.master.user");
        this.DB_PASSWORD = properties.getProperty("db.master.password");
        this.DB_ROOT_SCHEME = properties.getProperty("db.root.name");
        this.DB_SCHEME = schemaName;
    }

    @Override
    public String getUrl() {
        return this.DB_URL;
    }

    @Override
    public String getDriverClassName() {
        return this.DB_DRIVER_CLASSNAME;
    }

    @Override
    public String getUser() {
        return this.DB_USERNAME;
    }

    @Override
    public String getPassword() {
        return this.DB_PASSWORD;
    }

    @Override
    public String getSchemaName() {
        return this.DB_SCHEME;
    }

    @Override
    public String getRootSchemeName() {
        return this.DB_ROOT_SCHEME;
    }
}
