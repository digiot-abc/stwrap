package digiot.stwrap.infrastructure;

import org.apache.commons.lang3.StringUtils;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvUtils {
    
    private static final Dotenv dotenv = Dotenv.load();

    private EnvUtils() {
    }

    public static String getEnv(String key) {
        
        String value = System.getenv(key);

        if (StringUtils.isEmpty(value)) {
            value = dotenv.get(key);
        }

        return value;
    }
}