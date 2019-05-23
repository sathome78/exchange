package web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import framework.model.BasePresentBodyRequest;
import framework.model.Block;
import framework.model.HttpMethodBlock;
import framework.model.Operation;
import framework.model.Response;
import framework.model.impl.Config;
import framework.model.impl.auth.Authentication;
import framework.model.impl.database.Dump;
import framework.model.impl.execute.ExecuteOperation;
import framework.model.impl.instructruction.Break;
import framework.model.impl.mock.When;
import framework.model.impl.variable.Variable;
import framework.parser.ConfigCollector;
import migration.Migration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.imagination.comparator.Comparator;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import web.config.DatabaseConfig;
import web.config.TestContextConfig;
import web.config.TestDatabaseConfig;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static web.Parameters.FILE_NAME_PATCH_DEFAULT;
import static web.Parameters.FOLDER_CONFIGS;
import static web.Parameters.FOLDER_CREDENTIALS;
import static web.Parameters.FOLDER_PATCHES;
import static web.Parameters.FOLDER_SCENARIOS;
import static web.Parameters.FOLDER_SCENARIOS_ERROR;
import static web.Parameters.FOLDER_TEST;
import static web.Parameters.PATTERN_DIR_FILTER;
import static web.Parameters.ROOT;
import static web.Parameters.SETTINGS_STOP_ON_ERROR;

@RunWith(Parameterized.class)
@ContextConfiguration(classes = {TestContextConfig.class, TestDatabaseConfig.class})
@WebAppConfiguration
public class RootTest {

    /**
     * Please, use ";;" as a delimiter in the .sql files that are applied during the tests run.
     * It is required to separate line delimiters and the ";" symbol in, for example, email templates.
     */

    private static final String SQL_DELIMITER = ";";

    private static final Logger LOGGER = LoggerFactory.getLogger(RootTest.class);

    private static final List<Transformation> TRANSFORMATION_LIST = new ArrayList<Transformation>() {{
    }};

    @Parameterized.Parameters(name = " {index}. {0} ")
    public static Collection<Object[]> data() {
        ConfigCollector.Result result =
                new ConfigCollector(FOLDER_CONFIGS, PATTERN_DIR_FILTER).collect();

        return FluentIterable.from(filterOnlyThis(result.get())).transform(e -> {
            String shortPath = e.getKey().getPath().replace(FOLDER_CONFIGS.toString(), "");
            return new Object[]{shortPath, e.getKey(), e.getValue()};
        }).toList();
    }

    private static Set<Map.Entry<File, ConfigCollector.MappingResult>> filterOnlyThis(Map<File, ConfigCollector.MappingResult> original) {
        Set<Map.Entry<File, ConfigCollector.MappingResult>> onlyThis =
                FluentIterable.from(original.entrySet()).filter(m -> {
                    ConfigCollector.MappingResult v = m.getValue();
                    return v.isOk() && v.config.get().getActive() && v.config.get().getOnlyThis();
                }).toSet();

        return onlyThis.isEmpty() ? original.entrySet() : onlyThis;
    }

    @Autowired
    protected WebApplicationContext ctx;

    @Autowired
    protected DatabaseConfig databaseConfig;

    @Autowired
    @Qualifier("testDataSource")
    protected DataSource dataSource;

    @Autowired
    protected AutowireCapableBeanFactory beanFactory;

    private MockMvc api;

    private File file;
    private ConfigCollector.MappingResult result;

    @SuppressWarnings({"unchecked", "unused"})
    public RootTest(String ignore, Object file, Object result) {
        this.file = (File) file;
        this.result = (ConfigCollector.MappingResult) result;
    }

    @BeforeClass
    public static void beforeAll() throws Exception {
        if (FOLDER_SCENARIOS_ERROR.exists())
            FileUtils.cleanDirectory(FOLDER_SCENARIOS_ERROR);
        else
            FOLDER_SCENARIOS_ERROR.mkdirs();

        Map<String, Object> info = new LinkedHashMap<>();
        info.put("Root folder", ROOT);
        info.put("Test folder", FOLDER_TEST);
        info.put("Scenarios folder", FOLDER_SCENARIOS);
        info.put("Scenarios error folder", FOLDER_SCENARIOS_ERROR);
        info.put("Credentials folder", FOLDER_CREDENTIALS);
        info.put("Patches folder", FOLDER_PATCHES);
        info.put("Stop on error setting", SETTINGS_STOP_ON_ERROR);
        info.put("File directories settings", PATTERN_DIR_FILTER);

        printInformation(info);
    }

    @Before
    public void setUpBefore() throws Exception {
        //this is where the magic happens, we actually do "by hand" what the spring runner would do for us,
        // read the JavaDoc for the class bellow to know exactly what it does, the method names are quite accurate though
        new TestContextManager(getClass()).prepareTestInstance(this);

        this.api = MockMvcBuilders.webAppContextSetup(ctx).build();

        String url = databaseConfig.getUrl();
        if (!url.startsWith("jdbc:mysql://localhost")) {
            throw new RuntimeException("Test framework should apply migrations ONLY for local database. " +
                    "URL which has been used is " + url);
        }

        if (result.isOk()) {
            Config config = result.config.get();
            if (config.getActive()) {
                new DatabaseMigrationRunner(dataSource, databaseConfig).execute();
            } else {
                Assume.assumeTrue(config.getDescription(), false);
            }
        }
    }

    private static void printInformation(Map<String, Object> info) {
        for (Map.Entry<String, ?> each : info.entrySet()) {
            String name = each.getKey();
            Object value = each.getValue();

            final String arg2;
            if (value instanceof File) {
                File file = (File) value;
                if (file.exists()) {
                    arg2 = "Exists   " + file.getAbsolutePath();
                } else {
                    arg2 = "Not exists " + file.getAbsolutePath();
                }
            } else {
                arg2 = value.toString();
            }
            LOGGER.info(String.format("%40s => %-20s", name, arg2));
        }
    }

    @Test
    public void test() throws Throwable {
        if (result.isOk()) {
            Config cfg = result.config.get();
            LOGGER.info("Description:");
            LOGGER.info(cfg.getDescription());

            migrateDatabase(cfg);
            new ConfigRunner(file, cfg).run();

        } else {
            throw result.exception.get();
        }
    }

    private void migrateDatabase(Config cfg) throws Exception {
        List<String> patches = cfg.getPatch().transform(patchesNames -> {
            String[] names = patchesNames.split(",");

            for (int index = 0; index < names.length; index++) {
                String name = names[index];
                names[index] = name.endsWith(".sql") ? name : name + ".sql";
            }

            return Arrays.asList(names);
        }).or(Collections.singletonList(FILE_NAME_PATCH_DEFAULT));

        List<SQLSource> patchesFiles = new ArrayList<>();
        patchesFiles.add(new StringsSource(clearDatabaseQuery(dataSource, databaseConfig.getSchema())));

        for (String each : patches) {
            patchesFiles.add(new FileSource(new File(FOLDER_PATCHES, each)));
        }

        apply(dataSource, patchesFiles);
    }

    abstract static class SQLSource {
        abstract String getSQLs();

        FluentIterable<String> iterator() {
            List<String> split = Arrays.asList(getSQLs().split(SQL_DELIMITER));
            return FluentIterable.from(split).filter(s -> s.trim().length() > 0);
        }
    }

    private static class FileSource extends SQLSource {

        private final File file;

        FileSource(File file) {
            if (!file.exists())
                throw new RuntimeException("File " + file + " does not exist ");

            this.file = file;
        }

        @Override
        String getSQLs() {
            try {
                return FileUtils.readFileToString(file);
            } catch (IOException e) {
                throw new RuntimeException("Unable to read content from file " + file);
            }
        }
    }

    private static class StringsSource extends SQLSource {

        private final String queries;

        StringsSource(String queries) {
            this.queries = queries;
        }

        @Override
        String getSQLs() {
            return queries;
        }
    }

    private static void apply(DataSource dataSource, List<SQLSource> sources) throws Exception {
        Connection c = dataSource.getConnection();
        try {
            c.setAutoCommit(false);
            for (SQLSource each : sources) {
                apply(c, each);
            }
            c.commit();
        } catch (Exception e) {
            c.rollback();
            throw e;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private static void apply(Connection c, SQLSource queries) throws Exception {
        String runningSql = null;
        try {
            for (String sql : queries.iterator()) {
                runningSql = sql;
                c.prepareStatement(sql).execute();
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to apply queries " + queries + ",\n line:" +
                    runningSql + " , message: " + e.getMessage(), e);
        }
    }

    private class ConfigRunner {

        private final Pattern routePattern = Pattern.compile("#\\{(.*?)\\}", Pattern.DOTALL);
        private final Pattern functionPattern = Pattern.compile("function:(.*?)", Pattern.DOTALL);

        private final String path;
        private final Config cfg;
        private final AtomicInteger pos = new AtomicInteger();
        private final Context ctx = new Context();

        private Optional<Throwable> error = Optional.absent();

        ConfigRunner(File file, Config cfg) {
            this.path = file.getAbsolutePath().replace(FOLDER_SCENARIOS.getAbsolutePath(), "");
            this.cfg = cfg;
        }

        void run() throws Throwable {
            try {
                scenario(cfg.getBlocks(), Optional.absent());

                if (error.isPresent())
                    throw error.get();

            } catch (StopSignal ignore) {
            }
        }

        private void scenario(List<Block> blocks, Optional<String> token) throws Throwable {
            for (Block each : blocks) {
                try {
                    block(each, token);
                } catch (StopSignal e) {
                    throw e;

                } catch (Throwable e) {
                    if (!error.isPresent())
                        error = Optional.of(e);

                    if (SETTINGS_STOP_ON_ERROR)
                        throw e;
                }
            }
        }

        private void block(Block each, Optional<String> token) throws Throwable {
            LOGGER.info("....................................................................................");

            if (StringUtils.isNoneBlank(each.getComment())) {
                LOGGER.info(each.getComment());
            }

            if (each instanceof Response && each instanceof HttpMethodBlock) {
                pos.incrementAndGet();

                Response asResponse = (Response) each;
                HttpMethodBlock asMethod = (HttpMethodBlock) each;

                int expectedCode = asResponse.getCode();
                Map<String, String> expectedHeaders = asResponse.getResponseHeaders();
                Optional<File> maybeExpectedBody = asResponse.getResponseFile();

                WsResponse response = exchange(asMethod, token);

                int actualCode = response.code;
                Map<String, String> actualHeaders = response.headers;
                String actualBody = response.content;

                ctx.setBody(actualBody);

                TestValidator v = new TestValidator();
                v.validateAndSave(expectedCode, actualCode).
                        validateAndSave(expectedHeaders, actualHeaders).
                        validateAndSave(maybeExpectedBody, actualBody);

                v.rethrowOnErrors();

            } else if (each instanceof Dump) {
                pos.incrementAndGet();

                Dump dump = (Dump) each;

                ImmutableList<String> sql = FluentIterable.from(dump.getSql()).transform(s -> inject(s)).toList();

                LOGGER.info(String.format("%2s. %-8s", String.valueOf(pos.get()), "Dump"));
                for (String e : sql) {
                    LOGGER.info(String.format("%10s %-100s", "", e));
                }

                String expected = FileUtils.readFileToString(dump.getFile().get());
                String actual = new SQLReader(dataSource).readAsJson(sql);

                ctx.setBody(actual);

                try {
                    getTreeComparator().compare(expected, actual);
                } catch (Throwable e) {
                    save("dump", "json", expected, actual, new DumpAdditionalOperation(dump));
                    throw e;
                }

            } else if (each instanceof Authentication) {
                Authentication auth = (Authentication) each;
                Preconditions.checkArgument(auth.getCredentials().isPresent(), "Credentials for authentication not provided");

                Credentials credentials = new Credentials(auth.getCredentials().get());
                LOGGER.info(String.format("%15s", "Authentication [" + credentials.credentialsFile.getName() + "]"));

                scenario(auth.getBlocks(), Optional.of(credentials.authenticate()));

            } else if (each instanceof Variable) {
                Variable var = (Variable) each;

                LOGGER.info(String.format("%2s. %-8s %40s", String.valueOf(pos.get()), "Jpath variable", var.getName() + " -> " + var.getPath()));
                try {
                    String result = ctx.setJPath(var.getName(), var.getPath());
                    LOGGER.info(String.format("%5s [Value in contextOf]=%s", "", result));
                } catch (Exception e) {
                    LOGGER.info("Failed [variable] " + var.getName() + " [path] " + var.getPath());
                    throw e;
                }
            } else if (each instanceof Break) {
                LOGGER.info(String.format("%2s. %-8s", String.valueOf(pos.get()), "BREAK"));

                throw new StopSignal();
            } else if (each instanceof When) {
                When when = (When) each;
            } else if (each instanceof ExecuteOperation) {
                pos.incrementAndGet();

                ExecuteOperation operation = (ExecuteOperation) each;
                LOGGER.info(String.format("%2s. %-8s", String.valueOf(pos.get()), "ExecuteOperation on " + operation.getClassName()));

                Operation op = (Operation) Class.forName(operation.getClassName()).newInstance();
                beanFactory.autowireBean(op);

                Object result = op.applyOperation();

                Optional<String> expectedBody = operation.getFile().transform(file -> {
                    try {
                        return FileUtils.readFileToString(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                if (expectedBody.isPresent()) {
                    String actual = result instanceof CharSequence ? result.toString() :
                            new ObjectMapper().writeValueAsString(result);
                    String expected = expectedBody.get();

                    try {
                        getTreeComparator().compare(expected, actual);
                    } catch (Throwable e) {
                        save("execute", "json", expected, actual, null);
                        throw e;
                    }
                }
            }
        }

        private class TestValidator {
            private static final int LIMIT = 80;

            private List<String> result = new ArrayList<>();

            TestValidator validateAndSave(int expectedCode,
                                          int actualCode) throws Exception {
                try {
                    Assert.assertEquals(expectedCode, actualCode);
                } catch (Throwable e) {
                    e.printStackTrace();
                    result.add(" HTTP CODE should be [" + expectedCode + "] but was [" + actualCode + "]");
                    save("code", "json", String.valueOf(expectedCode), String.valueOf(actualCode), null);
                }
                return this;
            }

            TestValidator validateAndSave(Map<String, String> expectedHeaders,
                                          Map<String, String> actualHeaders) throws Exception {
                try {
                    for (Map.Entry<String, String> entry : expectedHeaders.entrySet()) {
                        String value = actualHeaders.get(entry.getKey());
                        Assert.assertEquals(entry.getValue(), value);
                    }
                } catch (Throwable e) {
                    String expected = stingify(expectedHeaders);
                    String actual = stingify(actualHeaders);

                    result.add(" HTTP HEADERS should be [" + cut(expected) + "] but was [" + cut(actual) + "]");
                    save("header", "json", expected, actual, null);
                }
                return this;
            }

            TestValidator validateAndSave(Optional<File> expectedBodyFile,
                                          String actualBody) throws Exception {
                Optional<String> expectedBody = expectedBodyFile.transform(file -> {
                    try {
                        return FileUtils.readFileToString(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                try {
                    if (expectedBody.isPresent()) {
                        getTreeComparator().compare(expectedBody.get(), actualBody);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    result.add(" HTTP BODY should be [" + cut(expectedBody.get()) + "] but was [" + cut(actualBody) + "]");
                    save("body", "json", expectedBody.get(), actualBody, null);
                }
                return this;
            }

            void rethrowOnErrors() {
                if (result.size() > 0) {
                    throw new RuntimeException("Errors:\n " + FluentIterable.from(result).join(Joiner.on(" \n")));
                }
            }

            private String stingify(Map<String, String> map) {
                StringBuilder o = new StringBuilder();
                for (Map.Entry<String, String> each : map.entrySet()) {
                    o.append(each.getKey()).append("=").append(each.getValue());
                }
                return o.toString();
            }

            private String cut(String s) {
                return StringUtils.abbreviate(s, LIMIT);
            }

        }

        private WsResponse exchange(HttpMethodBlock method, Optional<String> token) throws Exception {
            Map<String, String> headers = inject(method.getRequestHeaders());
            Optional<Pair<String, String>> tokenHeader = token.transform(s -> Pair.of("Authorization", "Bearer " + s));

            if (tokenHeader.isPresent()) {
                Pair<String, String> tkn = tokenHeader.get();
                headers.put(tkn.getLeft(), tkn.getRight());
            }

            MockHttpServletRequestBuilder request = createRequest(method);
            request = addRequestHeaders(request, headers);
            request = addRequestBody(request, method);

            MockHttpServletResponse result = api.perform(request).andReturn().getResponse();
            return new WsResponse(result.getStatus(), extractHeaders(result), result.getContentAsString());
        }

        private Map<String, String> extractHeaders(MockHttpServletResponse response) {
            Map<String, String> result = new LinkedHashMap<>();
            for (String each : response.getHeaderNames()) {
                result.put(each, response.getHeaderValue(each).toString());
            }
            return result;
        }

        private MockHttpServletRequestBuilder createRequest(HttpMethodBlock method) {
            String url = inject(method.getUrl());
            String name = method.getName();

            LOGGER.info(String.format("%2s. %-8s %-40s", String.valueOf(pos.get()), name, url));

            if ("GET".equalsIgnoreCase(name)) {
                return get(url);
            } else if ("POST".equalsIgnoreCase(name)) {
                BasePresentBodyRequest body = (BasePresentBodyRequest) method;

                if (body.getMultipart() || !body.getFiles().isEmpty()) {
                    return fileUpload(url);
                } else {
                    return post(url);
                }
            } else if ("PUT".equalsIgnoreCase(name)) {
                return put(url);
            } else if ("DELETE".equalsIgnoreCase(name)) {
                return delete(url);
            } else if ("PATCH".equalsIgnoreCase(name)) {
                return patch(url);
            } else {
                throw new IllegalArgumentException("Unknown method " + name);
            }
        }

        private MockHttpServletRequestBuilder addRequestHeaders(MockHttpServletRequestBuilder request, Map<String, String> headers) {
            if (!headers.isEmpty()) {
                LOGGER.info(String.format("%25s", "[header]"));
                for (Map.Entry<String, String> each : headers.entrySet()) {
                    LOGGER.info(String.format("%20s=%s", each.getKey(), each.getValue()));
                }
            }

            for (String key : headers.keySet()) {
                String value = headers.get(key);
                request = request.header(key, value);
            }
            return request;
        }

        private MockHttpServletRequestBuilder addRequestBody(MockHttpServletRequestBuilder request, HttpMethodBlock method) throws IOException {
            if (method instanceof BasePresentBodyRequest) {
                BasePresentBodyRequest b = (BasePresentBodyRequest) method;

                Map<String, String> newMap = new LinkedHashMap<>();
                for (Map.Entry<String, String> entry : b.getParams().entrySet()) {
                    newMap.put(inject(entry.getKey()), inject(entry.getValue()));
                }

                if (!newMap.isEmpty()) {
                    LOGGER.info(String.format("%24s", "[body]"));
                    for (Map.Entry<String, String> each : newMap.entrySet()) {
                        LOGGER.info(String.format("%20s=%s", each.getKey(), each.getValue()));
                    }
                }

                for (Map.Entry<String, String> entry : newMap.entrySet()) {
                    request = request.param(entry.getKey(), entry.getValue());
                }

                if (!b.getFiles().isEmpty()) {
                    for (Map.Entry<String, File> entry : b.getFiles().entrySet()) {
                        request = ((MockMultipartHttpServletRequestBuilder) request).
                                file(entry.getKey(), FileUtils.readFileToByteArray(entry.getValue()));
                    }
                }

                return request;
            }
            return request;
        }

        private class WsResponse {
            private int code;
            private Map<String, String> headers;
            private String content;

            WsResponse(int code,
                       Map<String, String> headers,
                       String content) {
                this.code = code;
                this.headers = headers;
                this.content = content;
            }
        }

        private void save(String prefix,
                          String suffix,
                          String expected,
                          String actual,
                          AdditionalOperation op) throws IOException, InterruptedException {

            int position = pos.get();

            File folder = new File(FOLDER_SCENARIOS_ERROR, path.replace("config.xml", ""));

            System.out.flush();
            System.err.println("See error output in:" + folder);
            if (op != null) {
                op.apply();
            }

            final Pair<String, String> names;
            if ("body".equals(prefix)) {
                names = Pair.of("expected_" + position, "action_" + position);
            } else {
                names = Pair.of(prefix + "_expected_" + position, prefix + "_action_" + position);
            }

            FileUtils.writeStringToFile(new File(folder, names.getLeft() + "." + suffix), expected);
            FileUtils.writeStringToFile(new File(folder, names.getRight() + "." + suffix), prettify(actual));
        }

        private String prettify(String actual) {
            try {
                if (actual.startsWith("{") || actual.startsWith("[")) {
                    ObjectMapper mapper = new ObjectMapper();
                    Object json = mapper.readValue(actual, Object.class);
                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
                }
            } catch (Exception ignore) {
            }
            return actual;
        }

        String inject(String original) {
            Matcher functionMatcher = functionPattern.matcher(original);
            if (functionMatcher.matches()) {
                String functionAlias = functionMatcher.group(1).trim();
                for (Transformation each : TRANSFORMATION_LIST) {
                    if (each.matches(functionAlias)) {
                        return each.apply(functionAlias);
                    }
                }
                throw new RuntimeException("Unable to find function with alias " + functionAlias);
            } else {
                return recursiveInject(original);
            }
        }

        String recursiveInject(String original) {
            Matcher m = routePattern.matcher(original);
            if (m.find()) {
                return original.substring(0, m.start()) + ctx.get(m.group(1)) + original.substring(m.end());
            } else {
                return original;
            }
        }

        Map<String, String> inject(Map<String, String> input) {
            Map<String, String> result = new LinkedHashMap<>();
            for (String key : input.keySet()) {
                result.put(key, inject(input.get(key)));
            }
            return result;
        }
    }

    private Comparator getTreeComparator() {
        return Comparator.java().strict();
    }

    private class Credentials {

        final File credentialsFile;

        final String email;
        final String password;

        Credentials(String credentials) throws IOException {
            credentials = credentials.endsWith(".json") ? credentials : credentials + ".json";

            this.credentialsFile = new File(FOLDER_CREDENTIALS, credentials);
            Preconditions.checkArgument(credentialsFile.exists(),
                    "File with credentials for authentication not found by path:" + credentialsFile);

            DocumentContext ctx = JsonPath.parse(FileUtils.readFileToString(credentialsFile));

            this.email = ctx.read("$.email").toString();
            this.password = ctx.read("$.password").toString();
        }

        String authenticate() throws Exception {
            MockHttpServletResponse result = null;
            try {
                result = api.perform(post("/api/v1/authentication/token").
                        param("email", this.email).
                        param("password", this.password)).
                        andReturn().getResponse();

                return JsonPath.parse(result.getContentAsString()).read("$.token").toString();

            } catch (Throwable e) {
                StringBuilder o = new StringBuilder();
                o.append("Unable to authenticate with credentials").append("\n");
                o.append("Email:").append(email).append("\n");
                o.append("Password:").append(password).append("\n");
                o.append("=========================").append("\n");
                o.append("Exception").append("\n");
                o.append(e.getLocalizedMessage()).append("\n");

                if (result != null) {
                    o.append("HTTP code:").append(result.getStatus()).append("\n");
                    o.append("HTTP body:").append("\n");
                    String content = result.getContentAsString();
                    o.append(content.isEmpty() ? "EMPTY" : content);
                }
                throw new RuntimeException(o.toString());
            }
        }
    }

    private static class StopSignal extends RuntimeException {
    }

    private class Context {
        private final Map<String, String> context = new HashMap<>();

        private final String BODY = UUID.randomUUID().toString();

        void set(String key, String value) {
            context.put(key, value);
        }

        void setBody(String value) {
            set(BODY, value);
        }

        String setJPath(String key, String value) {
            String result = JsonPath.parse(getBody()).read(value).toString();
            set(key, result);
            return result;
        }

        String getBody() {
            return context.get(BODY);
        }

        String get(String key) {
            String result = context.get(key);
            if (result == null)
                throw new IllegalArgumentException("Unable to find value for key " + key + ". Available keys " + context);
            return result;
        }
    }


    interface Transformation {
        boolean matches(String input);

        String apply(String input);
    }


    public static class Table {
        public final String sql;
        public final List<Map<String, String>> content = new ArrayList<>();

        Table(String sql) {
            this.sql = sql;
        }

        void addRow(Map<String, String> row) {
            this.content.add(row);
        }
    }

    private static class DatabaseMigrationRunner {
        private static volatile boolean wasExecuted;

        private final DataSource dataSource;
        private final DatabaseConfig databaseConfig;

        DatabaseMigrationRunner(DataSource dataSource, DatabaseConfig databaseConfig) {
            this.dataSource = dataSource;
            this.databaseConfig = databaseConfig;
        }

        synchronized void execute() throws Exception {
            if (!wasExecuted) {
                try {
                    migrate();
                } catch (Exception e) {
                    StringsSource source = new StringsSource(dropDatabaseQuery(dataSource, databaseConfig.getSchema()));
                    apply(dataSource, Collections.singletonList(source));
                    migrate();
                }
                wasExecuted = true;
            }
        }

        private void migrate() {
            String description = String.format("Testing tool URL[%s] USER[%s]",
                    databaseConfig.getUrl(), databaseConfig.getUser());
            new Migration().apply(dataSource, description);
        }
    }

    private static String dropDatabaseQuery(DataSource dataSource, String schema) {
        StringBuilder o = new StringBuilder();
        o.append("SET FOREIGN_KEY_CHECKS = 0" + SQL_DELIMITER);
        for (String each : tables(dataSource, schema, true)) {
            o.append("DROP TABLE IF EXISTS ").append(each).append(" CASCADE" + SQL_DELIMITER);
        }
        for (String each : views(dataSource, schema)) {
            o.append("DROP VIEW ").append(each).append(SQL_DELIMITER);
        }
        for (String each : functions(dataSource, schema)) {
            o.append("DROP FUNCTION ").append(each).append(SQL_DELIMITER);
        }
        o.append("SET FOREIGN_KEY_CHECKS = 1" + SQL_DELIMITER);
        return o.toString();
    }

    private static String clearDatabaseQuery(DataSource dataSource, String schema) {
        StringBuilder o = new StringBuilder();
        o.append("SET FOREIGN_KEY_CHECKS = 0" + SQL_DELIMITER);
        for (String each : tables(dataSource, schema, false)) {
            o.append("TRUNCATE TABLE ").append(each).append(SQL_DELIMITER);
        }
        o.append("SET FOREIGN_KEY_CHECKS = 1" + SQL_DELIMITER);
        return o.toString();
    }

    public static List<String> tables(DataSource ds, String schema, boolean addSchemaVersion) {
        String filter = addSchemaVersion ? "" : " AND TABLE_NAME != 'schema_version'";
        String sql = String.format("select * from information_schema.tables where table_type = 'BASE TABLE' AND table_schema = '%s' %s" + SQL_DELIMITER, schema, filter);
        return query(ds, sql, new ArrayList<>(), (ResultSetReader<List<String>>) (strings, q) -> strings.add(q.getString("TABLE_NAME")));
    }

    public static List<String> views(DataSource ds, String schema) {
        String sql = "select * from information_schema.views where table_schema = '" + schema + "'" + SQL_DELIMITER;
        return query(ds, sql, new ArrayList<>(), (ResultSetReader<List<String>>) (strings, q) -> strings.add(q.getString("TABLE_NAME")));
    }

    public static List<String> functions(DataSource ds, String schema) {
        String sql = "select * from information_schema.routines where routine_schema = '" + schema + "'" + SQL_DELIMITER;
        return query(ds, sql, new ArrayList<>(), (ResultSetReader<List<String>>) (strings, q) -> strings.add(q.getString("SPECIFIC_NAME")));
    }

    interface ResultSetReader<E> {
        void accumulate(E e, ResultSet q) throws Exception;
    }

    private static <E> E query(DataSource dataSource,
                               String sql,
                               E e,
                               ResultSetReader<E> rsr) {
        try {
            try (Connection c = dataSource.getConnection()) {
                try (Statement statement = c.createStatement()) {
                    try (ResultSet q = statement.executeQuery(sql)) {
                        while (q.next()) {
                            rsr.accumulate(e, q);
                        }
                    }
                }
            }
            return e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    interface AdditionalOperation {
        void apply();
    }

    static class DumpAdditionalOperation implements AdditionalOperation {

        private Dump dump;

        public DumpAdditionalOperation(Dump dump) {
            this.dump = dump;
        }

        @Override
        public void apply() {
            if (dump != null) {
                System.err.println("Original source is in" + dump.getFile());
                if (dump.getComment() != null)
                    System.err.println("test's comment is:" + dump.getComment());
            }
        }
    }

    public static class SQLReader {

        private DataSource dataSource;

        public SQLReader(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        List<Table> read(List<String> list) throws Exception {
            try (Connection c = dataSource.getConnection()) {
                return FluentIterable.from(list).transform(s -> dump(c, s)).toList();
            }
        }

        private Table dump(Connection c, String sql) {
            try {
                try (Statement statement = c.createStatement()) {
                    try (ResultSet q = statement.executeQuery(sql)) {
                        Table table = new Table(sql.replaceAll("\n", "").replaceAll(" +", " ").trim());

                        ResultSetMetaData metadata = q.getMetaData();
                        while (q.next()) {
                            table.addRow(readRow(q, metadata));
                        }
                        return table;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private Map<String, String> readRow(ResultSet rs, ResultSetMetaData metadata) throws Exception {
            Map<String, String> map = new LinkedHashMap<>();

            int columnCount = metadata.getColumnCount();
            for (int index = 1; index <= columnCount; index++) {
                String key = metadata.getColumnLabel(index);
                String value = rs.getString(index);
                map.put(key, value);
            }

            return map;
        }

        public String readAsJson(List<String> list) throws Exception {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(read(list));
        }
    }
}
