package ma.uir.awd.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/** One single JDBC connection for the whole application. */
public class SingletonConnection {

    private static Connection connection;

    static {
        try (InputStream in = SingletonConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties not found "
                        + "in src/main/resources");
            }
            Properties db = new Properties();
            db.load(in);
            // Inside Tomcat, the driver must be loaded explicitly.
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(db.getProperty("db.url"),
                    db.getProperty("db.user"), db.getProperty("db.password"));
        } catch (Exception e) {
            // Fail loudly: without a connection, nothing else can work.
            throw new ExceptionInInitializerError(e);
        }
    }

    private SingletonConnection() { } // never instantiated

    public static Connection getConnection() {
        return connection;
    }
}