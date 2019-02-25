package me.exrates.dao.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@PropertySource(value = "classpath:/test-db.properties")
public class TestConfiguration {

    @Value("${db.user}")
    private String dbSlaveUser;
    @Value("${db.password}")
    private String dbSlavePassword;
    @Value("${db.url}")
    private String dbSlaveUrl;
    @Value("${db.classname}")
    private String dbSlaveClassname;

    @Bean(name = "slaveDataSource")
    public DataSource slaveDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(dbSlaveClassname);
        hikariConfig.setJdbcUrl(dbSlaveUrl);
        hikariConfig.setUsername(dbSlaveUser);
        hikariConfig.setPassword(dbSlavePassword);
        hikariConfig.setMaximumPoolSize(50);
        hikariConfig.setReadOnly(true);
        return new HikariDataSource(hikariConfig);
    }

    @DependsOn("slaveDataSource")
    @Bean(name = "slaveTemplate")
    public NamedParameterJdbcTemplate slaveNamedParameterJdbcTemplate(@Qualifier("slaveDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "masterDataSource")
    public DataSource masterDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(dbSlaveClassname);
        hikariConfig.setJdbcUrl(dbSlaveUrl);
        hikariConfig.setUsername(dbSlaveUser);
        hikariConfig.setPassword(dbSlavePassword);
        hikariConfig.setMaximumPoolSize(50);
        hikariConfig.setReadOnly(true);
        return new HikariDataSource(hikariConfig);
    }

    @DependsOn("masterDataSource")
    @Bean(name = "masterTemplate")
    public NamedParameterJdbcTemplate masterNamedParameterJdbcTemplate(@Qualifier("masterDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}