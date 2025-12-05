import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection utility class for Contact Management System.
 * <p>
 * Provides methods to establish and manage MySQL database connections.
 * Uses UTF-8 encoding for Turkish character support.
 * All connections use the cms database with root user credentials.
 * 
 * <p><b>Database Configuration:</b></p>
 * <ul>
 *   <li>Host: localhost</li>
 *   <li>Port: 3306</li>
 *   <li>Database: cms</li>
 *   <li>Username: root</li>
 *   <li>Character Set: UTF-8</li>
 * </ul>
 * 
 * @author Group 22
 * @version 1.0
 * @since 2024
 */
public class DatabaseConnection {

    /**
     * JDBC URL for MySQL connection.
     * Includes UTF-8 encoding, timezone, and SSL settings.
     */
    
    private static final String URL =
            "jdbc:mysql://localhost:3306/cmpe";
            
            /*+
            "useUnicode=true&" +
            "characterEncoding=UTF-8&" +
            "serverTimezone=UTC&" +
            "useSSL=false&" +
            "allowPublicKeyRetrieval=true";


    /**
     * Database username for authentication.
     */
    private static final String USER = "root";

    /**
     * Database password for authentication.
     */
    private static final String PASSWORD = "nilaybbb18";

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with static methods only.
     */
    private DatabaseConnection() {
        throw new AssertionError("DatabaseConnection is a utility class and should not be instantiated");
    }

    /**
     * Establishes and returns a connection to the MySQL database.
     * <p>
     * This method creates a new database connection each time it is called.
     * It is the caller's responsibility to close the connection after use,
     * preferably using try-with-resources statement.
     * </p>
     * 
     * <p><b>Example usage:</b></p>
     * <pre>{@code
     * try (Connection conn = DatabaseConnection.getConnection()) {
     *     // Use the connection
     *     PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
     *     // ... execute query
     * } catch (SQLException e) {
     *     // Handle exception
     * }
     * }</pre>
     * 
     * @return Connection object to the cms database
     * @throws SQLException if connection cannot be established due to:
     *         <ul>
     *           <li>MySQL server not running</li>
     *           <li>Invalid credentials</li>
     *           <li>Database does not exist</li>
     *           <li>Network issues</li>
     *         </ul>
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Tests if a database connection can be established successfully.
     * <p>
     * This method is useful for:
     * <ul>
     *   <li>Initial application setup verification</li>
     *   <li>Debugging connection issues</li>
     *   <li>Health checks</li>
     * </ul>
     * </p>
     * 
     * <p>The method automatically closes the test connection after verification.</p>
     * 
     * @return true if connection is successful and database is accessible, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Safely closes a database connection.
     * <p>
     * This method ignores any exceptions that occur during the close operation,
     * making it safe to call in finally blocks or when the connection state is unknown.
     * </p>
     * 
     * <p>It is recommended to use try-with-resources instead of manual closing:</p>
     * <pre>{@code
     * // Preferred approach
     * try (Connection conn = DatabaseConnection.getConnection()) {
     *     // Use connection
     * }
     * 
     * // Manual approach (if try-with-resources is not suitable)
     * Connection conn = null;
     * try {
     *     conn = DatabaseConnection.getConnection();
     *     // Use connection
     * } finally {
     *     DatabaseConnection.closeConnection(conn);
     * }
     * }</pre>
     * 
     * @param conn Connection to close (can be null, in which case method does nothing)
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                // Ignore - connection cleanup is best effort
                // Logging could be added here in production environment
            }
        }
    }

    /**
     * Validates if a connection is still active and usable.
     * <p>
     * This method checks two conditions:
     * <ol>
     *   <li>Connection is not closed</li>
     *   <li>Connection responds within timeout (2 seconds)</li>
     * </ol>
     * </p>
     * 
     * <p><b>Use cases:</b></p>
     * <ul>
     *   <li>Before executing a query on a cached connection</li>
     *   <li>In connection pooling scenarios</li>
     *   <li>To detect stale connections</li>
     * </ul>
     * 
     * @param conn Connection to validate
     * @return true if connection is valid and open, false if connection is null, 
     *         closed, or does not respond within timeout
     */
    public static boolean isConnectionValid(Connection conn) {
        if (conn == null) {
            return false;
        }
        try {
            // isValid() checks if connection is alive within the given timeout (in seconds)
            return !conn.isClosed() && conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Returns the database URL being used for connections.
     * <p>
     * This method is useful for debugging and logging purposes.
     * The password is not included in the URL for security reasons.
     * </p>
     * 
     * @return the JDBC URL string (without password)
     */
    public static String getDatabaseURL() {
        return URL;
    }

    /**
     * Returns the username being used for database connections.
     * <p>
     * This method is useful for debugging and logging purposes.
     * </p>
     * 
     * @return the database username
     */
    public static String getDatabaseUser() {
        return USER;
    }
}