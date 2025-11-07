import boundary.LoginMenu;
import control.UserManager;
import entity.UserRole;
import util.FileManager;
import java.util.Scanner;

/**
 * Main entry point for the Internship Placement Management System.
 * Initializes the system and loads initial data from CSV files.
 */
public class InternshipPlacementSystem {
    /**
     * Main method that starts the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        FileManager.ensureDataDirectory();

        UserManager userManager = UserManager.getInstance();
        userManager.loadUsersFromCSV("students.csv", UserRole.STUDENT);
        userManager.loadUsersFromCSV("staff.csv", UserRole.CAREER_CENTER_STAFF);

        System.out.println("Internship Placement Management System initialized.");
        System.out.println("Data loaded from CSV files.");

        LoginMenu loginMenu = new LoginMenu(scanner);

        while (true) {
            loginMenu.displayMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                loginMenu.handleMenuChoice(choice);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
