package web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

class Parameters {

    private static final File PROJECT_HOME = findProjectHome();

    private static File findProjectHome() {
        String dir = System.getProperty("user.dir");
        if (dir.endsWith("web")) {
            return new File(dir).getParentFile();
        } else {
            return new File(dir);
        }
    }

    static final File ROOT = PROJECT_HOME;

    static final File FOLDER_TEST = new File(ROOT, "/web/src/test");
    static final File FOLDER_SCENARIOS = new File(FOLDER_TEST, "scenarios");
    static final File FOLDER_CONFIGS = new File(FOLDER_SCENARIOS, "configs");

    static final File FOLDER_SCENARIOS_ERROR = new File(FOLDER_TEST, "_error");

    static final File FOLDER_CREDENTIALS = new File(FOLDER_SCENARIOS, "_credentials");
    static final File FOLDER_PATCHES = new File(FOLDER_SCENARIOS, "_patches");

    static final String FILE_NAME_PATCH_DEFAULT = "default.sql";

    static final Boolean SETTINGS_STOP_ON_ERROR;
    static final Pattern PATTERN_DIR_FILTER;

    static {
        Properties property = new Properties();
        try {
            FileInputStream fis = new FileInputStream(new File(FOLDER_SCENARIOS, "config.properties"));
            property.load(fis);

            SETTINGS_STOP_ON_ERROR = Boolean.parseBoolean(property.getProperty("stopOnError").trim());
            PATTERN_DIR_FILTER = Pattern.compile(property.getProperty("filterDirectoryPattern").trim(), Pattern.DOTALL);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
