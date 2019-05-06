package me.exrates.dao.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/db.properties")
public class TestDatabaseConfig {

    @Value("#{systemProperties['db.master.url'] ?: 'jdbc:mysql://localhost:3306/birzha?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true'}")
    private String url;

    @Value("#{systemProperties['db.master.classname'] ?: 'com.mysql.jdbc.Driver'}")
    private String driverClassName;

    @Value("${db.master.user:root}")
    private String user;

    @Value("${db.master.password:root}")
    private String password;

    @Bean
    public DatabaseConfig databaseConfig() {
        return new DatabaseConfig() {
            @Override
            public String getUrl() {
                return url;
            }

            @Override
            public String getDriverClassName() {
                return driverClassName;
            }

            @Override
            public String getUser() {
                return user;
            }

            @Override
            public String getPassword() {
                return password;
            }
        };
    }
}
