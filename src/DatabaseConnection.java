import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/cmpe";
    
    private static final String USER = "root";
    private static final String PASSWORD = "nilaybbb18";
    private DatabaseConnection() {
        throw new AssertionError("DatabaseConnection is a utility class and should not be instantiated");
    }

    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isConnectionValid(Connection conn) {
        if (conn == null) {
            return false;
        }
        try {
            return !conn.isClosed() && conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    public static String getDatabaseURL() {
        return URL;
    }

    public static String getDatabaseUser() {
        return USER;
    }
}
