import java.sql.*;
import java.util.Scanner;

public class JuniorDeveloper extends Role {

    public JuniorDeveloper(int id, String u, String n, String s) {
        super(id, u, n, s, "Junior Developer");
    }

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

    private void displayMenuOptions() {
        System.out.println(GREEN + "[1] - CONTACTS MENU" + RESET);
        System.out.println(GREEN + "[2] - UPDATE CONTACT" + RESET);  // NEW!
        System.out.println(GREEN + "[3] - CHANGE PASSWORD" + RESET);
        System.out.println("\n" + RED + "[4] - LOGOUT" + RESET);
        System.out.print("\n" + CYAN + "Pick an Option (1-4): " + RESET);
    }

    private String getUserChoice() {
        return sc.nextLine().trim();
    }

    private boolean isValidChoice(String choice) {
        return choice.matches("[1-4]");
    }

    private void handleInvalidChoice() {
        System.out.println(RED + "Invalid choice. Please enter 1, 2, 3, or 4." + RESET);
        pause();
    }

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

    private void handleContactsMenu() {
        try {
            contactsMenu();
        } catch (Exception e) {
            System.err.println(RED + "Error accessing contacts menu: " + e.getMessage() + RESET);
            pause();
        }
    }

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

    private void displayContactInfo(Contact contact) {
        System.out.println();
        System.out.println(GREEN + "Current Contact Information:" + RESET);
        System.out.println("════════════════════════════════════════");
        Contact.printHeader();
        contact.print();
        System.out.println("════════════════════════════════════════");
        System.out.println();
    }

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

    @Override
    public String toString() {
        return "JuniorDeveloper{" +
                "id=" + userId +
                ", username='" + username + '\'' +
                ", name='" + name + " " + surname + '\'' +
                '}';
    }

}
