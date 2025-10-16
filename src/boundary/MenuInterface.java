package boundary;

import java.util.Scanner;

public abstract class MenuInterface {
    protected Scanner scanner;

    public MenuInterface(Scanner scanner) {
        this.scanner = scanner;
    }

    public abstract void displayMenu();

    public abstract void handleMenuChoice(int choice);

    protected void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    protected void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    protected void printHeader(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(title);
        System.out.println("=".repeat(60));
    }

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

    protected String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
