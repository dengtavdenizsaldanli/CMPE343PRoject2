import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class LoginMenu {

    // ==========================================
    // ANSI COLOR CODES
    // ==========================================
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String CYAN = "\u001B[36m";
    public static final String PURPLE = "\u001B[35m";
    public static final String BLUE = "\u001B[34m";

    // ==========================================
    // CONSTANTS
    // ==========================================

    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final String HASH_ALGORITHM = "SHA-256";

    // ==========================================
    // PUBLIC LOGIN METHOD
    // ==========================================
    
    public static void login() {
        Scanner scanner = Group22.getScanner();
        Role loggedUser = null;
        int attempts = 0;

        while (loggedUser == null && attempts < MAX_LOGIN_ATTEMPTS) {
            displayLoginHeader();

            System.out.print(CYAN + "Username: " + RESET);
            String username = scanner.nextLine().trim();

            if (username.isEmpty()) {
                System.out.println("\n" + RED + "Username cannot be empty!" + RESET);
                pause(scanner);
                Group22.clearConsole();
                continue;
            }

            System.out.println();


            System.out.print(CYAN + "Password: " + RESET);
            String password = scanner.nextLine();

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
                return;
            } catch (NoSuchAlgorithmException e) {
                handleHashingError(e);
                return;
            } catch (Exception e) {
                handleUnexpectedError(e);
                pause(scanner);
                Group22.clearConsole();
            }
        }

        if (attempts >= MAX_LOGIN_ATTEMPTS && loggedUser == null) {
            handleMaxAttemptsReached();
        }
    }

    // ==========================================
    // AUTHENTICATION LOGIC
    // ==========================================

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
,
                if (storedHash.equalsIgnoreCase(enteredHash)) {

                    int id = rs.getInt("user_id");
                    String name = rs.getString("name");
                    String surname = rs.getString("surname");
                    String role = rs.getString("role");

                    return createRoleObject(id, username, name, surname, role);
                }
            }
        }
        return null;
    }

    
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

    private static void handleSuccessfulLogin(Role loggedUser) {
        System.out.println("\n" + GREEN + "✅ Login successful! Welcome, " + 
                          loggedUser.getFullName() + "!" + RESET);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Group22.clearConsole();
        loggedUser.showMenu();
    }

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

    private static void handleUnexpectedError(Exception e) {
        System.err.println();
        System.err.println(RED + "An unexpected error occurred: " + e.getMessage() + RESET);
        System.err.println(CYAN + "Please try again." + RESET);
        System.err.println();
    }

    // ==========================================
    // UTILITY METHODS
    // ==========================================

    private static void pause(Scanner scanner) {
        System.out.print(CYAN + "Press ENTER to continue..." + RESET);
        try {
            scanner.nextLine();
        } catch (Exception e) {
        }
    }
}
