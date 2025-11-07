package boundary;

import control.*;
import entity.*;
import util.DateUtils;
import util.PasswordChangeResult;

import java.util.*;

/**
 * Menu for career center staff operations including approvals, reviews, and report generation.
 */
public class CareerCenterStaffMenu extends MenuInterface {
    private AuthenticationController authController;
    private CareerCenterStaff staff;
    private UserManager userManager;
    private InternshipManager internshipManager;
    private ApplicationManager applicationManager;
    private WithdrawalManager withdrawalManager;
    private FilterManager filterManager;

    /**
     * Creates a career center staff menu.
     *
     * @param scanner scanner for user input
     * @param authController authentication controller
     */
    public CareerCenterStaffMenu(Scanner scanner, AuthenticationController authController) {
        super(scanner);
        this.authController = authController;
        this.staff = (CareerCenterStaff) authController.getCurrentUser();
        this.userManager = UserManager.getInstance();
        this.internshipManager = InternshipManager.getInstance();
        this.applicationManager = ApplicationManager.getInstance();
        this.withdrawalManager = WithdrawalManager.getInstance();
        this.filterManager = FilterManager.getInstance();
    }

    @Override
    public void displayMenu() {
        clearScreen();
        printHeader("CAREER CENTER STAFF MENU - " + staff.getName());
        System.out.println("1. Review Company Representative Registrations");
        System.out.println("2. Review Internship Opportunities");
        System.out.println("3. Review Withdrawal Requests");
        System.out.println("4. Generate Reports");
        System.out.println("5. View All Students");
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
                reviewRepresentatives();
                break;
            case 2:
                reviewInternships();
                break;
            case 3:
                reviewWithdrawals();
                break;
            case 4:
                generateReports();
                break;
            case 5:
                viewAllStudents();
                break;
            case 6:
                viewProfile();
                break;
            case 7:
                changePassword();
                break;
            case 8:
                configureFilters();
            case 0:
                break;
            default:
                System.out.println("Invalid choice.");
                pause();
        }
    }

    /**
     * Handles review and approval/rejection of company representative registrations.
     */
    private void reviewRepresentatives() {
        printHeader("REVIEW COMPANY REPRESENTATIVE REGISTRATIONS");

        List<CompanyRepresentative> pendingReps = userManager.getPendingRepresentatives();

        if (pendingReps.isEmpty()) {
            System.out.println("No pending registrations.");
            pause();
            return;
        }

        for (int i = 0; i < pendingReps.size(); i++) {
            CompanyRepresentative rep = pendingReps.get(i);
            System.out.printf("\n%d. [%s] %s\n", i + 1, rep.getUserId(), rep.getName());
            System.out.printf("   Email: %s\n", rep.getEmail());
            System.out.printf("   %s\n", rep.getProfileInfo());
        }

        int choice = getIntInput("\nSelect representative to review (0 to cancel): ");
        if (choice < 1 || choice > pendingReps.size()) {
            System.out.println("Cancelled.");
            pause();
            return;
        }

        CompanyRepresentative selectedRep = pendingReps.get(choice - 1);
        System.out.println("\n1. Approve");
        System.out.println("2. Reject");
        int decision = getIntInput("Enter choice: ");
        userManager.reviewRepresentative(selectedRep, decision);
        if (decision == 1) {
            System.out.println("Representative approved!");
        } else if (decision == 2) {
            System.out.println("Representative rejected.");
        } else {
            System.out.println("Invalid choice.");
        }
        pause();
    }

    /**
     * Handles review and approval/rejection of internship opportunities submitted by representatives.
     */
    private void reviewInternships() {
        printHeader("REVIEW INTERNSHIP OPPORTUNITIES");

        List<Internship> allInternships = internshipManager.getPendingInternships();
        FilterSettings settings = filterManager.getFilterSettings(staff.getUserId());
        List<Internship> pendingInternships = internshipManager.filterInternships(allInternships, settings);

        if (pendingInternships.isEmpty()) {
            System.out.println("No pending internship opportunities.");
            pause();
            return;
        }

        for (int i = 0; i < pendingInternships.size(); i++) {
            Internship intern = pendingInternships.get(i);
            System.out.printf("\n%d. [%s] %s\n", i + 1, intern.getInternshipId(), intern.getTitle());
            System.out.printf("   Company: %s\n", intern.getCompanyName());
            System.out.printf("   Level: %s | Major: %s | Slots: %d\n",
                    intern.getLevel(), intern.getPreferredMajor(), intern.getTotalSlots());
            System.out.printf("   Period: %s to %s\n", DateUtils.formatDate(intern.getOpeningDate()), DateUtils.formatDate(intern.getClosingDate()));
            System.out.printf("   Description: %s\n", intern.getDescription());
        }

        int choice = getIntInput("\nSelect internship to review (0 to cancel): ");
        if (choice < 1 || choice > pendingInternships.size()) {
            System.out.println("Cancelled.");
            pause();
            return;
        }

        Internship selectedInternship = pendingInternships.get(choice - 1);
        System.out.println("\n1. Approve");
        System.out.println("2. Reject");
        int decision = getIntInput("Enter choice: ");
        internshipManager.reviewInternship(selectedInternship, decision);
        if (decision == 1) {
            System.out.println("Internship approved! Now visible to eligible students.");
        } else if (decision == 2) {
            System.out.println("Internship rejected.");
        } else {
            System.out.println("Invalid choice.");
        }
        pause();
    }

    /**
     * Handles review and approval/rejection of student withdrawal requests.
     */
    private void reviewWithdrawals() {
        printHeader("REVIEW WITHDRAWAL REQUESTS");

        List<WithdrawalRequest> pendingWithdrawals = withdrawalManager.getPendingWithdrawals();

        if (pendingWithdrawals.isEmpty()) {
            System.out.println("No pending withdrawal requests.");
            pause();
            return;
        }

        for (int i = 0; i < pendingWithdrawals.size(); i++) {
            WithdrawalRequest request = pendingWithdrawals.get(i);
            Application app = applicationManager.getApplicationById(request.getApplicationId());
            if (app == null) {
                System.out.printf("\n%d. [%s] Withdrawal Request - [APPLICATION NOT FOUND]\n", i + 1, request.getRequestId());
                continue;
            }
            Student student = (Student) userManager.getUserById(request.getStudentId());
            if (student == null) {
                System.out.printf("\n%d. [%s] Withdrawal Request - [STUDENT NOT FOUND]\n", i + 1, request.getRequestId());
                continue;
            }
            Internship intern = internshipManager.getInternshipById(app.getInternshipId());
            if (intern == null) {
                System.out.printf("\n%d. [%s] Withdrawal Request\n", i + 1, request.getRequestId());
                System.out.printf("   Student: %s (%s)\n", student.getName(), student.getUserId());
                System.out.printf("   Internship: [NOT FOUND]\n");
                System.out.printf("   Application Status: %s\n", app.getStatus());
                System.out.printf("   Reason: %s\n", request.getReason());
                System.out.printf("   Requested: %s\n", request.getRequestDate());
                continue;
            }

            System.out.printf("\n%d. [%s] Withdrawal Request\n", i + 1, request.getRequestId());
            System.out.printf("   Student: %s (%s)\n", student.getName(), student.getUserId());
            System.out.printf("   Internship: %s - %s\n", intern.getTitle(), intern.getCompanyName());
            System.out.printf("   Application Status: %s\n", app.getStatus());
            System.out.printf("   Reason: %s\n", request.getReason());
            System.out.printf("   Requested: %s\n", request.getRequestDate());
        }

        int choice = getIntInput("\nSelect request to review (0 to cancel): ");
        if (choice < 1 || choice > pendingWithdrawals.size()) {
            System.out.println("Cancelled.");
            pause();
            return;
        }

        WithdrawalRequest selectedRequest = pendingWithdrawals.get(choice - 1);
        System.out.println("\n1. Approve");
        System.out.println("2. Reject");
        int decision = getIntInput("Enter choice: ");
        withdrawalManager.reviewWithdrawal(selectedRequest, staff.getUserId(), decision);
        if (decision == 1) {
            System.out.println("Withdrawal approved. Application removed.");
        } else if (decision == 2) {
            System.out.println("Withdrawal rejected.");
        } else {
            System.out.println("Invalid choice.");
        }
        pause();
    }

    /**
     * Displays menu for generating various reports.
     */
    private void generateReports() {
        printHeader("GENERATE REPORTS");

        System.out.println("1. All Internships Report");
        System.out.println("2. Filtered Internships Report");
        System.out.println("3. Student Applications Summary");
        System.out.println("0. Back");

        int choice = getIntInput("\nEnter choice: ");

        switch (choice) {
            case 1:
                generateAllInternshipsReport();
                break;
            case 2:
                generateFilteredInternshipsReport();
                break;
            case 3:
                generateStudentApplicationsSummary();
                break;
        }
    }

    /**
     * Generates a report of all internships grouped by status.
     */
    private void generateAllInternshipsReport() {
        printHeader("ALL INTERNSHIPS REPORT");
        Map<InternshipStatus, List<Internship>> groupedInternships = internshipManager.getAllInternships(Internship::getStatus);
        if (groupedInternships.isEmpty()) {
            System.out.println("No internships available.");
            pause();
            return;
        }

        System.out.println("Loading all internships...");
        for (Map.Entry<InternshipStatus, List<Internship>> entry : groupedInternships.entrySet()) {
            InternshipStatus status = entry.getKey();
            List<Internship> list = entry.getValue();
            System.out.printf("\n--- %s: %d ---\n", status, list.size());

            for (Internship intern : list) {
                System.out.printf("[%s] %s - %s | Slots: %d/%d\n",
                        intern.getInternshipId(),
                        intern.getTitle(),
                        intern.getCompanyName(),
                        intern.getConfirmedSlots(),
                        intern.getTotalSlots());
            }
        }

        pause();
    }

    /**
     * Generates a filtered report of internships based on selected criteria.
     */
    private void generateFilteredInternshipsReport() {
        printHeader("FILTERED INTERNSHIPS REPORT");

        List<Internship> allInternships = internshipManager.getAllInternships();
        FilterSettings settings = filterManager.getFilterSettings(staff.getUserId());
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
     * Generates a summary report of all student applications and their statuses.
     */
    private void generateStudentApplicationsSummary() {
        printHeader("STUDENT APPLICATIONS SUMMARY");

        List<Student> students = userManager.getAllStudents();

        System.out.printf("Total Students: %d\n\n", students.size());

        for (Student student : students) {
            List<Application> apps = applicationManager.getApplicationsByStudent(student.getUserId());
            if (!apps.isEmpty()) {
                System.out.printf("[%s] %s - Year %d, %s\n",
                        student.getUserId(), student.getName(),
                        student.getYearOfStudy(), student.getMajor());
                System.out.printf("  Applications: %d | Confirmed Placement: %s\n",
                        apps.size(), student.hasConfirmedPlacement() ? "YES" : "NO");

                long pending = applicationManager.getApplicationCount(apps, ApplicationStatus.PENDING);
                long successful = applicationManager.getApplicationCount(apps, ApplicationStatus.SUCCESSFUL);
                long unsuccessful = applicationManager.getApplicationCount(apps, ApplicationStatus.UNSUCCESSFUL);

                System.out.printf("  Pending: %d | Successful: %d | Unsuccessful: %d\n",
                        pending, successful, unsuccessful);
            }
        }
        pause();
    }

    /**
     * Displays a list of all students in the system.
     */
    private void viewAllStudents() {
        printHeader("ALL STUDENTS");

        List<Student> students = userManager.getAllStudents();
        System.out.printf("Total: %d students\n\n", students.size());

        for (Student student : students) {
            System.out.printf("[%s] %s - %s\n", student.getUserId(), student.getName(),
                    student.getProfileInfo());
        }
        pause();
    }

    /**
     * Displays the career center staff member's profile information.
     */
    private void viewProfile() {
        printHeader("MY PROFILE");
        System.out.println("User ID: " + staff.getUserId());
        System.out.println("Name: " + staff.getName());
        System.out.println("Email: " + staff.getEmail());
        System.out.println(staff.getProfileInfo());
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

        FilterSettings settings = filterManager.getFilterSettings(staff.getUserId());

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
