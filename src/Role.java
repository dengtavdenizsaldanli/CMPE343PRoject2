import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class Role {

    // ==========================================
    // PAGINATION FIELDS
    // ==========================================
    
    private List<Contact> visibleList = new ArrayList<>();
    
    private String currentListTitle = "";
    
    private boolean hasEverShownList = false;
    
    private int currentPage = 0;
    
    private final int PAGE_SIZE = 10;

    // ==========================================
    // ANSI COLOR CODES
    // ==========================================
    
    /** ANSI reset code. */
    protected static final String RESET = "\u001b[0m";
    
    /** ANSI red color. */
    protected static final String RED = "\u001b[31m";
    
    /** ANSI blue color. */
    protected static final String BLUE = "\u001b[34m";
    
    /** ANSI green color. */
    protected static final String GREEN = "\u001b[32m";
    
    /** ANSI cyan color. */
    protected static final String CYAN = "\u001b[36m";
    
    /** ANSI purple color. */
    protected static final String PURPLE = "\u001b[35m";

    protected static final String YELLOW = "\u001b[33m";

    // ==========================================
    // USER IDENTITY FIELDS
    // ==========================================
    
    /** User's database ID. */
    protected int userId;
    
    /** User's username. */
    protected String username;
    
    /** User's first name. */
    protected String name;
    
    /** User's surname. */
    protected String surname;
    
    /** User's role name. */
    protected String role;
    
    /** Scanner for user input. */
    protected Scanner sc;

    // ==========================================
    // MULTI-FIELD SEARCH CONFIGURATION
    // ==========================================
    
    protected static final String[][] MULTI_FIELDS = new String[][]{
        {"first_name", "First Name"},
        {"middle_name", "Middle Name"},
        {"last_name", "Last Name"},
        {"nickname", "Nickname"},
        {"city", "City"},
        {"phone_primary", "Primary Phone"},
        {"phone_secondary", "Secondary Phone"},
        {"email", "Email"},
        {"linkedin_url", "LinkedIn"},
        {"birth_date", "Birth Date"}
    };

    // ==========================================
    // SEARCH ERROR TRACKING
    // ==========================================
    
    /** Flag indicating if last search had no results. */
    protected boolean lastSearchHadNoResults;
    
    /** Error message from last search. */
    protected String lastSearchErrorMessage;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    
    public Role(int userId, String username, String name, String surname, String role) {
        this.sc = Group22.getScanner(); // Use shared scanner
        this.lastSearchHadNoResults = false;
        this.lastSearchErrorMessage = null;
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.role = role;
    }

    // ==========================================
    // GETTERS
    // ==========================================
    
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return name + " " + surname;
    }
    // ==========================================
    // USER HEADER DISPLAY
    // ==========================================
    
public void showUserHeader() {
    System.out.println(CYAN + "╔════════════════════════════════════════════════╗" + RESET);
    
    // Welcome line
    String welcomeLine = "  Welcome! " + name + " " + surname;
    int welcomePadding = 48 - welcomeLine.length();
    System.out.print(CYAN + "║" + welcomeLine);
    for (int i = 0; i < welcomePadding; i++) {
        System.out.print(" ");
    }
    System.out.println("║" + RESET);
    
    // Role line
    String roleLine = "  Role: " + role;
    int rolePadding = 48 - roleLine.length();
    System.out.print(CYAN + "║" + roleLine);
    for (int i = 0; i < rolePadding; i++) {
        System.out.print(" ");
    }
    System.out.println("║" + RESET);
    
    System.out.println(CYAN + "╚════════════════════════════════════════════════╝" + RESET);
    System.out.println();
}

    // ==========================================
    // PASSWORD CHANGE FUNCTIONALITY
    // ==========================================
    
    public void changePassword() {
        int failedAttempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (failedAttempts < MAX_ATTEMPTS) {
            System.out.print(CYAN + "Enter Current Password (or type 'cancel' to abort): " + RESET);
            String currentPassword = sc.nextLine().trim();

            // Check for cancel
            if (currentPassword.equalsIgnoreCase("cancel")) {
                System.out.println(YELLOW + "Password change cancelled." + RESET);
                return;
            }

            // Check for empty
            if (currentPassword.isEmpty()) {
                System.out.println(RED + "Password cannot be empty." + RESET);
                pause();
                Group22.clearConsole();
                continue;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                
                // Verify current password
                String checkSql = "SELECT password_hash FROM users WHERE user_id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setInt(1, userId);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    System.out.println(RED + "User not found." + RESET);
                    pause();
                    return;
                }

                String storedHash = rs.getString("password_hash");

                // Hash entered password
                String enteredHash = LoginMenu.hashPassword(currentPassword);

                // Compare hashes (case-insensitive)
                if (!storedHash.equalsIgnoreCase(enteredHash)) {
                    failedAttempts++;
                    System.out.println(RED + "Wrong current password. Attempt " + 
                                     failedAttempts + "/" + MAX_ATTEMPTS + "." + RESET);
                    
                    if (failedAttempts == MAX_ATTEMPTS) {
                        System.out.println(RED + "Too many failed attempts. You have been logged out." + RESET);
                        pause();
                        Group22.clearConsole();
                        return; // Exit to main menu
                    }
                    
                    pause();
                    Group22.clearConsole();
                    continue;
                }

                // Current password correct - get new password
                boolean passwordChanged = false;

                while (!passwordChanged) {
                    Group22.clearConsole();
                    
                    // Show requirements based on role
                    displayPasswordRequirements();

                    System.out.println();
                    System.out.print(CYAN + "Enter New Password (or type 'cancel' to abort): " + RESET);
                    String newPassword = sc.nextLine().trim();
                    
                    // Check for cancel
                    if (newPassword.equalsIgnoreCase("cancel")) {
                        System.out.println(YELLOW + "Password change cancelled." + RESET);
                        return;
                    }

                    // Check for empty
                    if (newPassword.isEmpty()) {
                        System.out.println(RED + "Password cannot be empty." + RESET);
                        pause();
                        continue;
                    }

                    System.out.println();
                    System.out.print(CYAN + "Confirm New Password (or type 'cancel' to abort): " + RESET);
                    String confirmPassword = sc.nextLine().trim();

                    // Check for cancel
                    if (confirmPassword.equalsIgnoreCase("cancel")) {
                        System.out.println(YELLOW + "Password change cancelled." + RESET);
                        return;
                    }

                    // Check if passwords match
                    if (!newPassword.equals(confirmPassword)) {
                        System.out.println();
                        System.out.println(RED + "❌ Password confirmation does not match." + RESET);
                        System.out.println(CYAN + "Please try again..." + RESET);
                        pause();
                        continue;
                    }

                    // Validate new password
                    String validationError = validatePassword(newPassword);
                    if (validationError != null) {
                        System.out.println();
                        System.out.println(RED + "❌ " + validationError + RESET);
                        pause();
                        continue;
                    }

                    // Update password in database
                    String updateSql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    updateStmt.setString(1, LoginMenu.hashPassword(newPassword));
                    updateStmt.setInt(2, userId);
                    updateStmt.executeUpdate();

                    System.out.println();
                    System.out.println(GREEN + "✅ Password updated successfully." + RESET);
                    pause();
                    passwordChanged = true;
                }

                return; // Exit method after successful change

            } catch (Exception e) {
                System.out.println();
                System.out.println(RED + "Password update failed: " + e.getMessage() + RESET);
                pause();
                return;
            }
        }
    }

    private void displayPasswordRequirements() {
        if (role.equals("Manager")) {
            System.out.println(CYAN + "Password Requirements (Manager):" + RESET);
            System.out.println("• 2 to 16 characters");
            System.out.println("• Must contain at least ONE letter");
            System.out.println("• Numbers are OPTIONAL");
            System.out.println("• Symbols are OPTIONAL");
            System.out.println("• Allowed symbols: ! @ # $ % & * - _ = + , . < > ? ~");
            System.out.println("• NO spaces allowed");
            System.out.println("• CAN match your username");
        } else {
            System.out.println(CYAN + "Password Requirements:" + RESET);
            System.out.println("• 8 to 16 characters");
            System.out.println("• Must contain at least ONE letter");
            System.out.println("• Must contain at least ONE number");
            System.out.println("• Must contain at least ONE symbol: ! @ # $ % & * - _ = + , . < > ? ~");
            System.out.println("• NO spaces allowed");
            System.out.println("• CANNOT match your username");
        }
    }

    private String validatePassword(String password) {
        if (password.contains(" ")) {
            return "Password cannot contain spaces.";
        }

        if (role.equals("Manager")) {
            return validateManagerPassword(password);
        } else {
            return validateStandardPassword(password);
        }
    }

    private String validateManagerPassword(String password) {
        if (password.length() < 2 || password.length() > 16) {
            return "Password must be between 2 and 16 characters.";
        }

        if (!password.matches(".*[A-Za-zÇçĞğİıÖöŞşÜü].*")) {
            return "Password must contain at least one letter.";
        }

        // Check for invalid characters
        if (!password.matches("[A-Za-zÇçĞğİıÖöŞşÜü0-9!@#$%&*\\-_=+,.<>?~]+")) {
            return "Password contains invalid characters. Only letters, numbers, and these symbols: ! @ # $ % & * - _ = + , . < > ? ~";
        }

        return null; // Valid
    }
    
    private String validateStandardPassword(String password) {
        if (password.length() < 8 || password.length() > 16) {
            return "Password must be between 8 and 16 characters.";
        }

        if (!password.matches(".*[A-Za-zÇçĞğİıÖöŞşÜü].*")) {
            return "Password must contain at least one letter.";
        }

        if (!password.matches(".*[0-9].*")) {
            return "Password must contain at least one number.";
        }

        if (!password.matches(".*[!@#$%&*\\-_=+,.<>?~].*")) {
            return "Password must contain at least one symbol: ! @ # $ % & * - _ = + , . < > ? ~";
        }

        if (password.equalsIgnoreCase(username)) {
            return "Password cannot match your username.";
        }

        return null; // Valid
    }

    protected boolean isValidManagerPassword(String password) {
        return validateManagerPassword(password) == null;
    }
    // ==========================================
    // CONTACTS MENU (Main Contact Operations)
    // ==========================================
    
    protected void contactsMenu() {
        boolean running = true;

        while (running) {
            clearScreen();

            // Display current list if available
            if (hasEverShownList && !visibleList.isEmpty()) {
                printVisibleListPaginated();
                System.out.println();
            } else {
                // Show helpful message if no list displayed yet
                if (lastSearchHadNoResults) {
                    System.out.println(RED + lastSearchErrorMessage + RESET);
                    lastSearchHadNoResults = false;
                    lastSearchErrorMessage = null;
                }

                System.out.println(CYAN + "No contact list is currently displayed." + RESET);
                System.out.println(CYAN + "Use:" + GREEN + " [1] List All Contacts " + 
                                 CYAN + "or" + GREEN + " [2] Search For Contacts\n" + RESET);
            }

            // Display menu options
            System.out.println(GREEN + "[1] - List All Contacts" + RESET);
            System.out.println(GREEN + "[2] - Search For Contacts" + RESET);
            System.out.println(GREEN + "[3] - Sort Contacts List" + RESET);
            System.out.println("\n" + RED + "[4] - Back to Main Menu" + RESET);
            System.out.print("\n" + CYAN + "Pick (1-4) | n = Next | p = Previous | j = Jump: " + RESET);

            String choice = sc.nextLine().trim().toLowerCase();

            // Handle navigation commands
            if (choice.equals("n")) {
                handleNextPage();
                continue;
            }

            if (choice.equals("p")) {
                handlePreviousPage();
                continue;
            }

            if (choice.equals("j")) {
                handleJumpToPage();
                continue;
            }

            // Handle menu options
            if (!choice.matches("[1-4]")) {
                System.out.println(RED + "Invalid choice." + RESET);
                pause();
                continue;
            }

            switch (choice) {
                case "1" -> handleListAllContacts();
                case "2" -> handleSearchContacts();
                case "3" -> handleSortContacts();
                case "4" -> running = false;
            }
        }
    }

    // ==========================================
    // CONTACTS MENU - OPTION 1: LIST ALL
    // ==========================================
    
    private void handleListAllContacts() {
    if (!hasEverShownList || visibleList.isEmpty() || 
        confirmYesNo("replace the CURRENT visible list with ALL contacts")) {
        
        clearScreen();
        System.out.println(CYAN + "Loading all contacts..." + RESET);
        
        visibleList = fetchAllContacts();
        currentListTitle = CYAN + "List of All Company Contacts (Unsorted)" + RESET;
        currentPage = 0;
        hasEverShownList = true;
        
        // PAUSE to see if list loaded
        System.out.println("Contacts loaded: " + visibleList.size());
        try {
            Thread.sleep(2000); // 2 saniye bekle
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

    // ==========================================
    // CONTACTS MENU - OPTION 2: SEARCH
    // ==========================================
    
    private void handleSearchContacts() {
        boolean searchMenuRunning = true;
        
        while (searchMenuRunning) {
            clearScreen();
            
            if (!visibleList.isEmpty()) {
                printVisibleListPaginated();
                System.out.println();
            }

            System.out.println(CYAN + "╔════════════════════════════════════════════════╗" + RESET);
            System.out.println(CYAN + "║            SEARCH CONTACTS MENU                ║" + RESET);
            System.out.println(CYAN + "╚════════════════════════════════════════════════╝" + RESET);
            System.out.println();
            System.out.println(GREEN + "[1] - Single Field Search" + RESET);
            System.out.println(GREEN + "[2] - Multiple Field Search" + RESET);
            System.out.println(RED + "[3] - Back to Contacts Menu" + RESET);
            System.out.print("\n" + CYAN + "Pick (1-3): " + RESET);

            String searchChoice = sc.nextLine().trim();

            switch (searchChoice) {
                case "1" -> {
                    boolean singleSearchResult = handleSingleFieldSearch();
                    // If search was completed successfully, exit to contacts menu
                    if (singleSearchResult) {
                        searchMenuRunning = false;
                    }
                    // If search was cancelled (returns false), stay in search menu
                }
                
                case "2" -> {
                    boolean multiSearchResult = handleMultiFieldSearch();
                    // If search was completed successfully, exit to contacts menu
                    if (multiSearchResult) {
                        searchMenuRunning = false;
                    }
                    // If search was cancelled (returns false), stay in search menu
                }
                
                case "3" -> {
                    searchMenuRunning = false; // Back to contacts menu
                }
                
                default -> {
                    System.out.println(RED + "Invalid input. Enter 1, 2, or 3." + RESET);
                    pause();
                }
            }
        }
    }
    // ==========================================
    // CONTACTS MENU - OPTION 3: SORT
    // ==========================================
    
 private void handleSortContacts() {
        if (!hasEverShownList || visibleList.isEmpty()) {
            System.out.println(RED + "Cannot sort empty list." + RESET);
            pause();
            return;
        }

        if (confirmYesNo("sort the CURRENT visible list")) {
            
            // OUTER LOOP - Field selection with retry
            while (true) {
                clearScreen();
                printVisibleListPaginated();
                System.out.println();
                
                System.out.println(CYAN + "Sorting list..." + RESET);
                
                int field = askSortField();
                
                // Check for cancel - return to contacts menu
                if (field == 0) {
                    System.out.println(YELLOW + "Sort cancelled." + RESET);
                    pause();
                    return;  // Exit to contacts menu
                }
                
                String fieldName = (field == 1) ? "First Name" : "Last Name";
                
                // INNER LOOP - Order selection with retry
                while (true) {
                    clearScreen();
                    printVisibleListPaginated();
                    System.out.println();
                    
                    System.out.println(CYAN + "Sorting by " + fieldName + "..." + RESET);
                    
                    int order = askSortOrder();
                    
                    // Check for cancel - return to FIELD selection
                    if (order == 0) {
                        System.out.println(YELLOW + "Sort order cancelled. Returning to field selection..." + RESET);
                        pause();
                        break;  // Break inner loop, go back to field selection
                    }
                    
                    String orderName = (order == 1) ? "ASC" : "DESC";
                    
                    // Perform sort
                    visibleList.sort((a, b) -> {
                        int cmp = (field == 1) 
                            ? a.getFirstName().compareToIgnoreCase(b.getFirstName())
                            : a.getLastName().compareToIgnoreCase(b.getLastName());
                        return (order == 1) ? cmp : -cmp;
                    });
                    
                    currentListTitle = CYAN + "List of Contacts sorted by " + fieldName + 
                                     " in " + orderName + " order" + RESET;
                    currentPage = 0;
                    
                    System.out.println();
                    System.out.println(GREEN + "✅ List sorted successfully!" + RESET);
                    pause();
                    
                    return;  // Exit completely after successful sort
                }
                
                // If we're here, user cancelled order selection
                // Loop continues to field selection
            }
        }
    }
    // ==========================================
    // PAGINATION NAVIGATION
    // ==========================================
    
    private void handleNextPage() {
        if (!hasEverShownList || visibleList.isEmpty()) {
            System.out.println(RED + "No list to paginate." + RESET);
            pause();
            return;
        }

        if ((currentPage + 1) * PAGE_SIZE >= visibleList.size()) {
            System.out.println(RED + "Already on last page." + RESET);
            pause();
        } else {
            currentPage++;
        }
    }

    private void handlePreviousPage() {
        if (!hasEverShownList || visibleList.isEmpty()) {
            System.out.println(RED + "No list to paginate." + RESET);
            pause();
            return;
        }

        if (currentPage == 0) {
            System.out.println(RED + "Already on first page." + RESET);
            pause();
        } else {
            currentPage--;
        }
    }

    private void handleJumpToPage() {
        if (!hasEverShownList || visibleList.isEmpty()) {
            System.out.println(RED + "No list to jump pages." + RESET);
            pause();
            return;
        }

        int totalPages = (int) Math.ceil((double) visibleList.size() / PAGE_SIZE);
        System.out.print(CYAN + "Enter page (1-" + totalPages + "): " + RESET);
        String input = sc.nextLine().trim();

        if (input.matches("\\d+")) {
            int page = Integer.parseInt(input);
            if (page >= 1 && page <= totalPages) {
                currentPage = page - 1;
            } else {
                System.out.println(RED + "Invalid page number." + RESET);
                pause();
            }
        } else {
            System.out.println(RED + "Enter a valid number." + RESET);
            pause();
        }
    }

    // ==========================================
    // PAGINATION DISPLAY
    // ==========================================
    
    protected void printVisibleListPaginated() {
        int totalContacts = visibleList.size();
        int totalPages = (int) Math.ceil((double) totalContacts / PAGE_SIZE);
        int startIdx = currentPage * PAGE_SIZE;
        int endIdx = Math.min(startIdx + PAGE_SIZE, totalContacts);

        System.out.println(currentListTitle);
        System.out.println("════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
        Contact.printHeader();

        for (int i = startIdx; i < endIdx; i++) {
            visibleList.get(i).print();
        }

        System.out.println();
        System.out.println("Showing " + (startIdx + 1) + "–" + endIdx + " of " + totalContacts + 
                         " | Page " + (currentPage + 1) + "/" + totalPages + 
                         "  " + progressBar(currentPage, totalPages));
    }

    protected String progressBar(int current, int total) {
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < total; i++) {
            bar.append(i <= current ? "■" : "□");
        }
        return bar.toString();
    }
    // ==========================================
    // SINGLE FIELD SEARCH
    // ==========================================
    
    private boolean handleSingleFieldSearch() {
        if (!hasEverShownList || visibleList.isEmpty() || 
            confirmYesNo("replace the CURRENT visible list with search results")) {
            
            clearScreen();
            
            if (!visibleList.isEmpty()) {
                printVisibleListPaginated();
                System.out.println();
            }

            System.out.println(CYAN + "Single Field Search" + RESET);
            System.out.println();

            // Field selection loop
            while (true) {
                System.out.println(GREEN + "[1] - First Name" + RESET);
                System.out.println(GREEN + "[2] - Middle Name" + RESET);
                System.out.println(GREEN + "[3] - Last Name" + RESET);
                System.out.println(GREEN + "[4] - Nickname" + RESET);
                System.out.println(GREEN + "[5] - City" + RESET);
                System.out.println(GREEN + "[6] - Phone" + RESET);
                System.out.println(GREEN + "[7] - Birth Year (YYYY)" + RESET);
                System.out.println(GREEN + "[8] - Birth Month (1-12)" + RESET);
                System.out.println(GREEN + "[9] - LinkedIn (y/n)" + RESET);
                System.out.println(RED + "[0] - Back to Search Menu" + RESET);  // ← DEĞİŞTİ
                System.out.print("\n" + CYAN + "Pick (0-9): " + RESET);

                String fieldChoice = sc.nextLine().trim();

                // Cancel check
                if (fieldChoice.equals("0")) {
                    System.out.println(YELLOW + "Search cancelled. Returning to search menu..." + RESET);
                    pause();
                    return false;  // ← FALSE DÖNDÜR (cancelled)
                }

                if (!fieldChoice.matches("[1-9]")) {
                    System.out.println(RED + "Invalid input. Enter a number from 0 to 9." + RESET);
                    continue;
                }

                int fieldNum = Integer.parseInt(fieldChoice);

                // Get search value with validation
                String searchValue = getSearchValueForField(fieldNum);
                if (searchValue == null) {
                    // User cancelled during value entry, return to search menu
                    return false;  // ← FALSE DÖNDÜR (cancelled)
                }

                // Map field number to database column
                String columnName = mapFieldNumberToColumn(fieldNum);
                String displayName = getFieldDisplayName(fieldNum);

                // Process special cases for search value
                String processedValue = processSearchValue(fieldNum, searchValue);

                // Perform search
                visibleList = searchContacts(columnName, processedValue);
                currentListTitle = CYAN + "List of Search Results (" + displayName + ")" + RESET;
                currentPage = 0;
                hasEverShownList = true;
                
                return true;  // ← TRUE DÖNDÜR (completed)
            }
        }
        
        return false;  // User said no to confirmYesNo
    }

    private String getSearchValueForField(int fieldNum) {
        while (true) {
            Group22.clearConsole();
            
            if (!visibleList.isEmpty()) {
                printVisibleListPaginated();
                System.out.println();
            }

            String fieldName = getFieldDisplayName(fieldNum);
            String guide = getSearchGuide(fieldNum);

            System.out.println(CYAN + "Single Field Search - " + fieldName + RESET);
            if (!guide.isEmpty()) {
                System.out.println(BLUE + "Guide: " + guide + RESET);
            }

            System.out.print(CYAN + "Enter value (null0=empty, 0=cancel): " + RESET);
            String value = sc.nextLine().trim();

// Check for cancel
            if (value.equals("0")) {
                System.out.println(YELLOW + "Search cancelled." + RESET);
                pause();
                return null;
            }
            // Check if empty
            if (isBlank(value)) {
                Group22.clearConsole();
                if (!visibleList.isEmpty()) {
                    printVisibleListPaginated();
                    System.out.println();
                }
                System.out.println(RED + "Input cannot be empty." + RESET);
                pause();
                continue;
            }

            // Allow "null0" for searching empty fields
            if (value.equalsIgnoreCase("null0")) {
                return value;
            }

            // Validate input
            String errorMessage = validateSearchInput(fieldNum, value);
            if (errorMessage != null) {
                System.out.println(RED + "Invalid input format for this field." + RESET);
                System.out.println(RED + errorMessage + RESET);
                pause();
                continue;
            }

            return value;
        }
    }

    private String getSearchGuide(int fieldNum) {
        return switch (fieldNum) {
            case 1, 2, 3, 4 -> "Letters, spaces and apostrophes only. Example: Mikael, O'Neil";
            case 5 -> "Letters, spaces, apostrophe ('), dot (.) and dash (-) only. Example: San Francisco";
            case 6 -> "Numbers and optional + only. Example: +905321112233 or +";
            case 7 -> "4-digit year. Example: 1987. Use null0 to search contacts with empty birth date.";
            case 8 -> "Number 1–12 (or 01–12). Example: 8 or 08 for August. Use null0 for empty birth date.";
            case 9 -> "Does the contact have a LinkedIn? y = has LinkedIn, n = no LinkedIn, null0 = empty column.";
            default -> "";
        };
    }

    private String getFieldDisplayName(int fieldNum) {
        return switch (fieldNum) {
            case 1 -> "First Name";
            case 2 -> "Middle Name";
            case 3 -> "Last Name";
            case 4 -> "Nickname";
            case 5 -> "City";
            case 6 -> "Phone";
            case 7 -> "Birth Year";
            case 8 -> "Birth Month";
            case 9 -> "LinkedIn";
            default -> "Field";
        };
    }

    private String validateSearchInput(int fieldNum, String value) {
        boolean valid;
        
        switch (fieldNum) {
            case 1, 2, 3, 4 -> { // Names
                valid = isValidPersonName(value);
                if (!valid) {
                    return "Names may contain ONLY:\n" +
                           "- Letters (A–Z, a–z, Turkish: Ç, Ğ, İ, Ö, Ş, Ü)\n" +
                           "- Spaces\n" +
                           "- Apostrophes (')\n" +
                           "Example: O'Neil, Anne Marie";
                }
            }
            case 5 -> { // City
                valid = isValidCity(value);
                if (!valid) {
                    return "City may contain ONLY:\n" +
                           "- Letters (including Turkish)\n" +
                           "- Spaces\n" +
                           "- Apostrophe (')\n" +
                           "- Dot (.)\n" +
                           "- Dash (-)\n" +
                           "Example: San Francisco, St. Louis";
                }
            }
            case 6 -> { // Phone
                valid = value.equals("+") || isValidPhone(value);
                if (!valid) {
                    return "Phone may contain ONLY:\n" +
                           "- Numbers 0–9\n" +
                           "- Optional leading +\n" +
                           "Examples:\n" +
                           "+905321112233\n" +
                           "02124445566\n" +
                           "+ (to search all international numbers)";
                }
            }
            case 7 -> { // Birth year
                valid = value.matches("\\d{4}");
                if (!valid) {
                    return "Birth year must be a 4-digit number.\n" +
                           "Examples: 1980, 2001";
                }
            }
            case 8 -> { // Birth month
                valid = value.matches("\\d{1,2}");
                if (valid) {
                    int month = Integer.parseInt(value);
                    valid = month >= 1 && month <= 12;
                }
                if (!valid) {
                    return "Birth month must be between 1 and 12.\n" +
                           "Examples: 1, 8, 12 or 01, 08, 12";
                }
            }
            case 9 -> { // LinkedIn
                valid = value.equalsIgnoreCase("y") || value.equalsIgnoreCase("n");
                if (!valid) {
                    return "For LinkedIn, please enter:\n" +
                           "- y  → has LinkedIn\n" +
                           "- n  → does NOT have LinkedIn\n" +
                           "- null0 → to search empty LinkedIn field";
                }
            }
            default -> valid = true;
        }

        return null; // Valid
    }

    private String mapFieldNumberToColumn(int fieldNum) {
        return switch (fieldNum) {
            case 1 -> "first_name";
            case 2 -> "middle_name";
            case 3 -> "last_name";
            case 4 -> "nickname";
            case 5 -> "city";
            case 6 -> "phone_primary";
            case 7, 8 -> "birth_date";
            case 9 -> "linkedin_url";
            default -> "first_name";
        };
    }

    private String processSearchValue(int fieldNum, String value) {
        if (value.equalsIgnoreCase("null0")) {
            return value;
        }

        // Birth month: convert to -MM- format for SQL LIKE
        if (fieldNum == 8) {
            int month = Integer.parseInt(value);
            String monthStr = String.format("%02d", month);
            return "-" + monthStr + "-";
        }

        // LinkedIn: convert y/n to yes/no
        if (fieldNum == 9) {
            if (value.equalsIgnoreCase("y")) {
                return "yes";
            } else if (value.equalsIgnoreCase("n")) {
                return "no";
            }
        }

        return value;
    }

    protected List<Contact> searchContacts(String column, String value) {
        List<Contact> results = new ArrayList<>();

        if (value == null || value.trim().isEmpty()) {
            System.out.println(RED + "Search value cannot be empty." + RESET);
            pause();
            return results;
        }

        value = value.trim();

        // Build SQL query
        String sql;
        if (value.equalsIgnoreCase("null0")) {
            sql = "SELECT * FROM contacts WHERE " + column + " IS NULL OR " + column + " = ''";
        } else if (column.equals("linkedin_url") && value.equalsIgnoreCase("yes")) {
            sql = "SELECT * FROM contacts WHERE linkedin_url IS NOT NULL AND linkedin_url != ''";
        } else if (column.equals("linkedin_url") && value.equalsIgnoreCase("no")) {
            sql = "SELECT * FROM contacts WHERE linkedin_url IS NULL OR linkedin_url = ''";
        } else {
            sql = "SELECT * FROM contacts WHERE " + column + " LIKE ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set parameter if using LIKE
            if (!value.equalsIgnoreCase("null0") && 
                !(column.equals("linkedin_url") && 
                  (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("no")))) {
                String pattern = "%" + value.trim() + "%";
                stmt.setString(1, pattern);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(new Contact(
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
                ));
            }

        } catch (Exception e) {
            System.out.println(RED + "Search failed: " + e.getMessage() + RESET);
        }

        return results;
    }
    // ==========================================
    // MULTI-FIELD SEARCH
    // ==========================================
    
    private boolean handleMultiFieldSearch() {
        clearScreen();
        
        if (!visibleList.isEmpty()) {
            printVisibleListPaginated();
            System.out.println();
        }

        System.out.println(CYAN + "Multi-Field Search" + RESET);
        System.out.println(CYAN + "Select how many fields (2 to 4, or 0 to cancel)" + RESET);
        System.out.print(CYAN + "Enter number of fields: " + RESET);

        String input = sc.nextLine().trim();

        // Check for cancel
        if (input.equals("0")) {
            System.out.println(YELLOW + "Search cancelled. Returning to search menu..." + RESET);
            pause();
            return false;  // ← FALSE DÖNDÜR (cancelled)
        }

        int numFields;
        try {
            numFields = Integer.parseInt(input);
            if (numFields < 2 || numFields > 4) {
                throw new Exception();
            }
        } catch (Exception e) {
            System.out.println(RED + "You must select between 2 and 4 fields." + RESET);
            pause();
            return false;  // ← FALSE DÖNDÜR (invalid)
        }

        String[] selectedFields = new String[numFields];
        String[] searchValues = new String[numFields];

        // Get field selections and values
        for (int i = 0; i < numFields; i++) {
            // Field selection
            String field = selectMultiFieldOption(i + 1, selectedFields, i);
            if (field == null) {
                return false; // ← FALSE DÖNDÜR (cancelled)
            }
            selectedFields[i] = field;

            // Value input
            String value = getMultiFieldValue(field);
            if (value == null) {
                return false; // ← FALSE DÖNDÜR (cancelled)
            }
            searchValues[i] = value;
        }

        // Perform multi-field search
        visibleList = multiFieldSearch(selectedFields, searchValues);
        currentListTitle = CYAN + "Multi-Field Search Results" + RESET;
        currentPage = 0;
        hasEverShownList = true;
        
        return true;  // ← TRUE DÖNDÜR (completed)
    }

    private String selectMultiFieldOption(int fieldNumber, String[] alreadySelected, int currentIndex) {
        while (true) {
            clearScreen();
            
            if (!visibleList.isEmpty()) {
                printVisibleListPaginated();
                System.out.println();
            }

            System.out.println(CYAN + "Choose field #" + fieldNumber + RESET);

// Display all available fields
for (int i = 0; i < MULTI_FIELDS.length; i++) {
    System.out.println(GREEN + "[" + (i + 1) + "] - " + MULTI_FIELDS[i][1] + RESET);
}
System.out.println(RED + "[0] - Cancel" + RESET);  // ← YENİ!

System.out.print(CYAN + "Pick field (0 to cancel): " + RESET);  // ← Güncellendi
            int choice;
try {
    choice = Integer.parseInt(sc.nextLine().trim());
    
    // Cancel check
    if (choice == 0) {
        System.out.println(YELLOW + "Search cancelled." + RESET);
        pause();
        return null;
    }
    
    choice--; // Convert to 0-based index
    if (choice < 0 || choice >= MULTI_FIELDS.length) {
        throw new Exception();
    }
} catch (Exception e) {
    System.out.println(RED + "Invalid field selection." + RESET);
    pause();
    continue;
}

            String selectedField = MULTI_FIELDS[choice][0];

            // Check if already selected
            boolean duplicate = false;
            for (int j = 0; j < currentIndex; j++) {
                if (alreadySelected[j].equals(selectedField)) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate) {
                System.out.println(RED + "Field already selected." + RESET);
                pause();
                continue;
            }

            return selectedField;
        }
    }

    private String getMultiFieldValue(String fieldName) {
        while (true) {
            Group22.clearConsole();
            
            if (!visibleList.isEmpty()) {
                printVisibleListPaginated();
                System.out.println();
            }

            String displayName = getDisplayNameForColumn(fieldName);
            System.out.println(CYAN + "Enter value for: " + displayName + RESET);
            
            // Show field-specific guide
            displayMultiFieldGuide(fieldName);

            System.out.print(CYAN + "Enter value (0=cancel, null0=empty): " + RESET);  // ← DEĞİŞTİ
            String value = sc.nextLine().trim();

            // Check for cancel - YENİ BLOK BAŞLANGIÇ
            if (value.equals("0") || value.equalsIgnoreCase("cancel")) {
                System.out.println(YELLOW + "Search cancelled." + RESET);
                pause();
                return null;
            }
            // YENİ BLOK BİTİŞ

            if (isBlank(value)) {
                Group22.clearConsole();
                if (!visibleList.isEmpty()) {
                    printVisibleListPaginated();
                    System.out.println();
                }
                System.out.println(RED + "Input cannot be empty." + RESET);
                pause();
                continue;
            }

            if (value.equalsIgnoreCase("null0")) {
                return value;
            }

            // Validate input
            boolean valid = validateMultiFieldInput(fieldName, value);
            if (valid) {
                return value;
            } else {
                System.out.println(RED + "Invalid input format for this field." + RESET);
                pause();
            }
        }
    }

    private void displayMultiFieldGuide(String fieldName) {
        String guide = switch (fieldName) {
            case "first_name", "middle_name", "last_name", "nickname" ->
                "Guide: Letters and apostrophe only. Example: O'Neil";
            case "city" ->
                "Guide: Letters, apostrophe ('), dot (.), dash (-) only";
            case "phone_primary", "phone_secondary" ->
                "Guide: Numbers and optional + only. Example: +905321112233";
            case "birth_date" ->
                "Guide: Numbers and dashes only. Example: 1999-05-12";
            case "email" ->
                "Guide: Example: barney.stinson@gmail.com";
            case "linkedin_url" ->
                "Does the contact have a LinkedIn? (y/n)";
            default -> "";
        };

        if (!guide.isEmpty()) {
            System.out.println(BLUE + guide + RESET);
        }
    }

    private boolean validateMultiFieldInput(String fieldName, String value) {
        return switch (fieldName) {
            case "first_name", "middle_name", "last_name", "nickname" ->
                isValidPersonName(value);
            case "city" ->
                isValidCity(value);
            case "phone_primary", "phone_secondary" ->
                value.equals("+") || isValidPhone(value);
            case "birth_date" ->
                isValidBirthDate(value);
            case "email" -> {
                if (!isValidEmail(value)) {
                    System.out.println(RED + "Invalid email format.\n" +
                        "Guide: Example: barney.stinson@gmail.com\n" +
                        "Rules:\n" +
                        "- Must contain exactly one @\n" +
                        "- Cannot start or end with @\n" +
                        "- No spaces allowed\n" +
                        "- Use null0 to search for empty emails" + RESET);
                    yield false;
                }
                yield true;
            }
            case "linkedin_url" ->
                value.equalsIgnoreCase("y") || value.equalsIgnoreCase("n");
            default -> true;
        };
    }

    private String getDisplayNameForColumn(String column) {
        for (String[] field : MULTI_FIELDS) {
            if (field[0].equals(column)) {
                return field[1];
            }
        }
        return column;
    }

    protected List<Contact> multiFieldSearch(String[] fields, String[] values) {
        List<Contact> results = new ArrayList<>();
        
        // Build SQL query
        StringBuilder sql = new StringBuilder("SELECT * FROM contacts WHERE ");

        for (int i = 0; i < fields.length; i++) {
            if (values[i].equalsIgnoreCase("null0")) {
                sql.append(fields[i]).append(" IS NULL OR ").append(fields[i]).append(" = ''");
            } else if (values[i].equals("HAS_LINKEDIN")) {
                sql.append(fields[i]).append(" IS NOT NULL AND ").append(fields[i]).append(" != ''");
            } else if (values[i].equals("NO_LINKEDIN")) {
                sql.append(fields[i]).append(" IS NULL OR ").append(fields[i]).append(" = ''");
            } else {
                sql.append(fields[i]).append(" LIKE ?");
            }

            if (i < fields.length - 1) {
                sql.append(" AND ");
            }
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // Set parameters
            int paramIndex = 1;
            for (int i = 0; i < values.length; i++) {
                // Process special values
                String processedValue = values[i];
                
                if (processedValue.equalsIgnoreCase("y")) {
                    processedValue = "HAS_LINKEDIN";
                } else if (processedValue.equalsIgnoreCase("n")) {
                    processedValue = "NO_LINKEDIN";
                }

                // Only set parameter if using LIKE
                if (!processedValue.equalsIgnoreCase("null0") && 
                    !processedValue.equals("HAS_LINKEDIN") && 
                    !processedValue.equals("NO_LINKEDIN")) {
                    
                    String pattern = "%" + values[i].trim() + "%";
                    stmt.setString(paramIndex++, pattern);
                }
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(new Contact(
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
                ));
            }

        } catch (Exception e) {
            System.out.println(RED + "Multi-field search failed: " + e.getMessage() + RESET);
        }

        if (results.isEmpty()) {
            lastSearchHadNoResults = true;
            lastSearchErrorMessage = "No Database entries matched your recent search's criteria";
        }

        return results;
    }

    // ==========================================
    // FETCH ALL CONTACTS
    // ==========================================
    
    protected List<Contact> fetchAllContacts() {
        List<Contact> contacts = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM contacts ORDER BY contact_id")) {

            while (rs.next()) {
                contacts.add(new Contact(
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
                ));
            }

        } catch (Exception e) {
            System.out.println(RED + "Error loading contacts: " + e.getMessage() + RESET);
        }

        return contacts;
    }
    // ==========================================
    // SORT METHODS
    // ==========================================
    
    protected int askSortField() {
        while (true) {
            System.out.println(GREEN + "[1] - First Name" + RESET);
            System.out.println(GREEN + "[2] - Last Name" + RESET);
            System.out.println(RED + "[0] - Cancel" + RESET);  // ← YENİ!
            System.out.print("\n" + CYAN + "Pick an Option (0-2): " + RESET);  // ← 0-2 oldu
            
            String choice = sc.nextLine().trim();
            
            if (choice.equals("0")) {  // ← YENİ!
                return 0;  // Cancel
            }
            
            if (choice.matches("[1-2]")) {
                return Integer.parseInt(choice);
            }
            
            System.out.println(RED + "Invalid input. Enter (0-2)." + RESET);
        }
    }

    protected int askSortOrder() {
        while (true) {
            System.out.println(GREEN + "1 - ASC | 2 - DESC" + RESET);
            System.out.println(RED + "0 - Cancel" + RESET);  // ← YENİ!
            System.out.print("\n" + CYAN + "Pick an Option (0-2): " + RESET);  // ← 0-2 oldu
            
            String choice = sc.nextLine().trim();
            
            if (choice.equals("0")) {  // ← YENİ!
                return 0;  // Cancel
            }
            
            if (choice.matches("[1-2]")) {
                return Integer.parseInt(choice);
            }
            
            System.out.println(RED + "Invalid input. Enter (0-2)." + RESET);
        }
    }

    // ==========================================
    // VALIDATION HELPER METHODS
    // ==========================================
    
    protected boolean isValidPersonName(String name) {
        return name != null && name.matches("[A-Za-zÇçĞğİıÖöŞşÜü' ]+");
    }

    protected boolean isValidCity(String city) {
        return city != null && city.matches("[A-Za-zÇçĞğİıÖöŞşÜü'.\\- ]+");
    }

    protected boolean isValidPhone(String phone) {
        return phone != null && phone.matches("[+0-9]+");
    }

    protected boolean isValidBirthDate(String dateStr) {
        return dateStr != null && dateStr.matches("[0-9\\-]+");
    }

    protected boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        if (email.equalsIgnoreCase("null0")) {
            return true;
        }
        return !email.trim().isEmpty();
    }

    protected boolean isValidName(String name) {
        return name != null && name.matches("[A-Za-zÇçĞğİıÖöŞşÜü']+");
    }
    
    protected boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    // ==========================================
    // UTILITY METHODS
    // ==========================================
    
    protected void clearScreen() {
        try {
            String os = System.getProperty("os.name");
            if (os != null && os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls")
                    .inheritIO()
                    .start()
                    .waitFor();
            } else {
                System.out.print("\u001b[H\u001b[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Console clearing failed - ignore
        }
    }

    protected boolean confirmYesNo(String action) {
        while (true) {
            System.out.print(CYAN + "This will " + action + ". Continue (" +
                           GREEN + "y" + CYAN + "/" + RED + "n" + CYAN + "): " + RESET);
            String response = sc.nextLine().trim().toLowerCase();
            
            if (response.equals("y")) {
                return true;
            }
            if (response.equals("n")) {
                return false;
            }
            
            System.out.println(RED + "Invalid input. Enter y or n." + RESET);
        }
    }

    protected void pause() {
        System.out.println();
        System.out.println("Press ENTER to continue...");
        try {
            sc.nextLine();
        } catch (Exception e) {
            // Ignore input errors
        }
    }
    
    public void logout() {
        System.out.println(CYAN + "Logging out..." + RESET);
        try {
            Thread.sleep(500); // Brief pause
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ==========================================
    // ABSTRACT METHOD (must be implemented by subclasses)
    // ==========================================
    
    public abstract void showMenu();

    // ==========================================
    // toString() METHOD
    // ==========================================
    
    @Override
    public String toString() {
        return "Role{" +
                "type='" + role + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", name='" + name + " " + surname + '\'' +
                '}';
    }
}

