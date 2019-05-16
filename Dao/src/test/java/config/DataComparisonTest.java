package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.imagination.comparator.Comparator;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Pattern;

@Log4j2
public class DataComparisonTest extends AbstractDatabaseContextTest {

    private static final String JSON_SUFFIX = ".json";
    private static final String ACTUAL = "actual/";
    private static final String any_PATTERN =
            "yyyy-MM-dd [012][0-9]:[0-5][0-9]:[0-5][0-9](?:\\.[0-9]+)?";

    private final ObjectMapper objectMapper = createObjectMapper();

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(df);
        return objectMapper;
    }

    private void assertSQLWithFile(String... queries) {
        SQLReader reader = new SQLReader();
        try {
            List<Table> read = reader.read(queries);
            assertJsonWithFile(toStringJson(queries.length == 1 ? read.get(0) : read));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertObjectWithFile(Object obj) {
        assertJsonWithFile(toStringJson(obj));
    }

    protected AssertWrapperBuilder around() {
        return new AssertWrapperBuilder();
    }

    private void assertJsonWithFile(String actual) {
        String resourcePath = RESOURCES_ROOT + steppedTestPath.nextExpectedStepPathForMethod();
        createFileIfNotExist(resourcePath);
        String expected = readPath(resourcePath);
        compareJson(actual, expected, resourcePath);
    }

    private void createFileIfNotExist(String stringPath) {
        Path path = Paths.get(stringPath);
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String toStringJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String readPath(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void compareJson(String actual, String expected, String path) {
        try {
            createTreeComparator().compare(expected, actual);
        } catch (Exception err) {
            String testClassIdentifier = getClass().getSimpleName();
            String testIdentifier = getTestIdentifier(path);

            String fullPath = RESOURCES_ROOT + testClassIdentifier +
                    File.separator + testIdentifier + File.separator + ACTUAL +
                    getFileIdentifier(path) + JSON_SUFFIX;
            try {
                FileUtils.writeStringToFile(new File(fullPath), prettify(actual), "UTF-8");
            } catch (IOException e) {
                throw new RuntimeException("Unable to write actual file to path " + fullPath, e);
            }

            if (log.isErrorEnabled()) {
                log.error("!!!       Found comparison issue for " + testClassIdentifier + "." + testIdentifier);
                log.error(formatLine("Expected Path", path));
                log.error(formatLine("Actual Path", fullPath));
                log.error(formatLine("Error message", err.getMessage()));
            }

            throw new RuntimeException(err);
        }
    }

    private String getFileIdentifier(String path) {
        return FilenameUtils.removeExtension(new File(path).getName());
    }

    private String getTestIdentifier(String path) {
        return new File(path).getParentFile().getParentFile().getName();
    }

    private static Comparator createTreeComparator() {
        Map<String, Pattern> aliases = new HashMap<String, Pattern>() {
            {
                put("current_date", Pattern.compile(DateTime.now().toString("yyyy-MM-dd")));
                put("any", Pattern.compile(".*")); // TODO fix time where near now +/- 3-5 minutes
            }
        };
        return Comparator.java().strict(aliases);
    }

    private String prettify(String actual) {
        try {
            if (actual.startsWith("{") || actual.startsWith("[")) {
                Object json = objectMapper.readValue(actual, Object.class);
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            }
        } catch (Exception ignore) {
        }
        return actual;
    }

    private class SQLReader {

        List<Table> read(String[] sqls) throws SQLException {
            QueryRunner run = new QueryRunner();

            Connection conn = dataSource.getConnection();
            List<Table> tables = new ArrayList<>();
            try {
                for (String sql : sqls) {
                    ResultSetHandler<Table> h = rs -> {
                        Table table = new Table(sql.replaceAll("\n", "").replaceAll(" +", " ").trim());
                        ResultSetMetaData metadata = rs.getMetaData();
                        while (rs.next()) {
                            table.addRow(readRow(rs, metadata));
                        }
                        return table;
                    };
                    tables.add(run.query(conn, sql, h));
                }
            } finally {
                conn.close();
            }
            return tables;
        }

        private Map<String, String> readRow(ResultSet rs, ResultSetMetaData metadata) {
            Map<String, String> map = new LinkedHashMap<>();
            try {
                int columnCount = metadata.getColumnCount();
                for (int index = 1; index <= columnCount; index++) {
                    String key = metadata.getColumnLabel(index);
                    String value = rs.getString(index);
                    map.put(key, value);
                }
                return map;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void truncateTables(String ... tableNames) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            for(String table : tableNames) {
                conn.createStatement().execute(String.format("TRUNCATE TABLE %s", table));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(conn)) {
                conn.close();
            }
        }
    }

    protected void prepareTestData(String ... sqls) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            for(String sql : sqls) {
                conn.createStatement().execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(conn)) {
                conn.close();
            }
        }
    }

    protected class AssertWrapperBuilder {

        private final List<Runnable> ops = Lists.newArrayList();

        public AssertWrapperBuilder withSQL(String... sqls) {
            ops.add(() -> assertSQLWithFile(sqls));
            return this;
        }

        public AssertWrapperBuilder withObject(Object object) {
            ops.add(() -> assertObjectWithFile(object));
            return this;
        }

        public void run(Runnable runnable) {
            ops.forEach(Runnable::run);
            runnable.run();
            ops.forEach(Runnable::run);
        }
    }

    private static class Table {
        public final String sql;
        public final List<Map<String, String>> content = new ArrayList<>();

        Table(String sql) {
            this.sql = sql;
        }

        void addRow(Map<String, String> row) {
            this.content.add(row);
        }
    }
}
