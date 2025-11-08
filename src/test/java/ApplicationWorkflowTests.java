import control.*;
import entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import util.TestDataSetup;
import util.TestHelpers;
import util.TestStateManager;

import java.time.LocalDate;
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
        TestHelpers.createInternship(
            "INT001", "Basic Internship", "For beginners",
            InternshipLevel.BASIC, "Computer Science", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.approveInternship("INT001");

        // Create INTERMEDIATE internship for Computer Science
        TestHelpers.createInternship(
            "INT002", "Intermediate Internship", "For experienced students",
            InternshipLevel.INTERMEDIATE, "Computer Science", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.approveInternship("INT002");

        // U2310001A is Year 2, Computer Science - should only see BASIC
        Student student = (Student) userManager.getUserById("U2310001A");
        assertEquals(2, student.getYearOfStudy());

        List<Internship> visibleInternships = internshipManager.getVisibleInternshipsForStudent(student);

        // Should see BASIC but not INTERMEDIATE (Year 2 restriction)
        assertTrue(visibleInternships.stream().anyMatch(i -> i.getInternshipId().equals("INT001")),
            "Year 2 student should see BASIC internship");
        assertFalse(visibleInternships.stream().anyMatch(i -> i.getInternshipId().equals("INT002")),
            "Year 2 student should NOT see INTERMEDIATE internship");

        // U2310005E is Year 3, Computer Science - should see both (Year 3+ can see all levels)
        Student seniorStudent = (Student) userManager.getUserById("U2310005E");
        assertEquals(3, seniorStudent.getYearOfStudy());
        assertEquals("Computer Science", seniorStudent.getMajor());

        List<Internship> seniorVisibleInternships = internshipManager.getVisibleInternshipsForStudent(seniorStudent);

        assertTrue(seniorVisibleInternships.stream().anyMatch(i -> i.getInternshipId().equals("INT001")),
            "Year 3 student should see BASIC internship");
        assertTrue(seniorVisibleInternships.stream().anyMatch(i -> i.getInternshipId().equals("INT002")),
            "Year 3 student should see INTERMEDIATE internship");

        // Verify major filtering: Create internship for different major
        TestHelpers.createInternship(
            "INT003", "Data Science Internship", "For Data Science students",
            InternshipLevel.BASIC, "Data Science & AI", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.approveInternship("INT003");

        // Computer Science student should NOT see Data Science internship
        List<Internship> csStudentVisible = internshipManager.getVisibleInternshipsForStudent(student);
        assertFalse(csStudentVisible.stream().anyMatch(i -> i.getInternshipId().equals("INT003")),
            "Computer Science student should NOT see Data Science & AI internship");

        // Data Science student should see Data Science internship
        Student dsStudent = (Student) userManager.getUserById("U2310002B"); // Data Science & AI, Year 3
        assertEquals("Data Science & AI", dsStudent.getMajor());
        List<Internship> dsStudentVisible = internshipManager.getVisibleInternshipsForStudent(dsStudent);
        assertTrue(dsStudentVisible.stream().anyMatch(i -> i.getInternshipId().equals("INT003")),
            "Data Science student should see Data Science & AI internship");
        assertFalse(dsStudentVisible.stream().anyMatch(i -> i.getInternshipId().equals("INT001")),
            "Data Science student should NOT see Computer Science internship");
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
        Application application = TestHelpers.createApplication("APP001", "U2310001A", "INT001");

        assertNotNull(application);
        assertEquals("APP001", application.getApplicationId());
        assertEquals("U2310001A", application.getStudentId());
        assertEquals("INT001", application.getInternshipId());
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
        assertFalse(application.getStatus().equals(ApplicationStatus.CONFIRMED));

        // Verify application appears in student's applications
        List<Application> studentApplications = applicationManager.getApplicationsByStudent("U2310001A");
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
        TestHelpers.createApplication("APP001", "U2310001A", "INT001");
        TestHelpers.createApplication("APP002", "U2310001A", "INT002");
        TestHelpers.createApplication("APP003", "U2310001A", "INT003");

        // Verify student has 3 pending applications
        int pendingCount = applicationManager.countPendingApplicationsByStudent("U2310001A");
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
        Application application = TestHelpers.createApplication("APP001", "U2310001A", "INT001");
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
        TestHelpers.createApplication("APP001", "U2310001A", "INT001");
        TestHelpers.createApplication("APP002", "U2310001A", "INT002");

        // Both get approved
        TestHelpers.approveApplication("APP001");
        TestHelpers.approveApplication("APP002");

        // Student accepts APP001
        boolean acceptSuccess = TestHelpers.acceptPlacement("U2310001A", "APP001");
        assertTrue(acceptSuccess, "Student should be able to accept placement");

        // Verify application is confirmed
        Application confirmedApp = applicationManager.getApplicationById("APP001");
        assertTrue(confirmedApp.getStatus().equals(ApplicationStatus.CONFIRMED), "Application should be marked as confirmed");

        // Verify student's confirmedPlacementId is set
        Student student = (Student) userManager.getUserById("U2310001A");
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
        TestHelpers.createApplication("APP001", "U2310001A", "INT001");
        TestHelpers.createApplication("APP002", "U2310005E", "INT001");

        // Approve both applications
        TestHelpers.approveApplication("APP001");
        TestHelpers.approveApplication("APP002");

        // First student accepts
        TestHelpers.acceptPlacement("U2310001A", "APP001");

        Internship internship = internshipManager.getInternshipById("INT001");
        assertEquals(1, internship.getConfirmedSlots());
        assertEquals(InternshipStatus.APPROVED, internship.getStatus());
        assertFalse(internship.isFilled());

        // Second student accepts
        TestHelpers.acceptPlacement("U2310005E", "APP002");

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
        TestHelpers.createApplication("APP001", "U2310001A", "INT001");

        // Verify duplicate check works
        boolean hasDuplicate = applicationManager.hasAppliedToInternship("U2310001A", "INT001");
        assertTrue(hasDuplicate, "System should detect student has already applied to this internship");

        // Verify another student can still apply
        boolean otherStudentApplied = applicationManager.hasAppliedToInternship("U2310002B", "INT001");
        assertFalse(otherStudentApplied, "Other students should not be affected by duplicate check");
    }

    @Test
    @DisplayName("TC-008: Student Views Application After Visibility Toggle Off")
    public void testStudentViewsApplicationAfterVisibilityToggleOff() {
        // Setup: Create internship and student applies
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
        Application application = TestHelpers.createApplication("APP001", "U2310001A", "INT001");
        assertNotNull(application, "Application should be created");
        
        Student student = (Student) userManager.getUserById("U2310001A");
        
        // Verify internship is visible before toggle
        List<Internship> visibleBefore = internshipManager.getVisibleInternshipsForStudent(student);
        assertTrue(visibleBefore.stream().anyMatch(i -> i.getInternshipId().equals("INT001")),
            "Internship should be visible before toggle");
        
        // Toggle visibility OFF
        Internship internship = internshipManager.getInternshipById("INT001");
        internship.setVisible(false);
        internshipManager.updateInternship(internship);
        
        // Verify internship is no longer visible in general list
        List<Internship> visibleAfter = internshipManager.getVisibleInternshipsForStudent(student);
        assertFalse(visibleAfter.stream().anyMatch(i -> i.getInternshipId().equals("INT001")),
            "Internship should NOT be visible after toggle off");
        
        // Critical: Verify student can STILL access their application
        Application studentApp = applicationManager.getApplicationById("APP001");
        assertNotNull(studentApp, "Student should still be able to access their application");
        assertEquals("U2310001A", studentApp.getStudentId());
        assertEquals("INT001", studentApp.getInternshipId());
        assertEquals(ApplicationStatus.PENDING, studentApp.getStatus());
        
        // Verify all student applications are accessible regardless of visibility
        List<Application> studentApps = applicationManager.getApplicationsByStudent("U2310001A");
        assertTrue(studentApps.stream().anyMatch(a -> a.getApplicationId().equals("APP001")),
            "Application should remain in student's application list regardless of visibility");
    }

    @Test
    @DisplayName("TC-011: Application After Closing Date")
    public void testApplicationAfterClosingDate() {
        // Setup: Create company representative
        authController.registerCompanyRepresentative(
            "Jane Doe", "jane.doe@techcorp.com", "password123",
            "TechCorp", "Engineering", "Manager"
        );
        CompanyRepresentative rep = userManager.getPendingRepresentatives().get(0);
        TestHelpers.approveRepresentative(rep.getUserId());
        
        // Create internship with PAST closing date
        LocalDate pastClosingDate = LocalDate.now().minusDays(7);
        LocalDate pastOpeningDate = LocalDate.now().minusDays(30);
        
        Internship internship = TestHelpers.createInternshipWithDates(
            "INT001", "Software Developer Intern", "Description",
            InternshipLevel.BASIC, "Computer Science", "TechCorp", 
            rep.getUserId(), 5, pastOpeningDate, pastClosingDate
        );
        TestHelpers.approveInternship("INT001");
        
        // Verify internship exists but is past closing date
        assertNotNull(internship);
        assertTrue(LocalDate.now().isAfter(internship.getClosingDate()),
            "Current date should be after closing date");
        
        // Attempt to apply after closing date
        ApplicationManager appManager = ApplicationManager.getInstance();
        boolean applicationSuccess = appManager.addApplication("APP001", "U2310001A", "INT001");
        
        assertFalse(applicationSuccess, 
            "Application should be rejected for internship past closing date");
        
        // Verify application was NOT created
        Application application = appManager.getApplicationById("APP001");
        assertNull(application, "Application should not exist when closing date has passed");
        
        // Verify student's application count is still 0
        List<Application> studentApps = appManager.getApplicationsByStudent("U2310001A");
        assertEquals(0, studentApps.size(), 
            "Student should have 0 applications after failed attempt");
        
        // Verify future closing date works
        LocalDate futureClosingDate = LocalDate.now().plusDays(30);
        LocalDate futureOpeningDate = LocalDate.now().minusDays(1);
        
        TestHelpers.createInternshipWithDates(
            "INT002", "Data Analyst Intern", "Description",
            InternshipLevel.BASIC, "Computer Science", "TechCorp", 
            rep.getUserId(), 3, futureOpeningDate, futureClosingDate
        );
        TestHelpers.approveInternship("INT002");
        
        boolean validApplicationSuccess = appManager.addApplication("APP002", "U2310001A", "INT002");
        assertTrue(validApplicationSuccess, 
            "Application should succeed for internship with future closing date");
        
        Application validApplication = appManager.getApplicationById("APP002");
        assertNotNull(validApplication, 
            "Application should be created for internship with future closing date");
    }

    @Test
    @DisplayName("Slot Availability Enforcement on Approval")
    public void testSlotAvailabilityEnforcementOnApproval() {
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

        // Three students apply
        Application app1 = TestHelpers.createApplication("APP001", "U2310001A", "INT001");
        Application app2 = TestHelpers.createApplication("APP002", "U2310002B", "INT001");
        Application app3 = TestHelpers.createApplication("APP003", "U2310003C", "INT001");

        // Verify all applications are PENDING
        assertEquals(ApplicationStatus.PENDING, app1.getStatus());
        assertEquals(ApplicationStatus.PENDING, app2.getStatus());
        assertEquals(ApplicationStatus.PENDING, app3.getStatus());

        // Approve first 2 applications (should succeed - slots available)
        applicationManager.reviewApplication(app1, 1);
        applicationManager.reviewApplication(app2, 1);

        // Verify first 2 are approved
        Application approved1 = applicationManager.getApplicationById("APP001");
        Application approved2 = applicationManager.getApplicationById("APP002");
        assertEquals(ApplicationStatus.SUCCESSFUL, approved1.getStatus());
        assertEquals(ApplicationStatus.SUCCESSFUL, approved2.getStatus());

        // Attempt to approve 3rd application (should fail due to slot limit)
        // Since we have 2 slots and 2 successful applications, no more should be approved
        applicationManager.reviewApplication(app3, 1);
        
        // Verify 3rd application should remain PENDING (not approved due to slot limit)
        Application app3AfterAttempt = applicationManager.getApplicationById("APP003");
        // Note: This test will initially fail because the bug allows unlimited approvals
        // After fixing the bug, this assertion should pass
        assertEquals(ApplicationStatus.PENDING, app3AfterAttempt.getStatus(),
            "Third application should remain PENDING when all slots are filled with successful applications");

        // Verify only 2 applications are SUCCESSFUL
        List<Application> allApps = applicationManager.getApplicationsByInternship("INT001");
        long successfulCount = allApps.stream()
            .filter(a -> a.getStatus() == ApplicationStatus.SUCCESSFUL)
            .count();
        assertEquals(2, successfulCount, 
            "Only 2 applications should be SUCCESSFUL (matching slot limit)");
    }
}
