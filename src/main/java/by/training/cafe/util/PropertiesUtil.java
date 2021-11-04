package by.training.cafe.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * The class {@code PropertiesUtil} is and utility class that
 * provides access to properties from the application.properties file.
 *
 * @author Nikita Romanov
 */
public final class PropertiesUtil {

    private static final Logger log
            = LogManager.getLogger(PropertiesUtil.class);
    private static final Properties PROPERTIES = new Properties();
    private static final String RESOURCE_NAME = "application.properties";

    static {
        loadProperties();
    }

    private PropertiesUtil() {
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
        try (var inputStream = PropertiesUtil.class
                .getClassLoader()
                .getResourceAsStream(RESOURCE_NAME)) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            log.fatal(e);
            throw new PropertiesException(e);
        }
    }

    private static class PropertiesException extends RuntimeException {

        PropertiesException() {
        }

        PropertiesException(String message) {
            super(message);
        }

        PropertiesException(String message, Throwable cause) {
            super(message, cause);
        }

        PropertiesException(Throwable cause) {
            super(cause);
        }
    }
}
