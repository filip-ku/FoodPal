package client.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Utility class for managing application configuration persistence.
 * Stores user preferences in a properties file located in the user's home directory.
 */
public class ConfigUtils {

    private static final String CONFIG_DIR = System.getProperty("user.home")
            + File.separator + ".foodpal";
    private static final String CONFIG_FILE = CONFIG_DIR + File.separator
            + "config.properties";
    
    private static final String KEY_UI_LANGUAGE = "ui.language";
    private static final String KEY_RECIPES_LANGUAGE_FILTER = "recipes.languageFilter";

    /**
     * Ensures the config directory exists.
     *
     * @throws IOException if the directory cannot be created
     */
    private static void ensureConfigDirectory() throws IOException {
        Path configPath = Paths.get(CONFIG_DIR);
        if (!Files.exists(configPath)) {
            Files.createDirectories(configPath);
        }
    }

    /**
     * Loads the configuration properties from the config file.
     *
     * @return a Properties object containing the configuration,
     * or an empty Properties if the file doesn't exist
     */
    private static Properties loadProperties() {
        Properties props = new Properties();
        try {
            ensureConfigDirectory();
            File configFile = new File(CONFIG_FILE);
            if (configFile.exists()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    props.load(fis);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load config file: " + e.getMessage());
        }
        return props;
    }

    /**
     * Saves the configuration properties to the config file.
     *
     * @param props the properties to save
     */
    private static void saveProperties(Properties props) {
        try {
            ensureConfigDirectory();
            File configFile = new File(CONFIG_FILE);
            try (FileOutputStream fos = new FileOutputStream(configFile)) {
                props.store(fos, "FoodPal Configuration");
            }
        } catch (IOException e) {
            System.err.println("Failed to save config file: " + e.getMessage());
        }
    }

    /**
     * Gets the saved UI language code from the config file.
     *
     * @return the language code (e.g., "en", "nl", "es"), or null if not set
     */
    public static String getUILanguage() {
        Properties props = loadProperties();
        return props.getProperty(KEY_UI_LANGUAGE);
    }

    /**
     * Saves the UI language code to the config file.
     *
     * @param languageCode the language code to save (e.g., "en", "nl", "es")
     */
    public static void setUILanguage(String languageCode) {
        Properties props = loadProperties();
        if (languageCode != null) {
            props.setProperty(KEY_UI_LANGUAGE, languageCode);
        } else {
            props.remove(KEY_UI_LANGUAGE);
        }
        saveProperties(props);
    }

    /**
     * Gets the saved recipe language filter from the config file.
     *
     * @return a list of language codes (e.g., ["en", "nl"]), or an empty list if not set
     */
    public static List<String> getRecipeLanguageFilter() {
        Properties props = loadProperties();
        String filterValue = props.getProperty(KEY_RECIPES_LANGUAGE_FILTER);
        if (filterValue == null || filterValue.trim().isEmpty()) {
            return new ArrayList<>();
        }
        // Split by comma, trim each element, and return as mutable list
        List<String> result = new ArrayList<>();
        for (String code : filterValue.split(",")) {
            String trimmed = code.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    /**
     * Saves the recipe language filter to the config file.
     *
     * @param languageCodes a list of language codes to save (e.g., ["en", "nl"])
     */
    public static void setRecipeLanguageFilter(List<String> languageCodes) {
        Properties props = loadProperties();
        if (languageCodes == null || languageCodes.isEmpty()) {
            props.remove(KEY_RECIPES_LANGUAGE_FILTER);
        } else {
            // Join language codes with comma
            String filterValue = String.join(",", languageCodes);
            props.setProperty(KEY_RECIPES_LANGUAGE_FILTER, filterValue);
        }
        saveProperties(props);
    }
}

