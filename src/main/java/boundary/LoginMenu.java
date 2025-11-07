package boundary;

import control.AuthenticationController;
import entity.*;
import util.InputValidator;
import util.PasswordChangeResult;
import java.util.Scanner;

/**
 * Menu for user login and company representative registration.
 */
public class LoginMenu extends MenuInterface {
    private AuthenticationController authController;

    /**
     * Creates a login menu.
     *
     * @param scanner scanner for user input
     */
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
            default:
                System.out.println("Invalid choice.");
        }
    }

    /**
     * Handles user login process and navigates to appropriate menu on success.
     */
    private void handleLogin() {
        System.out.println("\n--- Login ---");

        String userId = getStringInput("User ID: ");
        String password = getStringInput("Password: ");

        if (!authController.login(userId, password)) {
            System.out.println("Login failed. Please check your User ID and password.");
            System.out.println("If your account is new, make sure it has been approved by an administrator.");
            pause();
            return;
        }

        User user = authController.getCurrentUser();
        System.out.println("Login successful! Welcome, " + user.getName() + ".");
        postLoginFlow(user);
        
    }

    /**
     * Handles post-login flow including mandatory password change for first-time users.
     *
     * @param user logged in user
     */
    private void postLoginFlow(User user) {
        if (user.isFirstLogin()) {
            System.out.println("\nFirst-time login detected. You must change your password before continuing.");
            boolean passwordChanged = handlePasswordChange();

            if (!passwordChanged) {
                System.out.println("Password change cancelled or failed. You cannot proceed until your password is updated.");
                pause();
                return;
            }

            System.out.println("Password updated successfully. Proceeding to your account...");
        }

        pause();
        navigateToUserMenu(user);
    }

    /**
     * Handles password change process with validation and attempt limits.
     *
     * @return true if password changed successfully, false otherwise
     */
    private boolean handlePasswordChange() {
        final int MAX_ATTEMPTS = 3;
        int attempts = 0;
        while (attempts < MAX_ATTEMPTS) {
            String oldPassword = getStringInput("Enter current password: ");
            String newPassword = getStringInput("Enter new password (min 6 characters): ");
            String confirmPassword = getStringInput("Confirm new password: ");
            attempts++;
            PasswordChangeResult result = authController.attemptPasswordChange(oldPassword, newPassword, confirmPassword);

            switch (result) {
                case SUCCESS:
                    System.out.println("Password changed successfully!");
                    return true;
                case MISMATCH:
                    System.out.println("Passwords do not match. Try again.");
                    break;
                case INVALID_FORMAT:
                    System.out.println("Password must be at least 6 characters. Try again.");
                    break;
                case INCORRECT_OLD:
                    System.out.println("Current password incorrect. Try again.");
                    break;
                case DUPLICATE:
                    System.out.println("Your new password must be different from the old one. Try again.");
                    break;
            }
        }

        System.out.println("Too many failed attempts.");
        return false;
    }

    /**
     * Handles company representative registration process.
     */
    private void handleRegistration() {
        System.out.println("\n--- Company Representative Registration ---");
        String name = getStringInput("Full Name: ");

        String email;
        while (true) {
            email = getStringInput("Email: ");
            if (!InputValidator.isValidEmail(email)) {
                System.out.println("Invalid email format. Try again.");
                continue;
            }
            break;
            
        }
        
        String password;
        while (true) {
            password = getStringInput("Password (min 6 characters): ");
            if (!InputValidator.isValidPassword(password)) {
                System.out.println("Password must be at least 6 characters. Try again.");
                continue;
            }
            break;
        }

        String companyName = getStringInput("Company Name: ");
        String department = getStringInput("Department: ");
        String position = getStringInput("Position: ");

        Boolean success = authController.registerCompanyRepresentative(name, email, password,
                companyName, department, position);
        if (success) {
            System.out.println("\nRegistration successful! Your account is pending approval.");
            System.out.println("You will be able to login once approved by Career Center Staff.");
        }
        else {
            System.out.println("\nRegistration failed!");
            System.out.println("An account with this email already exists!");
        }
        pause();
    }

    /**
     * Navigates to the appropriate menu based on user role.
     *
     * @param user logged in user
     */
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
