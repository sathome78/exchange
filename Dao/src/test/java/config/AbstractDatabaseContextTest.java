package config;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Closeables;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.io.File.separator;


@Transactional
@Log4j2
public abstract class AbstractDatabaseContextTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    static final String RESOURCES_ROOT = "src/test/resources/";

    @Autowired
    private DatabaseConfig dbConfig;

    @Autowired
    @Qualifier("testDataSource")
    protected DataSource dataSource;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeClass
    public static void beforeClass() {

    }

    @PostConstruct
    public void prepareTestSchema() throws SQLException {
        Preconditions.checkNotNull(dbConfig.getSchemaName(), "Scheme name must be defined");
        String testSchemaUrl = createConnectionURL(dbConfig.getUrl(), dbConfig.getSchemaName());
        try {
            DriverManager.getConnection(testSchemaUrl, dbConfig.getUser(), dbConfig.getPassword());
        } catch (Exception e) {
            String dbServerUrl = createConnectionURL(dbConfig.getUrl(), "");
            Connection connection = DriverManager.getConnection(dbServerUrl, dbConfig.getUser(), dbConfig.getPassword());

            Statement statement = connection.createStatement();
            statement.execute(String.format("CREATE DATABASE %s;", dbConfig.getSchemaName()));

            DataSource rootDataSource = createRootDataSource(dbConfig, testSchemaUrl);

            populateSchema(rootDataSource);

            if (!isSchemeValid(rootDataSource)) {
                throw new RuntimeException("Test scheme " + dbConfig.getSchemaName() + " doesn't exist");
            }
        }
    }

    @AfterClass
    public static void afterClass() {
    }

    @Rule
    public final SteppedTestPath steppedTestPath = new SteppedTestPath();

    @Before
    public final void setup() throws Exception {
        createFileTreeForTesting();
        before();
    }

    protected void before() {

    }

    @After
    public void after() {

    }

    private void createFileTreeForTesting() throws IOException {
        Path testRootFolder = Paths.get(RESOURCES_ROOT, getClass().getSimpleName(), steppedTestPath.getMethodName());
        Path expected = testRootFolder.resolve("expected");
        Path actual = testRootFolder.resolve("actual");
        Files.createDirectories(expected);
        FileUtils.deleteQuietly(actual.toFile());
    }

    @Configuration
    public static abstract class AppContextConfig {

        protected abstract String getSchema();

        @Autowired
        private DatabaseConfig databaseConfig;

        @Bean
        public DatabaseConfig databaseConfig() {
            Properties properties = getProperties();
            return new DatabaseConfig() {
                @Override
                public String getUrl() {
                    return properties.getProperty("db.master.url");
                }

                @Override
                public String getDriverClassName() {
                    return properties.getProperty("db.master.classname");
                }

                @Override
                public String getUser() {
                    return properties.getProperty("db.master.user");
                }

                @Override
                public String getPassword() {
                    return properties.getProperty("db.master.password");
                }

                @Override
                public String getSchemaName() {
                    return getSchema();
                }
            };
        }

        @Bean(name = "testDataSource")
        public DataSource dataSource() {
            log.debug("DB PROPS: DB URL: " + databaseConfig.getUrl());
            String dbUrl = createConnectionURL(databaseConfig.getUrl(), getSchema());
            return createDataSource(databaseConfig.getUser(), databaseConfig.getPassword(), dbUrl);
        }

        @Bean(name = "slaveTemplate")
        public NamedParameterJdbcTemplate slaveTemplate(@Qualifier("testDataSource") DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }

        @Bean(name = "masterTemplate")
        public NamedParameterJdbcTemplate masterTemplate(@Qualifier("testDataSource") DataSource dataSource) {
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

        private Properties getProperties() {
            String resourceDirectory = System.getProperty("profileId");
            List<String> allowedDirs = ImmutableList.of("dev", "devtest", "uat", "prod");

            if (StringUtils.isBlank(resourceDirectory)
                    || allowedDirs.stream().noneMatch(resourceDirectory::equalsIgnoreCase)) {
                throw new RuntimeException("NOT ALLOWED DIR");
            }
            String path = "./../Controller/src/main/" + resourceDirectory + "/db.properties";
            File propsFile = new File(path);
            final Properties properties = new Properties();
            String message = "Failed to find file db.properties to load db props";
            if (propsFile.exists()) {
                log.debug("RESOURCE EXISTS: ");
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(propsFile);
                    properties.load(inputStream);
                } catch (IOException e) {
                    log.error(message, e);
                    throw new RuntimeException(message, e);
                } finally {
                    Closeables.closeQuietly(inputStream);
                }
            } else {
                log.error(message);
                throw new RuntimeException(message);
            }
            return properties;
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

    private boolean isSchemeValid(DataSource rootDataSource) {
        boolean result;
        try {
            final Statement statement = rootDataSource.getConnection().createStatement();
            result = statement.execute("SELECT 1 FROM USER");
        } catch (SQLException e) {
            logger.error(String.format("Failed to check scheme %s validity", dbConfig.getSchemaName()), e);
            throw new RuntimeException(e);
        }
        return result;
    }
}
