import java.util.Scanner;

/**
 * Main entry point for the Role-Based Contact Management System.
 * <p>
 * This class initializes the application, displays startup animations,
 * tests database connectivity, and manages the main application menu.
 * The system provides role-based access control for different user types:
 * Tester, Junior Developer, Senior Developer, and Manager.
 * </p>
 * 
 * <p><b>Application Flow:</b></p>
 * <ol>
 *   <li>Display startup animation</li>
 *   <li>Test database connection</li>
 *   <li>Show main menu with options: Login or Terminate</li>
 *   <li>Handle user authentication via LoginMenu</li>
 *   <li>Return to main menu after logout</li>
 *   <li>Display exit animation on termination</li>
 * </ol>
 * 
 * <p><b>System Requirements:</b></p>
 * <ul>
 *   <li>MySQL server running on localhost:3306</li>
 *   <li>Database 'cms' must exist</li>
 *   <li>MySQL JDBC driver in classpath</li>
 *   <li>Console with ANSI color support (optional)</li>
 * </ul>
 * 
 * @author Group 22
 * @version 1.0
 * @since 2024
 */
public class Group22 {

    // ==========================================
    // ANSI COLOR CODES
    // ==========================================
    
    /** ANSI code to reset all text formatting. */
    public static final String RESET = "\u001B[0m";
    
    /** ANSI code for green text (success messages). */
    public static final String GREEN = "\u001B[32m";
    
    /** ANSI code for red text (error messages). */
    public static final String RED = "\u001B[31m";
    
    /** ANSI code for cyan text (prompts and information). */
    public static final String CYAN = "\u001B[36m";
    
    /** ANSI code for purple text (special messages). */
    public static final String PURPLE = "\u001B[35m";
    
    /** ANSI code for blue text (headers). */
    public static final String BLUE = "\u001B[34m";

    // ==========================================
    // SINGLETON SCANNER
    // ==========================================
    
    /**
     * Singleton Scanner instance for reading user input.
     * <p>
     * Using a single Scanner throughout the application prevents
     * resource conflicts and ensures System.in is not closed prematurely.
     * </p>
     */
    private static final Scanner SCANNER = new Scanner(System.in);

    // ==========================================
    // MAIN METHOD (Application Entry Point)
    // ==========================================
    
    /**
     * Main entry point of the application.
     * <p>
     * Initializes the system with UTF-8 encoding for Turkish character support,
     * plays startup animation, tests database connection, and launches the main menu.
     * </p>
     * 
     * <p>If database connection fails, the application displays an error message
     * with troubleshooting steps and exits gracefully.</p>
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Initialize UTF-8 encoding for Turkish character support
        initializeEncoding();
        
        // Play startup animation
        AnimationFrames.playStartupAnimation();
        
        // Test database connection before proceeding
        if (!testDatabaseConnection()) {
            displayDatabaseError();
            return; // Exit if database is not available
        }
        
        // Database OK - show main menu
        showMenu();
    }

    // ==========================================
    // INITIALIZATION METHODS
    // ==========================================
    
    /**
     * Initializes UTF-8 encoding for the application.
     * <p>
     * This ensures Turkish characters (Ç, Ğ, İ, Ö, Ş, Ü) are displayed
     * correctly in the console. The method sets system properties for
     * file and console encoding.
     * </p>
     * 
     * <p>If encoding initialization fails, the application continues
     * anyway, but Turkish characters may not display correctly.</p>
     */
    private static void initializeEncoding() {
        try {
            System.setProperty("file.encoding", "UTF-8");
            System.setProperty("console.encoding", "UTF-8");
        } catch (Exception e) {
            // Encoding initialization failed, continue anyway
            System.err.println("Warning: UTF-8 encoding could not be set. Turkish characters may not display correctly.");
        }
    }

    /**
     * Tests the database connection before starting the application.
     * <p>
     * This method verifies that:
     * <ul>
     *   <li>MySQL server is running</li>
     *   <li>Database 'cms' is accessible</li>
     *   <li>Credentials are correct</li>
     *   <li>JDBC driver is available</li>
     * </ul>
     * </p>
     * 
     * @return true if database connection is successful, false otherwise
     */
    private static boolean testDatabaseConnection() {
        System.out.println(CYAN + "🔌 Testing database connection..." + RESET);
        
        boolean connected = DatabaseConnection.testConnection();
        
        if (connected) {
            System.out.println(GREEN + "✅ Database connected successfully!" + RESET);
            System.out.println();
        }
        
        return connected;
    }

    /**
     * Displays a detailed error message when database connection fails.
     * <p>
     * Provides troubleshooting steps to help users resolve common
     * database connection issues.
     * </p>
     */
    private static void displayDatabaseError() {
        System.err.println();
        System.err.println(RED + "❌ DATABASE CONNECTION FAILED!" + RESET);
        System.err.println();
        System.err.println(YELLOW + "Please check the following:" + RESET);
        System.err.println("  1. MySQL server is running");
        System.err.println("     → Windows: net start MySQL80");
        System.err.println("     → Check Services: services.msc");
        System.err.println();
        System.err.println("  2. Database 'cms' exists");
        System.err.println("     → mysql -u root -p");
        System.err.println("     → SHOW DATABASES;");
        System.err.println();
        System.err.println("  3. Username and password are correct");
        System.err.println("     → User: " + DatabaseConnection.getDatabaseUser());
        System.err.println();
        System.err.println("  4. MySQL JDBC driver is in classpath");
        System.err.println("     → Check lib/ folder for mysql-connector-java.jar");
        System.err.println();
        System.err.println(RED + "Application cannot start without database connection." + RESET);
    }

    // ==========================================
    // MAIN MENU
    // ==========================================
    
    /**
     * Displays and manages the main application menu.
     * <p>
     * This method runs in an infinite loop, presenting the user with
     * two options:
     * <ol>
     *   <li>User Login - Opens the authentication screen</li>
     *   <li>Terminate - Exits the application with animation</li>
     * </ol>
     * </p>
     * 
     * <p>The loop continues until the user chooses to terminate.
     * After logout, the user returns to this main menu.</p>
     * 
     * <p><b>Input Validation:</b> Only accepts "1" or "2".
     * Invalid inputs display an error message and re-prompt.</p>
     */
    public static void showMenu() {
        while (true) {
            try {
                displayMainMenu();
                String choice = getUserChoice();
                handleMenuChoice(choice);
            } catch (Exception e) {
                System.err.println(RED + "An unexpected error occurred: " + e.getMessage() + RESET);
                System.err.println(YELLOW + "Returning to main menu..." + RESET);
                pause();
                clearConsole();
            }
        }
    }

    /**
     * Displays the main menu options.
     */
    private static void displayMainMenu() {
        System.out.println();
        System.out.println(CYAN + "╔════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + "║  Welcome to Contact Management System!         ║" + RESET);
        System.out.println(CYAN + "╚════════════════════════════════════════════════╝" + RESET);
        System.out.println();
        System.out.println(GREEN + "[1] User Login Menu" + RESET);
        System.out.println(RED + "[2] Terminate" + RESET);
        System.out.println();
        System.out.print(CYAN + "Please pick an option (1-2): " + RESET);
    }

    /**
     * Gets the user's menu choice.
     * 
     * @return the user's input, trimmed of whitespace
     */
    private static String getUserChoice() {
        return SCANNER.nextLine().trim();
    }

    /**
     * Handles the user's menu choice.
     * <p>
     * Routes the user to the appropriate action based on their selection:
     * <ul>
     *   <li>Option 1: Opens login menu</li>
     *   <li>Option 2: Exits application</li>
     *   <li>Invalid: Displays error and re-prompts</li>
     * </ul>
     * </p>
     * 
     * @param choice the user's menu selection
     */
    private static void handleMenuChoice(String choice) {
        switch (choice) {
            case "1" -> handleLogin();
            case "2" -> handleTerminate();
            default -> handleInvalidChoice();
        }
    }

    /**
     * Handles the login option.
     * <p>
     * Clears the console, calls the LoginMenu for authentication,
     * and returns to main menu after logout.
     * </p>
     */
    private static void handleLogin() {
        clearConsole();
        try {
            LoginMenu.login();
        } catch (Exception e) {
            System.err.println(RED + "Login failed due to an error: " + e.getMessage() + RESET);
            System.err.println(YELLOW + "Please try again." + RESET);
            pause();
        }
        clearConsole();
    }

    /**
     * Handles the terminate option.
     * <p>
     * Displays exit animation, closes the scanner, and terminates
     * the application with a clean exit code (0).
     * </p>
     */
    private static void handleTerminate() {
        clearConsole();
        AnimationFrames.playExitAnimation();
        clearConsole();
        
        System.out.println(PURPLE + "═══════════════════════════════════════" + RESET);
        System.out.println(GREEN + "  Application terminated successfully." + RESET);
        System.out.println(CYAN + "  Thank you for using our system!" + RESET);
        System.out.println(PURPLE + "═══════════════════════════════════════" + RESET);
        
        // Close scanner before exit
        SCANNER.close();
        
        // Clean exit
        System.exit(0);
    }

    /**
     * Handles invalid menu choices.
     * <p>
     * Displays an error message and re-prompts the user.
     * </p>
     */
    private static void handleInvalidChoice() {
        clearConsole();
        System.out.println(RED + "❌ Invalid choice! Please enter 1 or 2." + RESET);
        pause();
        clearConsole();
    }

    // ==========================================
    // UTILITY METHODS
    // ==========================================
    
    /**
     * Clears the console screen.
     * <p>
     * This method is cross-platform compatible:
     * <ul>
     *   <li>Windows: Uses CMD "cls" command</li>
     *   <li>Unix/Linux/Mac: Uses ANSI escape sequences</li>
     * </ul>
     * </p>
     * 
     * <p>If clearing fails (e.g., in an IDE console), the method
     * fails silently to prevent application crashes.</p>
     */
    public static void clearConsole() {
        try {
            String os = System.getProperty("os.name");
            if (os != null && os.contains("Windows")) {
                // Windows: use cls command
                new ProcessBuilder("cmd", "/c", "cls")
                        .inheritIO()
                        .start()
                        .waitFor();
            } else {
                // Unix/Linux/Mac: use ANSI escape codes
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Console clearing failed - ignore
            // This commonly happens in IDE consoles
        }
    }

    /**
     * Pauses execution and waits for user to press ENTER.
     * <p>
     * This gives the user time to read messages before the screen
     * is cleared or updated.
     * </p>
     */
    private static void pause() {
        System.out.println();
        System.out.print(CYAN + "Press ENTER to continue..." + RESET);
        try {
            SCANNER.nextLine();
        } catch (Exception e) {
            // Ignore input errors
        }
    }

    /**
     * Returns the singleton Scanner instance.
     * <p>
     * This method can be used by other classes to access the
     * shared Scanner instance, preventing resource conflicts.
     * </p>
     * 
     * @return the shared Scanner instance
     */
    public static Scanner getScanner() {
        return SCANNER;
    }

    // ==========================================
    // ANSI COLOR CODE CONSTANT (For Yellow)
    // ==========================================
    
    /** ANSI code for yellow text (warnings). */
    private static final String YELLOW = "\u001B[33m";
}