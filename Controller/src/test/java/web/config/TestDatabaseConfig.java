package web.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import framework.model.impl.DatabaseConfigImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan({"me.exrates.ngcontroller"})
@PropertySource(value = {"classpath:/db.properties"})
public class TestDatabaseConfig {

    @Value("#{systemProperties['db.master.url'] ?: 'jdbc:mysql://localhost:3306/birzha?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true'}")
    private String url;

    @Value("#{systemProperties['db.master.classname'] ?: 'com.mysql.jdbc.Driver'}")
    private String driverClassName;

    @Value("${db.master.user:root}")
    private String user;

    @Value("${db.master.password:root}")
    private String password;

    private static String schemaName = "birzha";

    @Bean
    public DatabaseConfig databaseConfig() {
        DatabaseConfigImpl config = new DatabaseConfigImpl();
        config.setUrl(url);
        config.setSchema(schemaName);
        config.setDriverClassName(driverClassName);
        config.setPassword(password);
        config.setUser(user);
        return config;
    }

    @Bean(name = "testDataSource")
    public DataSource dataSource() {
        String dbUrl = createConnectionURL(this.url, schemaName);
        return createDataSource(this.user, this.password, dbUrl);
    }

    private static String createConnectionURL(String dbUrl, String newSchemaName) {
        return dbUrl.replace(schemaName, newSchemaName);
    }

    private HikariDataSource createDataSource(String user, String password, String url) {
        HikariConfig config = new HikariConfig();
        config.setInitializationFailTimeout(-1);
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setRegisterMbeans(true);
        config.setMaximumPoolSize(2);
        config.setLeakDetectionThreshold(TimeUnit.MILLISECONDS.convert(45, TimeUnit.SECONDS));
        config.setMinimumIdle(1);
        config.setIdleTimeout(30000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        return new HikariDataSource(config);
    }
}
