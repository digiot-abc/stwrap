package digiot.stwrap.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    private static final String PROPERTIES_FILE = "stwrap.properties";
    private static final Properties properties = new Properties();

    static {
        // プロパティファイルを読み込む
        try (InputStream input = PropertiesLoader.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new IOException("Unable to find " + PROPERTIES_FILE);
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading properties file", ex);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

}

