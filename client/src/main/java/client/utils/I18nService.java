package client.utils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Service for managing application internationalization (i18n).
 * Provides ResourceBundle instances based on the persisted UI language.
 */
public class I18nService {

    private static final String BUNDLE_BASE_NAME = "i18n.messages";
    private static final String DEFAULT_LANGUAGE = "en";

    /**
     * Gets the ResourceBundle for the current UI language.
     * The language is determined by reading from the config system.
     *
     * @return ResourceBundle for the current locale
     */
    public ResourceBundle getBundle() {
        Locale locale = getCurrentLocale();
        return ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
    }

    /**
     * Determines the current locale based on the persisted UI language.
     * Falls back to English if no language is configured.
     *
     * @return the current Locale
     */
    private Locale getCurrentLocale() {
        String languageCode = ConfigUtils.getUILanguage();
        
        // Default to English if not set
        if (languageCode == null || languageCode.isEmpty()) {
            languageCode = DEFAULT_LANGUAGE;
        }

        // Map language codes to Locales
        switch (languageCode) {
            case "nl":
                return new Locale("nl");
            case "es":
                return new Locale("es");
            case "fr":
                return new Locale("fr");
            case "en":
            default:
                return new Locale("en");
        }
    }
}

