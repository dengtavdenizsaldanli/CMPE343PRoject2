import java.sql.*;
import java.util.*;

/**
 * SeniorDeveloper role class with full CRUD permissions on contacts.
 * <p>
 * SeniorDeveloper is the most powerful role for contact management,
 * extending Junior Developer capabilities with the ability to add new
 * contacts, delete existing contacts, and undo recent operations.
 * </p>
 * 
 * <p><b>Permissions:</b></p>
 * <ul>
 *   <li>✅ List all contacts (inherited from Tester)</li>
 *   <li>✅ Search contacts (inherited from Tester)</li>
 *   <li>✅ Sort contacts (inherited from Tester)</li>
 *   <li>✅ Update existing contacts (inherited from Junior Developer)</li>
 *   <li>✅ <b>Add new contacts</b> (NEW - Senior Developer only)</li>
 *   <li>✅ <b>Add multiple contacts in batch</b> (NEW - Senior Developer only)</li>
 *   <li>✅ <b>Delete existing contacts</b> (NEW - Senior Developer only)</li>
 *   <li>✅ <b>Delete multiple contacts</b> (NEW - Senior Developer only)</li>
 *   <li>✅ <b>Undo last operation</b> (NEW - Senior Developer only)</li>
 *   <li>✅ Change own password (inherited)</li>
 *   <li>✅ Logout (inherited)</li>
 *   <li>❌ User management (not allowed - requires Manager)</li>
 * </ul>
 * 
 * <p><b>UNDO System:</b></p>
 * <ul>
 *   <li>Maintains a stack of recent operations (last 10)</li>
 *   <li>Supports undo for: ADD, UPDATE, DELETE operations</li>
 *   <li>ADD operation → Undo deletes the added contact</li>
 *   <li>UPDATE operation → Undo restores old values</li>
 *   <li>DELETE operation → Undo re-inserts the contact</li>
 *   <li>Undo history is cleared on logout</li>
 * </ul>
 * 
 * <p><b>Data Validation:</b></p>
 * <ul>
 *   <li>All inputs validated using Contact class validation methods</li>
 *   <li>Required fields: firstName, lastName, phonePrimary, email</li>
 *   <li>Optional fields: middleName, nickname, city, phoneSecondary, linkedinUrl, birthDate</li>
 *   <li>Turkish character support (Ç, Ğ, İ, Ö, Ş, Ü)</li>
 * </ul>
 * 
 * @author Group 22
 * @version 1.0
 * @since 2024
 * @see JuniorDeveloper
 * @see Contact
 */
public class SeniorDeveloper extends JuniorDeveloper {

    // ==========================================
    // UNDO SYSTEM
    // ==========================================
    
    /**
     * Stack to store recent operations for undo functionality.
     * Limited to last 10 operations to prevent memory overflow.
     */
    private Stack<ContactOperation> undoStack = new Stack<>();
    
    /**
     * Maximum number of operations to keep in undo stack.
     */
    private static final int MAX_UNDO_HISTORY = 10;

    /**
     * Constructs a SeniorDeveloper role with specified user credentials.
     * <p>
     * Initializes the Senior Developer role and prepares the undo stack.
     * </p>
     * 
     * @param id user ID from database (unique identifier)
     * @param u username for authentication
     * @param n first name of the user
     * @param s surname (last name) of the user
     */
    public SeniorDeveloper(int id, String u, String n, String s) {
        super(id, u, n, s);
        this.role = "Senior Developer";
    }

    /**
     * Displays and manages the Senior Developer role menu.
     * <p>
     * Extends Junior Developer menu with additional options for
     * adding contacts, deleting contacts, and undo operations.
     * </p>
     * 
     * <p><b>Menu Options:</b></p>
     * <ol>
     *   <li>Contacts Menu - View, search, sort</li>
     *   <li>Update Contact - Modify existing contact</li>
     *   <li><b>Add Contact(s)</b> - Add new contact(s) (NEW)</li>
     *   <li><b>Delete Contact(s)</b> - Remove contact(s) (NEW)</li>
     *   <li><b>Undo Last Operation</b> - Revert last change (NEW)</li>
     *   <li>Change Password - Update own password</li>
     *   <li>Logout - Exit to main menu</li>
     * </ol>
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
     * Displays Senior Developer menu options.
     */
    private void displayMenuOptions() {
        System.out.println(GREEN + "[1] - CONTACTS MENU" + RESET);
        System.out.println(GREEN + "[2] - UPDATE CONTACT" + RESET);
        System.out.println(GREEN + "[3] - ADD CONTACT(S)" + RESET);  // NEW!
        System.out.println(GREEN + "[4] - DELETE CONTACT(S)" + RESET);  // NEW!
        System.out.println(PURPLE + "[5] - UNDO LAST OPERATION" + RESET);  // NEW!
        System.out.println(GREEN + "[6] - CHANGE PASSWORD" + RESET);
        System.out.println("\n" + RED + "[7] - LOGOUT" + RESET);
        System.out.print("\n" + CYAN + "Pick an Option (1-7): " + RESET);
    }

    /**
     * Reads user input.
     */
    private String getUserChoice() {
        return sc.nextLine().trim();
    }

    /**
     * Validates menu choice (1-7).
     */
    private boolean isValidChoice(String choice) {
        return choice.matches("[1-7]");
    }

    /**
     * Handles invalid choices.
     */
    private void handleInvalidChoice() {
        System.out.println(RED + "Invalid choice. Please enter 1-7." + RESET);
        pause();
    }

    /**
     * Processes menu choice.
     */
    private boolean processMenuChoice(String choice) {
        switch (choice) {
            case "1" -> {
                handleContactsMenu();
                return true;
            }

            case "2" -> {
                handleUpdateContact();
                return true;
            }

            case "3" -> {
                handleAddContact();
                return true;
            }

            case "4" -> {
                handleDeleteContact();
                return true;
            }

            case "5" -> {
                handleUndo();
                return true;
            }

            case "6" -> {
                handleChangePassword();
                return true;
            }

            case "7" -> {
                return handleLogout();
            }

            default -> {
                return true;
            }
        }
    }

    // ==========================================
    // EXISTING METHODS (from JuniorDeveloper)
    // ==========================================

    private void handleContactsMenu() {
        try {
            contactsMenu();
        } catch (Exception e) {
            System.err.println(RED + "Error: " + e.getMessage() + RESET);
            pause();
        }
    }

    private void handleChangePassword() {
        try {
            clearScreen();
            changePassword();
            pause();
        } catch (Exception e) {
            System.err.println(RED + "Error: " + e.getMessage() + RESET);
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
            // Clear undo stack on logout for security
            undoStack.clear();
            logout();
            return false;
        } else {
            System.out.println(GREEN + "Logout cancelled." + RESET);
            try { Thread.sleep(800); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return true;
        }
    }

    // ==========================================
    // ADD CONTACT FUNCTIONALITY (NEW!)
    // ==========================================

    /**
     * Handles adding new contact(s).
     * <p>
     * Presents options to add a single contact or multiple contacts in batch.
     * Each added contact is recorded in the undo stack for potential reversal.
     * </p>
     */
    /**
     * Handles adding new contact(s).
     * <p>
     * Presents options to add a single contact or multiple contacts in batch.
     * Each added contact is recorded in the undo stack for potential reversal.
     * </p>
     */
    private void handleAddContact() {
        boolean addMenuRunning = true;
        
        while (addMenuRunning) {
            clearScreen();
            System.out.println(CYAN + "╔════════════════════════════════════════════════╗" + RESET);
            System.out.println(CYAN + "║          ADD NEW CONTACT(S)                    ║" + RESET);
            System.out.println(CYAN + "╚════════════════════════════════════════════════╝" + RESET);
            System.out.println();
            System.out.println(GREEN + "[1] - Add Single Contact" + RESET);
            System.out.println(GREEN + "[2] - Add Multiple Contacts (Batch)" + RESET);
            System.out.println(RED + "[0] - Back to Main Menu" + RESET);
            System.out.print("\n" + CYAN + "Choose (0-2): " + RESET);

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    boolean result = addSingleContact();
                    // If contact added successfully, return to main menu
                    if (result) {
                        addMenuRunning = false;
                    }
                    // If cancelled, stay in add menu
                }
                
                case "2" -> {
                    addMultipleContacts();
                    // After batch operation, return to main menu
                    addMenuRunning = false;
                }
                
                case "0" -> {
                    System.out.println(YELLOW + "Returning to main menu..." + RESET);
                    addMenuRunning = false;
                }
                
                default -> {
                    System.out.println(RED + "Invalid choice. Enter 0, 1, or 2." + RESET);
                    pause();
                }
            }
        }
    }

    /**
     * Adds a single contact with full validation.
     * <p>
     * Prompts user for all contact fields, validates each input,
     * inserts into database, and records operation for undo.
     * </p>
     */
     private boolean addSingleContact() {
        clearScreen();
        System.out.println(CYAN + "=== Add New Contact ===" + RESET);
        System.out.println(CYAN + "(Type '0' or 'cancel' at any step to abort)" + RESET);
        System.out.println();

        try {
            // Required fields
            String firstName = getRequiredInput("First Name", Contact::isValidName,
                "Letters, spaces, apostrophes, Turkish characters only");
            if (firstName == null) {
                System.out.println(YELLOW + "Add contact cancelled." + RESET);
                pause();
                return false;
            }

            String lastName = getRequiredInput("Last Name", Contact::isValidName,
                "Letters, spaces, apostrophes, Turkish characters only");
            if (lastName == null) {
                System.out.println(YELLOW + "Add contact cancelled." + RESET);
                pause();
                return false;
            }

            String phonePrimary = getRequiredInput("Primary Phone", Contact::isValidPhone,
                "10-15 digits, optional leading + (e.g., +905321234567)");
            if (phonePrimary == null) {
                System.out.println(YELLOW + "Add contact cancelled." + RESET);
                pause();
                return false;
            }

            String email = getRequiredInput("Email", Contact::isValidEmail,
                "Valid email format (must contain @)");
            if (email == null) {
                System.out.println(YELLOW + "Add contact cancelled." + RESET);
                pause();
                return false;
            }

            // Optional fields
            String middleName = getOptionalInput("Middle Name", Contact::isValidName);
            if (middleName == null) {
                System.out.println(YELLOW + "Add contact cancelled." + RESET);
                pause();
                return false;
            }
            
            String nickname = getOptionalInput("Nickname", Contact::isValidName);
            if (nickname == null) {
                System.out.println(YELLOW + "Add contact cancelled." + RESET);
                pause();
                return false;
            }
            
            String city = getOptionalInput("City", Contact::isValidCity);
            if (city == null) {
                System.out.println(YELLOW + "Add contact cancelled." + RESET);
                pause();
                return false;
            }
            
            String phoneSecondary = getOptionalInput("Secondary Phone", Contact::isValidPhone);
            if (phoneSecondary == null) {
                System.out.println(YELLOW + "Add contact cancelled." + RESET);
                pause();
                return false;
            }
            
            String linkedinUrl = getOptionalInput("LinkedIn URL", s -> true);
            if (linkedinUrl == null) {
                System.out.println(YELLOW + "Add contact cancelled." + RESET);
                pause();
                return false;
            }
            
            String birthDate = getOptionalInput("Birth Date (YYYY-MM-DD)", Contact::isValidBirthDate);
            if (birthDate == null) {
                System.out.println(YELLOW + "Add contact cancelled." + RESET);
                pause();
                return false;
            }

            // Confirm before inserting
            System.out.println();
            System.out.print(CYAN + "Confirm add contact? (" + GREEN + "y" + CYAN + "/" + RED + "n" + CYAN + "): " + RESET);
            String confirm = sc.nextLine().trim();

            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println(YELLOW + "Contact not added." + RESET);
                pause();
                return false;
            }

            // Insert into database
            int newContactId = insertContactToDatabase(firstName, middleName, lastName, nickname, city,
                phonePrimary, phoneSecondary, email, linkedinUrl, birthDate);

            if (newContactId > 0) {
                System.out.println();
                System.out.println(GREEN + "✅ Contact added successfully! (ID: " + newContactId + ")" + RESET);

                // Add to undo stack
                Contact addedContact = fetchContactById(newContactId);
                if (addedContact != null) {
                    addToUndoStack(new AddOperation(addedContact));
                }
                
                pause();
                return true;  // Successfully added
            } else {
                System.out.println();
                System.out.println(RED + "❌ Failed to add contact." + RESET);
                pause();
                return false;
            }

        } catch (Exception e) {
            System.err.println(RED + "Error adding contact: " + e.getMessage() + RESET);
            pause();
            return false;
        }
    }

    /**
     * Adds multiple contacts in batch mode.
     */
    private void addMultipleContacts() {
        clearScreen();
        System.out.println(CYAN + "=== Add Multiple Contacts (Batch Mode) ===" + RESET);
        System.out.println();

        System.out.print(CYAN + "How many contacts to add (0 to cancel)? " + RESET);
        String input = sc.nextLine().trim();

        // Check for cancel
        if (input.equals("0")) {
            System.out.println(YELLOW + "Batch add cancelled." + RESET);
            pause();
            return;
        }

        int count;
        try {
            count = Integer.parseInt(input);
            if (count < 1 || count > 50) {
                System.out.println(RED + "Please enter a number between 1 and 50." + RESET);
                pause();
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println(RED + "Invalid number." + RESET);
            pause();
            return;
        }

        int successCount = 0;
        int cancelledCount = 0;
        
        for (int i = 1; i <= count; i++) {
            clearScreen();
            System.out.println(CYAN + "=== Contact " + i + " of " + count + " ===" + RESET);
            System.out.println();

            boolean result = addSingleContact();
            
            if (result) {
                successCount++;
            } else {
                cancelledCount++;
                System.out.print(CYAN + "Contact " + i + " cancelled. Continue with next? (y/n): " + RESET);
                String cont = sc.nextLine().trim();
                if (!cont.equalsIgnoreCase("y")) {
                    System.out.println(YELLOW + "Batch operation terminated." + RESET);
                    break;
                }
            }
        }

        clearScreen();
        System.out.println(GREEN + "╔════════════════════════════════════════════════╗" + RESET);
        System.out.println(GREEN + "║        BATCH ADD COMPLETED                     ║" + RESET);
        System.out.println(GREEN + "╚════════════════════════════════════════════════╝" + RESET);
        System.out.println();
        System.out.println("  • Successfully added: " + GREEN + successCount + RESET);
        System.out.println("  • Cancelled: " + YELLOW + cancelledCount + RESET);
        System.out.println();
        pause();
    }
    
   /**
     * Gets required input with validation.
     * 
     * @return input string if valid, null if cancelled
     */
    private String getRequiredInput(String fieldName, java.util.function.Predicate<String> validator, String rules) {
        while (true) {
            System.out.println(BLUE + fieldName + " (Required - 0 to cancel):" + RESET);
            System.out.println("  Rules: " + rules);
            System.out.print("  Value: ");
            String value = sc.nextLine().trim();

            // Check for cancel
            if (value.equals("0") || value.equalsIgnoreCase("cancel")) {
                return null;  // Signal cancellation
            }

            if (value.isEmpty()) {
                System.out.println(RED + "This field is required!" + RESET);
                continue;
            }

            if (validator.test(value)) {
                return value;
            } else {
                System.out.println(RED + "Invalid format!" + RESET);
            }
        }
    }

    /**
     * Gets optional input with validation.
     * 
     * @return input string if valid, empty string if skipped, null if cancelled
     */
    private String getOptionalInput(String fieldName, java.util.function.Predicate<String> validator) {
        System.out.println(BLUE + fieldName + " (Optional - press ENTER to skip, 0 to cancel):" + RESET);
        System.out.print("  Value: ");
        String value = sc.nextLine().trim();

        // Check for cancel
        if (value.equals("0") || value.equalsIgnoreCase("cancel")) {
            return null;  // Signal cancellation
        }

        if (value.isEmpty()) {
            return "";  // Return empty string for optional skip
        }

        if (validator.test(value)) {
            return value;
        } else {
            System.out.println(RED + "Invalid format! Skipping..." + RESET);
            return "";
        }
    }

    /**
     * Inserts contact into database.
     * 
     * @return new contact ID, or -1 on failure
     */
    private int insertContactToDatabase(String firstName, String middleName, String lastName,
                                       String nickname, String city, String phonePrimary,
                                       String phoneSecondary, String email, String linkedinUrl,
                                       String birthDate) {
        String sql = "INSERT INTO contacts (first_name, middle_name, last_name, nickname, city, " +
                    "phone_primary, phone_secondary, email, linkedin_url, birth_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, firstName);
            stmt.setString(2, middleName);
            stmt.setString(3, lastName);
            stmt.setString(4, nickname);
            stmt.setString(5, city);
            stmt.setString(6, phonePrimary);
            stmt.setString(7, phoneSecondary);
            stmt.setString(8, email);
            stmt.setString(9, linkedinUrl);
            stmt.setString(10, birthDate);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println(RED + "Database error: " + e.getMessage() + RESET);
        }

        return -1;
    }

    /**
     * Fetches contact by ID (used for undo).
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

    // ==========================================
    // DELETE CONTACT FUNCTIONALITY (NEW!)
    // ==========================================

    /**
     * Handles deleting contact(s).
     */
    /**
     * Handles deleting contact(s).
     */
    private void handleDeleteContact() {
        boolean deleteMenuRunning = true;
        
        while (deleteMenuRunning) {
            clearScreen();
            System.out.println(RED + "╔════════════════════════════════════════════════╗" + RESET);
            System.out.println(RED + "║          DELETE CONTACT(S)                     ║" + RESET);
            System.out.println(RED + "╚════════════════════════════════════════════════╝" + RESET);
            System.out.println();
            System.out.println(YELLOW + "⚠️  WARNING: This action will delete contact data!" + RESET);
            System.out.println();
            System.out.println(GREEN + "[1] - Delete Single Contact" + RESET);
            System.out.println(GREEN + "[2] - Delete Multiple Contacts" + RESET);
            System.out.println(RED + "[0] - Back to Main Menu" + RESET);
            System.out.print("\n" + CYAN + "Choose (0-2): " + RESET);

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    boolean result = deleteSingleContact();
                    // If contact deleted successfully, return to main menu
                    if (result) {
                        deleteMenuRunning = false;
                    }
                    // If cancelled, stay in delete menu
                }
                
                case "2" -> {
                    deleteMultipleContacts();
                    // After batch operation, return to main menu
                    deleteMenuRunning = false;
                }
                
                case "0" -> {
                    System.out.println(YELLOW + "Returning to main menu..." + RESET);
                    deleteMenuRunning = false;
                }
                
                default -> {
                    System.out.println(RED + "Invalid choice. Enter 0, 1, or 2." + RESET);
                    pause();
                }
            }
        }
    }

    /**
     * Deletes a single contact.
     * 
     * @return true if contact deleted, false if cancelled
     */
    private boolean deleteSingleContact() {
        System.out.println();
        System.out.print(CYAN + "Enter Contact ID to delete (or 0 to cancel): " + RESET);
        String input = sc.nextLine().trim();

        if (input.equals("0")) {
            System.out.println(YELLOW + "Delete cancelled." + RESET);
            pause();
            return false;
        }

        int contactId;
        try {
            contactId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println(RED + "Invalid ID." + RESET);
            pause();
            return false;
        }

        // Fetch contact first for undo
        Contact contact = fetchContactById(contactId);
        if (contact == null) {
            System.out.println(RED + "Contact not found!" + RESET);
            pause();
            return false;
        }

        // Display contact
        System.out.println();
        System.out.println(RED + "Contact to be deleted:" + RESET);
        Contact.printHeader();
        contact.print();
        System.out.println();

        // Confirm deletion
        System.out.print(RED + "Type 'DELETE' to confirm (or 0 to cancel): " + RESET);
        String confirm = sc.nextLine().trim();

        if (confirm.equals("0")) {
            System.out.println(YELLOW + "Deletion cancelled." + RESET);
            pause();
            return false;
        }

        if (!confirm.equals("DELETE")) {
            System.out.println(YELLOW + "Deletion cancelled. You must type 'DELETE' exactly." + RESET);
            pause();
            return false;
        }

        // Delete from database
        if (deleteContactFromDatabase(contactId)) {
            System.out.println();
            System.out.println(GREEN + "✅ Contact deleted successfully!" + RESET);

            // Add to undo stack
            addToUndoStack(new DeleteOperation(contact));
            
            pause();
            return true;  // Successfully deleted
        } else {
            System.out.println();
            System.out.println(RED + "❌ Failed to delete contact." + RESET);
            pause();
            return false;
        }
    }
/**
     * Deletes multiple contacts.
     * 
     * @return true if contacts deleted, false if cancelled
     */
    private void deleteMultipleContacts() {
        System.out.println();
        System.out.println(CYAN + "Enter contact IDs separated by commas (e.g., 5,12,23)" + RESET);
        System.out.println(CYAN + "Or type '0' to cancel." + RESET);
        System.out.print("IDs: ");
        String input = sc.nextLine().trim();

        // Check for cancel
        if (input.equals("0")) {
            System.out.println(YELLOW + "Multiple delete cancelled." + RESET);
            pause();
            return;  // Return to delete menu
        }

        if (input.isEmpty()) {
            System.out.println(YELLOW + "No IDs provided." + RESET);
            pause();
            return;  // Return to delete menu
        }

        String[] idStrings = input.split(",");
        List<Integer> ids = new ArrayList<>();

        for (String idStr : idStrings) {
            try {
                ids.add(Integer.parseInt(idStr.trim()));
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid ID: " + idStr + RESET);
            }
        }

        if (ids.isEmpty()) {
            System.out.println(RED + "No valid IDs." + RESET);
            pause();
            return;  // Return to delete menu
        }

        // Show contacts to be deleted
        System.out.println();
        System.out.println(RED + "Contacts to be deleted:" + RESET);
        System.out.println(RED + "════════════════════════════════════════════════" + RESET);
        
        List<Contact> contactsToDelete = new ArrayList<>();
        int foundCount = 0;
        
        for (int id : ids) {
            Contact contact = fetchContactById(id);
            if (contact != null) {
                if (foundCount == 0) {
                    Contact.printHeader();
                }
                contact.print();
                contactsToDelete.add(contact);
                foundCount++;
            } else {
                System.out.println(YELLOW + "  ID " + id + " not found (skipped)" + RESET);
            }
        }
        
        if (contactsToDelete.isEmpty()) {
            System.out.println(RED + "No valid contacts found to delete." + RESET);
            pause();
            return;  // Return to delete menu
        }
        
        System.out.println(RED + "════════════════════════════════════════════════" + RESET);
        System.out.println();
        System.out.println(RED + "⚠️  This action CANNOT be undone!" + RESET);
        System.out.print(RED + "Delete " + contactsToDelete.size() + " contact(s)? Type 'DELETE' to confirm (or 0 to cancel): " + RESET);
        String confirm = sc.nextLine().trim();

        // Check for cancel
        if (confirm.equals("0")) {
            System.out.println(YELLOW + "Deletion cancelled." + RESET);
            pause();
            return;  // Return to delete menu
        }

        if (!confirm.equals("DELETE")) {
            System.out.println(YELLOW + "Deletion cancelled. You must type 'DELETE' exactly." + RESET);
            pause();
            return;  // Return to delete menu
        }

        // If we reach here, user confirmed - proceed with deletion
        int deletedCount = 0;
        int failedCount = 0;
        
        for (Contact contact : contactsToDelete) {
            if (deleteContactFromDatabase(contact.getId())) {
                deletedCount++;
                addToUndoStack(new DeleteOperation(contact));
            } else {
                failedCount++;
            }
        }

        System.out.println();
        System.out.println(GREEN + "╔════════════════════════════════════════════════╗" + RESET);
        System.out.println(GREEN + "║        MULTIPLE DELETE COMPLETED               ║" + RESET);
        System.out.println(GREEN + "╚════════════════════════════════════════════════╝" + RESET);
        System.out.println();
        System.out.println("  • Successfully deleted: " + GREEN + deletedCount + RESET);
        if (failedCount > 0) {
            System.out.println("  • Failed to delete: " + RED + failedCount + RESET);
        }
        System.out.println();
        pause();
        
        // Note: Method returns here after success
        // handleDeleteContact() will exit loop and go to main menu
    }
    /**
     * Deletes contact from database.
     */
    private boolean deleteContactFromDatabase(int contactId) {
        String sql = "DELETE FROM contacts WHERE contact_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contactId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println(RED + "Database error: " + e.getMessage() + RESET);
            return false;
        }
    }

    // ==========================================
    // UNDO FUNCTIONALITY (NEW!)
    // ==========================================

    /**
     * Handles undo operation.
     */
    private void handleUndo() {
        clearScreen();
        System.out.println(PURPLE + "╔════════════════════════════════════════════════╗" + RESET);
        System.out.println(PURPLE + "║          UNDO LAST OPERATION                   ║" + RESET);
        System.out.println(PURPLE + "╚════════════════════════════════════════════════╝" + RESET);
        System.out.println();

        if (undoStack.isEmpty()) {
            System.out.println(YELLOW + "No operations to undo." + RESET);
            System.out.println(CYAN + "Undo history is empty or was cleared after logout." + RESET);
            pause();
            return;
        }

        ContactOperation lastOp = undoStack.peek();
        System.out.println(CYAN + "Last operation: " + lastOp.getDescription() + RESET);
        System.out.println();
        System.out.print(PURPLE + "Confirm undo? (" + GREEN + "y" + PURPLE + "/" + RED + "n" + PURPLE + "): " + RESET);

        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("y")) {
            ContactOperation op = undoStack.pop();
            boolean success = op.revert();

            if (success) {
                System.out.println();
                System.out.println(GREEN + "✅ Operation undone successfully!" + RESET);
            } else {
                System.out.println();
                System.out.println(RED + "❌ Undo failed." + RESET);
            }
        } else {
            System.out.println(YELLOW + "Undo cancelled." + RESET);
        }

        pause();
    }

    /**
     * Adds operation to undo stack.
     */
    private void addToUndoStack(ContactOperation operation) {
        // Limit stack size
        if (undoStack.size() >= MAX_UNDO_HISTORY) {
            undoStack.remove(0);
        }
        undoStack.push(operation);
    }

    // ==========================================
    // UNDO OPERATION CLASSES
    // ==========================================

    /**
     * Abstract base class for undoable operations.
     */
    private abstract class ContactOperation {
        abstract boolean revert();
        abstract String getDescription();
    }

    /**
     * Represents an ADD operation (undo = delete).
     */
    private class AddOperation extends ContactOperation {
        private Contact addedContact;

        AddOperation(Contact contact) {
            this.addedContact = contact;
        }

        @Override
        boolean revert() {
            return deleteContactFromDatabase(addedContact.getId());
        }

        @Override
        String getDescription() {
            return "ADD Contact (ID: " + addedContact.getId() + ", Name: " + 
                   addedContact.getFirstName() + " " + addedContact.getLastName() + ")";
        }
    }

    /**
     * Represents a DELETE operation (undo = re-insert).
     */
    private class DeleteOperation extends ContactOperation {
        private Contact deletedContact;

        DeleteOperation(Contact contact) {
            this.deletedContact = contact;
        }

        @Override
        boolean revert() {
            // Re-insert with same ID
            String sql = "INSERT INTO contacts (contact_id, first_name, middle_name, last_name, " +
                        "nickname, city, phone_primary, phone_secondary, email, linkedin_url, birth_date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, deletedContact.getId());
                stmt.setString(2, deletedContact.getFirstName());
                stmt.setString(3, deletedContact.getMiddleName());
                stmt.setString(4, deletedContact.getLastName());
                stmt.setString(5, deletedContact.getNickname());
                stmt.setString(6, deletedContact.getCity());
                stmt.setString(7, deletedContact.getPhonePrimary());
                stmt.setString(8, deletedContact.getPhoneSecondary());
                stmt.setString(9, deletedContact.getEmail());
                stmt.setString(10, deletedContact.getLinkedinUrl());
                stmt.setString(11, deletedContact.getBirthDateString());

                return stmt.executeUpdate() > 0;

            } catch (SQLException e) {
                System.err.println(RED + "Database error: " + e.getMessage() + RESET);
                return false;
            }
        }

        @Override
        String getDescription() {
            return "DELETE Contact (ID: " + deletedContact.getId() + ", Name: " + 
                   deletedContact.getFirstName() + " " + deletedContact.getLastName() + ")";
        }
    }

    /**
     * Error handler.
     */
    private void handleUnexpectedError(Exception e) {
        System.err.println();
        System.err.println(RED + "╔════════════════════════════════════════════════╗" + RESET);
        System.err.println(RED + "║  UNEXPECTED ERROR                              ║" + RESET);
        System.err.println(RED + "╚════════════════════════════════════════════════╝" + RESET);
        System.err.println();
        System.err.println(RED + "Error: " + e.getMessage() + RESET);
        pause();
    }

@Override
    public String toString() {
        return "SeniorDeveloper{" +
                "id=" + userId +
                ", username='" + username + '\'' +
                ", name='" + name + " " + surname + '\'' +
                ", undoStackSize=" + undoStack.size() +
                '}';
    }
}

