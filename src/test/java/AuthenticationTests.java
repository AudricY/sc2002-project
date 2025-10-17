import control.AuthenticationController;
import control.UserManager;
import entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import util.TestDataSetup;
import util.TestHelpers;
import util.TestStateManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Authentication and User Management Tests
 * Tests TC-001, TC-002, TC-003 from the testing documentation.
 */
@DisplayName("Authentication Tests")
public class AuthenticationTests {

    private AuthenticationController authController;
    private UserManager userManager;

    @BeforeEach
    public void setUp() {
        // Reset system state before each test
        TestStateManager.resetSystemState();
        TestDataSetup.initializeTestData();

        authController = new AuthenticationController();
        userManager = UserManager.getInstance();
    }

    @Test
    @DisplayName("TC-001: System Initialization")
    public void testSystemInitialization() {
        // Verify data directory exists
        assertNotNull(userManager, "UserManager should be initialized");

        // Verify students were loaded from CSV
        Student student = (Student) userManager.getUserById("S001");
        assertNotNull(student, "Student S001 should be loaded from CSV");
        assertEquals("Alice Tan", student.getName());
        assertEquals(2, student.getYearOfStudy());
        assertEquals("Computer Science", student.getMajor());

        // Verify staff were loaded from CSV
        CareerCenterStaff staff = (CareerCenterStaff) userManager.getUserById("STAFF001");
        assertNotNull(staff, "Staff STAFF001 should be loaded from CSV");
        assertEquals("Michael Brown", staff.getName());
        assertEquals("Career Services", staff.getStaffDepartment());

        // Verify default passwords
        assertEquals("password", student.getPassword());
        assertEquals("password", staff.getPassword());

        // Verify first login flag
        assertTrue(student.isFirstLogin(), "Students should have first login flag set");
        assertTrue(staff.isFirstLogin(), "Staff should have first login flag set");
    }

    @Test
    @DisplayName("TC-002: Student Login with Default Password")
    public void testStudentLoginWithDefaultPassword() {
        // Attempt login with default credentials
        boolean loginSuccess = authController.login("S001", "password");
        assertTrue(loginSuccess, "Login should succeed with default password");

        // Verify current user is set
        User currentUser = authController.getCurrentUser();
        assertNotNull(currentUser, "Current user should be set after login");
        assertEquals("S001", currentUser.getUserId());
        assertTrue(currentUser instanceof Student, "Current user should be a Student");

        // Verify first login flag
        assertTrue(currentUser.isFirstLogin(), "First login flag should be true");

        // Simulate password change on first login
        boolean passwordChangeSuccess = authController.changePassword("password", "newPassword123");
        assertTrue(passwordChangeSuccess, "Password change should succeed");

        // Verify first login flag is cleared
        assertFalse(currentUser.isFirstLogin(), "First login flag should be false after password change");

        // Verify old password no longer works
        authController.logout();
        boolean oldPasswordLogin = authController.login("S001", "password");
        assertFalse(oldPasswordLogin, "Old password should not work");

        // Verify new password works
        boolean newPasswordLogin = authController.login("S001", "newPassword123");
        assertTrue(newPasswordLogin, "New password should work");
    }

    @Test
    @DisplayName("TC-003: Staff Login and Representative Approval")
    public void testStaffLoginAndRepresentativeApproval() {
        // Staff logs in
        boolean staffLogin = authController.login("STAFF001", "password");
        assertTrue(staffLogin, "Staff login should succeed");

        User staff = authController.getCurrentUser();
        assertTrue(staff instanceof CareerCenterStaff, "Current user should be CareerCenterStaff");

        // Register a company representative
        authController.logout();
        boolean registerSuccess = TestHelpers.registerCompanyRep(
            authController,
            "John Smith",
            "john.smith@techcorp.com",
            "password123",
            "TechCorp",
            "HR",
            "Recruiter"
        );
        assertTrue(registerSuccess, "Company representative registration should succeed");

        // Find the newly registered representative
        CompanyRepresentative newRep = userManager.getPendingRepresentatives().stream()
            .filter(r -> r.getEmail().equals("john.smith@techcorp.com"))
            .findFirst()
            .orElse(null);

        assertNotNull(newRep, "New representative should be in pending list");
        assertEquals(ApprovalStatus.PENDING, newRep.getApprovalStatus());

        // Verify representative cannot login before approval
        boolean unapprovedLogin = authController.login(newRep.getUserId(), "password123");
        assertFalse(unapprovedLogin, "Unapproved representative should not be able to login");

        // Staff approves the representative
        newRep.setApprovalStatus(ApprovalStatus.APPROVED);
        userManager.updateUser(newRep);

        // Verify representative can now login
        boolean approvedLogin = authController.login(newRep.getUserId(), "password123");
        assertTrue(approvedLogin, "Approved representative should be able to login");

        User currentUser = authController.getCurrentUser();
        assertTrue(currentUser instanceof CompanyRepresentative, "Current user should be CompanyRepresentative");
        assertEquals(ApprovalStatus.APPROVED, ((CompanyRepresentative) currentUser).getApprovalStatus());
    }
}
