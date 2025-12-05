/**
 * Tester role class with read-only permissions on contacts.
 * <p>
 * Tester is the most restricted role in the Contact Management System.
 * Users with this role can view and search contacts but cannot modify,
 * add, or delete any data.
 * </p>
 * 
 * <p><b>Permissions:</b></p>
 * <ul>
 *   <li>✅ List all contacts (view-only)</li>
 *   <li>✅ Search contacts by single field</li>
 *   <li>✅ Search contacts by multiple fields</li>
 *   <li>✅ Sort contacts by any field (ascending/descending)</li>
 *   <li>✅ Change own password</li>
 *   <li>✅ Logout</li>
 *   <li>❌ Update contacts (not allowed)</li>
 *   <li>❌ Add contacts (not allowed)</li>
 *   <li>❌ Delete contacts (not allowed)</li>
 *   <li>❌ User management (not allowed)</li>
 * </ul>
 * 
 * <p><b>Typical Use Case:</b></p>
 * <p>
 * Testers use this role to verify contact data quality, test search
 * functionality, and ensure data integrity without risk of accidental
 * modifications.
 * </p>
 * 
 * <p><b>Menu Structure:</b></p>
 * <ol>
 *   <li>Contacts Menu - Access to list/search/sort operations</li>
 *   <li>Change Password - Update own password</li>
 *   <li>Logout - Exit to main menu</li>
 * </ol>
 * 
 * @author Group 22
 * @version 1.0
 * @since 2024
 * @see Role
 * @see JuniorDeveloper
 * @see SeniorDeveloper
 * @see Manager
 */
public class Tester extends Role {

    /**
     * Constructs a Tester role with specified user credentials.
     * <p>
     * This constructor initializes the Tester role by calling the parent
     * Role constructor with the role name "Tester".
     * </p>
     * 
     * @param id user ID from database (unique identifier)
     * @param u username for authentication
     * @param n first name of the user
     * @param s surname (last name) of the user
     */
    public Tester(int id, String u, String n, String s) {
        super(id, u, n, s, "Tester");
    }

    /**
     * Displays and manages the Tester role menu.
     * <p>
     * This method presents a console-based menu with options specific to
     * the Tester role. The menu runs in a loop until the user chooses to
     * logout. All operations are exception-safe and will not crash the
     * application.
     * </p>
     * 
     * <p><b>Menu Options:</b></p>
     * <ol>
     *   <li><b>Contacts Menu:</b> Access to list, search, and sort contacts.
     *       This opens a sub-menu with various contact viewing options.</li>
     *   <li><b>Change Password:</b> Update the user's password. The new password
     *       must meet security requirements.</li>
     *   <li><b>Logout:</b> Exit the current session and return to main menu.
     *       Requires confirmation to prevent accidental logout.</li>
     * </ol>
     * 
     * <p><b>Input Validation:</b></p>
     * <ul>
     *   <li>Only accepts inputs "1", "2", or "3"</li>
     *   <li>Invalid inputs display an error message and re-prompt</li>
     *   <li>Empty inputs are treated as invalid</li>
     * </ul>
     * 
     * <p><b>Exception Handling:</b></p>
     * <p>
     * All operations are wrapped in try-catch blocks to prevent crashes.
     * Any unexpected errors are caught, logged, and the user is returned
     * to the menu.
     * </p>
     * 
     * <p><b>Thread Safety:</b></p>
     * <p>
     * This method is not thread-safe and should only be called from a
     * single thread per user session.
     * </p>
     */
    @Override
    public void showMenu() {
        boolean running = true;

        while (running) {
            try {
                // Clear screen and show header
                clearScreen();
                showUserHeader();

                // Display menu options
                displayMenuOptions();

                // Get user choice
                String choice = getUserChoice();

                // Validate input
                if (!isValidChoice(choice)) {
                    handleInvalidChoice();
                    continue;
                }

                // Process choice
                running = processMenuChoice(choice);

            } catch (Exception e) {
                // Catch any unexpected errors to prevent crashes
                handleUnexpectedError(e);
            }
        }
    }

    /**
     * Displays the menu options for Tester role.
     * <p>
     * Shows a formatted menu with color-coded options:
     * <ul>
     *   <li>Green: Available operations</li>
     *   <li>Red: Exit option</li>
     *   <li>Cyan: Input prompt</li>
     * </ul>
     * </p>
     */
    private void displayMenuOptions() {
        System.out.println(GREEN + "[1] - CONTACTS MENU" + RESET);
        System.out.println(GREEN + "[2] - CHANGE PASSWORD" + RESET);
        System.out.println("\n" + RED + "[3] - LOGOUT" + RESET);
        System.out.print("\n" + CYAN + "Pick an Option (1-3): " + RESET);
    }

    /**
     * Reads the user's menu choice from input.
     * <p>
     * Trims whitespace from the input for better user experience.
     * </p>
     * 
     * @return the user's trimmed input string
     */
    private String getUserChoice() {
        return sc.nextLine().trim();
    }

    /**
     * Validates if the user's choice is valid.
     * <p>
     * Valid choices are: "1", "2", or "3"
     * </p>
     * 
     * @param choice the user's input
     * @return true if choice is valid, false otherwise
     */
    private boolean isValidChoice(String choice) {
        return choice.matches("[1-3]");
    }

    /**
     * Handles invalid menu choices.
     * <p>
     * Displays an error message and pauses to allow user to read it.
     * </p>
     */
    private void handleInvalidChoice() {
        System.out.println(RED + "Invalid choice. Please enter 1, 2, or 3." + RESET);
        pause();
    }

    /**
     * Processes the user's valid menu choice.
     * <p>
     * Routes the user to the appropriate function based on their selection.
     * </p>
     * 
     * @param choice the validated user choice ("1", "2", or "3")
     * @return true to continue menu loop, false to exit (logout)
     */
    private boolean processMenuChoice(String choice) {
        switch (choice) {
            case "1" -> {
                handleContactsMenu();
                return true;
            }

            case "2" -> {
                handleChangePassword();
                return true;
            }

            case "3" -> {
                return handleLogout();
            }

            default -> {
                // Should never reach here due to validation
                return true;
            }
        }
    }

    /**
     * Handles the Contacts Menu option.
     * <p>
     * Opens the contacts sub-menu where users can list, search, and sort contacts.
     * This method wraps the call in exception handling for safety.
     * </p>
     */
    private void handleContactsMenu() {
        try {
            contactsMenu();
        } catch (Exception e) {
            System.err.println(RED + "Error accessing contacts menu: " + e.getMessage() + RESET);
            System.err.println(CYAN + "Returning to main menu..." + RESET);
            pause();
        }
    }

    /**
     * Handles the Change Password option.
     * <p>
     * Clears the screen, calls the password change function, and pauses
     * after completion to show success/error messages.
     * This method wraps the call in exception handling for safety.
     * </p>
     */
    private void handleChangePassword() {
        try {
            clearScreen();
            changePassword();
            pause();
        } catch (Exception e) {
            System.err.println(RED + "Error changing password: " + e.getMessage() + RESET);
            System.err.println(CYAN + "Please try again later." + RESET);
            pause();
        }
    }

    /**
     * Handles the Logout option with confirmation.
     * <p>
     * Prompts the user to confirm logout to prevent accidental exits.
     * If confirmed, calls the logout method and returns false to exit menu loop.
     * </p>
     * 
     * <p><b>Confirmation Prompt:</b></p>
     * <ul>
     *   <li>"y" or "Y" - Confirms logout</li>
     *   <li>"n" or "N" - Cancels logout</li>
     *   <li>Any other input - Cancels logout</li>
     * </ul>
     * 
     * @return false if user confirms logout (exits menu), true otherwise (stays in menu)
     */
    private boolean handleLogout() {
        System.out.print(
            CYAN + "Are you sure you want to logout (" +
            GREEN + "y" + CYAN + "/" + RED + "n" + CYAN + "): " + RESET
        );

        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("y")) {
            logout();
            return false; // Exit menu loop
        } else {
            System.out.println(GREEN + "Logout cancelled. Returning to menu..." + RESET);
            try {
                Thread.sleep(800); // Brief pause to show message
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return true; // Continue menu loop
        }
    }

    /**
     * Handles unexpected errors that occur during menu operations.
     * <p>
     * Logs the error, displays a user-friendly message, and pauses
     * to allow the user to read the error before returning to menu.
     * </p>
     * 
     * <p><b>Error Information Displayed:</b></p>
     * <ul>
     *   <li>Error type and message</li>
     *   <li>Suggestion to try again</li>
     *   <li>Option to continue using the system</li>
     * </ul>
     * 
     * @param e the exception that was caught
     */
    private void handleUnexpectedError(Exception e) {
        System.err.println();
        System.err.println(RED + "╔════════════════════════════════════════════════╗" + RESET);
        System.err.println(RED + "║  UNEXPECTED ERROR                              ║" + RESET);
        System.err.println(RED + "╚════════════════════════════════════════════════╝" + RESET);
        System.err.println();
        System.err.println(RED + "An unexpected error occurred in the Tester menu." + RESET);
        System.err.println(CYAN + "Error details: " + e.getMessage() + RESET);
        System.err.println();
        System.err.println(CYAN + "The error has been logged. You can continue using the system." + RESET);
        System.err.println(CYAN + "If the problem persists, please contact support." + RESET);
        System.err.println();

        // Log to console for debugging (in production, log to file)
        System.err.println("Stack trace:");
        e.printStackTrace();

        pause();
    }

    /**
     * Returns a string representation of this Tester object.
     * <p>
     * Useful for debugging and logging purposes.
     * </p>
     * 
     * @return string containing user ID, username, and full name
     */
    @Override
    public String toString() {
        return "Tester{" +
                "id=" + userId +
                ", username='" + username + '\'' +
                ", name='" + name + " " + surname + '\'' +
                '}';
    }
}