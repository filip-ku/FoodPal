package client.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigUtilsTest {

    @Test
    public void testCustomConfigPath() throws IOException {
        Path tempFile = Files.createTempFile("test-config", ".properties");
        String testPath = tempFile.toAbsolutePath().toString();

        try {
            ConfigUtils.setCustomConfigPath(testPath);
            ConfigUtils.setUILanguage("fr");

            assertEquals("fr", ConfigUtils.getUILanguage());

            File file = new File(testPath);
            assert(file.exists());
        } finally {
            ConfigUtils.setCustomConfigPath(null);
            Files.deleteIfExists(tempFile);
        }
    }
}