package pokitdok;

import org.apache.commons.io.FileUtils;
import org.imagination.comparator.Comparator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public abstract class BaseComparatorTest extends TestHelper {

    private final String dir;
    private final String input;
    private final String expected;

    public BaseComparatorTest(String in, String out) throws IOException {
        this.dir = in.replaceAll(".json", "");
        this.input = toString(in);
        this.expected = toString(out);
    }

    @BeforeClass
    public static void beforeAll() throws Exception {
        if (FOLDER_TEST_ERROR.exists())
            FileUtils.cleanDirectory(FOLDER_TEST_ERROR);
        else
            FOLDER_TEST_ERROR.mkdirs();
    }

    @Test
    public void parserTest() throws Throwable {
        String actual = toJsonString(applyTransformation());

        try {
            Comparator.java().strict().compare(expected, actual);
        } catch (Throwable e) {
            FileUtils.writeStringToFile(new File(FOLDER_TEST_ERROR, dir + "/expected.json"), toPrettyString(expected));
            FileUtils.writeStringToFile(new File(FOLDER_TEST_ERROR, dir + "/actual.json"), toPrettyString(actual));
            throw e;
        }
    }

    protected abstract Object applyTransformation() throws Exception;

    String getInput() {
        return input;
    }
}
