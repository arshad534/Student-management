package attendance.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC connection helper. Override defaults with environment variables
 * DB_URL, DB_USER and DB_PASS.
 */
public final class DBConnection {
    private static final String URL =
            env("DB_URL", "jdbc:mysql://localhost:3306/attendance_db?useSSL=false&allowPublicKeyRetrieval=true");
    private static final String USER = env("DB_USER", "root");
    private static final String PASS = env("DB_PASS", "");

    private DBConnection() { }

    private static String env(String key, String fallback) {
        String v = System.getenv(key);
        return (v == null || v.isEmpty()) ? fallback : v;
    }

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
