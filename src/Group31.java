import java.util.Scanner;

public class Group31 {

    // ==========================================
    // ANSI COLOR CODES
    // ==========================================
    
    private static final String YELLOW = "\u001B[33m";
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String CYAN = "\u001B[36m";
    public static final String PURPLE = "\u001B[35m";
    public static final String BLUE = "\u001B[34m";

    private static final Scanner SCANNER = new Scanner(System.in);

    // ==========================================
    // MAIN METHOD (Application Entry Point)
    // ==========================================
    
    public static void main(String[] args) {
        initializeEncoding();
        AnimationFrames.playStartupAnimation();
        if (!testDatabaseConnection()) {
            displayDatabaseError();
        }
        showMenu();
    }

    // ==========================================
    // INITIALIZATION METHODS
    // ==========================================
    
    private static void initializeEncoding() {
        try {
            System.setProperty("file.encoding", "UTF-8");
            System.setProperty("console.encoding", "UTF-8");
        } catch (Exception e) {
            System.err.println("Warning: UTF-8 encoding could not be set. Turkish characters may not display correctly.");
        }
    }

    private static boolean testDatabaseConnection() {
        System.out.println(CYAN + "🔌 Testing database connection..." + RESET);
        
        boolean connected = DatabaseConnection.testConnection();
        
        if (connected) {
            System.out.println(GREEN + "✅ Database connected successfully!" + RESET);
            System.out.println();
        }
        
        return connected;
    }

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

    private static String getUserChoice() {
        return SCANNER.nextLine().trim();
    }

    private static void handleMenuChoice(String choice) {
        switch (choice) {
            case "1" -> handleLogin();
            case "2" -> handleTerminate();
            default -> handleInvalidChoice();
        }
    }

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

    private static void handleTerminate() {
        clearConsole();
        AnimationFrames.playExitAnimation();
        clearConsole();
        
        System.out.println(PURPLE + "═══════════════════════════════════════" + RESET);
        System.out.println(GREEN + "  Application terminated successfully." + RESET);
        System.out.println(CYAN + "  Thank you for using our system!" + RESET);
        System.out.println(PURPLE + "═══════════════════════════════════════" + RESET);
        SCANNER.close();
        System.exit(0);
    }

    private static void handleInvalidChoice() {
        clearConsole();
        System.out.println(RED + "❌ Invalid choice! Please enter 1 or 2." + RESET);
        pause();
        clearConsole();
    }

    // ==========================================
    // UTILITY METHODS
    // ==========================================
    
    public static void clearConsole() {
        try {
            String os = System.getProperty("os.name");
            if (os != null && os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls")
                        .inheritIO()
                        .start()
                        .waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
        }
    }

    private static void pause() {
        System.out.println();
        System.out.print(CYAN + "Press ENTER to continue..." + RESET);
        try {
            SCANNER.nextLine();
        } catch (Exception e) {
        }
    }

    public static Scanner getScanner() {
        return SCANNER;
    }
}
