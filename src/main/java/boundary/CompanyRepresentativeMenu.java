package boundary;

import control.*;
import entity.*;
import util.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Menu for company representative operations including creating internships and reviewing applications.
 */
public class CompanyRepresentativeMenu extends MenuInterface {
    private AuthenticationController authController;
    private CompanyRepresentative representative;
    private InternshipManager internshipManager;
    private ApplicationManager applicationManager;
    private UserManager userManager;
    private FilterManager filterManager;

    /**
     * Creates a company representative menu.
     *
     * @param scanner scanner for user input
     * @param authController authentication controller
     */
    public CompanyRepresentativeMenu(Scanner scanner, AuthenticationController authController) {
        super(scanner);
        this.authController = authController;
        this.representative = (CompanyRepresentative) authController.getCurrentUser();
        this.internshipManager = InternshipManager.getInstance();
        this.applicationManager = ApplicationManager.getInstance();
        this.userManager = UserManager.getInstance();
        this.filterManager = FilterManager.getInstance();
    }

    @Override
    public void displayMenu() {
        clearScreen();
        printHeader("COMPANY REPRESENTATIVE MENU - " + representative.getName());
        System.out.println("1. Create Internship Opportunity");
        System.out.println("2. View My Internship Opportunities");
        System.out.println("3. Edit Internship Opportunity");
        System.out.println("4. Toggle Internship Visibility");
        System.out.println("5. View Applications for Internship");
        System.out.println("6. Review Application");
        System.out.println("7. View Profile");
        System.out.println("8. Change Password");
        System.out.println("9. Configure Filters");
        System.out.println("0. Logout");
        System.out.print("\nEnter choice: ");
    }

    @Override
    public void handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                createInternship();
                break;
            case 2:
                viewMyInternships();
                break;
            case 3:
                editInternship();
                break;
            case 4:
                toggleVisibility();
                break;
            case 5:
                viewApplicationsForInternship();
                break;
            case 6:
                reviewApplication();
                break;
            case 7:
                viewProfile();
                break;
            case 8:
                changePassword();
                break;
            case 9:
                configureFilters();
            case 0:
                break;
            default:
                System.out.println("Invalid choice.");
                pause();
        }
    }

    /**
     * Handles creation of a new internship opportunity.
     * Enforces the 5 internship limit per representative.
     */
    private void createInternship() {
        printHeader("CREATE INTERNSHIP OPPORTUNITY");

        int count = internshipManager.countInternshipsByRepresentative(representative.getUserId());
        if (count >= 5) {
            System.out.println("You have reached the maximum limit of 5 internships.");
            pause();
            return;
        }

        String title = getStringInput("Internship Title: ");
        String description = getStringInput("Description: ");

        System.out.println("Level: 1=BASIC, 2=INTERMEDIATE, 3=ADVANCED");
        int levelChoice = getIntInput("Select level: ");
        if (levelChoice < 1 || levelChoice > 3) {
            System.out.println("Invalid level.");
            pause();
            return;
        }
        InternshipLevel level = InternshipLevel.values()[levelChoice - 1];

        String preferredMajor = getStringInput("Preferred Major: ");

        LocalDate openingDate = null;
        while (openingDate == null) {
            String dateStr = getStringInput("Opening Date (yyyy-MM-dd): ");
            if (InputValidator.isValidDate(dateStr)) {
                openingDate = DateUtils.parseDate(dateStr);
            } else {
                System.out.println("Invalid date format.");
            }
        }

        LocalDate closingDate = null;
        while (closingDate == null) {
            String dateStr = getStringInput("Closing Date (yyyy-MM-dd): ");
            if (InputValidator.isValidDate(dateStr)) {
                closingDate = DateUtils.parseDate(dateStr);
                if (closingDate.isBefore(openingDate)) {
                    System.out.println("Closing date must be after opening date.");
                    closingDate = null;
                }
            } else {
                System.out.println("Invalid date format.");
            }
        }

        int totalSlots = 0;
        while (totalSlots <= 0 || totalSlots > 10) {
            totalSlots = getIntInput("Number of Slots (1-10): ");
            if (totalSlots <= 0 || totalSlots > 10) {
                System.out.println("Must be between 1 and 10.");
            }
        }
        String internshipId = IdGenerator.generateInternshipId();
        internshipManager.addInternship(internshipId, title, description, level,
                preferredMajor, openingDate, closingDate, representative.getCompanyName(),
                representative.getUserId(), totalSlots);
        System.out.println("Internship opportunity created! ID: " + internshipId);
        System.out.println("Status: PENDING (awaiting Career Center Staff approval)");
        pause();
    }

    /**
     * Displays menu for viewing internships with filtering options.
     */
    private void viewMyInternships() {
        printHeader("CREATED INTERNSHIPS");

        System.out.println("1. All Internships");
        System.out.println("2. Filtered Internships");
        System.out.println("0. Back");

        int choice = getIntInput("\nEnter choice: ");

        switch (choice) {
            case 1:
                viewAllInternships();
                break;
            
            case 2:
                viewFilteredInternships();
        
            default:
                break;
        }
        
    }

    /**
     * Displays all internships created by the representative.
     */
    private void viewAllInternships() {
        clearScreen();
        printHeader("MY INTERNSHIP OPPORTUNITIES");

        List<Internship> internships = internshipManager.getInternshipsByRepresentative(
                representative.getUserId());

        if (internships.isEmpty()) {
            System.out.println("You have not created any internship opportunities.");
        } else {
            for (Internship intern : internships) {
                System.out.printf("\n[%s] %s\n", intern.getInternshipId(), intern.getTitle());
                System.out.printf("Level: %s | Major: %s\n", intern.getLevel(), intern.getPreferredMajor());
                System.out.printf("Status: %s | Visible: %s\n", intern.getStatus(), intern.isVisible());
                System.out.printf("Slots: %d/%d confirmed\n", intern.getConfirmedSlots(), intern.getTotalSlots());
                System.out.printf("Period: %s to %s\n", DateUtils.formatDate(intern.getOpeningDate()), DateUtils.formatDate(intern.getClosingDate()));
            }
        }
        pause();
    }

    /**
     * Displays filtered internships based on user-selected criteria (status, level, or major).
     */
    private void viewFilteredInternships() {
        clearScreen();
        printHeader("FILTERED INTERNSHIPS REPORT");
        
        FilterSettings settings = filterManager.getFilterSettings(representative.getUserId());
        List<Internship> allInternships = internshipManager.getInternshipsByRepresentative(
                representative.getUserId());
        List<Internship> filtered = internshipManager.filterInternships(allInternships, settings);

        System.out.printf("\nFound %d internships:\n", filtered.size());
        for (Internship intern : filtered) {
            System.out.printf("[%s] %s - %s | Level: %s | Major: %s | Status: %s\n",
                    intern.getInternshipId(), intern.getTitle(), intern.getCompanyName(),
                    intern.getLevel(), intern.getPreferredMajor(), intern.getStatus());
        }
        pause();
    }

    /**
     * Allows editing of internship details before approval.
     * Editing is restricted for approved or filled internships.
     */
    private void editInternship() {
        printHeader("EDIT INTERNSHIP OPPORTUNITY");

        FilterSettings settings = filterManager.getFilterSettings(representative.getUserId());
        List<Internship> allInternships = internshipManager.getInternshipsByRepresentative(
                representative.getUserId());
        List<Internship> internships = internshipManager.filterInternships(allInternships, settings);

        if (internships.isEmpty()) {
            System.out.println("You have no internships to edit.");
            pause();
            return;
        }

        for (int i = 0; i < internships.size(); i++) {
            Internship intern = internships.get(i);
            System.out.printf("%d. [%s] %s - Status: %s\n", i + 1,
                    intern.getInternshipId(), intern.getTitle(), intern.getStatus());
        }

        int choice = getIntInput("\nSelect internship to edit (0 to cancel): ");
        if (choice < 1 || choice > internships.size()) {
            System.out.println("Cancelled.");
            pause();
            return;
        }

        Internship selectedInternship = internships.get(choice - 1);

        if (selectedInternship.getStatus() == InternshipStatus.APPROVED ||
                selectedInternship.getStatus() == InternshipStatus.FILLED) {
            System.out.println("Cannot edit approved or filled internships.");
            pause();
            return;
        }

        System.out.println("\nWhat would you like to edit?");
        System.out.println("1. Title");
        System.out.println("2. Description");
        System.out.println("3. Preferred Major");
        System.out.println("0. Cancel");

        int editChoice = getIntInput("Enter choice: ");
        String newValue = null;
        switch (editChoice) {
            case 1:
                newValue = getStringInput("New Title: ");
                break;
            case 2:
                newValue = getStringInput("New Description: ");
                break;
            case 3:
                newValue = getStringInput("New Preferred Major: ");
                break;
            default:
                System.out.println("Cancelled.");
                pause();
                return;
        }

        if (newValue == null || newValue.trim().isEmpty()) {
            System.out.println("Invalid input. Update cancelled.");
            pause();
            return;
        }

        internshipManager.editInternshipField(selectedInternship, editChoice, newValue);
        System.out.println("Internship updated successfully!");
        pause();
    }

    /**
     * Toggles the visibility of an internship opportunity for students.
     */
    private void toggleVisibility() {
        printHeader("TOGGLE INTERNSHIP VISIBILITY");

        FilterSettings settings = filterManager.getFilterSettings(representative.getUserId());
        List<Internship> allInternships = internshipManager.getInternshipsByRepresentative(
                representative.getUserId());
        List<Internship> internships = internshipManager.filterInternships(allInternships, settings);

        if (internships.isEmpty()) {
            System.out.println("You have no internships.");
            pause();
            return;
        }

        for (int i = 0; i < internships.size(); i++) {
            Internship intern = internships.get(i);
            System.out.printf("%d. [%s] %s - Visible: %s\n", i + 1,
                    intern.getInternshipId(), intern.getTitle(), intern.isVisible());
        }

        int choice = getIntInput("\nSelect internship (0 to cancel): ");
        if (choice < 1 || choice > internships.size()) {
            System.out.println("Cancelled.");
            pause();
            return;
        }

        Internship selectedInternship = internships.get(choice - 1);
        internshipManager.toggleInternshipVisibility(selectedInternship);

        System.out.println("Visibility toggled. Now: " + (selectedInternship.isVisible() ? "VISIBLE" : "HIDDEN"));
        pause();
    }

    /**
     * Displays all applications received for a selected internship opportunity.
     */
    private void viewApplicationsForInternship() {
        printHeader("VIEW APPLICATIONS");

        FilterSettings settings = filterManager.getFilterSettings(representative.getUserId());
        List<Internship> allInternships = internshipManager.getInternshipsByRepresentative(
                representative.getUserId());
        List<Internship> internships = internshipManager.filterInternships(allInternships, settings);

        if (internships.isEmpty()) {
            System.out.println("You have no internships.");
            pause();
            return;
        }

        for (int i = 0; i < internships.size(); i++) {
            Internship intern = internships.get(i);
            int appCount = applicationManager.getApplicationsByInternship(intern.getInternshipId()).size();
            System.out.printf("%d. [%s] %s - %d applications\n", i + 1,
                    intern.getInternshipId(), intern.getTitle(), appCount);
        }

        int choice = getIntInput("\nSelect internship (0 to cancel): ");
        if (choice < 1 || choice > internships.size()) {
            System.out.println("Cancelled.");
            pause();
            return;
        }

        Internship selectedInternship = internships.get(choice - 1);
        List<Application> applications = applicationManager.getApplicationsByInternship(
                selectedInternship.getInternshipId());

        if (applications.isEmpty()) {
            System.out.println("No applications for this internship.");
        } else {
            System.out.println("\nApplications:");
            for (Application app : applications) {
                Student student = (Student) userManager.getUserById(app.getStudentId());
                if (student == null) {
                    System.out.printf("\n[%s] [STUDENT NOT FOUND]\n", app.getApplicationId());
                    System.out.printf("Status: %s | Applied: %s\n", app.getStatus(), app.getApplicationDate());
                    continue;
                }
                System.out.printf("\n[%s] %s\n", app.getApplicationId(), student.getName());
                System.out.printf("Student ID: %s | %s\n", student.getUserId(), student.getProfileInfo());
                System.out.printf("Status: %s | Applied: %s\n", app.getStatus(), app.getApplicationDate());
                if (app.getStatus().equals(ApplicationStatus.CONFIRMED)) {
                    System.out.println("*** CONFIRMED ***");
                }
            }
        }
        pause();
    }

    /**
     * Handles the review and approval/rejection of student applications.
     */
    private void reviewApplication() {
        printHeader("REVIEW APPLICATION");

        FilterSettings settings = filterManager.getFilterSettings(representative.getUserId());
        List<Internship> allInternships = internshipManager.getInternshipsByRepresentative(
                representative.getUserId());
        List<Internship> internships = internshipManager.filterInternships(allInternships, settings);

        if (internships.isEmpty()) {
            System.out.println("You have no internships.");
            pause();
            return;
        }

        for (int i = 0; i < internships.size(); i++) {
            Internship intern = internships.get(i);
            System.out.printf("%d. [%s] %s\n", i + 1, intern.getInternshipId(), intern.getTitle());
        }

        int choice = getIntInput("\nSelect internship (0 to cancel): ");
        if (choice < 1 || choice > internships.size()) {
            System.out.println("Cancelled.");
            pause();
            return;
        }

        Internship selectedInternship = internships.get(choice - 1);
        List<Application> pendingApps = applicationManager.getApplicationsByInternshipandStatus(selectedInternship.getInternshipId(), ApplicationStatus.PENDING);

        if (pendingApps.isEmpty()) {
            System.out.println("No pending applications for this internship.");
            pause();
            return;
        }

        for (int i = 0; i < pendingApps.size(); i++) {
            Application app = pendingApps.get(i);
            Student student = (Student) userManager.getUserById(app.getStudentId());
            System.out.printf("%d. [%s] %s - %s\n", i + 1, app.getApplicationId(),
                    student.getName(), student.getProfileInfo());
        }

        int appChoice = getIntInput("\nSelect application (0 to cancel): ");
        if (appChoice < 1 || appChoice > pendingApps.size()) {
            System.out.println("Cancelled.");
            pause();
            return;
        }

        Application selectedApp = pendingApps.get(appChoice - 1);
        System.out.println("\n1. Approve");
        System.out.println("2. Reject");
        int decision = getIntInput("Enter choice: ");

        applicationManager.reviewApplication(selectedApp, decision);
        if (decision == 1) {
            System.out.println("Application approved! Student can now accept this placement.");
        } else if (decision == 2) {
            System.out.println("Application rejected.");
        } else {
            System.out.println("Invalid choice.");
        }
        pause();
    }

    /**
     * Displays the company representative's profile information.
     */
    private void viewProfile() {
        printHeader("MY PROFILE");
        System.out.println("User ID: " + representative.getUserId());
        System.out.println("Name: " + representative.getName());
        System.out.println("Email: " + representative.getEmail());
        System.out.println(representative.getProfileInfo());
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

    private void configureFilters() {
        printHeader("CONFIGURE FILTERS");

        FilterSettings settings = filterManager.getFilterSettings(representative.getUserId());

        System.out.println("1. Set Level Filter");
        System.out.println("2. Set Major Filter");
        System.out.println("3. Set Sort Criteria");
        System.out.println("4. Clear All Filters");
        System.out.println("0. Back");

        int choice = getIntInput("\nEnter choice: ");

        switch (choice) {
            case 0:
                pause();
                return;
            case 1:
                System.out.println("Select Level: 1=BASIC, 2=INTERMEDIATE, 3=ADVANCED, 0=NONE");
                int level = getIntInput("Choice: ");
                if (level == 0) {
                    settings.setLevelFilter(null);
                } else if (level >= 1 && level <= 3) {
                    settings.setLevelFilter(InternshipLevel.values()[level - 1]);
                }
                break;
            case 2:
                String major = getStringInput("Enter preferred major (or leave blank): ");
                settings.setMajorFilter(major.isEmpty() ? null : major);
                break;
            case 3:
                System.out.println("1=ALPHABETICAL, 2=OPENING_DATE, 3=CLOSING_DATE, 4=LEVEL");
                int sort = getIntInput("Choice: ");
                if (sort >= 1 && sort <= 4) {
                    settings.setSortBy(FilterSettings.SortCriteria.values()[sort - 1]);
                }
                break;
            case 4:
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
