package me.exrates.dao.util;

import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.configuration.DatabaseConfig;
import me.exrates.dao.configuration.TestDatabaseConfig;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static java.io.File.separator;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@Log4j2
public abstract class AbstractDatabaseContextTest {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    static final String RESOURCES_ROOT = "src/test/resources/";
    private static final File RESOURCES_FOLDER = new File(RESOURCES_ROOT);
    private static String schemaName;
    private static Connection connection;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private DatabaseConfig dbConfig;

    @Autowired
    protected DataSource dataSource;


    public AbstractDatabaseContextTest(String tetSchemaName) {
        schemaName = tetSchemaName;
    }

    private static final TimeZonePreferences TIME_ZONE_PREFERENCES = new TimeZonePreferences();


    @BeforeClass
    public static void beforeClass() {
        TIME_ZONE_PREFERENCES.saveDefault();
        System.setProperty("user.timezone", "UTC");
        TimeZone.setDefault(null);
    }

    @PostConstruct
    public void prepareTestSchema() throws SQLException {
        Preconditions.checkNotNull(schemaName, "Schema name must be defined");
        String testSchemaUrl = createConnectionURL(dbConfig.getUrl(), schemaName);
        try {
            connection = DriverManager.getConnection(testSchemaUrl, dbConfig.getUser(), dbConfig.getPassword());
        } catch (Exception e) {
            String dbServerUrl = createConnectionURL(dbConfig.getUrl(), "");
            connection = DriverManager.getConnection(dbServerUrl, dbConfig.getUser(), dbConfig.getPassword());

            Statement statement = connection.createStatement();
            statement.execute(String.format("CREATE DATABASE %s;", schemaName));

            DataSource rootDataSource = createRootDataSource(dbConfig, testSchemaUrl);

            populateSchema(rootDataSource);

            migrateSchema(rootDataSource);
        }
    }

    @AfterClass
    public static void afterClass() throws SQLException {
        TIME_ZONE_PREFERENCES.restoreDefault();
        if (StringUtils.isNotBlank(schemaName) && Objects.nonNull(connection) && !connection.isClosed()) {
            Statement statement = connection.createStatement();
            statement.execute(String.format("DROP DATABASE IF EXISTS %s;", schemaName));
        }
        DbUtils.closeQuietly(connection);
    }

    @Rule
    public final SteppedTestPath steppedTestPath = new SteppedTestPath();

    @Before
    public final void setup() throws Exception {
        createFileTreeForTesting();
    }

    @After
    public final void after() {

    }

    private void createFileTreeForTesting() throws IOException {
        Path testRootFolder = Paths.get(RESOURCES_ROOT, getClass().getSimpleName(), steppedTestPath.getMethodName());
        Path expected = testRootFolder.resolve("expected");
        Path actual = testRootFolder.resolve("actual");
        Files.createDirectories(expected);
        FileUtils.deleteQuietly(actual.toFile());
    }

    @Configuration
    @Import({
            TestDatabaseConfig.class
    })
    public static abstract class AppContextConfig {

        private static final Logger configLogger = LoggerFactory.getLogger(AppContextConfig.class);

        @Autowired
        public DatabaseConfig dbConfig;

        @Bean(name = "testDataSource")
        public DataSource dataSource() {
            String dbUrl = createConnectionURL(dbConfig.getUrl(), schemaName);
            return createDataSource(dbConfig.getUser(), dbConfig.getPassword(), dbUrl);
        }

        @Bean(name = "slaveTemplate")
        public NamedParameterJdbcOperations slaveTemplate(@Qualifier("testDataSource") DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }

        @Bean(name = "masterTemplate")
        public NamedParameterJdbcOperations masterTemplate(@Qualifier("testDataSource") DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }

        @Bean
        public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("testDataSource") DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        private HikariDataSource createDataSource(String user, String password, String url) {
            HikariConfig config = new HikariConfig();
            config.setInitializationFailTimeout(-1);
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);
//            config.setPoolName("UnitTest");
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

    protected static String formatLine(Object key, Object value) {
        return String.format("|%-30s|%-100s|", String.valueOf(key), String.valueOf(value));
    }

    protected class SteppedTestPath extends TestName {

        private MutableInt step = new MutableInt();

        String nextExpectedStepPathForMethod() {
            return getRootMethodPath() + separator + "expected" + separator + getFileName();
        }

        String getFileName() {
            return String.format("step_%d.json", step.incrementAndGet());
        }

        private String getRootMethodPath() {
            return AbstractDatabaseContextTest.this.getClass().getSimpleName() + separator + getMethodName();
        }
    }

    private static class TimeZonePreferences {

        private String defaultUserTimeZoneSysProperty;
        private TimeZone defaultTimeZone;

        void saveDefault() {
            defaultUserTimeZoneSysProperty = System.getProperty("user.timezone");
            defaultTimeZone = TimeZone.getDefault();
        }

        void restoreDefault() {
            if (defaultUserTimeZoneSysProperty != null) {
                System.setProperty("user.timezone", defaultUserTimeZoneSysProperty);
            }
            TimeZone.setDefault(defaultTimeZone);
        }
    }

    private void migrateSchema(DataSource rootDataSource) {
        final boolean canAccessDefaultMigrations = new ClassPathResource("db/migration").exists();

        Flyway flyway = new Flyway();
        if (!canAccessDefaultMigrations) {
            final File migrationDirectory = new File("./../Controller/src/main/resources/db/migration").getAbsoluteFile();
            Preconditions.checkArgument(migrationDirectory.exists(), "Default directory with db migrations not found");
            flyway.setLocations("filesystem:" + migrationDirectory.getPath());
        }
        flyway.setDataSource(rootDataSource);
        flyway.setSchemas(schemaName);
        flyway.migrate();
    }

    private void populateSchema(DataSource rootDataSource) throws SQLException {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/structure.sql"));
        populator.addScript(new ClassPathResource("db/POPULATE_CURRENCY.sql"));
        populator.addScript(new ClassPathResource("db/POPULATE_USER_ROLE_BUSINESS_FEATURE.sql"));
        populator.addScript(new ClassPathResource("db/POPULATE_USER_ROLE_GROUP_FEATURE.sql"));
        populator.addScript(new ClassPathResource("db/POPULATE_USER_ROLE_REPORT_GROUP_FEATURE.sql"));
        populator.addScript(new ClassPathResource("db/POPULATE_USER_ROLE.sql"));
        populator.addScript(new ClassPathResource("db/POPULATE_OPERATION_TYPE.sql"));
        populator.populate(rootDataSource.getConnection());
    }

    private HikariDataSource createRootDataSource(DatabaseConfig dbConfig, String dbUrl) {
        HikariConfig config = new HikariConfig();
        config.setInitializationFailTimeout(-1);
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbConfig.getUser());
        config.setPassword(dbConfig.getPassword());
        return new HikariDataSource(config);
    }

    private static String createConnectionURL(String dbUrl, String newSchemaName) {
        return dbUrl.replace("birzha", newSchemaName);
    }
}
