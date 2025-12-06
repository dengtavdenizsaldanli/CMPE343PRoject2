public class Tester extends Role {

    public Tester(int id, String u, String n, String s) {
        super(id, u, n, s, "Tester");
    }

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

    private void displayMenuOptions() {
        System.out.println(GREEN + "[1] - CONTACTS MENU" + RESET);
        System.out.println(GREEN + "[2] - CHANGE PASSWORD" + RESET);
        System.out.println("\n" + RED + "[3] - LOGOUT" + RESET);
        System.out.print("\n" + CYAN + "Pick an Option (1-3): " + RESET);
    }

    private String getUserChoice() {
        return sc.nextLine().trim();
    }

    private boolean isValidChoice(String choice) {
        return choice.matches("[1-3]");
    }

    private void handleInvalidChoice() {
        System.out.println(RED + "Invalid choice. Please enter 1, 2, or 3." + RESET);
        pause();
    }

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
                return true;
            }
        }
    }

    private void handleContactsMenu() {
        try {
            contactsMenu();
        } catch (Exception e) {
            System.err.println(RED + "Error accessing contacts menu: " + e.getMessage() + RESET);
            System.err.println(CYAN + "Returning to main menu..." + RESET);
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
            System.err.println(CYAN + "Please try again later." + RESET);
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
            System.out.println(GREEN + "Logout cancelled. Returning to menu..." + RESET);
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return true;
        }
    }

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

        System.err.println("Stack trace:");
        e.printStackTrace();

        pause();
    }

    @Override
    public String toString() {
        return "Tester{" +
                "id=" + userId +
                ", username='" + username + '\'' +
                ", name='" + name + " " + surname + '\'' +
                '};
    }
}


