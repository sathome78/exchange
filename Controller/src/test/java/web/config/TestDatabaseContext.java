package web.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import framework.model.impl.DatabaseConfigImpl;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.ResultSetMetaData;
import java.util.concurrent.TimeUnit;

@Configuration
@TestPropertySource(value = {"classpath:/db.properties"})
public class TestDatabaseContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestDatabaseContext.class);
    private final static String SCHEMA_NAME = "birzha_test";

    @Value("#{systemProperties['db.test.url'] ?: 'jdbc:mysql://localhost:3306/birzha_test?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true'}")
    private String url;

    @Value("#{systemProperties['db.test.classname'] ?: 'com.mysql.jdbc.Driver'}")
    private String driverClassName;

    @Value("${db.test.user:root}")
    private String user;

    @Value("${db.test.password:root}")
    private String password;

    @PostConstruct
    protected void checkTestDatabase() {
        if (hasStructure()) {
            LOGGER.info("Database structure exists.");
        } else {
            LOGGER.info("DB doesn't have structure.");
            throw new RuntimeException("DB doesn't have structure.");
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
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
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

    private boolean hasStructure() {
        ResultSetHandler<Object[]> handler = rs -> {
            if (!rs.next()) {
                return null;
            }

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            Object[] result = new Object[cols];

            for (int i = 0; i < cols; i++) {
                result[i] = rs.getObject(i + 1);
            }

            return result;
        };

        try {
            QueryRunner run = new QueryRunner(dataSource());
            run.query("SELECT 1 FROM USER LIMIT 1", handler);
            LOGGER.info("DB has structure.");
            return true;
        } catch (Exception ignore) {
            LOGGER.info("DB doesn't have structure.");
            return false;
        }
    }
}
