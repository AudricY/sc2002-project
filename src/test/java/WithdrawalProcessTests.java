import control.*;
import entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import util.TestDataSetup;
import util.TestHelpers;
import util.TestStateManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Withdrawal Process Tests
 * Tests TC-013 and TC-014 from the testing documentation.
 */
@DisplayName("Withdrawal Process Tests")
public class WithdrawalProcessTests {

    private AuthenticationController authController;
    private UserManager userManager;
    private InternshipManager internshipManager;
    private ApplicationManager applicationManager;
    private WithdrawalManager withdrawalManager;

    @BeforeEach
    public void setUp() {
        TestStateManager.resetSystemState();
        TestDataSetup.initializeTestData();

        authController = new AuthenticationController();
        userManager = UserManager.getInstance();
        internshipManager = InternshipManager.getInstance();
        applicationManager = ApplicationManager.getInstance();
        withdrawalManager = WithdrawalManager.getInstance();
    }

    @Test
    @DisplayName("TC-013: Student Requests Withdrawal")
    public void testStudentRequestsWithdrawal() {
        // Setup: Create internship and application
        authController.registerCompanyRepresentative(
            "Jane Doe", "jane.doe@techcorp.com", "password123",
            "TechCorp", "Engineering", "Manager"
        );
        CompanyRepresentative rep = userManager.getPendingRepresentatives().get(0);
        TestHelpers.approveRepresentative(rep.getUserId());

        TestHelpers.createInternship(
            "INT001", "Software Developer Intern", "Description",
            InternshipLevel.BASIC, "Computer Science", "TechCorp", rep.getUserId(), 5
        );
        TestHelpers.approveInternship("INT001");

        // Student applies
        Application application = TestHelpers.createApplication("APP001", "S001", "INT001");

        // Student requests withdrawal
        WithdrawalRequest withdrawalRequest = TestHelpers.createWithdrawalRequest(
            "WR001",
            "S001",
            "APP001",
            "Found alternative opportunity"
        );

        assertNotNull(withdrawalRequest, "Withdrawal request should be created");
        assertEquals("WR001", withdrawalRequest.getRequestId());
        assertEquals("S001", withdrawalRequest.getStudentId());
        assertEquals("APP001", withdrawalRequest.getApplicationId());
        assertEquals("Found alternative opportunity", withdrawalRequest.getReason());
        assertEquals(ApprovalStatus.PENDING, withdrawalRequest.getStatus());

        // Verify request appears in pending queue
        List<WithdrawalRequest> pendingWithdrawals = withdrawalManager.getPendingWithdrawals();
        assertTrue(pendingWithdrawals.stream().anyMatch(w -> w.getRequestId().equals("WR001")),
            "Withdrawal request should appear in pending queue");

        // Verify application remains active until approved
        Application stillActiveApp = applicationManager.getApplicationById("APP001");
        assertNotNull(stillActiveApp, "Application should still exist while withdrawal is pending");
        assertEquals(ApplicationStatus.PENDING, stillActiveApp.getStatus());
    }

    @Test
    @DisplayName("TC-014: Staff Approves Withdrawal")
    public void testStaffApprovesWithdrawal() {
        // Setup: Create internship and application
        authController.registerCompanyRepresentative(
            "Jane Doe", "jane.doe@techcorp.com", "password123",
            "TechCorp", "Engineering", "Manager"
        );
        CompanyRepresentative rep = userManager.getPendingRepresentatives().get(0);
        TestHelpers.approveRepresentative(rep.getUserId());

        TestHelpers.createInternship(
            "INT001", "Software Developer Intern", "Description",
            InternshipLevel.BASIC, "Computer Science", "TechCorp", rep.getUserId(), 5
        );
        TestHelpers.approveInternship("INT001");

        // Student applies, gets approved, and accepts
        Application application = TestHelpers.createApplication("APP001", "S001", "INT001");
        TestHelpers.approveApplication("APP001");
        TestHelpers.acceptPlacement("S001", "APP001");

        // Verify initial state
        Internship internship = internshipManager.getInternshipById("INT001");
        assertEquals(1, internship.getConfirmedSlots(), "Internship should have 1 confirmed slot");

        Student student = (Student) userManager.getUserById("S001");
        assertEquals("APP001", student.getConfirmedPlacementId(), "Student should have confirmed placement");

        // Student requests withdrawal
        WithdrawalRequest withdrawalRequest = TestHelpers.createWithdrawalRequest(
            "WR001",
            "S001",
            "APP001",
            "Personal reasons"
        );

        // Staff approves withdrawal
        withdrawalRequest.setStatus(ApprovalStatus.APPROVED);
        withdrawalRequest.setReviewedByStaffId("STAFF001");
        withdrawalRequest.setReviewDate(LocalDateTime.now());
        withdrawalManager.updateWithdrawalRequest(withdrawalRequest);

        // Simulate withdrawal approval logic
        applicationManager.removeApplication(application);
        internship.decrementConfirmedSlots();
        internshipManager.updateInternship(internship);
        student.setConfirmedPlacementId(null);
        userManager.updateUser(student);

        // Verify withdrawal status
        WithdrawalRequest approvedRequest = withdrawalManager.getWithdrawalById("WR001");
        assertEquals(ApprovalStatus.APPROVED, approvedRequest.getStatus());
        assertEquals("STAFF001", approvedRequest.getReviewedByStaffId());

        // Verify application removed
        Application removedApp = applicationManager.getApplicationById("APP001");
        assertNull(removedApp, "Application should be removed from system");

        // Verify slot count decremented
        Internship updatedInternship = internshipManager.getInternshipById("INT001");
        assertEquals(0, updatedInternship.getConfirmedSlots(),
            "Confirmed slots should be decremented after withdrawal");

        // Verify student's confirmed placement cleared
        Student updatedStudent = (Student) userManager.getUserById("S001");
        assertNull(updatedStudent.getConfirmedPlacementId(),
            "Student's confirmed placement ID should be cleared");
    }

    @Test
    @DisplayName("TC-014 Extended: Staff Approves Withdrawal Before Placement Confirmation")
    public void testStaffApprovesWithdrawalBeforePlacement() {
        // Setup: Create internship and application
        authController.registerCompanyRepresentative(
            "Jane Doe", "jane.doe@techcorp.com", "password123",
            "TechCorp", "Engineering", "Manager"
        );
        CompanyRepresentative rep = userManager.getPendingRepresentatives().get(0);
        TestHelpers.approveRepresentative(rep.getUserId());

        TestHelpers.createInternship(
            "INT001", "Software Developer Intern", "Description",
            InternshipLevel.BASIC, "Computer Science", "TechCorp", rep.getUserId(), 5
        );
        TestHelpers.approveInternship("INT001");

        // Student applies (but does NOT accept placement yet)
        Application application = TestHelpers.createApplication("APP001", "S001", "INT001");
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
        assertFalse(application.isConfirmed());

        // Student requests withdrawal before acceptance
        WithdrawalRequest withdrawalRequest = TestHelpers.createWithdrawalRequest(
            "WR001",
            "S001",
            "APP001",
            "Changed my mind"
        );

        // Staff approves withdrawal
        withdrawalRequest.setStatus(ApprovalStatus.APPROVED);
        withdrawalRequest.setReviewedByStaffId("STAFF001");
        withdrawalManager.updateWithdrawalRequest(withdrawalRequest);

        // Simulate withdrawal approval (no slot decrement needed since not confirmed)
        applicationManager.removeApplication(application);

        // Verify application removed
        Application removedApp = applicationManager.getApplicationById("APP001");
        assertNull(removedApp, "Application should be removed from system");

        // Verify internship slots unchanged (student never confirmed)
        Internship internship = internshipManager.getInternshipById("INT001");
        assertEquals(0, internship.getConfirmedSlots(),
            "Confirmed slots should remain 0 since student never accepted");

        // Verify student has no confirmed placement
        Student student = (Student) userManager.getUserById("S001");
        assertNull(student.getConfirmedPlacementId(),
            "Student should not have confirmed placement");
    }
}
