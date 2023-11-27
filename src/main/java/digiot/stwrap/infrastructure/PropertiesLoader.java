package digiot.stwrap.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class PropertiesLoader {

    private static final Properties properties = new Properties();

    static {
        load("stwrap.properties");
    }

    public static void load(String fileName) {
        
        try (InputStream input = PropertiesLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new IOException("Unable to find " + fileName);
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading properties file", ex);
        }
    }

    public static String getProperty(String key) {
        return getSafeProperty(key).get();
    }

    public static Optional<String> getSafeProperty(String key) {
        return Optional.ofNullable(properties.getProperty(key));
    }

    public static String getPropertyOrDefault(String key, String defaultValue) {
        return getSafeProperty(key).orElse(defaultValue);
    }
}

