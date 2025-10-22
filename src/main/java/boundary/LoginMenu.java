package boundary;

import control.AuthenticationController;
import control.UserManager;
import entity.*;
import util.InputValidator;
import java.util.Scanner;

public class LoginMenu extends MenuInterface {
    private AuthenticationController authController;

    public LoginMenu(Scanner scanner) {
        super(scanner);
        this.authController = new AuthenticationController();
    }

    @Override
    public void displayMenu() {
        clearScreen();
        printHeader("INTERNSHIP PLACEMENT MANAGEMENT SYSTEM");
        System.out.println("1. Login");
        System.out.println("2. Register (Company Representative)");
        System.out.println("3. Exit");
        System.out.print("\nEnter choice: ");
    }

    @Override
    public void handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                handleLogin();
                break;
            case 2:
                handleRegistration();
                break;
            case 3:
                System.out.println("Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void handleLogin() {
        System.out.println("\n--- Login ---");
        String userId = getStringInput("User ID: ");
        String password = getStringInput("Password: ");

        if (authController.login(userId, password)) {
            User user = authController.getCurrentUser();
            System.out.println("Login successful! Welcome, " + user.getName());

            if (user.isFirstLogin()) {
                System.out.println("\nFirst time login detected. Please change your password.");
                handlePasswordChange();
            }

            pause();
            navigateToUserMenu(user);
        } else {
            System.out.println("Login failed. Invalid credentials or account not approved.");
            pause();
        }
    }

    private void handlePasswordChange() {
        while (true) {
            String oldPassword = getStringInput("Enter current password: ");
            String newPassword = getStringInput("Enter new password (min 6 characters): ");
            String confirmPassword = getStringInput("Confirm new password: ");

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("Passwords do not match. Try again.");
                continue;
            }

            if (!InputValidator.isValidPassword(newPassword)) {
                System.out.println("Password must be at least 6 characters. Try again.");
                continue;
            }

            if (authController.changePassword(oldPassword, newPassword)) {
                System.out.println("Password changed successfully!");
                break;
            } else {
                System.out.println("Current password incorrect. Try again.");
            }
        }
    }

    private void handleRegistration() {
        System.out.println("\n--- Company Representative Registration ---");
        String name = getStringInput("Full Name: ");

        String email;
        while (true) {
            email = getStringInput("Email: ");
            if (InputValidator.isValidEmail(email)) {
                break;
            }
            System.out.println("Invalid email format. Try again.");
        }
        
        String password;
        while (true) {
            password = getStringInput("Password (min 6 characters): ");
            if (InputValidator.isValidPassword(password)) {
                break;
            }
            System.out.println("Password must be at least 6 characters. Try again.");
        }

        String companyName = getStringInput("Company Name: ");
        String department = getStringInput("Department: ");
        String position = getStringInput("Position: ");

        authController.registerCompanyRepresentative(name, email, password,
                companyName, department, position);

        System.out.println("\nRegistration successful! Your account is pending approval.");
        System.out.println("You will be able to login once approved by Career Center Staff.");
        pause();
    }

    private void navigateToUserMenu(User user) {
        MenuInterface userMenu;

        if (user instanceof Student) {
            userMenu = new StudentMenu(scanner, authController);
        } else if (user instanceof CompanyRepresentative) {
            userMenu = new CompanyRepresentativeMenu(scanner, authController);
        } else if (user instanceof CareerCenterStaff) {
            userMenu = new CareerCenterStaffMenu(scanner, authController);
        } else {
            return;
        }

        while (true) {
            userMenu.displayMenu();
            int choice = getIntInput("");
            if (choice == 0) {
                authController.logout();
                break;
            }
            userMenu.handleMenuChoice(choice);
        }
    }

    public AuthenticationController getAuthController() {
        return authController;
    }
}
