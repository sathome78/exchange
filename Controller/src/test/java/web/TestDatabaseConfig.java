package web;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class TestDatabaseConfig implements DatabaseConfig {

    @Bean
    @Qualifier("testMasterDatasource")
    public DataSource dataSource() {

    }

    @Bean
    @Qualifier("testSlaveDatasource")
    public DataSource dataSource() {

    }

    @Override
    public String getSchema() {
        return null;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public String getDriverClassName() {
        return null;
    }

    @Override
    public String getUser() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }
}
