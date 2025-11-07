package boundary;

import control.*;
import entity.*;
import util.DateUtils;
import util.IdGenerator;
import util.PasswordChangeResult;

import java.util.*;

/**
 * Menu for student operations including viewing internships, applying, and managing applications.
 */
public class StudentMenu extends MenuInterface {
    private AuthenticationController authController;
    private Student student;
    private InternshipManager internshipManager;
    private ApplicationManager applicationManager;
    private WithdrawalManager withdrawalManager;
    private FilterManager filterManager;

    /**
     * Creates a student menu.
     *
     * @param scanner scanner for user input
     * @param authController authentication controller
     */
    public StudentMenu(Scanner scanner, AuthenticationController authController) {
        super(scanner);
        this.authController = authController;
        this.student = (Student) authController.getCurrentUser();
        this.internshipManager = InternshipManager.getInstance();
        this.applicationManager = ApplicationManager.getInstance();
        this.withdrawalManager = WithdrawalManager.getInstance();
        this.filterManager = FilterManager.getInstance();
    }

    @Override
    public void displayMenu() {
        clearScreen();
        printHeader("STUDENT MENU - " + student.getName());
        System.out.println("1. View Available Internships");
        System.out.println("2. View My Applications");
        System.out.println("3. Apply for Internship");
        System.out.println("4. Accept Placement");
        System.out.println("5. Request Withdrawal");
        System.out.println("6. View Profile");
        System.out.println("7. Change Password");
        System.out.println("8. Configure Filters");
        System.out.println("0. Logout");
        System.out.print("\nEnter choice: ");
    }

    @Override
    public void handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                viewAvailableInternships();
                break;
            case 2:
                viewMyApplications();
                break;
            case 3:
                applyForInternship();
                break;
            case 4:
                acceptPlacement();
                break;
            case 5:
                requestWithdrawal();
                break;
            case 6:
                viewProfile();
                break;
            case 7:
                changePassword();
                break;
            case 8:
                configureFilters();
                break;
            case 0:
                break;
            default:
                System.out.println("Invalid choice.");
                pause();
        }
    }

    /**
     * Displays all available internships for the student based on eligibility and filters.
     */
    private void viewAvailableInternships() {
        printHeader("AVAILABLE INTERNSHIPS");

        FilterSettings settings = filterManager.getFilterSettings(student.getUserId());
        List<Internship> internships = internshipManager.getVisibleInternshipsForStudent(student);
        internships = internshipManager.filterInternships(internships, settings);

        if (internships.isEmpty()) {
            System.out.println("No internships available.");
        } else {
            for (int i = 0; i < internships.size(); i++) {
                Internship intern = internships.get(i);
                System.out.printf("\n%d. [%s] %s\n", i + 1, intern.getInternshipId(), intern.getTitle());
                System.out.printf("   Company: %s\n", intern.getCompanyName());
                System.out.printf("   Level: %s | Major: %s\n", intern.getLevel(), intern.getPreferredMajor());
                System.out.printf("   Slots: %d/%d available\n",
                        intern.getTotalSlots() - intern.getConfirmedSlots(), intern.getTotalSlots());
                System.out.printf("   Period: %s to %s\n", DateUtils.formatDate(intern.getOpeningDate()), DateUtils.formatDate(intern.getClosingDate()));
            }
        }
        pause();
    }

    /**
     * Displays all applications submitted by the student with their current status.
     */
    private void viewMyApplications() {
        printHeader("MY APPLICATIONS");

        List<Application> applications = applicationManager.getApplicationsByStudent(student.getUserId());

        if (applications.isEmpty()) {
            System.out.println("You have no applications.");
        } else {
            for (Application app : applications) {
                Internship intern = internshipManager.getInternshipById(app.getInternshipId());
                if (intern == null) {
                    System.out.printf("\nApplication ID: %s\n", app.getApplicationId());
                    System.out.println("Internship: [NOT FOUND]");
                    System.out.printf("Status: %s\n", app.getStatus());
                    continue;
                }
                System.out.printf("\nApplication ID: %s\n", app.getApplicationId());
                System.out.printf("Internship: %s - %s\n", intern.getInternshipId(), intern.getTitle());
                System.out.printf("Company: %s\n", intern.getCompanyName());
                System.out.printf("Status: %s\n", app.getStatus());
                if (app.getStatus().equals(ApplicationStatus.CONFIRMED)) {
                    System.out.println("*** CONFIRMED PLACEMENT ***");
                }
                System.out.printf("Applied: %s\n", app.getApplicationDate());
            }
        }
        pause();
    }

    /**
     * Handles the process of applying for an internship opportunity.
     * Enforces the 3 concurrent application limit and date validation.
     */
    private void applyForInternship() {
        printHeader("APPLY FOR INTERNSHIP");

        if (student.hasConfirmedPlacement()) {
            System.out.println("You already have a confirmed placement.");
            pause();
            return;
        }

        int pendingCount = applicationManager.countPendingApplicationsByStudent(student.getUserId());
        if (pendingCount >= 3) {
            System.out.println("You already have 3 pending applications. Cannot apply for more.");
            pause();
            return;
        }

        FilterSettings settings = filterManager.getFilterSettings(student.getUserId());
        List<Internship> internships = internshipManager.getVisibleInternshipsForStudent(student);
        internships = internshipManager.filterInternships(internships, settings);

        if (internships.isEmpty()) {
            System.out.println("No internships available to apply.");
        } else {
            for (int i = 0; i < internships.size(); i++) {
                Internship intern = internships.get(i);
                System.out.printf("\n%d. [%s] %s\n", i + 1, intern.getInternshipId(), intern.getTitle());
                System.out.printf("   Company: %s\n", intern.getCompanyName());
                System.out.printf("   Level: %s | Major: %s\n", intern.getLevel(), intern.getPreferredMajor());
                System.out.printf("   Slots: %d/%d available\n",
                        intern.getTotalSlots() - intern.getConfirmedSlots(), intern.getTotalSlots());
                System.out.printf("   Period: %s to %s\n", DateUtils.formatDate(intern.getOpeningDate()), DateUtils.formatDate(intern.getClosingDate()));
            }
        }

        int choice = getIntInput("\nSelect internship (0 to cancel): ");
        if (choice < 1 || choice > internships.size()) {
            System.out.println("Cancelled.");
            pause();
            return;
        }

        Internship selectedInternship = internships.get(choice - 1);

        if (applicationManager.hasAppliedToInternship(student.getUserId(),
                selectedInternship.getInternshipId())) {
            System.out.println("You have already applied to this internship.");
            pause();
            return;
        }

        String appId = IdGenerator.generateApplicationId();
        boolean success = applicationManager.addApplication(appId, student.getUserId(),
                selectedInternship.getInternshipId());
        if(success) System.out.println("Application submitted successfully!");
        else System.out.println("Application failed. Past closing date.");
        pause();
    }

    /**
     * Allows student to accept a successful application as a confirmed placement.
     * Withdraws all other successful applications automatically.
     */
    private void acceptPlacement() {
        printHeader("ACCEPT PLACEMENT");

        if (student.hasConfirmedPlacement()) {
            System.out.println("You already have a confirmed placement.");
            pause();
            return;
        }

        List<Application> successfulApps = applicationManager.getApplicationsByStudentandStatus(student.getUserId(), ApplicationStatus.SUCCESSFUL);

        if (successfulApps.isEmpty()) {
            System.out.println("You have no successful applications to accept.");
            pause();
            return;
        }

        System.out.println("Successful Applications:");
        for (int i = 0; i < successfulApps.size(); i++) {
            Application app = successfulApps.get(i);
            Internship intern = internshipManager.getInternshipById(app.getInternshipId());
            if (intern == null) {
                System.out.printf("%d. [%s] [INTERNSHIP NOT FOUND]\n", i + 1, app.getApplicationId());
                continue;
            }
            System.out.printf("%d. [%s] %s - %s\n", i + 1, intern.getInternshipId(),
                    intern.getTitle(), intern.getCompanyName());
        }

        int choice = getIntInput("\nSelect placement to accept (0 to cancel): ");
        if (choice < 1 || choice > successfulApps.size()) {
            System.out.println("Cancelled.");
            pause();
            return;
        }

        Application selectedApp = successfulApps.get(choice - 1);
        
        applicationManager.handleApplicationAcceptance(student, selectedApp);

        System.out.println("Placement accepted! Other successful applications have been withdrawn.");
        pause();
    }

    /**
     * Handles student request to withdraw from an application or confirmed placement.
     * Requires staff approval.
     */
    private void requestWithdrawal() {
        printHeader("REQUEST WITHDRAWAL");

        List<Application> pendingApps = applicationManager.getApplicationsByStudentandStatus(student.getUserId(), ApplicationStatus.PENDING);
        List<Application> successfulApps = applicationManager.getApplicationsByStudentandStatus(student.getUserId(), ApplicationStatus.SUCCESSFUL);

        List<Application> applications = new ArrayList<>();
        applications.addAll(pendingApps);
        applications.addAll(successfulApps);


        if (applications.isEmpty()) {
            System.out.println("You have no applications to withdraw.");
            pause();
            return;
        }

        for (int i = 0; i < applications.size(); i++) {
            Application app = applications.get(i);
            Internship intern = internshipManager.getInternshipById(app.getInternshipId());
            if (intern == null) {
                System.out.printf("%d. [%s] [INTERNSHIP NOT FOUND] - Status: %s\n", i + 1, app.getApplicationId(), app.getStatus());
                continue;
            }
            System.out.printf("%d. [%s] %s - Status: %s\n", i + 1, app.getApplicationId(),
                    intern.getTitle(), app.getStatus());
        }

        int choice = getIntInput("\nSelect application to withdraw (0 to cancel): ");
        if (choice < 1 || choice > applications.size()) {
            System.out.println("Cancelled.");
            pause();
            return;
        }

        Application selectedApp = applications.get(choice - 1);

        if (withdrawalManager.hasPendingWithdrawal(selectedApp.getApplicationId())) {
            System.out.println("A withdrawal request is already pending for this application.");
            pause();
            return;
        }

        String reason = getStringInput("Reason for withdrawal: ");
        String requestId = IdGenerator.generateWithdrawalId();
        withdrawalManager.addWithdrawalRequest(requestId, student.getUserId(), selectedApp.getApplicationId(), reason);

        System.out.println("Withdrawal request submitted. Awaiting staff approval.");
        pause();
    }

    /**
     * Displays the student's profile information including confirmed placement if any.
     */
    private void viewProfile() {
        printHeader("MY PROFILE");
        System.out.println("User ID: " + student.getUserId());
        System.out.println("Name: " + student.getName());
        System.out.println("Email: " + student.getEmail());
        System.out.println(student.getProfileInfo());
        if (student.hasConfirmedPlacement()) {
            Internship placement = internshipManager.getInternshipById(student.getConfirmedPlacementId());
            if (placement != null) {
                System.out.println("\nConfirmed Placement: " + placement.getTitle() +
                        " at " + placement.getCompanyName());
            } else {
                System.out.println("\nConfirmed Placement: [INTERNSHIP NOT FOUND]");
            }
        }
        pause();
    }

    /**
     * Handles password change process with validation and attempt limits.
     */
    private void changePassword() {
        printHeader("CHANGE PASSWORD");
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
                    pause();
                    return;
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
        pause();
    }

    /**
     * Allows student to configure filter settings for internship listings.
     */
    private void configureFilters() {
        printHeader("CONFIGURE FILTERS");

        FilterSettings settings = filterManager.getFilterSettings(student.getUserId());

        System.out.println("1. Set Sort Criteria");
        System.out.println("2. Clear All Filters");
        System.out.println("0. Back");

        int choice = getIntInput("\nEnter choice: ");

        switch (choice) {
            case 1:
                System.out.println("1=ALPHABETICAL, 2=OPENING_DATE, 3=CLOSING_DATE, 4=LEVEL");
                int sort = getIntInput("Choice: ");
                if (sort >= 1 && sort <= 4) {
                    settings.setSortBy(FilterSettings.SortCriteria.values()[sort - 1]);
                }
                break;
            case 2:
                settings.setLevelFilter(null);
                settings.setMajorFilter(null);
                settings.setSortBy(FilterSettings.SortCriteria.ALPHABETICAL);
                System.out.println("All filters cleared.");
                break;
        }

        filterManager.updateFilterSettings(settings);
        System.out.println("Filter settings updated.");
        pause();
    }
}
