package framework.model.impl;

import web.config.DatabaseConfig;

public class DatabaseConfigImpl implements DatabaseConfig {

    private String url;
    private String driverClassName;
    private String user;
    private String password;
    private String schema;

    @Override
    public String getSchema() {
        return this.schema;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public String getDriverClassName() {
        return this.driverClassName;
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
