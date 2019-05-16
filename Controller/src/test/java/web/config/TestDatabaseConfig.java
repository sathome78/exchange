package web.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import framework.model.impl.DatabaseConfigImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan({"me.exrates.ngcontroller"})
@PropertySource(value = {"classpath:/db.properties"})
public class TestDatabaseConfig {
    private final static String SCHEMA_NAME = "birzha";
    private final static String VERIFICATION_TABLE_NAME = "user";

    @Value("#{systemProperties['db.master.url'] ?: 'jdbc:mysql://localhost:3306/birzha?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true'}")
    private String url;

    @Value("#{systemProperties['db.master.classname'] ?: 'com.mysql.jdbc.Driver'}")
    private String driverClassName;

    @Value("${db.master.user:root}")
    private String user;

    @Value("${db.master.password:root}")
    private String password;

    @PostConstruct
    protected void initialize() {
        try {
            DatabaseMetaData md = dataSource().getConnection().getMetaData();
            ResultSet rs = md.getTables(null, null, VERIFICATION_TABLE_NAME, null);
            if (rs.first()) {
                System.out.println("\nDatabase structure exists!!!\n");
            } else {
                System.out.println("\nCreating Database structure!!!\n");
                dataSourceInitializer();
            }
        } catch (SQLException e) {
            throw new RuntimeException("There was an error creating an Database Connectivity.");
        }
    }

    @Bean
    public DatabaseConfig databaseConfig() {
        DatabaseConfigImpl config = new DatabaseConfigImpl();
        config.setUrl(url);
        config.setSchema(SCHEMA_NAME);
        config.setDriverClassName(driverClassName);
        config.setPassword(password);
        config.setUser(user);
        return config;
    }

    @Bean(name = "testDataSource")
    public DataSource dataSource() {
        String dbUrl = createConnectionURL(this.url, SCHEMA_NAME);
        return createDataSource(this.user, this.password, dbUrl);
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer() {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("/initdb/init_structure.sql"));

        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource());
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        return dataSourceInitializer;
    }

    private static String createConnectionURL(String dbUrl, String newSchemaName) {
        return dbUrl.replace(SCHEMA_NAME, newSchemaName);
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
