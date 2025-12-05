import java.sql.*;
import java.util.Scanner;

/**
 * JuniorDeveloper role class with contact viewing and update permissions.
 * <p>
 * JuniorDeveloper extends Tester capabilities by adding the ability to
 * update existing contact information. This role is suitable for users
 * who need to maintain and correct contact data but should not have
 * full CRUD permissions.
 * </p>
 * 
 * <p><b>Permissions:</b></p>
 * <ul>
 *   <li>✅ List all contacts (inherited from Tester)</li>
 *   <li>✅ Search contacts by single/multiple fields (inherited)</li>
 *   <li>✅ Sort contacts (inherited)</li>
 *   <li>✅ <b>Update existing contacts</b> (NEW - Junior Developer only)</li>
 *   <li>✅ Change own password (inherited)</li>
 *   <li>✅ Logout (inherited)</li>
 *   <li>❌ Add new contacts (not allowed - requires Senior Developer)</li>
 *   <li>❌ Delete contacts (not allowed - requires Senior Developer)</li>
 *   <li>❌ User management (not allowed - requires Manager)</li>
 * </ul>
 * 
 * <p><b>Update Operations:</b></p>
 * <ul>
 *   <li>Update any field: name, phone, email, city, LinkedIn, birth date</li>
 *   <li>All updates are validated before saving</li>
 *   <li>Validation rules match those defined in Contact class</li>
 *   <li>Failed validations show clear error messages</li>
 * </ul>
 * 
 * <p><b>Typical Use Case:</b></p>
 * <p>
 * Junior developers maintain contact data quality by correcting typos,
 * updating phone numbers, adding missing information, etc.
 * </p>
 * 
 * @author Group 22
 * @version 1.0
 * @since 2024
 * @see Tester
 * @see SeniorDeveloper
 * @see Contact
 */
public class JuniorDeveloper extends Role {

    /**
     * Constructs a JuniorDeveloper role with specified user credentials.
     * <p>
     * This constructor initializes the Junior Developer role by calling
     * the parent Role constructor with the role name "Junior Developer".
     * </p>
     * 
     * @param id user ID from database (unique identifier)
     * @param u username for authentication
     * @param n first name of the user
     * @param s surname (last name) of the user
     */
    public JuniorDeveloper(int id, String u, String n, String s) {
        super(id, u, n, s, "Junior Developer");
    }

    /**
     * Displays and manages the Junior Developer role menu.
     * <p>
     * This method presents a console-based menu with options specific to
     * the Junior Developer role, including the ability to update contacts.
     * The menu runs in a loop until the user chooses to logout.
     * </p>
     * 
     * <p><b>Menu Options:</b></p>
     * <ol>
     *   <li><b>Contacts Menu:</b> View, search, and sort contacts</li>
     *   <li><b>Update Contact:</b> Modify existing contact information (NEW)</li>
     *   <li><b>Change Password:</b> Update own password</li>
     *   <li><b>Logout:</b> Exit to main menu</li>
     * </ol>
     * 
     * <p><b>Input Validation:</b></p>
     * <ul>
     *   <li>Only accepts inputs "1", "2", "3", or "4"</li>
     *   <li>Invalid inputs display error and re-prompt</li>
     * </ul>
     * 
     * <p><b>Exception Safety:</b></p>
     * <p>
     * All operations are exception-safe and will not crash the application.
     * </p>
     */
    @Override
    public void showMenu() {
        boolean running = true;

        while (running) {
            try {
                clearScreen();
                showUserHeader();

                displayMenuOptions();

                String choice = getUserChoice();

                if (!isValidChoice(choice)) {
                    handleInvalidChoice();
                    continue;
                }

                running = processMenuChoice(choice);

            } catch (Exception e) {
                handleUnexpectedError(e);
            }
        }
    }

    /**
     * Displays the menu options for Junior Developer role.
     */
    private void displayMenuOptions() {
        System.out.println(GREEN + "[1] - CONTACTS MENU" + RESET);
        System.out.println(GREEN + "[2] - UPDATE CONTACT" + RESET);  // NEW!
        System.out.println(GREEN + "[3] - CHANGE PASSWORD" + RESET);
        System.out.println("\n" + RED + "[4] - LOGOUT" + RESET);
        System.out.print("\n" + CYAN + "Pick an Option (1-4): " + RESET);
    }

    /**
     * Reads user input.
     */
    private String getUserChoice() {
        return sc.nextLine().trim();
    }

    /**
     * Validates menu choice (1-4).
     */
    private boolean isValidChoice(String choice) {
        return choice.matches("[1-4]");
    }

    /**
     * Handles invalid choices.
     */
    private void handleInvalidChoice() {
        System.out.println(RED + "Invalid choice. Please enter 1, 2, 3, or 4." + RESET);
        pause();
    }

    /**
     * Processes the user's menu choice.
     */
    private boolean processMenuChoice(String choice) {
        switch (choice) {
            case "1" -> {
                handleContactsMenu();
                return true;
            }

            case "2" -> {
                handleUpdateContact();  // NEW!
                return true;
            }

            case "3" -> {
                handleChangePassword();
                return true;
            }

            case "4" -> {
                return handleLogout();
            }

            default -> {
                return true;
            }
        }
    }

    /**
     * Handles contacts menu.
     */
    private void handleContactsMenu() {
        try {
            contactsMenu();
        } catch (Exception e) {
            System.err.println(RED + "Error accessing contacts menu: " + e.getMessage() + RESET);
            pause();
        }
    }

    /**
     * Handles change password.
     */
    private void handleChangePassword() {
        try {
            clearScreen();
            changePassword();
            pause();
        } catch (Exception e) {
            System.err.println(RED + "Error changing password: " + e.getMessage() + RESET);
            pause();
        }
    }

    /**
     * Handles logout with confirmation.
     */
    private boolean handleLogout() {
        System.out.print(
            CYAN + "Are you sure you want to logout (" +
            GREEN + "y" + CYAN + "/" + RED + "n" + CYAN + "): " + RESET
        );

        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("y")) {
            logout();
            return false;
        } else {
            System.out.println(GREEN + "Logout cancelled." + RESET);
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return true;
        }
    }

    // ==========================================
    // UPDATE CONTACT FUNCTIONALITY (NEW!)
    // ==========================================

    /**
     * Handles the Update Contact operation.
     * <p>
     * This is the main entry point for updating existing contacts.
     * The process includes:
     * <ol>
     *   <li>User enters contact ID to update</li>
     *   <li>System fetches and displays current contact information</li>
     *   <li>User selects which field to update</li>
     *   <li>User enters new value (with validation)</li>
     *   <li>System updates the database</li>
     *   <li>Success/failure message displayed</li>
     * </ol>
     * </p>
     * 
     * <p><b>Exception Safety:</b> All database and validation errors
     * are caught and displayed to the user.</p>
     */
    protected void handleUpdateContact() {
        clearScreen();
        System.out.println(CYAN + "╔════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + "║          UPDATE CONTACT                        ║" + RESET);
        System.out.println(CYAN + "╚════════════════════════════════════════════════╝" + RESET);
        System.out.println();

        try {
            // Step 1: Get contact ID
            int contactId = getContactIdFromUser();
            if (contactId == -1) {
                return; // User cancelled
            }

            // Step 2: Fetch current contact
            Contact contact = fetchContactById(contactId);
            if (contact == null) {
                System.out.println(RED + "Contact with ID " + contactId + " not found!" + RESET);
                pause();
                return;
            }

            // Step 3: Display current contact
            displayContactInfo(contact);

            // Step 4: Select field to update
            String field = selectFieldToUpdate();
            if (field == null) {
                return; // User cancelled
            }

            // Step 5: Get new value
            String newValue = getNewValueForField(field);
            if (newValue == null) {
                return; // User cancelled
            }

            // Step 6: Update database
            boolean success = updateContactInDatabase(contactId, field, newValue);

            // Step 7: Display result
            if (success) {
                System.out.println();
                System.out.println(GREEN + "✅ Contact updated successfully!" + RESET);
            } else {
                System.out.println();
                System.out.println(RED + "❌ Update failed. Please try again." + RESET);
            }

        } catch (Exception e) {
            System.err.println();
            System.err.println(RED + "Error updating contact: " + e.getMessage() + RESET);
        }

        pause();
    }

    /**
     * Gets contact ID from user input.
     * 
     * @return contact ID, or -1 if cancelled/invalid
     */
    private int getContactIdFromUser() {
        System.out.print(CYAN + "Enter Contact ID to update (or 0 to cancel): " + RESET);
        String input = sc.nextLine().trim();

        if (input.equals("0")) {
            System.out.println(YELLOW + "Update cancelled." + RESET);
            pause();
            return -1;
        }

        try {
            int id = Integer.parseInt(input);
            if (id < 0) {
                System.out.println(RED + "Invalid ID. Must be a positive number." + RESET);
                pause();
                return -1;
            }
            return id;
        } catch (NumberFormatException e) {
            System.out.println(RED + "Invalid input. Please enter a number." + RESET);
            pause();
            return -1;
        }
    }

    /**
     * Fetches a contact from the database by ID.
     * 
     * @param contactId the contact ID to fetch
     * @return Contact object if found, null otherwise
     */
    private Contact fetchContactById(int contactId) {
        String sql = "SELECT * FROM contacts WHERE contact_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contactId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Contact(
                    rs.getInt("contact_id"),
                    rs.getString("first_name"),
                    rs.getString("middle_name"),
                    rs.getString("last_name"),
                    rs.getString("nickname"),
                    rs.getString("city"),
                    rs.getString("phone_primary"),
                    rs.getString("phone_secondary"),
                    rs.getString("email"),
                    rs.getString("linkedin_url"),
                    rs.getString("birth_date")
                );
            }
        } catch (SQLException e) {
            System.err.println(RED + "Database error: " + e.getMessage() + RESET);
        }

        return null;
    }

    /**
     * Displays current contact information.
     */
    private void displayContactInfo(Contact contact) {
        System.out.println();
        System.out.println(GREEN + "Current Contact Information:" + RESET);
        System.out.println("════════════════════════════════════════");
        Contact.printHeader();
        contact.print();
        System.out.println("════════════════════════════════════════");
        System.out.println();
    }

    /**
     * Prompts user to select which field to update.
     * 
     * @return field name (database column), or null if cancelled
     */
    private String selectFieldToUpdate() {
        System.out.println(CYAN + "Select field to update:" + RESET);
        System.out.println(GREEN + "[1] - First Name" + RESET);
        System.out.println(GREEN + "[2] - Middle Name" + RESET);
        System.out.println(GREEN + "[3] - Last Name" + RESET);
        System.out.println(GREEN + "[4] - Nickname" + RESET);
        System.out.println(GREEN + "[5] - City" + RESET);
        System.out.println(GREEN + "[6] - Primary Phone" + RESET);
        System.out.println(GREEN + "[7] - Secondary Phone" + RESET);
        System.out.println(GREEN + "[8] - Email" + RESET);
        System.out.println(GREEN + "[9] - LinkedIn URL" + RESET);
        System.out.println(GREEN + "[10] - Birth Date" + RESET);
        System.out.println(RED + "[0] - Cancel" + RESET);
        System.out.print("\n" + CYAN + "Choose (0-10): " + RESET);

        String choice = sc.nextLine().trim();

        return switch (choice) {
            case "0" -> {
                System.out.println(YELLOW + "Update cancelled." + RESET);
                pause();
                yield null;
            }
            case "1" -> "first_name";
            case "2" -> "middle_name";
            case "3" -> "last_name";
            case "4" -> "nickname";
            case "5" -> "city";
            case "6" -> "phone_primary";
            case "7" -> "phone_secondary";
            case "8" -> "email";
            case "9" -> "linkedin_url";
            case "10" -> "birth_date";
            default -> {
                System.out.println(RED + "Invalid choice." + RESET);
                pause();
                yield null;
            }
        };
    }

    /**
     * Gets new value for the selected field with validation guidance.
     * 
     * @param field the database column name
     * @return new value, or null if cancelled
     */
    private String getNewValueForField(String field) {
        System.out.println();
        System.out.println(CYAN + "Enter new value for " + getFieldDisplayName(field) + ":" + RESET);
        
        // Show validation rules
        displayValidationRules(field);

        System.out.print(CYAN + "New value (or 'cancel' to abort): " + RESET);
        String value = sc.nextLine().trim();

        if (value.equalsIgnoreCase("cancel")) {
            System.out.println(YELLOW + "Update cancelled." + RESET);
            pause();
            return null;
        }

        // Validate input
        if (!validateFieldValue(field, value)) {
            System.out.println(RED + "Invalid value for this field!" + RESET);
            pause();
            return null;
        }

        return value;
    }

    /**
     * Returns display name for a field.
     */
    private String getFieldDisplayName(String field) {
        return switch (field) {
            case "first_name" -> "First Name";
            case "middle_name" -> "Middle Name";
            case "last_name" -> "Last Name";
            case "nickname" -> "Nickname";
            case "city" -> "City";
            case "phone_primary" -> "Primary Phone";
            case "phone_secondary" -> "Secondary Phone";
            case "email" -> "Email";
            case "linkedin_url" -> "LinkedIn URL";
            case "birth_date" -> "Birth Date";
            default -> field;
        };
    }

    /**
     * Displays validation rules for a field.
     */
    private void displayValidationRules(String field) {
        System.out.println(BLUE + "Validation rules:" + RESET);
        switch (field) {
            case "first_name", "middle_name", "last_name", "nickname" ->
                System.out.println("  - Letters, spaces, apostrophes, Turkish characters (Ç, Ğ, İ, Ö, Ş, Ü)");
            case "city" ->
                System.out.println("  - Letters, spaces, apostrophes, dots, dashes, Turkish characters");
            case "phone_primary", "phone_secondary" ->
                System.out.println("  - 10-15 digits, optional leading + (e.g., +905321234567)");
            case "email" ->
                System.out.println("  - Valid email format (must contain @)");
            case "linkedin_url" ->
                System.out.println("  - LinkedIn profile URL (optional)");
            case "birth_date" ->
                System.out.println("  - Format: YYYY-MM-DD (e.g., 1990-05-15)");
        }
        System.out.println();
    }

    /**
     * Validates the new value for a field.
     */
    private boolean validateFieldValue(String field, String value) {
        // Allow empty for optional fields
        if (value.isEmpty()) {
            return field.equals("middle_name") || field.equals("nickname") || 
                   field.equals("city") || field.equals("phone_secondary") || 
                   field.equals("linkedin_url");
        }

        return switch (field) {
            case "first_name", "middle_name", "last_name", "nickname" -> 
                Contact.isValidName(value);
            case "city" -> 
                Contact.isValidCity(value);
            case "phone_primary", "phone_secondary" -> 
                Contact.isValidPhone(value);
            case "email" -> 
                Contact.isValidEmail(value);
            case "birth_date" -> 
                Contact.isValidBirthDate(value);
            case "linkedin_url" -> 
                true; // No strict validation for URL
            default -> false;
        };
    }

    /**
     * Updates the contact in the database.
     * 
     * @param contactId the contact ID to update
     * @param field the field to update
     * @param newValue the new value
     * @return true if successful, false otherwise
     */
    private boolean updateContactInDatabase(int contactId, String field, String newValue) {
        String sql = "UPDATE contacts SET " + field + " = ?, updated_at = CURRENT_TIMESTAMP WHERE contact_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Handle empty values (set to NULL)
            if (newValue.isEmpty()) {
                stmt.setNull(1, Types.VARCHAR);
            } else {
                stmt.setString(1, newValue);
            }

            stmt.setInt(2, contactId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println(RED + "Database error: " + e.getMessage() + RESET);
            return false;
        }
    }

    /**
     * Handles unexpected errors.
     */
    private void handleUnexpectedError(Exception e) {
        System.err.println();
        System.err.println(RED + "╔════════════════════════════════════════════════╗" + RESET);
        System.err.println(RED + "║  UNEXPECTED ERROR                              ║" + RESET);
        System.err.println(RED + "╚════════════════════════════════════════════════╝" + RESET);
        System.err.println();
        System.err.println(RED + "An error occurred: " + e.getMessage() + RESET);
        System.err.println();
        pause();
    }

    /**
     * Returns string representation.
     */
    @Override
    public String toString() {
        return "JuniorDeveloper{" +
                "id=" + userId +
                ", username='" + username + '\'' +
                ", name='" + name + " " + surname + '\'' +
                '}';
    }
}