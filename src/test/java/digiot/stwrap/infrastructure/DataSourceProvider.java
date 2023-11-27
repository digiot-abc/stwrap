package digiot.stwrap.infrastructure;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.function.Consumer;

public class DataSourceProvider {

    private static final String PREFIX = "stwrap.datasource.";

    public static DataSource getDataSource() {

        HikariConfig config = new HikariConfig();

        // 数値や論理値のプロパティを安全に設定
        setPropertyAsLong(config::setConnectionTimeout, PREFIX + "connection-timeout");
        setPropertyAsLong(config::setValidationTimeout, PREFIX + "validation-timeout");
        setPropertyAsLong(config::setIdleTimeout, PREFIX + "idle-timeout");
        setPropertyAsLong(config::setLeakDetectionThreshold, PREFIX + "leak-detection-threshold");
        setPropertyAsLong(config::setMaxLifetime, PREFIX + "max-lifetime");
        setPropertyAsInt(config::setMaximumPoolSize, PREFIX + "max-pool-size");
        setPropertyAsInt(config::setMinimumIdle, PREFIX + "min-idle");
        setPropertyAsLong(config::setInitializationFailTimeout, PREFIX + "initialization-fail-timeout");
        setPropertyAsLong(config::setKeepaliveTime, PREFIX + "keepalive-time");

        // TODO null 対策: 文字列や他の型のプロパティはそのまま設定
//        config.setCatalog(PropertiesLoader.getProperty(PREFIX + "catalog"));
        config.setUsername(PropertiesLoader.getProperty(PREFIX + "username"));
        config.setPassword(PropertiesLoader.getProperty(PREFIX + "password"));
//        config.setConnectionInitSql(PropertiesLoader.getProperty(PREFIX + "connection-init-sql"));
//        config.setConnectionTestQuery(PropertiesLoader.getProperty(PREFIX + "connection-test-query"));
//        config.setDataSourceClassName(PropertiesLoader.getProperty(PREFIX + "data-source-class-name"));
//        config.setDataSourceJNDI(PropertiesLoader.getProperty(PREFIX + "data-source-jndi-name"));
//        config.setDriverClassName(PropertiesLoader.getProperty(PREFIX + "driver-class-name"));
//        config.setExceptionOverrideClassName(PropertiesLoader.getProperty(PREFIX + "exception-override-class-name"));
        config.setJdbcUrl(PropertiesLoader.getProperty(PREFIX + "jdbc-url"));
//        config.setPoolName(PropertiesLoader.getProperty(PREFIX + "pool-name"));
//        config.setSchema(PropertiesLoader.getProperty(PREFIX + "schema"));
//        config.setTransactionIsolation(PropertiesLoader.getProperty(PREFIX + "transaction-isolation-name"));
//        config.setAutoCommit(Boolean.parseBoolean(PropertiesLoader.getProperty(PREFIX + "auto-commit")));
//        config.setReadOnly(Boolean.parseBoolean(PropertiesLoader.getProperty(PREFIX + "read-only")));
//        config.setIsolateInternalQueries(Boolean.parseBoolean(PropertiesLoader.getProperty(PREFIX + "isolate-internal-queries")));
//        config.setRegisterMbeans(Boolean.parseBoolean(PropertiesLoader.getProperty(PREFIX + "register-mbeans")));
//        config.setAllowPoolSuspension(Boolean.parseBoolean(PropertiesLoader.getProperty(PREFIX + "allow-pool-suspension")));
        config.setHealthCheckProperties(System.getProperties());

        return new HikariDataSource(config);
    }

    private static void setPropertyAsString(Consumer<String> setterMethod, String propertyName) {
        PropertiesLoader.getSafeProperty(propertyName).ifPresent(setterMethod);
    }

    private static void setPropertyAsLong(Consumer<Long> setterMethod, String propertyName) {
        PropertiesLoader.getSafeProperty(propertyName).ifPresent(p -> {
            setterMethod.accept(Long.parseLong(p));
        });
    }

    private static void setPropertyAsInt(Consumer<Integer> setterMethod, String propertyName) {
        PropertiesLoader.getSafeProperty(propertyName).ifPresent(p -> {
            setterMethod.accept(Integer.parseInt(p));
        });
    }

}

