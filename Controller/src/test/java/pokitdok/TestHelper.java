package pokitdok;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;

abstract class TestHelper {

    private static final File PROJECT_HOME = findProjectHome();

    private static File findProjectHome() {
        String dir = System.getProperty("user.dir");
        if (dir.endsWith("web")) {
            return new File(dir).getParentFile();
        } else {
            return new File(dir);
        }
    }

    private static final File ROOT = PROJECT_HOME;
    private static final File FOLDER_TEST = new File(ROOT, "/web/src/test");
    static final File FOLDER_TEST_ERROR = new File(FOLDER_TEST, "_error");

    protected String toJsonString(Object o) throws JsonProcessingException {
        if (o instanceof String) {
            return (String) o;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return mapper.writeValueAsString(o);
    }

    protected String toPrettyString(String o) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(o);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
    }

    protected String toString(String path) throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(path));
    }

}
