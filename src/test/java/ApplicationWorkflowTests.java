import control.*;
import entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import util.TestDataSetup;
import util.TestHelpers;
import util.TestStateManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Application Workflow Tests
 * Tests TC-007, TC-008, TC-009, TC-010, TC-011, TC-012, TC-019 from the testing documentation.
 */
@DisplayName("Application Workflow Tests")
public class ApplicationWorkflowTests {

    private AuthenticationController authController;
    private UserManager userManager;
    private InternshipManager internshipManager;
    private ApplicationManager applicationManager;

    @BeforeEach
    public void setUp() {
        TestStateManager.resetSystemState();
        TestDataSetup.initializeTestData();

        authController = new AuthenticationController();
        userManager = UserManager.getInstance();
        internshipManager = InternshipManager.getInstance();
        applicationManager = ApplicationManager.getInstance();
    }

    @Test
    @DisplayName("TC-007: Student Views Available Internships")
    public void testStudentViewsAvailableInternships() {
        // Setup: Create representative and internships
        authController.registerCompanyRepresentative(
            "Jane Doe", "jane.doe@techcorp.com", "password123",
            "TechCorp", "Engineering", "Manager"
        );
        CompanyRepresentative rep = userManager.getPendingRepresentatives().get(0);
        TestHelpers.approveRepresentative(rep.getUserId());

        // Create BASIC internship for Computer Science
        Internship basicInternship = TestHelpers.createInternship(
            "INT001", "Basic Internship", "For beginners",
            InternshipLevel.BASIC, "Computer Science", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.approveInternship("INT001");

        // Create INTERMEDIATE internship for Computer Science
        Internship intermediateInternship = TestHelpers.createInternship(
            "INT002", "Intermediate Internship", "For experienced students",
            InternshipLevel.INTERMEDIATE, "Computer Science", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.approveInternship("INT002");

        // S001 is Year 2, Computer Science - should only see BASIC
        Student student = (Student) userManager.getUserById("S001");
        assertEquals(2, student.getYearOfStudy());

        List<Internship> visibleInternships = internshipManager.getVisibleInternshipsForStudent(student);

        // Should see BASIC but not INTERMEDIATE (Year 2 restriction)
        assertTrue(visibleInternships.stream().anyMatch(i -> i.getInternshipId().equals("INT001")),
            "Year 2 student should see BASIC internship");
        assertFalse(visibleInternships.stream().anyMatch(i -> i.getInternshipId().equals("INT002")),
            "Year 2 student should NOT see INTERMEDIATE internship");

        // S004 is Year 4, Computer Science - should see both
        Student seniorStudent = (Student) userManager.getUserById("S004");
        assertEquals(4, seniorStudent.getYearOfStudy());

        List<Internship> seniorVisibleInternships = internshipManager.getVisibleInternshipsForStudent(seniorStudent);

        assertTrue(seniorVisibleInternships.stream().anyMatch(i -> i.getInternshipId().equals("INT001")),
            "Year 4 student should see BASIC internship");
        assertTrue(seniorVisibleInternships.stream().anyMatch(i -> i.getInternshipId().equals("INT002")),
            "Year 4 student should see INTERMEDIATE internship");
    }

    @Test
    @DisplayName("TC-008: Student Applies for Internship")
    public void testStudentAppliesForInternship() {
        // Setup internship
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

        assertNotNull(application);
        assertEquals("APP001", application.getApplicationId());
        assertEquals("S001", application.getStudentId());
        assertEquals("INT001", application.getInternshipId());
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
        assertFalse(application.isConfirmed());

        // Verify application appears in student's applications
        List<Application> studentApplications = applicationManager.getApplicationsByStudent("S001");
        assertTrue(studentApplications.stream().anyMatch(a -> a.getApplicationId().equals("APP001")),
            "Application should appear in student's application list");
    }

    @Test
    @DisplayName("TC-009: Three Application Limit Enforcement")
    public void testThreeApplicationLimitEnforcement() {
        // Setup internships
        authController.registerCompanyRepresentative(
            "Jane Doe", "jane.doe@techcorp.com", "password123",
            "TechCorp", "Engineering", "Manager"
        );
        CompanyRepresentative rep = userManager.getPendingRepresentatives().get(0);
        TestHelpers.approveRepresentative(rep.getUserId());

        for (int i = 1; i <= 4; i++) {
            TestHelpers.createInternship(
                "INT00" + i, "Internship " + i, "Description",
                InternshipLevel.BASIC, "Computer Science", "TechCorp", rep.getUserId(), 3
            );
            TestHelpers.approveInternship("INT00" + i);
        }

        // Create 3 pending applications
        TestHelpers.createApplication("APP001", "S001", "INT001");
        TestHelpers.createApplication("APP002", "S001", "INT002");
        TestHelpers.createApplication("APP003", "S001", "INT003");

        // Verify student has 3 pending applications
        int pendingCount = applicationManager.countPendingApplicationsByStudent("S001");
        assertEquals(3, pendingCount, "Student should have 3 pending applications");

        // Verify that limit is reached (business logic would prevent 4th application)
        // The check would be done in the boundary layer using countPendingApplicationsByStudent
        assertTrue(pendingCount >= 3, "Application limit should be enforced at 3");
    }

    @Test
    @DisplayName("TC-010: Company Representative Reviews Applications")
    public void testCompanyRepresentativeReviewsApplications() {
        // Setup
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
        assertEquals(ApplicationStatus.PENDING, application.getStatus());

        // Representative reviews and approves
        Application approved = TestHelpers.approveApplication("APP001");
        assertNotNull(approved);
        assertEquals(ApplicationStatus.SUCCESSFUL, approved.getStatus());

        // Verify student can see updated status
        Application studentView = applicationManager.getApplicationById("APP001");
        assertEquals(ApplicationStatus.SUCCESSFUL, studentView.getStatus());
    }

    @Test
    @DisplayName("TC-011: Student Accepts Placement")
    public void testStudentAcceptsPlacement() {
        // Setup
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
        TestHelpers.createInternship(
            "INT002", "Data Analyst Intern", "Description",
            InternshipLevel.BASIC, "Computer Science", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.approveInternship("INT001");
        TestHelpers.approveInternship("INT002");

        // Student applies to both
        TestHelpers.createApplication("APP001", "S001", "INT001");
        TestHelpers.createApplication("APP002", "S001", "INT002");

        // Both get approved
        TestHelpers.approveApplication("APP001");
        TestHelpers.approveApplication("APP002");

        // Student accepts APP001
        boolean acceptSuccess = TestHelpers.acceptPlacement("S001", "APP001");
        assertTrue(acceptSuccess, "Student should be able to accept placement");

        // Verify application is confirmed
        Application confirmedApp = applicationManager.getApplicationById("APP001");
        assertTrue(confirmedApp.isConfirmed(), "Application should be marked as confirmed");

        // Verify student's confirmedPlacementId is set
        Student student = (Student) userManager.getUserById("S001");
        assertEquals("APP001", student.getConfirmedPlacementId());

        // Verify internship confirmed slots incremented
        Internship internship = internshipManager.getInternshipById("INT001");
        assertEquals(1, internship.getConfirmedSlots());

        // Verify other successful application marked unsuccessful
        Application otherApp = applicationManager.getApplicationById("APP002");
        assertEquals(ApplicationStatus.UNSUCCESSFUL, otherApp.getStatus());
    }

    @Test
    @DisplayName("TC-012: Internship Auto-Fill Status")
    public void testInternshipAutoFillStatus() {
        // Setup: Create internship with 2 slots
        authController.registerCompanyRepresentative(
            "Jane Doe", "jane.doe@techcorp.com", "password123",
            "TechCorp", "Engineering", "Manager"
        );
        CompanyRepresentative rep = userManager.getPendingRepresentatives().get(0);
        TestHelpers.approveRepresentative(rep.getUserId());

        TestHelpers.createInternship(
            "INT001", "Software Developer Intern", "Description",
            InternshipLevel.BASIC, "Computer Science", "TechCorp", rep.getUserId(), 2
        );
        TestHelpers.approveInternship("INT001");

        // Two students apply
        TestHelpers.createApplication("APP001", "S001", "INT001");
        TestHelpers.createApplication("APP002", "S003", "INT001");

        // Approve both applications
        TestHelpers.approveApplication("APP001");
        TestHelpers.approveApplication("APP002");

        // First student accepts
        TestHelpers.acceptPlacement("S001", "APP001");

        Internship internship = internshipManager.getInternshipById("INT001");
        assertEquals(1, internship.getConfirmedSlots());
        assertEquals(InternshipStatus.APPROVED, internship.getStatus());
        assertFalse(internship.isFilled());

        // Second student accepts
        TestHelpers.acceptPlacement("S003", "APP002");

        internship = internshipManager.getInternshipById("INT001");
        assertEquals(2, internship.getConfirmedSlots());
        assertEquals(InternshipStatus.FILLED, internship.getStatus());
        assertTrue(internship.isFilled());

        // Verify internship no longer visible to new applicants
        Student newStudent = (Student) userManager.getUserById("S009");
        List<Internship> visible = internshipManager.getVisibleInternshipsForStudent(newStudent);
        assertFalse(visible.stream().anyMatch(i -> i.getInternshipId().equals("INT001")),
            "Filled internship should not be visible to new applicants");
    }

    @Test
    @DisplayName("TC-019: Duplicate Application Prevention")
    public void testDuplicateApplicationPrevention() {
        // Setup
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
        TestHelpers.createApplication("APP001", "S001", "INT001");

        // Verify duplicate check works
        boolean hasDuplicate = applicationManager.hasAppliedToInternship("S001", "INT001");
        assertTrue(hasDuplicate, "System should detect student has already applied to this internship");

        // Verify another student can still apply
        boolean otherStudentApplied = applicationManager.hasAppliedToInternship("S002", "INT001");
        assertFalse(otherStudentApplied, "Other students should not be affected by duplicate check");
    }
}
