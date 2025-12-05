import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Handles user authentication and login process for the Contact Management System.
 * <p>
 * This class provides secure user authentication by:
 * <ul>
 *   <li>Validating credentials against the database</li>
 *   <li>Using SHA-256 password hashing</li>
 *   <li>Creating appropriate Role objects based on user type</li>
 *   <li>Enforcing maximum login attempts</li>
 *   <li>Providing detailed feedback on authentication failures</li>
 * </ul>
 * </p>
 * 
 * <p><b>Security Features:</b></p>
 * <ul>
 *   <li>Passwords are hashed using SHA-256</li>
 *   <li>PreparedStatements prevent SQL injection</li>
 *   <li>Case-insensitive hash comparison (MySQL compatibility)</li>
 *   <li>Maximum 3 failed login attempts before exit</li>
 * </ul>
 * 
 * <p><b>Supported Roles:</b></p>
 * <ul>
 *   <li>Tester - Read-only access to contacts</li>
 *   <li>Junior Developer - Read and update contacts</li>
 *   <li>Senior Developer - Full CRUD operations on contacts</li>
 *   <li>Manager - User management and statistics</li>
 * </ul>
 * 
 * @author Group 22
 * @version 1.0
 * @since 2024
 */
public class LoginMenu {

    // ==========================================
    // ANSI COLOR CODES
    // ==========================================
    
    /** ANSI code to reset all text formatting. */
    public static final String RESET = "\u001B[0m";
    
    /** ANSI code for green text (success messages). */
    public static final String GREEN = "\u001B[32m";
    
    /** ANSI code for red text (error messages). */
    public static final String RED = "\u001B[31m";
    
    /** ANSI code for cyan text (prompts). */
    public static final String CYAN = "\u001B[36m";
    
    /** ANSI code for purple text. */
    public static final String PURPLE = "\u001B[35m";
    
    /** ANSI code for blue text. */
    public static final String BLUE = "\u001B[34m";

    // ==========================================
    // CONSTANTS
    // ==========================================
    
    /**
     * Maximum number of failed login attempts allowed before exiting to main menu.
     */
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    
    /**
     * SHA-256 algorithm identifier for password hashing.
     */
    private static final String HASH_ALGORITHM = "SHA-256";

    // ==========================================
    // PUBLIC LOGIN METHOD
    // ==========================================
    
    /**
     * Initiates the login process and authenticates the user.
     * <p>
     * This method presents a login screen where users enter their credentials.
     * Upon successful authentication, the appropriate Role object is created
     * and the user's menu is displayed.
     * </p>
     * 
     * <p><b>Process Flow:</b></p>
     * <ol>
     *   <li>Display login prompt</li>
     *   <li>Read username and password</li>
     *   <li>Validate input (non-empty)</li>
     *   <li>Query database for user credentials</li>
     *   <li>Compare password hashes</li>
     *   <li>Create appropriate Role object if authentication succeeds</li>
     *   <li>Display role-specific menu</li>
     *   <li>Return to login screen on invalid credentials</li>
     * </ol>
     * 
     * <p><b>Security Notes:</b></p>
     * <ul>
     *   <li>Passwords are never stored or logged in plain text</li>
     *   <li>Only hashed passwords are compared</li>
     *   <li>Failed attempts are counted and limited</li>
     *   <li>Database queries use PreparedStatements</li>
     * </ul>
     * 
     * <p>After {@value #MAX_LOGIN_ATTEMPTS} failed attempts, the user is
     * returned to the main menu.</p>
     */
    public static void login() {
        Scanner scanner = Group22.getScanner();  // Use shared Scanner
        Role loggedUser = null;
        int attempts = 0;

        while (loggedUser == null && attempts < MAX_LOGIN_ATTEMPTS) {
            displayLoginHeader();
            
            // Get username
            System.out.print(CYAN + "Username: " + RESET);
            String username = scanner.nextLine().trim();

            // Validate username
            if (username.isEmpty()) {
                System.out.println("\n" + RED + "Username cannot be empty!" + RESET);
                pause(scanner);
                Group22.clearConsole();
                continue;
            }

            System.out.println();

            // Get password
            System.out.print(CYAN + "Password: " + RESET);
            String password = scanner.nextLine();

            // Validate password
            if (password.isEmpty()) {
                System.out.println("\n" + RED + "Password cannot be empty!" + RESET);
                pause(scanner);
                Group22.clearConsole();
                continue;
            }

            attempts++;

            try {
                loggedUser = authenticateAndCreateUser(username, password);

                if (loggedUser == null) {
                    handleFailedLogin(attempts);
                } else {
                    handleSuccessfulLogin(loggedUser);
                }

            } catch (SQLException e) {
                handleDatabaseError(e);
                return; // Exit login on database error
            } catch (NoSuchAlgorithmException e) {
                handleHashingError(e);
                return; // Exit login on hashing error
            } catch (Exception e) {
                handleUnexpectedError(e);
                pause(scanner);
                Group22.clearConsole();
            }
        }

        // Maximum attempts reached
        if (attempts >= MAX_LOGIN_ATTEMPTS && loggedUser == null) {
            handleMaxAttemptsReached();
        }
    }

    // ==========================================
    // AUTHENTICATION LOGIC
    // ==========================================
    
    /**
     * Authenticates user credentials and creates the appropriate Role object.
     * <p>
     * This method:
     * <ol>
     *   <li>Queries the database for the user with the given username</li>
     *   <li>Retrieves the stored password hash</li>
     *   <li>Hashes the entered password</li>
     *   <li>Compares hashes (case-insensitive for MySQL compatibility)</li>
     *   <li>Creates and returns the appropriate Role object</li>
     * </ol>
     * </p>
     * 
     * <p><b>SQL Query:</b> Uses PreparedStatement to prevent SQL injection.</p>
     * <p><b>Hash Comparison:</b> Case-insensitive to handle MySQL hash variations.</p>
     * 
     * @param username the username entered by the user
     * @param password the password entered by the user (plain text)
     * @return Role object (Tester, JuniorDeveloper, SeniorDeveloper, or Manager)
     *         if authentication succeeds, null otherwise
     * @throws SQLException if database query fails
     * @throws NoSuchAlgorithmException if SHA-256 algorithm is not available
     */
    private static Role authenticateAndCreateUser(String username, String password)
            throws SQLException, NoSuchAlgorithmException {

        String sql = "SELECT user_id, name, surname, role, password_hash FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String enteredHash = hashPassword(password);

                // Case-insensitive comparison for MySQL compatibility
                if (storedHash.equalsIgnoreCase(enteredHash)) {

                    int id = rs.getInt("user_id");
                    String name = rs.getString("name");
                    String surname = rs.getString("surname");
                    String role = rs.getString("role");

                    // Create appropriate Role object based on user's role
                    return createRoleObject(id, username, name, surname, role);
                }
            }
        }
        return null;
    }

    /**
     * Creates the appropriate Role object based on user's role string.
     * <p>
     * This method uses a switch expression to instantiate the correct
     * Role subclass. If the role is not recognized, returns null.
     * </p>
     * 
     * @param id user ID from database
     * @param username user's username
     * @param name user's first name
     * @param surname user's last name
     * @param role user's role as string ("Tester", "Junior Developer", etc.)
     * @return appropriate Role subclass instance, or null if role is unknown
     */
    private static Role createRoleObject(int id, String username, String name, 
                                        String surname, String role) {
        return switch (role) {
            case "Tester" -> new Tester(id, username, name, surname);
            case "Junior Developer" -> new JuniorDeveloper(id, username, name, surname);
            case "Senior Developer" -> new SeniorDeveloper(id, username, name, surname);
            case "Manager" -> new Manager(id, username, name, surname);
            default -> {
                System.err.println(RED + "Unknown role: " + role + RESET);
                yield null;
            }
        };
    }

    // ==========================================
    // PASSWORD HASHING
    // ==========================================
    
    /**
     * Hashes a password using SHA-256 algorithm.
     * <p>
     * This method converts the password string to bytes, applies SHA-256 hashing,
     * and converts the resulting byte array to a lowercase hexadecimal string.
     * </p>
     * 
     * <p><b>Algorithm:</b> SHA-256 (256-bit hash)</p>
     * <p><b>Output Format:</b> Lowercase hexadecimal string (64 characters)</p>
     * 
     * <p><b>Example:</b></p>
     * <pre>
     * hashPassword("tt") → "6f2268ed9a56452c92a884a123ebcf3728445a83de4a29c92a8405949ec91a38"
     * </pre>
     * 
     * @param password the plain text password to hash
     * @return the SHA-256 hash as a lowercase hexadecimal string
     * @throws NoSuchAlgorithmException if SHA-256 algorithm is not available
     */
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] encodedHash = digest.digest(password.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // ==========================================
    // UI DISPLAY METHODS
    // ==========================================
    
    /**
     * Displays the login screen header.
     */
    private static void displayLoginHeader() {
        System.out.println();
        System.out.println(BLUE + "╔════════════════════════════════════════════════╗" + RESET);
        System.out.println(BLUE + "║              USER LOGIN                        ║" + RESET);
        System.out.println(BLUE + "╚════════════════════════════════════════════════╝" + RESET);
        System.out.println();
        System.out.println(CYAN + "Please enter your credentials to continue." + RESET);
        System.out.println();
    }

    // ==========================================
    // SUCCESS AND ERROR HANDLERS
    // ==========================================
    
    /**
     * Handles successful login.
     * <p>
     * Clears the console and displays the user's role-specific menu.
     * After the user logs out, control returns to the login method.
     * </p>
     * 
     * @param loggedUser the authenticated user's Role object
     */
    private static void handleSuccessfulLogin(Role loggedUser) {
        System.out.println("\n" + GREEN + "✅ Login successful! Welcome, " + 
                          loggedUser.getFullName() + "!" + RESET);
        
        // Brief pause to show success message
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Group22.clearConsole();
        loggedUser.showMenu();  // Display role-specific menu
    }

    /**
     * Handles failed login attempt.
     * <p>
     * Displays an error message with remaining attempts count.
     * </p>
     * 
     * @param attempts number of attempts made so far
     */
    private static void handleFailedLogin(int attempts) {
        System.out.println("\n" + RED + "❌ Invalid credentials!" + RESET);
        
        int remaining = MAX_LOGIN_ATTEMPTS - attempts;
        if (remaining > 0) {
            System.out.println(RED + "Attempt " + attempts + "/" + MAX_LOGIN_ATTEMPTS + 
                             " - " + remaining + " attempts remaining." + RESET);
        }
        
        System.out.println();
        pause(Group22.getScanner());
        Group22.clearConsole();
    }

    /**
     * Handles the case when maximum login attempts are reached.
     */
    private static void handleMaxAttemptsReached() {
        Group22.clearConsole();
        System.out.println(RED + "╔════════════════════════════════════════════════╗" + RESET);
        System.out.println(RED + "║  MAXIMUM LOGIN ATTEMPTS REACHED                ║" + RESET);
        System.out.println(RED + "╚════════════════════════════════════════════════╝" + RESET);
        System.out.println();
        System.out.println(RED + "You have exceeded the maximum number of login attempts." + RESET);
        System.out.println(CYAN + "Returning to main menu..." + RESET);
        System.out.println();
        pause(Group22.getScanner());
    }

    /**
     * Handles database errors during authentication.
     * 
     * @param e the SQLException that occurred
     */
    private static void handleDatabaseError(SQLException e) {
        System.err.println();
        System.err.println(RED + "╔════════════════════════════════════════════════╗" + RESET);
        System.err.println(RED + "║  DATABASE ERROR                                ║" + RESET);
        System.err.println(RED + "╚════════════════════════════════════════════════╝" + RESET);
        System.err.println();
        System.err.println(RED + "Unable to connect to the database." + RESET);
        System.err.println(CYAN + "Error details: " + e.getMessage() + RESET);
        System.err.println();
        System.err.println(CYAN + "Please check database connection and try again." + RESET);
        System.err.println();
        pause(Group22.getScanner());
    }

    /**
     * Handles password hashing errors.
     * 
     * @param e the NoSuchAlgorithmException that occurred
     */
    private static void handleHashingError(NoSuchAlgorithmException e) {
        System.err.println();
        System.err.println(RED + "╔════════════════════════════════════════════════╗" + RESET);
        System.err.println(RED + "║  SYSTEM ERROR                                  ║" + RESET);
        System.err.println(RED + "╚════════════════════════════════════════════════╝" + RESET);
        System.err.println();
        System.err.println(RED + "Password hashing algorithm not available." + RESET);
        System.err.println(CYAN + "Error details: " + e.getMessage() + RESET);
        System.err.println();
        System.err.println(CYAN + "This is a critical system error. Please contact support." + RESET);
        System.err.println();
        pause(Group22.getScanner());
    }

    /**
     * Handles unexpected errors during login.
     * 
     * @param e the unexpected Exception that occurred
     */
    private static void handleUnexpectedError(Exception e) {
        System.err.println();
        System.err.println(RED + "An unexpected error occurred: " + e.getMessage() + RESET);
        System.err.println(CYAN + "Please try again." + RESET);
        System.err.println();
    }

    // ==========================================
    // UTILITY METHODS
    // ==========================================
    
    /**
     * Pauses execution and waits for user to press ENTER.
     * 
     * @param scanner the Scanner to read from
     */
    private static void pause(Scanner scanner) {
        System.out.print(CYAN + "Press ENTER to continue..." + RESET);
        try {
            scanner.nextLine();
        } catch (Exception e) {
            // Ignore input errors
        }
    }

    /**
     * Returns the full name by combining first name and surname.
     * This is a temporary method until Role.getFullName() is available.
     * 
     * @param name first name
     * @param surname last name
     * @return full name as "FirstName LastName"
     */
    private static String getFullName(String name, String surname) {
        return name + " " + surname;
    }
}