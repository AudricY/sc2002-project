package util;

import control.*;
import entity.*;

import java.time.LocalDate;

/**
 * Helper methods for common test operations.
 * Provides convenient methods for creating test entities and performing common actions.
 */
public class TestHelpers {

    /**
     * Performs a login operation and returns whether it was successful.
     *
     * @param authController The authentication controller
     * @param userId The user ID to login with
     * @param password The password
     * @return true if login successful, false otherwise
     */
    public static boolean login(AuthenticationController authController, String userId, String password) {
        return authController.login(userId, password);
    }

    /**
     * Creates and registers a new company representative.
     *
     * @param authController The authentication controller
     * @param name Representative name
     * @param email Representative email
     * @param password Representative password
     * @param companyName Company name
     * @param department Department
     * @param position Position
     * @return true if registration successful
     */
    public static boolean registerCompanyRep(AuthenticationController authController,
                                               String name, String email, String password,
                                               String companyName, String department, String position) {
        return authController.registerCompanyRepresentative(name, email, password, companyName, department, position);
    }

    /**
     * Approves a pending company representative by staff.
     *
     * @param repId The representative ID to approve
     * @return The approved representative, or null if not found
     */
    public static CompanyRepresentative approveRepresentative(String repId) {
        UserManager userManager = UserManager.getInstance();
        CompanyRepresentative rep = (CompanyRepresentative) userManager.getUserById(repId);
        if (rep != null) {
            rep.setApprovalStatus(ApprovalStatus.APPROVED);
            userManager.updateUser(rep);
        }
        return rep;
    }

    /**
     * Creates a new internship opportunity.
     *
     * @param internshipId Internship ID
     * @param title Internship title
     * @param description Internship description
     * @param level Internship level
     * @param preferredMajor Preferred major
     * @param companyName Company name
     * @param representativeId Representative ID
     * @param totalSlots Number of slots
     * @return The created internship
     */
    public static Internship createInternship(String internshipId, String title, String description,
                                                InternshipLevel level, String preferredMajor,
                                                String companyName, String representativeId, int totalSlots) {
        LocalDate openingDate = LocalDate.of(2025, 1, 1);
        LocalDate closingDate = LocalDate.of(2025, 12, 31);

        InternshipManager internshipManager = InternshipManager.getInstance();
        internshipManager.addInternship(internshipId, title, description, level, preferredMajor,
                openingDate, closingDate, companyName, representativeId, totalSlots);

        return internshipManager.getInternshipById(internshipId);
    }

    /**
     * Approves a pending internship.
     *
     * @param internshipId The internship ID to approve
     * @return The approved internship, or null if not found
     */
    public static Internship approveInternship(String internshipId) {
        InternshipManager internshipManager = InternshipManager.getInstance();
        Internship internship = internshipManager.getInternshipById(internshipId);
        if (internship != null) {
            internship.setStatus(InternshipStatus.APPROVED);
            internshipManager.updateInternship(internship);
        }
        return internship;
    }

    /**
     * Creates a student application for an internship.
     *
     * @param applicationId Application ID
     * @param studentId Student ID
     * @param internshipId Internship ID
     * @return The created application
     */
    public static Application createApplication(String applicationId, String studentId, String internshipId) {
        ApplicationManager applicationManager = ApplicationManager.getInstance();
        applicationManager.addApplication(applicationId, studentId, internshipId);
        Application application = applicationManager.getApplicationById(applicationId);
        return application;
    }

    /**
     * Approves a student application.
     *
     * @param applicationId The application ID to approve
     * @return The approved application, or null if not found
     */
    public static Application approveApplication(String applicationId) {
        ApplicationManager applicationManager = ApplicationManager.getInstance();
        Application application = applicationManager.getApplicationById(applicationId);
        if (application != null) {
            application.setStatus(ApplicationStatus.SUCCESSFUL);
            applicationManager.updateApplication(application);
        }
        return application;
    }

    /**
     * Student accepts a placement.
     *
     * @param studentId The student ID
     * @param applicationId The application ID to confirm
     * @return true if acceptance successful
     */
    public static boolean acceptPlacement(String studentId, String applicationId) {
        ApplicationManager applicationManager = ApplicationManager.getInstance();
        InternshipManager internshipManager = InternshipManager.getInstance();
        UserManager userManager = UserManager.getInstance();

        Application application = applicationManager.getApplicationById(applicationId);
        if (application == null || !application.getStatus().equals(ApplicationStatus.SUCCESSFUL)) {
            return false;
        }

        // Mark application as confirmed
        application.setStatus(ApplicationStatus.CONFIRMED);
        applicationManager.updateApplication(application);

        // Update student's confirmed placement
        Student student = (Student) userManager.getUserById(studentId);
        student.setConfirmedPlacementId(applicationId);
        userManager.updateUser(student);

        // Increment internship confirmed slots
        Internship internship = internshipManager.getInternshipById(application.getInternshipId());
        internship.incrementConfirmedSlots();
        internshipManager.updateInternship(internship);

        // Mark other successful applications as unsuccessful
        for (Application app : applicationManager.getApplicationsByStudent(studentId)) {
            if (!app.getApplicationId().equals(applicationId) &&
                app.getStatus().equals(ApplicationStatus.SUCCESSFUL)) {
                app.setStatus(ApplicationStatus.UNSUCCESSFUL);
                applicationManager.updateApplication(app);
            }
        }

        return true;
    }

    /**
     * Creates a withdrawal request.
     *
     * @param withdrawalId Withdrawal ID
     * @param studentId Student ID
     * @param applicationId Application ID
     * @param reason Withdrawal reason
     * @return The created withdrawal request
     */
    public static WithdrawalRequest createWithdrawalRequest(String withdrawalId, String studentId,
                                                              String applicationId, String reason) {
        WithdrawalManager withdrawalManager = WithdrawalManager.getInstance();
        withdrawalManager.addWithdrawalRequest(withdrawalId, studentId, applicationId, reason);
        return withdrawalManager.getWithdrawalById(withdrawalId);
    }

    /**
     * Gets a user by ID.
     *
     * @param userId The user ID
     * @return The user, or null if not found
     */
    public static User getUser(String userId) {
        UserManager userManager = UserManager.getInstance();
        return userManager.getUserById(userId);
    }

    /**
     * Gets an internship by ID.
     *
     * @param internshipId The internship ID
     * @return The internship, or null if not found
     */
    public static Internship getInternship(String internshipId) {
        InternshipManager internshipManager = InternshipManager.getInstance();
        return internshipManager.getInternshipById(internshipId);
    }

    /**
     * Gets an application by ID.
     *
     * @param applicationId The application ID
     * @return The application, or null if not found
     */
    public static Application getApplication(String applicationId) {
        ApplicationManager applicationManager = ApplicationManager.getInstance();
        return applicationManager.getApplicationById(applicationId);
    }
}
