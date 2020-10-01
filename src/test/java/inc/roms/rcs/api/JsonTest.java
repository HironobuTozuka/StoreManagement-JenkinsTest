package inc.roms.rcs.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@SpringBootTest(properties = {"zonky.test.database.postgres.client.properties.currentSchema=sm"})
@AutoConfigureEmbeddedDatabase
public class JsonTest {
    Logger log = LoggerFactory.getLogger(JsonTest.class);

    public static final String DEFAULT_BASE_PACKAGE = "inc.roms.rcs";
    public static final String SERVICE_INVENTORY_RESPONSE_PACKAGE = "inc.roms.rcs.service.inventory";

    @Autowired
    private ObjectMapper objectMapper;

    private static File base = FileUtils.getFile("src", "test", "resources", "api");
    private static File inventoryResponses = FileUtils.getFile("src", "test", "resources", "service", "inventory", "response");

    @ParameterizedTest(name = "Json Serialization/Deserialization test of: {1}, file: {2}")
    @MethodSource("jsonToTest")
    public void testSingleJsonFile(String jsonContent, Class<?> clazz, String fileName) {
        try {
            log.info("Class name to test: " + clazz.getCanonicalName());
            Object o = objectMapper.readValue(jsonContent, clazz);

            String s = objectMapper.writeValueAsString(o);

            assertEquals(jsonContent, s, true);
        } catch (JsonProcessingException | JSONException e) {
            log.warn("Exception while testing json!", e);
            throw new RuntimeException(e);
        }
    }

    public static Stream<Arguments> jsonToTest() {
        try {
            List<JsonContentWithClassName> apiJsonContent = getJsonContentWithClassName(base, new StringBuilder(DEFAULT_BASE_PACKAGE));
            List<JsonContentWithClassName> inventoryJsonContent = getJsonContentWithClassName(inventoryResponses, new StringBuilder(SERVICE_INVENTORY_RESPONSE_PACKAGE));
            List<JsonContentWithClassName> result = new ArrayList<>();

            result.addAll(apiJsonContent);
            result.addAll(inventoryJsonContent);

            return result.stream().map(it -> Arguments.of(it.getJsonContent(), it.getClazz(), it.getFilename()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<JsonContentWithClassName> getJsonContentWithClassName(File file, StringBuilder classNameBuilder) throws IOException, ClassNotFoundException {
        List<JsonContentWithClassName> result = new ArrayList<>();
        if (file.isDirectory()) {
            StringBuilder newClassNameBuilder = new StringBuilder(classNameBuilder).append(".").append(file.getName());
            for (File inner : Objects.requireNonNull(file.listFiles())) {
                result.addAll(getJsonContentWithClassName(inner, newClassNameBuilder));
            }
        } else {
            String jsonContent = Files.readString(file.toPath());
            result.add(new JsonContentWithClassName(jsonContent, Class.forName(classNameBuilder.toString()), file.getName()));
        }
        return result;
    }

    private static class JsonContentWithClassName {
        private final String jsonContent;
        private final Class<?> clazz;
        private final String filename;

        private JsonContentWithClassName(String jsonContent, Class<?> clazz, String filename) {
            this.jsonContent = jsonContent;
            this.clazz = clazz;
            this.filename = filename;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public String getJsonContent() {
            return jsonContent;
        }

        public String getFilename() {
            return filename;
        }
    }
}
