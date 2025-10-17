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
 * User Management Tests
 * Tests TC-004 and TC-021 from the testing documentation.
 */
@DisplayName("User Management Tests")
public class UserManagementTests {

    private AuthenticationController authController;
    private UserManager userManager;

    @BeforeEach
    public void setUp() {
        TestStateManager.resetSystemState();
        TestDataSetup.initializeTestData();

        authController = new AuthenticationController();
        userManager = UserManager.getInstance();
    }

    @Test
    @DisplayName("TC-004: Company Representative Registration")
    public void testCompanyRepresentativeRegistration() {
        // Register a new company representative
        boolean registrationSuccess = authController.registerCompanyRepresentative(
            "John Smith",
            "john.smith@techcorp.com",
            "password123",
            "TechCorp",
            "HR",
            "Recruiter"
        );

        assertTrue(registrationSuccess, "Registration should succeed");

        // Find the newly registered representative
        CompanyRepresentative newRep = userManager.getPendingRepresentatives().stream()
            .filter(r -> r.getEmail().equals("john.smith@techcorp.com"))
            .findFirst()
            .orElse(null);

        assertNotNull(newRep, "Representative should be created and in pending list");
        assertEquals("John Smith", newRep.getName());
        assertEquals("john.smith@techcorp.com", newRep.getEmail());
        assertEquals("TechCorp", newRep.getCompanyName());
        assertEquals("HR", newRep.getDepartment());
        assertEquals("Recruiter", newRep.getPosition());
        assertEquals(ApprovalStatus.PENDING, newRep.getApprovalStatus());

        // Verify login fails before approval
        boolean loginBeforeApproval = authController.login(newRep.getUserId(), "password123");
        assertFalse(loginBeforeApproval, "Login should fail for pending representative");

        // Approve the representative
        newRep.setApprovalStatus(ApprovalStatus.APPROVED);
        userManager.updateUser(newRep);

        // Verify login succeeds after approval
        boolean loginAfterApproval = authController.login(newRep.getUserId(), "password123");
        assertTrue(loginAfterApproval, "Login should succeed for approved representative");
    }

    @Test
    @DisplayName("TC-021: Password Change Functionality")
    public void testPasswordChangeFunctionality() {
        // Login as a student
        boolean loginSuccess = authController.login("S001", "password");
        assertTrue(loginSuccess, "Initial login should succeed");

        User user = authController.getCurrentUser();
        assertNotNull(user, "Current user should be set");

        // Change password
        boolean changeSuccess = authController.changePassword("password", "newSecurePassword456");
        assertTrue(changeSuccess, "Password change should succeed");

        // Verify password was changed in the user object
        assertEquals("newSecurePassword456", user.getPassword());

        // Logout and try to login with old password
        authController.logout();
        boolean oldPasswordLogin = authController.login("S001", "password");
        assertFalse(oldPasswordLogin, "Old password should not work");

        // Login with new password
        boolean newPasswordLogin = authController.login("S001", "newSecurePassword456");
        assertTrue(newPasswordLogin, "New password should work");

        // Verify user is correctly authenticated
        User currentUser = authController.getCurrentUser();
        assertNotNull(currentUser);
        assertEquals("S001", currentUser.getUserId());

        // Test password change with incorrect old password
        boolean incorrectOldPassword = authController.changePassword("wrongPassword", "anotherPassword");
        assertFalse(incorrectOldPassword, "Password change should fail with incorrect old password");

        // Verify password wasn't changed
        authController.logout();
        boolean verifyPasswordUnchanged = authController.login("S001", "newSecurePassword456");
        assertTrue(verifyPasswordUnchanged, "Password should remain unchanged after failed change attempt");
    }
}
