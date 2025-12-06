import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class Manager extends Role {

    // ==========================================
    // CONSTANTS FOR VALIDATION
    // ==========================================

    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_PASSWORD_LENGTH = 255;
    private static final int MIN_PASSWORD_LENGTH = 1;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    
    public Manager(int id, String u, String n, String s) {
        super(id, u, n, s, "Manager");
    }
    // ==========================================
    // MAIN MENU
    // ==========================================
    
    @Override
    public void showMenu() {
        boolean running = true;

        while (running) {
            try {
                clearScreen();
                showUserHeader();

                System.out.println(GREEN + "1- Change Password" + RESET);
                System.out.println(GREEN + "2- Contacts Statistical Info" + RESET);
                System.out.println(GREEN + "3- List All Users" + RESET);
                System.out.println(GREEN + "4- Update Existing User" + RESET);
                System.out.println(GREEN + "5- Add/Employ New User" + RESET);
                System.out.println(GREEN + "6- Delete/Fire Existing User" + RESET);
                System.out.println(GREEN + "7- List All Contacts" + RESET);
                System.out.println(GREEN + "8- Search Contacts (Single Field)" + RESET);
                System.out.println(GREEN + "9- Search Contacts (Multi-Field)" + RESET);
                System.out.println(RED + "10- Logout" + RESET);
                System.out.print("Choose (1-10): ");

                String choice = sc.nextLine();
                if (choice == null) {
                    choice = "";
                }
                choice = choice.trim();

                if (!choice.matches("10|[1-9]")) {
                    System.out.println(RED + "Invalid choice. Enter (1-10)." + RESET);
                    pause(sc);
                    continue;
                }

                switch (choice) {
                    case "1" -> {
                        clearScreen();
                        changePassword();
                        pause();
                    }

                    case "2" -> {
                        showContactsStatisticalInfo();
                        pause(sc);
                    }

                    case "3" -> {
                        listAllUsers();
                        pause(sc);
                    }

                    case "4" -> {
                        updateExistingUser();
                        pause(sc);
                    }

                    case "5" -> {
                        addNewUser();
                        pause(sc);
                    }

                    case "6" -> {
                        deleteExistingUser();
                        pause(sc);
                    }

                    case "7" -> contactsMenu();

                    case "8" -> handleSingleFieldSearchMenu();

                    case "9" -> handleMultiFieldSearchMenu();

                    case "10" -> {
                        System.out.print(
                            CYAN + "Are you sure you want to logout (" +
                            GREEN + "y" + CYAN + "/" + RED + "n" + CYAN + "): " + RESET
                        );
                        String confirm = sc.nextLine().trim();
                        if (confirm.equalsIgnoreCase("y")) {
                            logout();
                            running = false;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(RED + "Unexpected error in menu: " + e.getMessage() + RESET);
                pause(sc);
            }
        }
    }
    // ==========================================
    // SEARCH MENU HANDLERS (Options 8 & 9)
    // ==========================================
    
    private void handleSingleFieldSearchMenu() {
        boolean searchLoop = true;
        while (searchLoop) {
            clearScreen();
            System.out.println("Search Contacts (Single Field)");
            System.out.println("-------------------------");

            List<Contact> results = searchContactsWithResults();

            boolean resultLoop = true;
            while (resultLoop) {
                clearScreen();
                System.out.println("Search Results");
                System.out.println("--------------");

                if (results == null || results.isEmpty()) {
                    System.out.println("No results found.");
                } else {
                    Contact.printHeader();
                    for (Contact c : results) {
                        if (c != null) c.print();
                    }
                }

                System.out.println();
                System.out.println(PURPLE + "1- Search Again" + RESET);
                System.out.println(PURPLE + "2- Sort Search Results" + RESET);
                System.out.println(RED + "3- Back to Menu" + RESET);
                System.out.print("Choose (1-3): ");

                String searchChoice = safeReadLine(sc);
                if (!searchChoice.matches("[1-3]")) {
                    System.out.println(RED + "Invalid choice. Enter (1-3)." + RESET);
                    pause(sc);
                    continue;
                }

                switch (searchChoice) {
                    case "1" -> {
                        resultLoop = false;
                    }
                    case "2" -> {
                        if (results != null && !results.isEmpty()) {
                            sortSearchResults(results);
                        } else {
                            System.out.println(RED + "No results to sort." + RESET);
                        }
                        pause(sc);
                    }
                    case "3" -> {
                        resultLoop = false;
                        searchLoop = false;
                    }
                }
            }
        }
    }

    private void handleMultiFieldSearchMenu() {
        boolean multiSearchLoop = true;
        while (multiSearchLoop) {
            clearScreen();
            System.out.println("Search Contacts (Multi-Field)");
            System.out.println("-----------------------------");
            
            super.contactsMenu();
            
            System.out.println();
            System.out.println(PURPLE + "1- Search Again" + RESET);
            System.out.println(RED + "2- Back to Menu" + RESET);
            System.out.print("Choose (1-2): ");

            String multiChoice = safeReadLine(sc);
            if (!multiChoice.matches("[1-2]")) {
                System.out.println(RED + "Invalid choice. Enter (1-2)." + RESET);
                pause(sc);
                continue;
            }

            if (multiChoice.equals("2")) {
                multiSearchLoop = false;
            }
        }
    }

    private List<Contact> searchContactsWithResults() {
        System.out.println("1 - First Name");
        System.out.println("2 - Last Name");
        System.out.println("3 - Phone");
        System.out.print("Choose (1-3): ");

        String choice = sc.nextLine().trim();

        String column = switch (choice) {
            case "1" -> "first_name";
            case "2" -> "last_name";
            case "3" -> "phone_primary";
            default -> {
                System.out.println(RED + "Invalid choice." + RESET);
                yield null;
            }
        };

        if (column == null) {
            return new ArrayList<>();
        }

        System.out.print("Enter search value: ");
        String value = sc.nextLine().trim();

        List<Contact> results = searchContacts(column, value);

        if (results.isEmpty()) {
            System.out.println(RED + "No results found." + RESET);
        }

        return results;
    }

    private void sortSearchResults(List<Contact> results) {
        if (results == null || results.isEmpty()) {
            System.out.println(RED + "Nothing to sort." + RESET);
            return;
        }

        int field = askSortField();
        int order = askSortOrder();

        results.sort((a, b) -> {
            int cmp = (field == 1)
                ? a.getFirstName().compareToIgnoreCase(b.getFirstName())
                : a.getLastName().compareToIgnoreCase(b.getLastName());
            return (order == 1) ? cmp : -cmp;
        });

        System.out.println();
        System.out.println(GREEN + "Results sorted!" + RESET);
        System.out.println();
        
        Contact.printHeader();
        for (Contact c : results) {
            if (c != null) {
                c.print();
            }
        }
    }
    // ==========================================
    // HELPER METHODS
    // ==========================================
    
    private boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (username.length() > MAX_USERNAME_LENGTH) {
            return false;
        }
        return username.matches("[A-Za-z0-9_-]+");
    }

    private boolean isValidUserName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (name.length() > MAX_NAME_LENGTH) {
            return false;
        }
        return name.matches("[A-Za-zÇçĞğİıÖöŞşÜü' ]+");
    }

    private boolean isValidRole(String role) {
        if (role == null) {
            return false;
        }
        return role.equals("Tester") || 
               role.equals("Junior Developer") || 
               role.equals("Senior Developer") || 
               role.equals("Manager");
    }
    
    private String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        return input.replaceAll("[;'\"\\\\]", "").trim();
    }

    private String safeReadLine(Scanner scanner) {
        try {
            String line = scanner.nextLine();
            return line != null ? line.trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private void pause(Scanner scanner) {
        System.out.println();
        System.out.println("Press ENTER to continue...");
        try {
            scanner.nextLine();
        } catch (Exception e) {
        }
    }
    // ==========================================
    // OPTION 2: ADVANCED CONTACT STATISTICS (COMPLETELY NEW)
    // ==========================================
    
    private void showContactsStatisticalInfo() {
        clearScreen();
        System.out.println(CYAN + "╔════════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + "║        ADVANCED CONTACT ANALYTICS & STATISTICS                ║" + RESET);
        System.out.println(CYAN + "╚════════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println();

        try (Connection conn = DatabaseConnection.getConnection()) {
            showOverviewStatistics(conn);
            System.out.println();
            showGeographicDistribution(conn);
            System.out.println();
            showAgeDemographics(conn);
            System.out.println();
            showEmailDomainAnalysis(conn);
            System.out.println();
            showDataCompleteness(conn);
            System.out.println();
            showBirthMonthDistribution(conn);
            System.out.println();
            showLinkedInAdoptionByAge(conn);
            System.out.println();
            showGrowthTrends(conn);
            System.out.println();

        } catch (SQLException e) {
            System.out.println(RED + "Error fetching statistics: " + e.getMessage() + RESET);
            e.printStackTrace();
        }
    }

    private void showOverviewStatistics(Connection conn) throws SQLException {
        System.out.println(CYAN + "📊 OVERVIEW STATISTICS" + RESET);
        System.out.println("══════════════════════════════════════════════════════════════");

        try (Statement stmt = conn.createStatement()) {
            // Total contacts
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM contacts");
            if (rs.next()) {
                System.out.println("  • Total Contacts: " + GREEN + rs.getInt("total") + RESET);
            }
        }

        try (Statement stmt = conn.createStatement()) {
            // LinkedIn statistics
            ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) as with_linkedin FROM contacts " +
                "WHERE linkedin_url IS NOT NULL AND linkedin_url != ''"
            );
            if (rs.next()) {
                int withLinkedIn = rs.getInt("with_linkedin");
                
                try (Statement stmt2 = conn.createStatement()) {
                    ResultSet rs2 = stmt2.executeQuery("SELECT COUNT(*) as total FROM contacts");
                    rs2.next();
                    int total = rs2.getInt("total");
                    int percentage = (total > 0) ? (withLinkedIn * 100 / total) : 0;
                    System.out.println("  • With LinkedIn: " + GREEN + withLinkedIn + " (" + percentage + "%)" + RESET);
                }
            }
        }

        try (Statement stmt = conn.createStatement()) {
            // Average age
            ResultSet rs = stmt.executeQuery(
                "SELECT AVG(TIMESTAMPDIFF(YEAR, birth_date, CURDATE())) as avg_age " +
                "FROM contacts WHERE birth_date IS NOT NULL"
            );
            if (rs.next()) {
                double avgAge = rs.getDouble("avg_age");
                System.out.println("  • Average Age: " + GREEN + String.format("%.1f", avgAge) + " years" + RESET);
            }
        }
    }
    
   private void showGeographicDistribution(Connection conn) throws SQLException {
        System.out.println(CYAN + "📍 GEOGRAPHIC DISTRIBUTION (TOP 5 CITIES)" + RESET);
        System.out.println("══════════════════════════════════════════════════════════════");

        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT city, COUNT(*) as count FROM contacts " +
                         "WHERE city IS NOT NULL AND city != '' " +
                         "GROUP BY city ORDER BY count DESC LIMIT 5";
            ResultSet rs = stmt.executeQuery(sql);

            int total = 0;
            try (Statement stmt2 = conn.createStatement()) {
                ResultSet rsTotal = stmt2.executeQuery(
                    "SELECT COUNT(*) as total FROM contacts WHERE city IS NOT NULL AND city != ''"
                );
                rsTotal.next();
                total = rsTotal.getInt("total");
            }

            while (rs.next()) {
                String city = rs.getString("city");
                int count = rs.getInt("count");
                int percentage = (total > 0) ? (count * 100 / total) : 0;
                
                int barLength = (total > 0) ? (count * 20) / total : 0;
                if (barLength == 0 && count > 0) barLength = 1;
                String bar = "█".repeat(barLength);
                
                System.out.printf("   %-15s %s %d contacts (%d%%)%n", 
                    city, GREEN + bar + RESET, count, percentage);
            }
        }
    }

    private void showAgeDemographics(Connection conn) throws SQLException {
        System.out.println(CYAN + "👥 AGE DEMOGRAPHICS" + RESET);
        System.out.println("══════════════════════════════════════════════════════════════");

        try (Statement stmt = conn.createStatement()) {
            String sql = 
                "SELECT " +
                "  CASE " +
                "    WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) BETWEEN 18 AND 25 THEN '18-25' " +
                "    WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) BETWEEN 26 AND 35 THEN '26-35' " +
                "    WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) BETWEEN 36 AND 45 THEN '36-45' " +
                "    WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) BETWEEN 46 AND 55 THEN '46-55' " +
                "    WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) >= 56 THEN '56+' " +
                "  END as age_group, " +
                "  COUNT(*) as count " +
                "FROM contacts " +
                "WHERE birth_date IS NOT NULL " +
                "GROUP BY age_group " +
                "ORDER BY age_group";

            ResultSet rs = stmt.executeQuery(sql);
            
            int total = 0;
            try (Statement stmt2 = conn.createStatement()) {
                ResultSet rsTotal = stmt2.executeQuery(
                    "SELECT COUNT(*) as total FROM contacts WHERE birth_date IS NOT NULL"
                );
                rsTotal.next();
                total = rsTotal.getInt("total");
            }

            while (rs.next()) {
                String ageGroup = rs.getString("age_group");
                if (ageGroup != null) {
                    int count = rs.getInt("count");
                    int percentage = (total > 0) ? (count * 100 / total) : 0;
                    
                    int barLength = (total > 0) ? (count * 20) / total : 0;
                    if (barLength == 0 && count > 0) barLength = 1;
                    String bar = "█".repeat(barLength);
                    
                    System.out.printf("   %-8s %s %d contacts (%d%%)%n", 
                        ageGroup, GREEN + bar + RESET, count, percentage);
                }
            }
        }
    }

    private void showDataCompleteness(Connection conn) throws SQLException {
        System.out.println(CYAN + "📋 CONTACT DATA COMPLETENESS SCORE" + RESET);
        System.out.println("══════════════════════════════════════════════════════════════");

        try (Statement stmt = conn.createStatement()) {
            String sql = 
                "SELECT " +
                "  CASE " +
                "    WHEN field_count >= 9 THEN 'Complete (9-10 fields)' " +
                "    WHEN field_count >= 7 THEN 'High (7-8 fields)' " +
                "    WHEN field_count >= 5 THEN 'Medium (5-6 fields)' " +
                "    WHEN field_count >= 3 THEN 'Low (3-4 fields)' " +
                "    ELSE 'Incomplete (<3 fields)' " +
                "  END as completeness, " +
                "  COUNT(*) as count " +
                "FROM (" +
                "  SELECT " +
                "    (CASE WHEN first_name IS NOT NULL AND first_name != '' THEN 1 ELSE 0 END + " +
                "     CASE WHEN middle_name IS NOT NULL AND middle_name != '' THEN 1 ELSE 0 END + " +
                "     CASE WHEN last_name IS NOT NULL AND last_name != '' THEN 1 ELSE 0 END + " +
                "     CASE WHEN nickname IS NOT NULL AND nickname != '' THEN 1 ELSE 0 END + " +
                "     CASE WHEN city IS NOT NULL AND city != '' THEN 1 ELSE 0 END + " +
                "     CASE WHEN phone_primary IS NOT NULL AND phone_primary != '' THEN 1 ELSE 0 END + " +
                "     CASE WHEN phone_secondary IS NOT NULL AND phone_secondary != '' THEN 1 ELSE 0 END + " +
                "     CASE WHEN email IS NOT NULL AND email != '' THEN 1 ELSE 0 END + " +
                "     CASE WHEN linkedin_url IS NOT NULL AND linkedin_url != '' THEN 1 ELSE 0 END + " +
                "     CASE WHEN birth_date IS NOT NULL THEN 1 ELSE 0 END) as field_count " +
                "  FROM contacts" +
                ") as field_counts " +
                "GROUP BY completeness " +
                "ORDER BY MIN(field_count) DESC";

            ResultSet rs = stmt.executeQuery(sql);
            
            int total = 0;
            try (Statement stmt2 = conn.createStatement()) {
                ResultSet rsTotal = stmt2.executeQuery("SELECT COUNT(*) as total FROM contacts");
                rsTotal.next();
                total = rsTotal.getInt("total");
            }

            while (rs.next()) {
                String completeness = rs.getString("completeness");
                int count = rs.getInt("count");
                int percentage = (total > 0) ? (count * 100 / total) : 0;
                
                int barLength = (total > 0) ? (count * 20) / total : 0;
                if (barLength == 0 && count > 0) barLength = 1;
                String bar = "█".repeat(barLength);
                
                System.out.printf("   %-25s %s %d (%d%%)%n", 
                    completeness, GREEN + bar + RESET, count, percentage);
            }
        }
    }
    
   private void showEmailDomainAnalysis(Connection conn) throws SQLException {
        System.out.println(CYAN + "📧 TOP EMAIL PROVIDERS" + RESET);
        System.out.println("══════════════════════════════════════════════════════════════");

        try (Statement stmt = conn.createStatement()) {
            String sql = 
                "SELECT " +
                "  SUBSTRING_INDEX(email, '@', -1) as domain, " +
                "  COUNT(*) as count " +
                "FROM contacts " +
                "WHERE email IS NOT NULL AND email != '' " +
                "GROUP BY domain " +
                "ORDER BY count DESC " +
                "LIMIT 5";

            ResultSet rs = stmt.executeQuery(sql);
            
            int total = 0;
            try (Statement stmt2 = conn.createStatement()) {
                ResultSet rsTotal = stmt2.executeQuery(
                    "SELECT COUNT(*) as total FROM contacts WHERE email IS NOT NULL AND email != ''"
                );
                rsTotal.next();
                total = rsTotal.getInt("total");
            }

            while (rs.next()) {
                String domain = rs.getString("domain");
                int count = rs.getInt("count");
                int percentage = (total > 0) ? (count * 100 / total) : 0;
                
                int barLength = (total > 0) ? (count * 20) / total : 0;
                if (barLength == 0 && count > 0) barLength = 1;
                String bar = "█".repeat(barLength);
                
                System.out.printf("   %-20s %s %d (%d%%)%n", 
                    domain, GREEN + bar + RESET, count, percentage);
            }
        }
    }

   private void showBirthMonthDistribution(Connection conn) throws SQLException {
        System.out.println(CYAN + "🎂 BIRTHDAYS BY MONTH" + RESET);
        System.out.println("══════════════════════════════════════════════════════════════");

        try (Statement stmt = conn.createStatement()) {
            String sql = 
                "SELECT " +
                "  MONTH(birth_date) as month, " +
                "  COUNT(*) as count " +
                "FROM contacts " +
                "WHERE birth_date IS NOT NULL " +
                "GROUP BY month " +
                "ORDER BY month";

            ResultSet rs = stmt.executeQuery(sql);
            
            String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                                   "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            int[] counts = new int[12];
            int maxCount = 0;
            
            while (rs.next()) {
                int month = rs.getInt("month");
                int count = rs.getInt("count");
                if (month >= 1 && month <= 12) {
                    counts[month - 1] = count;
                    if (count > maxCount) maxCount = count;
                }
            }

            for (int i = 0; i < 6; i++) {
                String bar1 = "█".repeat(counts[i]);
                String bar2 = "█".repeat(counts[i + 6]);
                
                System.out.printf("   %s %s%-2d   %s %s%-2d%n",
                    monthNames[i], GREEN + bar1 + RESET, counts[i],
                    monthNames[i + 6], GREEN + bar2 + RESET, counts[i + 6]);
            }
            
            // Find peak month
            int peakMonth = 0;
            for (int i = 0; i < 12; i++) {
                if (counts[i] == maxCount) {
                    peakMonth = i;
                    break;
                }
            }
            System.out.println();
            System.out.println("   Peak month: " + GREEN + monthNames[peakMonth] + 
                             " (" + maxCount + " birthdays)" + RESET);
        }
    }
    
   private void showLinkedInAdoptionByAge(Connection conn) throws SQLException {
        System.out.println(CYAN + "💼 LINKEDIN ADOPTION BY AGE GROUP" + RESET);
        System.out.println("══════════════════════════════════════════════════════════════");

        try (Statement stmt = conn.createStatement()) {
            String sql = 
                "SELECT " +
                "  CASE " +
                "    WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) BETWEEN 18 AND 25 THEN '18-25' " +
                "    WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) BETWEEN 26 AND 35 THEN '26-35' " +
                "    WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) BETWEEN 36 AND 45 THEN '36-45' " +
                "    WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) BETWEEN 46 AND 55 THEN '46-55' " +
                "    WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) >= 56 THEN '56+' " +
                "  END as age_group, " +
                "  COUNT(*) as total, " +
                "  SUM(CASE WHEN linkedin_url IS NOT NULL AND linkedin_url != '' THEN 1 ELSE 0 END) as with_linkedin " +
                "FROM contacts " +
                "WHERE birth_date IS NOT NULL " +
                "GROUP BY age_group " +
                "ORDER BY age_group";

            ResultSet rs = stmt.executeQuery(sql);
            
            int maxPercentage = 0;
            
            // Store results first
            java.util.List<String> results = new java.util.ArrayList<>();
            while (rs.next()) {
                String ageGroup = rs.getString("age_group");
                if (ageGroup != null) {
                    int total = rs.getInt("total");
                    int withLinkedIn = rs.getInt("with_linkedin");
                    int percentage = (total > 0) ? (withLinkedIn * 100 / total) : 0;
                    
                    if (percentage > maxPercentage) {
                        maxPercentage = percentage;
                    }
                    
                    results.add(String.format("   %-8s: %d%% have LinkedIn", ageGroup, percentage));
                }
            }
            
            for (int i = 0; i < results.size(); i++) {
                String result = results.get(i);
                if (i == results.size() - 1 && result.contains(maxPercentage + "%")) {
                    System.out.println(result + GREEN + " ⭐ Highest" + RESET);
                } else {
                    System.out.println(result);
                }
            }
        }
    }

   private void showGrowthTrends(Connection conn) throws SQLException {
        System.out.println(CYAN + "📈 RECENT ACTIVITY (LAST 30 DAYS)" + RESET);
        System.out.println("══════════════════════════════════════════════════════════════");

        // New contacts
        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT COUNT(*) as count FROM contacts " +
                         "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                System.out.println("  • New contacts added: " + GREEN + rs.getInt("count") + RESET);
            }
        }

        // Updated contacts
        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT COUNT(*) as count FROM contacts " +
                      "WHERE updated_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
                      "AND updated_at > created_at";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                System.out.println("  • Contacts updated: " + GREEN + rs.getInt("count") + RESET);
            }
        }

        // Average age trend
        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT AVG(TIMESTAMPDIFF(YEAR, birth_date, CURDATE())) as avg_age " +
                      "FROM contacts " +
                      "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
                      "AND birth_date IS NOT NULL";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                double recentAvgAge = rs.getDouble("avg_age");
                
                if (recentAvgAge > 0) {
                    try (Statement stmt2 = conn.createStatement()) {
                        ResultSet rsOverall = stmt2.executeQuery(
                            "SELECT AVG(TIMESTAMPDIFF(YEAR, birth_date, CURDATE())) as avg_age " +
                            "FROM contacts WHERE birth_date IS NOT NULL"
                        );
                        rsOverall.next();
                        double overallAvgAge = rsOverall.getDouble("avg_age");
                        
                        String trend = (recentAvgAge < overallAvgAge) ? "Decreasing (younger)" : "Increasing (older)";
                        System.out.println("  • Average age trend: " + GREEN + trend + RESET);
                    }
                }
            }
        }

        // LinkedIn adoption trend
        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT " +
                      "  COUNT(*) as total, " +
                      "  SUM(CASE WHEN linkedin_url IS NOT NULL AND linkedin_url != '' THEN 1 ELSE 0 END) as with_linkedin " +
                      "FROM contacts " +
                      "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                int recentTotal = rs.getInt("total");
                int recentWithLinkedIn = rs.getInt("with_linkedin");
                
                if (recentTotal > 0) {
                    double recentLinkedInPercentage = (recentWithLinkedIn * 100.0) / recentTotal;
                    
                    try (Statement stmt2 = conn.createStatement()) {
                        ResultSet rsOverall = stmt2.executeQuery(
                            "SELECT COUNT(*) as total, " +
                            "SUM(CASE WHEN linkedin_url IS NOT NULL AND linkedin_url != '' THEN 1 ELSE 0 END) as with_linkedin " +
                            "FROM contacts"
                        );
                        rsOverall.next();
                        int overallTotal = rsOverall.getInt("total");
                        int overallWithLinkedIn = rsOverall.getInt("with_linkedin");
                        double overallLinkedInPercentage = (overallTotal > 0) ? (overallWithLinkedIn * 100.0) / overallTotal : 0;
                        
                        double difference = recentLinkedInPercentage - overallLinkedInPercentage;
                        String trend = (difference > 0) ? String.format("Increasing +%.1f%%", difference) : 
                                                         String.format("Decreasing %.1f%%", Math.abs(difference));
                        System.out.println("  • LinkedIn adoption: " + GREEN + trend + RESET);
                    }
                }
            }
        }
    }

    private int calculateAge(String birthDate) {
        try {
            LocalDate birth = LocalDate.parse(birthDate);
            LocalDate now = LocalDate.now();
            return Period.between(birth, now).getYears();
        } catch (Exception e) {
            return 0;
        }
    }
    // ==========================================
    // OPTION 3: LIST ALL USERS
    // ==========================================
    
    private void listAllUsers() {
        clearScreen();
        System.out.println(CYAN + "╔════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + "║            ALL SYSTEM USERS                    ║" + RESET);
        System.out.println(CYAN + "╚════════════════════════════════════════════════╝" + RESET);
        System.out.println();

        String sql = "SELECT user_id, username, name, surname, role FROM users ORDER BY user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println(GREEN + "════════════════════════════════════════════════════════════════════════════" + RESET);
            System.out.printf("%-8s %-20s %-15s %-15s %-20s%n", 
                "ID", "Username", "First Name", "Surname", "Role");
            System.out.println(GREEN + "════════════════════════════════════════════════════════════════════════════" + RESET);

            int count = 0;
            while (rs.next()) {
                int id = rs.getInt("user_id");
                String username = rs.getString("username");
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                String role = rs.getString("role");

                if (id == this.userId) {
                    System.out.print(CYAN);
                }

                System.out.printf("%-8d %-20s %-15s %-15s %-20s%n",
                    id,
                    username != null ? username : "",
                    name != null ? name : "",
                    surname != null ? surname : "",
                    role != null ? role : ""
                );

                if (id == this.userId) {
                    System.out.print(RESET);
                }

                count++;
            }

            System.out.println(GREEN + "════════════════════════════════════════════════════════════════════════════" + RESET);
            System.out.println();
            System.out.println(CYAN + "Total users: " + count + RESET);
            System.out.println(CYAN + "(Your account is highlighted in cyan)" + RESET);
            System.out.println();

        } catch (SQLException e) {
            System.out.println(RED + "Error listing users: " + e.getMessage() + RESET);
        }
    }
    // ==========================================
    // OPTION 4: UPDATE EXISTING USER
    // ==========================================
    
    private void updateExistingUser() {
        clearScreen();
        System.out.println(CYAN + "╔════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + "║          UPDATE EXISTING USER                  ║" + RESET);
        System.out.println(CYAN + "╚════════════════════════════════════════════════╝" + RESET);
        System.out.println();

        // Step 1: Get user ID
        System.out.print(CYAN + "Enter User ID to update (or 0 to cancel): " + RESET);
        String idInput = safeReadLine(sc);

        if (idInput.equals("0")) {
            System.out.println(YELLOW + "Operation cancelled." + RESET);
            return;
        }

        int targetUserId;
        try {
            targetUserId = Integer.parseInt(idInput);
        } catch (NumberFormatException e) {
            System.out.println(RED + "Invalid ID." + RESET);
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Step 2: Fetch current user data
            String fetchSql = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement fetchStmt = conn.prepareStatement(fetchSql);
            fetchStmt.setInt(1, targetUserId);
            ResultSet rs = fetchStmt.executeQuery();

            if (!rs.next()) {
                System.out.println(RED + "User with ID " + targetUserId + " not found." + RESET);
                conn.rollback();
                return;
            }

            String currentUsername = rs.getString("username");
            String currentName = rs.getString("name");
            String currentSurname = rs.getString("surname");
            String currentRole = rs.getString("role");

            System.out.println();
            System.out.println(GREEN + "Current User Information:" + RESET);
            System.out.println("══════════════════════════════════");
            System.out.println("ID: " + targetUserId);
            System.out.println("Username: " + currentUsername);
            System.out.println("First Name: " + currentName);
            System.out.println("Surname: " + currentSurname);
            System.out.println("Role: " + currentRole);
            System.out.println("══════════════════════════════════");
            System.out.println();

            // Step 3: Select field to update
            System.out.println(CYAN + "What would you like to update?" + RESET);
            System.out.println(GREEN + "[1] - Username" + RESET);
            System.out.println(GREEN + "[2] - First Name" + RESET);
            System.out.println(GREEN + "[3] - Surname" + RESET);
            System.out.println(GREEN + "[4] - Role" + RESET);
            System.out.println(GREEN + "[5] - Password" + RESET);
            System.out.println(RED + "[0] - Cancel" + RESET);
            System.out.print(CYAN + "Choose (0-5): " + RESET);

            String fieldChoice = safeReadLine(sc);

            if (fieldChoice.equals("0")) {
                System.out.println(YELLOW + "Update cancelled." + RESET);
                conn.rollback();
                return;
            }

            // Step 4: Get new value and update
            boolean updated = false;
            switch (fieldChoice) {
                case "1" -> updated = updateUsername(conn, targetUserId, currentUsername);
                case "2" -> updated = updateFirstName(conn, targetUserId);
                case "3" -> updated = updateSurname(conn, targetUserId);
                case "4" -> updated = updateRole(conn, targetUserId);
                case "5" -> updated = updatePassword(conn, targetUserId);
                default -> {
                    System.out.println(RED + "Invalid choice." + RESET);
                    conn.rollback();
                    return;
                }
            }

            if (updated) {
                conn.commit();
                System.out.println();
                System.out.println(GREEN + "✅ User updated successfully!" + RESET);
            } else {
                conn.rollback();
                System.out.println();
                System.out.println(RED + "❌ Update failed." + RESET);
            }

        } catch (SQLException e) {
            System.out.println(RED + "Database error: " + e.getMessage() + RESET);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    private boolean updateUsername(Connection conn, int userId, String currentUsername) throws SQLException {
        System.out.print(CYAN + "Enter new username: " + RESET);
        String newUsername = sanitizeInput(safeReadLine(sc));

        if (!isValidUsername(newUsername)) {
            System.out.println(RED + "Invalid username format." + RESET);
            return false;
        }

        String checkSql = "SELECT user_id FROM users WHERE username = ? AND user_id != ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        checkStmt.setString(1, newUsername);
        checkStmt.setInt(2, userId);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
            System.out.println(RED + "Username already exists." + RESET);
            return false;
        }

        String updateSql = "UPDATE users SET username = ? WHERE user_id = ?";
        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
        updateStmt.setString(1, newUsername);
        updateStmt.setInt(2, userId);
        return updateStmt.executeUpdate() > 0;
    }

    private boolean updateFirstName(Connection conn, int userId) throws SQLException {
        System.out.print(CYAN + "Enter new first name: " + RESET);
        String newName = sanitizeInput(safeReadLine(sc));

        if (!isValidUserName(newName)) {
            System.out.println(RED + "Invalid name format." + RESET);
            return false;
        }

        String updateSql = "UPDATE users SET name = ? WHERE user_id = ?";
        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
        updateStmt.setString(1, newName);
        updateStmt.setInt(2, userId);
        return updateStmt.executeUpdate() > 0;
    }

    private boolean updateSurname(Connection conn, int userId) throws SQLException {
        System.out.print(CYAN + "Enter new surname: " + RESET);
        String newSurname = sanitizeInput(safeReadLine(sc));

        if (!isValidUserName(newSurname)) {
            System.out.println(RED + "Invalid surname format." + RESET);
            return false;
        }

        String updateSql = "UPDATE users SET surname = ? WHERE user_id = ?";
        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
        updateStmt.setString(1, newSurname);
        updateStmt.setInt(2, userId);
        return updateStmt.executeUpdate() > 0;
    }

    private boolean updateRole(Connection conn, int userId) throws SQLException {
        System.out.println(CYAN + "Select new role:" + RESET);
        System.out.println("1 - Tester");
        System.out.println("2 - Junior Developer");
        System.out.println("3 - Senior Developer");
        System.out.println("4 - Manager");
        System.out.print(CYAN + "Choose (1-4): " + RESET);

        String roleChoice = safeReadLine(sc);

        String newRole = switch (roleChoice) {
            case "1" -> "Tester";
            case "2" -> "Junior Developer";
            case "3" -> "Senior Developer";
            case "4" -> "Manager";
            default -> null;
        };

        if (newRole == null) {
            System.out.println(RED + "Invalid role choice." + RESET);
            return false;
        }

        String updateSql = "UPDATE users SET role = ? WHERE user_id = ?";
        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
        updateStmt.setString(1, newRole);
        updateStmt.setInt(2, userId);
        return updateStmt.executeUpdate() > 0;
    }

    private boolean updatePassword(Connection conn, int userId) throws SQLException {
        System.out.print(CYAN + "Enter new password: " + RESET);
        String newPassword = sc.nextLine();

        System.out.print(CYAN + "Confirm new password: " + RESET);
        String confirmPassword = sc.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println(RED + "Passwords do not match." + RESET);
            return false;
        }
        
        if (newPassword.length() < MIN_PASSWORD_LENGTH || 
            newPassword.length() > MAX_PASSWORD_LENGTH) {
            System.out.println(RED + "Invalid password length." + RESET);
            return false;
        }

        try {
            String hashedPassword = LoginMenu.hashPassword(newPassword);
            String updateSql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, hashedPassword);
            updateStmt.setInt(2, userId);
            return updateStmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println(RED + "Error hashing password: " + e.getMessage() + RESET);
            return false;
        }
    }
    
    // ==========================================
    // OPTION 5: ADD/EMPLOY NEW USER
    // ==========================================
    
    private void addNewUser() {
        clearScreen();
        System.out.println(CYAN + "╔════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + "║          ADD/EMPLOY NEW USER                   ║" + RESET);
        System.out.println(CYAN + "╚════════════════════════════════════════════════╝" + RESET);
        System.out.println();

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Step 1: Get username
            System.out.print(CYAN + "Enter username: " + RESET);
            String username = sanitizeInput(safeReadLine(sc));

            if (!isValidUsername(username)) {
                System.out.println(RED + "Invalid username format." + RESET);
                System.out.println(CYAN + "Username must:" + RESET);
                System.out.println("  • Contain only letters, numbers, underscores, dashes");
                System.out.println("  • Be between 1 and " + MAX_USERNAME_LENGTH + " characters");
                conn.rollback();
                return;
            }

            String checkSql = "SELECT user_id FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println(RED + "Username already exists. Please choose another." + RESET);
                conn.rollback();
                return;
            }

            // Step 2: Get first name
            System.out.print(CYAN + "Enter first name: " + RESET);
            String firstName = sanitizeInput(safeReadLine(sc));

            if (!isValidUserName(firstName)) {
                System.out.println(RED + "Invalid first name format." + RESET);
                System.out.println(CYAN + "First name must contain only letters, spaces, and apostrophes." + RESET);
                conn.rollback();
                return;
            }

            // Step 3: Get surname
            System.out.print(CYAN + "Enter surname: " + RESET);
            String surname = sanitizeInput(safeReadLine(sc));

            if (!isValidUserName(surname)) {
                System.out.println(RED + "Invalid surname format." + RESET);
                System.out.println(CYAN + "Surname must contain only letters, spaces, and apostrophes." + RESET);
                conn.rollback();
                return;
            }

            // Step 4: Get role
            System.out.println();
            System.out.println(CYAN + "Select role for new user:" + RESET);
            System.out.println(GREEN + "[1] - Tester" + RESET);
            System.out.println(GREEN + "[2] - Junior Developer" + RESET);
            System.out.println(GREEN + "[3] - Senior Developer" + RESET);
            System.out.println(GREEN + "[4] - Manager" + RESET);
            System.out.print(CYAN + "Choose (1-4): " + RESET);

            String roleChoice = safeReadLine(sc);

            String role = switch (roleChoice) {
                case "1" -> "Tester";
                case "2" -> "Junior Developer";
                case "3" -> "Senior Developer";
                case "4" -> "Manager";
                default -> null;
            };

            if (role == null) {
                System.out.println(RED + "Invalid role selection." + RESET);
                conn.rollback();
                return;
            }

            // Step 5: Get password
            System.out.println();
            System.out.print(CYAN + "Enter password: " + RESET);
            String password = sc.nextLine();

            System.out.print(CYAN + "Confirm password: " + RESET);
            String confirmPassword = sc.nextLine();

            if (!password.equals(confirmPassword)) {
                System.out.println(RED + "Passwords do not match." + RESET);
                conn.rollback();
                return;
            }

            if (password.length() < MIN_PASSWORD_LENGTH || 
                password.length() > MAX_PASSWORD_LENGTH) {
                System.out.println(RED + "Password must be between " + MIN_PASSWORD_LENGTH + 
                                 " and " + MAX_PASSWORD_LENGTH + " characters." + RESET);
                conn.rollback();
                return;
            }

            // Step 6: Hash password
            String hashedPassword;
            try {
                hashedPassword = LoginMenu.hashPassword(password);
            } catch (Exception e) {
                System.out.println(RED + "Error hashing password: " + e.getMessage() + RESET);
                conn.rollback();
                return;
            }

            // Step 7: Insert new user
            String insertSql = "INSERT INTO users (username, name, surname, role, password_hash) " +
                             "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setString(1, username);
            insertStmt.setString(2, firstName);
            insertStmt.setString(3, surname);
            insertStmt.setString(4, role);
            insertStmt.setString(5, hashedPassword);

            int rowsAffected = insertStmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                int newUserId = -1;
                if (generatedKeys.next()) {
                    newUserId = generatedKeys.getInt(1);
                }

                conn.commit();

                System.out.println();
                System.out.println(GREEN + "════════════════════════════════════════════════" + RESET);
                System.out.println(GREEN + "✅ User created successfully!" + RESET);
                System.out.println(GREEN + "════════════════════════════════════════════════" + RESET);
                System.out.println();
                System.out.println("User Details:");
                System.out.println("  • User ID: " + newUserId);
                System.out.println("  • Username: " + username);
                System.out.println("  • Name: " + firstName + " " + surname);
                System.out.println("  • Role: " + role);
                System.out.println();
            } else {
                conn.rollback();
                System.out.println(RED + "❌ Failed to create user." + RESET);
            }

        } catch (SQLException e) {
            System.out.println(RED + "Database error: " + e.getMessage() + RESET);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }
    // ==========================================
    // OPTION 6: DELETE/FIRE EXISTING USER
    // ==========================================
    
    private void deleteExistingUser() {
        clearScreen();
        System.out.println(RED + "╔════════════════════════════════════════════════╗" + RESET);
        System.out.println(RED + "║          DELETE/FIRE EXISTING USER             ║" + RESET);
        System.out.println(RED + "╚════════════════════════════════════════════════╝" + RESET);
        System.out.println();
        System.out.println(YELLOW + "⚠️  WARNING: This action will permanently delete user data!" + RESET);
        System.out.println();

        // Step 1: Get user ID
        System.out.print(CYAN + "Enter User ID to delete (or 0 to cancel): " + RESET);
        String idInput = safeReadLine(sc);

        if (idInput.equals("0")) {
            System.out.println(YELLOW + "Operation cancelled." + RESET);
            return;
        }

        int targetUserId;
        try {
            targetUserId = Integer.parseInt(idInput);
        } catch (NumberFormatException e) {
            System.out.println(RED + "Invalid ID." + RESET);
            return;
        }

        if (targetUserId == this.userId) {
            System.out.println(RED + "❌ You cannot delete your own account!" + RESET);
            System.out.println(CYAN + "Please ask another Manager to delete your account if needed." + RESET);
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Step 2: Fetch user to delete
            String fetchSql = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement fetchStmt = conn.prepareStatement(fetchSql);
            fetchStmt.setInt(1, targetUserId);
            ResultSet rs = fetchStmt.executeQuery();

            if (!rs.next()) {
                System.out.println(RED + "User with ID " + targetUserId + " not found." + RESET);
                conn.rollback();
                return;
            }

            // Display user info
            String username = rs.getString("username");
            String name = rs.getString("name");
            String surname = rs.getString("surname");
            String role = rs.getString("role");

            System.out.println();
            System.out.println(RED + "User to be deleted:" + RESET);
            System.out.println(RED + "══════════════════════════════════" + RESET);
            System.out.println("ID: " + targetUserId);
            System.out.println("Username: " + username);
            System.out.println("Name: " + name + " " + surname);
            System.out.println("Role: " + role);
            System.out.println(RED + "══════════════════════════════════" + RESET);
            System.out.println();

            // Step 3: Confirmation
            System.out.println(RED + "⚠️  This action CANNOT be undone!" + RESET);
            System.out.print(RED + "Type 'DELETE' (exactly) to confirm: " + RESET);
            String confirmation = sc.nextLine();

            if (!confirmation.equals("DELETE")) {
                System.out.println(YELLOW + "Deletion cancelled. User was NOT deleted." + RESET);
                conn.rollback();
                return;
            }

            // Step 4: Delete user
            String deleteSql = "DELETE FROM users WHERE user_id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, targetUserId);

            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected > 0) {
                conn.commit();
                System.out.println();
                System.out.println(GREEN + "════════════════════════════════════════════════" + RESET);
                System.out.println(GREEN + "✅ User deleted successfully!" + RESET);
                System.out.println(GREEN + "════════════════════════════════════════════════" + RESET);
                System.out.println();
                System.out.println("Deleted user: " + username + " (" + name + " " + surname + ")");
                System.out.println();
            } else {
                conn.rollback();
                System.out.println(RED + "❌ Failed to delete user." + RESET);
            }

        } catch (SQLException e) {
            System.out.println(RED + "Database error: " + e.getMessage() + RESET);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }
    // ==========================================
    // INHERITED METHODS FROM ROLE
    // ==========================================
    
    @Override
    protected void contactsMenu() {
        super.contactsMenu();
    }

    @Override
    public String toString() {
        return "Manager{" +
                "id=" + userId +
                ", username='" + username + '\'' +
                ", name='" + name + " " + surname + '\'' +
                '}';
    }
}
