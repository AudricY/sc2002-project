package boundary;

import java.util.Scanner;

/**
 * Abstract base class for all menu interfaces.
 * Provides common utility methods for screen management and user input.
 */
public abstract class MenuInterface {
    protected Scanner scanner;

    /**
     * Creates a menu interface with the given scanner.
     *
     * @param scanner scanner for user input
     */
    public MenuInterface(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Displays the menu options.
     */
    public abstract void displayMenu();

    /**
     * Handles a menu choice selection.
     *
     * @param choice selected menu option
     */
    public abstract void handleMenuChoice(int choice);

    /**
     * Clears the console screen.
     */
    protected void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                // Support windows
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Works on terminals that support ANSI escape codes
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("Could not clear screen");
        }
    }

    /**
     * Pauses execution until user presses Enter.
     */
    protected void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Prints a formatted header.
     *
     * @param title header title
     */
    protected void printHeader(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(title);
        System.out.println("=".repeat(60));
    }

    /**
     * Gets integer input from user with validation.
     *
     * @param prompt prompt message
     * @return integer value entered
     */
    protected int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Gets string input from user.
     *
     * @param prompt prompt message
     * @return trimmed string input
     */
    protected String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
