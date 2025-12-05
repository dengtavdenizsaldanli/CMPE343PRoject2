public class AnimationFrames {

    // ==========================================
    // ANSI COLOR CODES (Static Constants)
    // ==========================================
    
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";

    // ==========================================
    // ANIMATION TIMING CONSTANTS
    // ==========================================
    
    private static final int STARTUP_DELAY = 800;
    private static final int STARTUP_DELAY_FINAL = 1500;
    private static final int EXIT_DELAY_DEFAULT = 500;
    private static final int EXIT_DELAY_SHORT = 300;
    private static final int EXIT_DELAY_MEDIUM = 800;
    private static final int EXIT_DELAY_FINAL = 2000;

    // ==========================================
    // PRIVATE CONSTRUCTOR (Utility Class)
    // ==========================================
    
    private AnimationFrames() {
        throw new AssertionError("AnimationFrames is a utility class and should not be instantiated");
    }

    public static void playStartupAnimation() {
        try {
            System.setProperty("file.encoding", "UTF-8");
            System.setProperty("console.encoding", "UTF-8");
        } catch (Exception e) {
        }

        String[] frames = getStartupFrames();
        int[] delays = {
            STARTUP_DELAY,
            STARTUP_DELAY,
            STARTUP_DELAY,
            STARTUP_DELAY,
            STARTUP_DELAY_FINAL
        };

        for (int i = 0; i < frames.length; i++) {
            if (i != frames.length - 1) {
                clearConsole();
            } else {
                System.out.print("\033[H");
            }
            
            System.out.println(frames[i]);
            
            int duration = (i < delays.length) ? delays[i] : EXIT_DELAY_DEFAULT;
            sleep(duration);
        }
        clearConsole();
    }
    
    public static void playExitAnimation() {
        String[] frames = getExitFrames();
        int[] delays = {
            EXIT_DELAY_DEFAULT,
            EXIT_DELAY_SHORT,
            EXIT_DELAY_SHORT,
            EXIT_DELAY_SHORT,
            EXIT_DELAY_MEDIUM,
            EXIT_DELAY_FINAL
        };

        for (int i = 0; i < frames.length; i++) {
            clearConsole();
            System.out.println(frames[i]);
            int duration = (i < delays.length) ? delays[i] : EXIT_DELAY_DEFAULT;
            sleep(duration);
        }
    }

    // ==========================================
    // PRIVATE HELPER METHODS
    // ==========================================
    
    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private static void clearConsole() {
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

    // ==========================================
    // ASCII ART DATA (Private Methods)
    // ==========================================
    private static String[] getStartupFrames() {
        
        // PART 1: WELCOME TO (Cyan)
        String p1 = CYAN +
        "   _  _  ____  __    ___  __  __  __  ____    ____  __  \n" +
        "  ( \\/ )(  __)(  )  / __)/  \\(  \\/  )(  __)  (_  _)/  \\ \n" +
        "  / \\/ \\ ) _) / (_/\\( (__(  O ))    (  ) _)    )( (  O )\n" +
        "  \\_)(_/(____)\\____/\\___)\\__/(_/\\/\\_)(____)   (__) \\__/ " + RESET;

        // PART 2: ROLE-BASED (Yellow)
        String p2 = YELLOW +
        "   ____  __  __    ____    ____   __   ___  ____  ____ \n" +
        "  (  _ \\/  \\(  )  (  __)  (  _ \\ / _\\ / __)(  __)(    \\\n" +
        "   )   (  O ) (_/\\ ) _)    ) _ (/    \\\\__ \\ ) _)  ) D (\n" +
        "  (__\\_)\\__/\\____/(____)  (____/\\_/\\_/(___/(____)(____/" + RESET;

        // PART 3: CONTACT (Green)
        String p3 = GREEN +
        "   ___  __  _  _  ____  __    ___  ____ \n" +
        "  / __)/  \\( \\( )(_  _)/ _\\  / __)(_  _)\n" +
        " ( (__(  O ))  (   )( /    \\( (__   )(  \n" +
        "  \\___)\\__/(_)\\_) (__)\\_/\\_/ \\___) (__)" + RESET;

        // PART 4: MANAGEMENT (Blue)
        String p4 = BLUE +
        "   __  __  __    _  _  __    ___  ____  __  __  ____  _  _  ____ \n" +
        "  (  \\/  )/ _\\  ( \\( )/ _\\  / __)(  __)(  \\/  )(  __)( \\( )(_  _)\n" +
        "   )    (/    \\  )  (/    \\( (_ \\ ) _)  )    (  ) _)  )  (   )(  \n" +
        "  (_/\\/\\_)\\_/\\_)(_)\\_)\\_/\\_/\\___/(____)(_/\\/\\_)(____)(_)\\_)(__) " + RESET;

        // PART 5: SYSTEM (Purple)
        String p5 = PURPLE +
        "   ___  _  _  ___  ____  ____  __  __ \n" +
        "  / __)( \\/ )/ __)(_  _)(  __)(  \\/  )\n" +
        "  \\__ \\ )  / \\__ \\  )(   ) _)  )    ( \n" +
        "  (___/(__/  (___/ (__) (____)(_/\\/\\_)" + RESET;

        return new String[]{
            "\n" + p1 + "\n",
            
            "\n" + p1 + "\n\n" + p2 + "\n",
            
            "\n" + p1 + "\n\n" + p2 + "\n\n" + p3 + "\n",
            
            "\n" + p1 + "\n\n" + p2 + "\n\n" + p3 + "\n" + p4 + "\n",

            "\n" + p1 + "\n\n" + p2 + "\n\n" + p3 + "\n" + p4 + "\n" + p5
        };
    }

    private static String[] getExitFrames() {
        return new String[]{
            RED + " [SYSTEM] Shutdown Sequence Initiated..." + RESET + "\n\n" + 
            YELLOW + "  Process ID: 2241 Terminating..." + RESET + "\n" + 
            RED + "  [##..................] 10%" + RESET,
            
            RED + " [SYSTEM] Closing Connections..." + RESET + "\n\n" + 
            CYAN + "  > Saving User Data... OK" + RESET + "\n" + 
            YELLOW + "  [######..............] 35%" + RESET,

            RED + " [SYSTEM] Cleaning Memory..." + RESET + "\n\n" + 
            CYAN + "  > Releasing Resources... OK" + RESET + "\n" + 
            YELLOW + "  [##########..........] 60%" + RESET,
            
            RED + " [SYSTEM] Finalizing..." + RESET + "\n\n" + 
            CYAN + "  > Secure Logout... OK" + RESET + "\n" + 
            YELLOW + "  [###############.....] 85%" + RESET,
            
            GREEN + " [SYSTEM] SHUTDOWN COMPLETE." + RESET + "\n\n" + 
            GREEN + "  > System Halted." + RESET + "\n" + 
            GREEN + "  [####################] 100%" + RESET,
            
            PURPLE + "  ____  _  _  ____  " + RESET + "\n" + 
            PURPLE + " (  _ \\( \\/ )(  __) " + RESET + "\n" + 
            PURPLE + "  ) _ ( )  /  ) _)  " + RESET + "\n" + 
            PURPLE + " (____/(__/  (____) " + RESET + "\n\n" + 
            CYAN + "Thank you for using Contact Management System!" + RESET + "\n" +
            CYAN + "Good Bye! See you soon." + RESET
        };
    }
}
