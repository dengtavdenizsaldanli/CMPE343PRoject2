/**
 * Provides ASCII art animations for application startup and exit sequences.
 * <p>
 * This class contains static methods to display colorful ASCII animations
 * with ANSI color codes. The animations enhance user experience by providing
 * visual feedback during application startup and shutdown.
 * </p>
 * 
 * <p><b>Features:</b></p>
 * <ul>
 *   <li>Startup animation with progressive text display</li>
 *   <li>Exit animation with shutdown sequence</li>
 *   <li>Cross-platform console clearing (Windows/Unix)</li>
 *   <li>ANSI color support for enhanced visuals</li>
 * </ul>
 * 
 * <p><b>Usage:</b></p>
 * <pre>{@code
 * // At application start
 * AnimationFrames.playStartupAnimation();
 * 
 * // At application exit
 * AnimationFrames.playExitAnimation();
 * }</pre>
 * 
 * @author Group 22
 * @version 1.0
 * @since 2024
 */
public class AnimationFrames {

    // ==========================================
    // ANSI COLOR CODES (Static Constants)
    // ==========================================
    
    /** ANSI code to reset all text formatting. */
    public static final String RESET = "\u001B[0m";
    
    /** ANSI code for red text. */
    public static final String RED = "\u001B[31m";
    
    /** ANSI code for green text. */
    public static final String GREEN = "\u001B[32m";
    
    /** ANSI code for yellow text. */
    public static final String YELLOW = "\u001B[33m";
    
    /** ANSI code for blue text. */
    public static final String BLUE = "\u001B[34m";
    
    /** ANSI code for purple text. */
    public static final String PURPLE = "\u001B[35m";
    
    /** ANSI code for cyan text. */
    public static final String CYAN = "\u001B[36m";

    // ==========================================
    // ANIMATION TIMING CONSTANTS
    // ==========================================
    
    /** Delay for first startup frame (milliseconds). */
    private static final int STARTUP_DELAY_1 = 800;
    
    /** Delay for second startup frame (milliseconds). */
    private static final int STARTUP_DELAY_2 = 800;
    
    /** Delay for third startup frame (milliseconds). */
    private static final int STARTUP_DELAY_3 = 800;
    
    /** Delay for final startup frame (milliseconds). */
    private static final int STARTUP_DELAY_FINAL = 1500;
    
    /** Default delay for exit animation frames (milliseconds). */
    private static final int EXIT_DELAY_DEFAULT = 500;
    
    /** Short delay for exit animation (milliseconds). */
    private static final int EXIT_DELAY_SHORT = 300;
    
    /** Medium delay for exit animation (milliseconds). */
    private static final int EXIT_DELAY_MEDIUM = 800;
    
    /** Final delay for exit animation (milliseconds). */
    private static final int EXIT_DELAY_FINAL = 2000;

    // ==========================================
    // PRIVATE CONSTRUCTOR (Utility Class)
    // ==========================================
    
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with static methods only.
     */
    private AnimationFrames() {
        throw new AssertionError("AnimationFrames is a utility class and should not be instantiated");
    }

    // ==========================================
    // PUBLIC ANIMATION METHODS
    // ==========================================
    
    /**
     * Plays the startup animation sequence.
     * <p>
     * Displays a progressive ASCII art animation showing the application title:
     * "Role-Based Contact Management System". Each frame adds more text with
     * different colors (cyan, yellow, green, blue, purple).
     * </p>
     * 
     * <p>The animation consists of 5 frames with the following delays:</p>
     * <ul>
     *   <li>Frame 1: 800ms - "WELCOME TO"</li>
     *   <li>Frame 2: 800ms - + "ROLE-BASED"</li>
     *   <li>Frame 3: 800ms - + "CONTACT"</li>
     *   <li>Frame 4: 1500ms - + "MANAGEMENT"</li>
     *   <li>Frame 5: Final - + "SYSTEM"</li>
     * </ul>
     * 
     * <p>After the animation completes, the console is cleared to prepare
     * for the main menu display.</p>
     */
    public static void playStartupAnimation() {
        // Set UTF-8 encoding for Turkish character support
        try {
            System.setProperty("file.encoding", "UTF-8");
            System.setProperty("console.encoding", "UTF-8");
        } catch (Exception e) {
            // Encoding setting failed, continue anyway
        }

        String[] frames = getStartupFrames();
        int[] delays = {
            STARTUP_DELAY_1,
            STARTUP_DELAY_2,
            STARTUP_DELAY_3,
            STARTUP_DELAY_FINAL
        };

        for (int i = 0; i < frames.length; i++) {
            if (i != frames.length - 1) {
                clearConsole();
            } else {
                // For last frame, just reset cursor position
                System.out.print("\033[H");
            }
            
            System.out.println(frames[i]);
            
            int duration = (i < delays.length) ? delays[i] : EXIT_DELAY_DEFAULT;
            sleep(duration);
        }
        clearConsole();
    }

    /**
     * Plays the exit animation sequence.
     * <p>
     * Displays a shutdown sequence with progress bars showing the application
     * termination process. The animation includes status messages and a final
     * goodbye message.
     * </p>
     * 
     * <p>The animation consists of 6 frames:</p>
     * <ol>
     *   <li>Shutdown initiated (10%)</li>
     *   <li>Closing connections (35%)</li>
     *   <li>Cleaning memory (60%)</li>
     *   <li>Finalizing (85%)</li>
     *   <li>Shutdown complete (100%)</li>
     *   <li>Goodbye message with ASCII art</li>
     * </ol>
     */
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
    
    /**
     * Pauses execution for the specified duration.
     * <p>
     * If the thread is interrupted during sleep, the interrupted status
     * is restored and the method returns immediately.
     * </p>
     * 
     * @param milliseconds the duration to sleep in milliseconds
     */
    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            // Restore interrupted status
            Thread.currentThread().interrupt();
            // Log could be added here for debugging
        }
    }

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
     * <p>If clearing fails (e.g., unsupported console), the method
     * fails silently to prevent application crashes.</p>
     */
    private static void clearConsole() {
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
                // \033[H moves cursor to home position
                // \033[2J clears entire screen
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Console clearing failed, ignore
            // This can happen in non-interactive consoles or IDEs
        }
    }

    // ==========================================
    // ASCII ART DATA (Private Methods)
    // ==========================================
    
    /**
     * Returns the startup animation frames.
     * <p>
     * Each frame contains progressively more text, building up the
     * complete application title. Colors are used to make each section
     * visually distinct.
     * </p>
     * 
     * @return array of ASCII art frames for startup animation
     */
    private static String[] getStartupFrames() {
        
        // PART 1: WELCOME TO (Cyan)
        String p1 = CYAN +
        "   _  _  ____  __    ___  __  __  __  ____    ____  __  \n" +
        "  ( \\/ )(  __)(  )  / __)/  \\(  \\/  )(  __)  (_  _)/  \\ \n" +
        "  / \\/ \\ ) _) / (_/\\( (__(  O ))    (  ) _)     )( (  O )\n" +
        "  \\_)(_/(____)\\____/\\___)\\__/(_/\\/\\_)(____)    (__) \\__/ " + RESET;

        // PART 2: ROLE-BASED (Yellow)
        String p2 = YELLOW +
        "   ____  __  __    ____    ____   __   ___  ____  ____ \n" +
        "  (  _ \\/  \\(  )  (  __)  (  _ \\ / _\\ / __)(  __)(    \\\n" +
        "   )   (  O ) (_/\\ ) _)    ) _ (/    \\\\__ \\ ) _)  ) D (\n" +
        "  (__\\_)\\__/\\____/(____)  (____/\\_/\\_/(___/(____)(____/" + RESET;

        // PART 3: CONTACT (Green)
        String p3 = GREEN +
        "    ___  __  _  _  ____  __    ___  ____ \n" +
        "   / __)/  \\( \\( )(_  _)/ _\\  / __)(_  _)\n" +
        "  ( (__(  O ))  (   )( /    \\( (__   )(  \n" +
        "   \\___)\\__/(_)\\_) (__)\\_/\\_/ \\___) (__)" + RESET;

        // PART 4: MANAGEMENT (Blue)
        String p4 = BLUE +
        "   __  __  __    _  _  __    ___  ____  __  __  ____  _  _  ____ \n" +
        "  (  \\/  )/ _\\  ( \\( )/ _\\  / __)(  __)(  \\/  )(  __)( \\( )(_  _)\n" +
        "   )    (/    \\  )  (/    \\( (_ \\ ) _)  )    (  ) _)  )  (   )(  \n" +
        "  (_/\\/\\_)\\_/\\_)(_)\\_)\\_/\\_/ \\___/(____)(_/\\/\\_)(____)(_)\\_) (__) " + RESET;

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

    /**
     * Returns the exit animation frames.
     * <p>
     * Each frame shows a step in the shutdown process with a progress bar
     * and status messages. The final frame displays a goodbye message.
     * </p>
     * 
     * @return array of ASCII art frames for exit animation
     */
    private static String[] getExitFrames() {
        return new String[]{
            // Frame 1: Shutdown initiated (10%)
            RED + " [SYSTEM] Shutdown Sequence Initiated..." + RESET + "\n\n" + 
            YELLOW + "  Process ID: 2241 Terminating..." + RESET + "\n" + 
            RED + "  [##..................] 10%" + RESET,
            
            // Frame 2: Closing connections (35%)
            RED + " [SYSTEM] Closing Connections..." + RESET + "\n\n" + 
            CYAN + "  > Saving User Data... OK" + RESET + "\n" + 
            YELLOW + "  [######..............] 35%" + RESET,
            
            // Frame 3: Cleaning memory (60%)
            RED + " [SYSTEM] Cleaning Memory..." + RESET + "\n\n" + 
            CYAN + "  > Releasing Resources... OK" + RESET + "\n" + 
            YELLOW + "  [##########..........] 60%" + RESET,
            
            // Frame 4: Finalizing (85%)
            RED + " [SYSTEM] Finalizing..." + RESET + "\n\n" + 
            CYAN + "  > Secure Logout... OK" + RESET + "\n" + 
            YELLOW + "  [###############.....] 85%" + RESET,
            
            // Frame 5: Complete (100%)
            GREEN + " [SYSTEM] SHUTDOWN COMPLETE." + RESET + "\n\n" + 
            GREEN + "  > System Halted." + RESET + "\n" + 
            GREEN + "  [####################] 100%" + RESET,
            
            // Frame 6: Goodbye message
            PURPLE + "  ____  _  _  ____  " + RESET + "\n" + 
            PURPLE + " (  _ \\( \\/ )(  __) " + RESET + "\n" + 
            PURPLE + "  ) _ ( )  /  ) _)  " + RESET + "\n" + 
            PURPLE + " (____/(__/  (____) " + RESET + "\n\n" + 
            CYAN + "Thank you for using Contact Management System!" + RESET + "\n" +
            CYAN + "Good Bye! See you soon." + RESET
        };
    }
}