import control.*;
import entity.*;
import entity.FilterSettings.SortCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import util.TestDataSetup;
import util.TestHelpers;
import util.TestStateManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Business Rules and Additional Features Tests
 * Tests TC-016, TC-020, TC-022, TC-023, TC-024, TC-025 from the testing documentation.
 */
@DisplayName("Business Rules Tests")
public class BusinessRulesTests {

    private AuthenticationController authController;
    private UserManager userManager;
    private InternshipManager internshipManager;
    private ApplicationManager applicationManager;
    private FilterManager filterManager;

    @BeforeEach
    public void setUp() {
        TestStateManager.resetSystemState();
        TestDataSetup.initializeTestData();

        authController = new AuthenticationController();
        userManager = UserManager.getInstance();
        internshipManager = InternshipManager.getInstance();
        applicationManager = ApplicationManager.getInstance();
        filterManager = FilterManager.getInstance();
    }

    @Test
    @DisplayName("TC-016: Filter Settings Persistence")
    public void testFilterSettingsPersistence() {
        // Get filter settings for student
        FilterSettings settings = filterManager.getFilterSettings("U2310001A");

        // Verify default settings
        assertEquals(SortCriteria.ALPHABETICAL, settings.getSortBy(),
            "Default sort should be alphabetical");
        assertNull(settings.getLevelFilter(), "Default level filter should be null");

        // Configure filters
        settings.setLevelFilter(InternshipLevel.BASIC);
        settings.setSortBy(SortCriteria.CLOSING_DATE);
        settings.setMajorFilter("Computer Science");
        filterManager.updateFilterSettings(settings);

        // Verify settings are saved
        FilterSettings retrieved = filterManager.getFilterSettings("U2310001A");
        assertEquals(InternshipLevel.BASIC, retrieved.getLevelFilter());
        assertEquals(SortCriteria.CLOSING_DATE, retrieved.getSortBy());
        assertEquals("Computer Science", retrieved.getMajorFilter());

        // Simulate logout/login by resetting and reloading system
        TestStateManager.resetSingletons();
        filterManager = FilterManager.getInstance();

        // Verify settings persisted across session
        FilterSettings afterReload = filterManager.getFilterSettings("U2310001A");
        assertEquals(InternshipLevel.BASIC, afterReload.getLevelFilter(),
            "Level filter should persist across sessions");
        assertEquals(SortCriteria.CLOSING_DATE, afterReload.getSortBy(),
            "Sort criteria should persist across sessions");
        assertEquals("Computer Science", afterReload.getMajorFilter(),
            "Major filter should persist across sessions");

        // Verify new user has default settings
        FilterSettings newUserSettings = filterManager.getFilterSettings("U2310002B");
        assertEquals(SortCriteria.ALPHABETICAL, newUserSettings.getSortBy(),
            "New users should have default alphabetical sort");
        assertNull(newUserSettings.getLevelFilter(), "Default level filter should be null");
    }

    @Test
    @DisplayName("TC-020: Year-Level Eligibility Enforcement")
    public void testYearLevelEligibilityEnforcement() {
        // Setup: Create representative and internships of different levels
        authController.registerCompanyRepresentative(
            "Jane Doe", "jane.doe@techcorp.com", "password123",
            "TechCorp", "Engineering", "Manager"
        );
        CompanyRepresentative rep = userManager.getPendingRepresentatives().get(0);
        TestHelpers.approveRepresentative(rep.getUserId());

        // Create internships of different levels for Computer Science (for Year 2 student)
        TestHelpers.createInternship(
            "INT001", "Basic Internship", "For beginners",
            InternshipLevel.BASIC, "Computer Science", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.createInternship(
            "INT002", "Intermediate Internship", "For experienced",
            InternshipLevel.INTERMEDIATE, "Computer Science", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.createInternship(
            "INT003", "Advanced Internship", "For experts",
            InternshipLevel.ADVANCED, "Computer Science", "TechCorp", rep.getUserId(), 3
        );

        TestHelpers.approveInternship("INT001");
        TestHelpers.approveInternship("INT002");
        TestHelpers.approveInternship("INT003");

        // Year 1 student - should only see BASIC for matching major
        // U2310004D is Information Engineering & Media, Year 1
        Student year1Student = (Student) userManager.getUserById("U2310004D"); // Year 1, Information Engineering & Media
        assertEquals(1, year1Student.getYearOfStudy());
        assertEquals("Information Engineering & Media", year1Student.getMajor());

        // Create Information Engineering & Media internships for Year 1 student
        TestHelpers.createInternship(
            "INT010", "Basic IEM Internship", "For beginners",
            InternshipLevel.BASIC, "Information Engineering & Media", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.createInternship(
            "INT011", "Intermediate IEM Internship", "For experienced",
            InternshipLevel.INTERMEDIATE, "Information Engineering & Media", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.approveInternship("INT010");
        TestHelpers.approveInternship("INT011");

        List<Internship> year1Visible = internshipManager.getVisibleInternshipsForStudent(year1Student);
        // Should NOT see Computer Science internships
        assertFalse(year1Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT001")),
            "Year 1 IEM student should NOT see Computer Science BASIC");
        assertFalse(year1Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT002")),
            "Year 1 IEM student should NOT see Computer Science INTERMEDIATE");
        // Should see BASIC for matching major
        assertTrue(year1Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT010")),
            "Year 1 student should see BASIC for matching major");
        assertFalse(year1Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT011")),
            "Year 1 student should NOT see INTERMEDIATE (year restriction)");

        // Year 2 student - should only see BASIC for matching major
        // U2310001A is Computer Science, Year 2
        Student year2Student = (Student) userManager.getUserById("U2310001A"); // Year 2, Computer Science
        assertEquals(2, year2Student.getYearOfStudy());
        assertEquals("Computer Science", year2Student.getMajor());

        List<Internship> year2Visible = internshipManager.getVisibleInternshipsForStudent(year2Student);
        assertTrue(year2Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT001")),
            "Year 2 student should see BASIC for matching major");
        assertFalse(year2Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT002")),
            "Year 2 student should NOT see INTERMEDIATE (year restriction)");
        assertFalse(year2Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT003")),
            "Year 2 student should NOT see ADVANCED (year restriction)");

        // Year 3 student - should see all levels for matching major
        // U2310002B is Data Science & AI, Year 3 - needs Data Science & AI internships
        Student year3Student = (Student) userManager.getUserById("U2310002B"); // Year 3, Data Science & AI
        assertEquals(3, year3Student.getYearOfStudy());
        assertEquals("Data Science & AI", year3Student.getMajor());

        // Create Data Science & AI internships for Year 3 student
        TestHelpers.createInternship(
            "INT004", "Basic DS Internship", "For beginners",
            InternshipLevel.BASIC, "Data Science & AI", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.createInternship(
            "INT005", "Intermediate DS Internship", "For experienced",
            InternshipLevel.INTERMEDIATE, "Data Science & AI", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.createInternship(
            "INT006", "Advanced DS Internship", "For experts",
            InternshipLevel.ADVANCED, "Data Science & AI", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.approveInternship("INT004");
        TestHelpers.approveInternship("INT005");
        TestHelpers.approveInternship("INT006");

        List<Internship> year3Visible = internshipManager.getVisibleInternshipsForStudent(year3Student);
        // Should NOT see Computer Science internships (INT001, INT002, INT003)
        assertFalse(year3Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT001")),
            "Year 3 Data Science student should NOT see Computer Science BASIC");
        assertFalse(year3Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT002")),
            "Year 3 Data Science student should NOT see Computer Science INTERMEDIATE");
        assertFalse(year3Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT003")),
            "Year 3 Data Science student should NOT see Computer Science ADVANCED");
        // Should see Data Science & AI internships (all levels)
        assertTrue(year3Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT004")),
            "Year 3 student should see BASIC for matching major");
        assertTrue(year3Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT005")),
            "Year 3 student should see INTERMEDIATE for matching major");
        assertTrue(year3Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT006")),
            "Year 3 student should see ADVANCED for matching major");

        // Year 4 student - should see all levels for matching major
        // U2310003C is Computer Engineering, Year 4 - needs Computer Engineering internships
        Student year4Student = (Student) userManager.getUserById("U2310003C"); // Year 4, Computer Engineering
        assertEquals(4, year4Student.getYearOfStudy());
        assertEquals("Computer Engineering", year4Student.getMajor());

        // Create Computer Engineering internships for Year 4 student
        TestHelpers.createInternship(
            "INT007", "Basic CE Internship", "For beginners",
            InternshipLevel.BASIC, "Computer Engineering", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.createInternship(
            "INT008", "Intermediate CE Internship", "For experienced",
            InternshipLevel.INTERMEDIATE, "Computer Engineering", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.createInternship(
            "INT009", "Advanced CE Internship", "For experts",
            InternshipLevel.ADVANCED, "Computer Engineering", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.approveInternship("INT007");
        TestHelpers.approveInternship("INT008");
        TestHelpers.approveInternship("INT009");

        List<Internship> year4Visible = internshipManager.getVisibleInternshipsForStudent(year4Student);
        // Should NOT see Computer Science internships (INT001, INT002, INT003)
        assertFalse(year4Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT001")),
            "Year 4 Computer Engineering student should NOT see Computer Science BASIC");
        assertFalse(year4Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT002")),
            "Year 4 Computer Engineering student should NOT see Computer Science INTERMEDIATE");
        assertFalse(year4Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT003")),
            "Year 4 Computer Engineering student should NOT see Computer Science ADVANCED");
        // Should see Computer Engineering internships (all levels)
        assertTrue(year4Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT007")),
            "Year 4 student should see BASIC for matching major");
        assertTrue(year4Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT008")),
            "Year 4 student should see INTERMEDIATE for matching major");
        assertTrue(year4Visible.stream().anyMatch(i -> i.getInternshipId().equals("INT009")),
            "Year 4 student should see ADVANCED for matching major");
    }

    @Test
    @DisplayName("TC-022: Report Generation")
    public void testReportGeneration() {
        // Setup: Create internships with different statuses
        authController.registerCompanyRepresentative(
            "Jane Doe", "jane.doe@techcorp.com", "password123",
            "TechCorp", "Engineering", "Manager"
        );
        CompanyRepresentative rep = userManager.getPendingRepresentatives().get(0);
        TestHelpers.approveRepresentative(rep.getUserId());

        // Create internships
        TestHelpers.createInternship(
            "INT001", "Internship 1", "Description 1",
            InternshipLevel.BASIC, "Computer Science", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.createInternship(
            "INT002", "Internship 2", "Description 2",
            InternshipLevel.INTERMEDIATE, "Engineering", "TechCorp", rep.getUserId(), 5
        );
        TestHelpers.createInternship(
            "INT003", "Internship 3", "Description 3",
            InternshipLevel.ADVANCED, "Computer Science", "TechCorp", rep.getUserId(), 2
        );

        // Approve some internships
        TestHelpers.approveInternship("INT001");
        TestHelpers.approveInternship("INT002");

        // Get all internships (simulating report generation)
        List<Internship> allInternships = internshipManager.getInternshipsByRepresentative(rep.getUserId());
        assertEquals(3, allInternships.size(), "Should have 3 internships total");

        // Count by status
        long pendingCount = allInternships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.PENDING)
            .count();
        long approvedCount = allInternships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.APPROVED)
            .count();

        assertEquals(1, pendingCount, "Should have 1 pending internship");
        assertEquals(2, approvedCount, "Should have 2 approved internships");

        // Verify all details are accessible for report
        for (Internship internship : allInternships) {
            assertNotNull(internship.getTitle());
            assertNotNull(internship.getDescription());
            assertNotNull(internship.getLevel());
            assertNotNull(internship.getStatus());
            assertTrue(internship.getTotalSlots() > 0);
        }
    }

    @Test
    @DisplayName("TC-023: Filtered Report by Level")
    public void testFilteredReportByLevel() {
        // Setup
        authController.registerCompanyRepresentative(
            "Jane Doe", "jane.doe@techcorp.com", "password123",
            "TechCorp", "Engineering", "Manager"
        );
        CompanyRepresentative rep = userManager.getPendingRepresentatives().get(0);
        TestHelpers.approveRepresentative(rep.getUserId());

        // Create internships of different levels
        TestHelpers.createInternship(
            "INT001", "Basic Internship 1", "Description",
            InternshipLevel.BASIC, "Computer Science", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.createInternship(
            "INT002", "Basic Internship 2", "Description",
            InternshipLevel.BASIC, "Engineering", "TechCorp", rep.getUserId(), 5
        );
        TestHelpers.createInternship(
            "INT003", "Intermediate Internship", "Description",
            InternshipLevel.INTERMEDIATE, "Computer Science", "TechCorp", rep.getUserId(), 2
        );
        TestHelpers.createInternship(
            "INT004", "Advanced Internship", "Description",
            InternshipLevel.ADVANCED, "Engineering", "TechCorp", rep.getUserId(), 4
        );

        // Apply filters
        FilterSettings filterSettings = new FilterSettings("report");
        filterSettings.setLevelFilter(InternshipLevel.BASIC);

        List<Internship> allInternships = internshipManager.getInternshipsByRepresentative(rep.getUserId());
        List<Internship> filtered = internshipManager.filterInternships(allInternships, filterSettings);

        // Verify only BASIC internships returned
        assertEquals(2, filtered.size(), "Should have 2 BASIC level internships");
        assertTrue(filtered.stream().allMatch(i -> i.getLevel() == InternshipLevel.BASIC),
            "All filtered internships should be BASIC level");

        // Test filter by major
        FilterSettings majorFilter = new FilterSettings("report2");
        majorFilter.setMajorFilter("Computer Science");

        List<Internship> majorFiltered = internshipManager.filterInternships(allInternships, majorFilter);
        assertTrue(majorFiltered.stream().allMatch(i -> i.getPreferredMajor().equals("Computer Science")),
            "All filtered internships should be for Computer Science major");
    }

    @Test
    @DisplayName("TC-024: Student Applications Summary")
    public void testStudentApplicationsSummary() {
        // Setup
        authController.registerCompanyRepresentative(
            "Jane Doe", "jane.doe@techcorp.com", "password123",
            "TechCorp", "Engineering", "Manager"
        );
        CompanyRepresentative rep = userManager.getPendingRepresentatives().get(0);
        TestHelpers.approveRepresentative(rep.getUserId());

        TestHelpers.createInternship(
            "INT001", "Internship 1", "Description",
            InternshipLevel.BASIC, "Computer Science", "TechCorp", rep.getUserId(), 5
        );
        TestHelpers.createInternship(
            "INT002", "Internship 2", "Description",
            InternshipLevel.BASIC, "Computer Science", "TechCorp", rep.getUserId(), 3
        );
        TestHelpers.approveInternship("INT001");
        TestHelpers.approveInternship("INT002");

        // Student applies to both
        TestHelpers.createApplication("APP001", "U2310001A", "INT001");
        TestHelpers.createApplication("APP002", "U2310001A", "INT002");

        // Approve one, reject another
        TestHelpers.approveApplication("APP001");
        Application app2 = applicationManager.getApplicationById("APP002");
        app2.setStatus(ApplicationStatus.UNSUCCESSFUL);
        applicationManager.updateApplication(app2);

        // Student accepts successful placement
        TestHelpers.acceptPlacement("U2310001A", "APP001");

        // Generate summary for student
        List<Application> studentApps = applicationManager.getApplicationsByStudent("U2310001A");
        assertEquals(2, studentApps.size(), "Student should have 2 applications");

        long pending = studentApps.stream()
            .filter(a -> a.getStatus() == ApplicationStatus.PENDING)
            .count();
        long successful = studentApps.stream()
            .filter(a -> a.getStatus() == ApplicationStatus.CONFIRMED)
            .count();
        long unsuccessful = studentApps.stream()
            .filter(a -> a.getStatus() == ApplicationStatus.UNSUCCESSFUL)
            .count();

        assertEquals(0, pending, "Should have 0 pending applications");
        assertEquals(1, successful, "Should have 1 successful confirmed application");
        assertEquals(1, unsuccessful, "Should have 1 unsuccessful application");

        // Verify confirmed placement status
        Student student = (Student) userManager.getUserById("U2310001A");
        assertNotNull(student.getConfirmedPlacementId(), "Student should have confirmed placement");
        assertEquals("APP001", student.getConfirmedPlacementId());
    }

    @Test
    @DisplayName("TC-025: Data Persistence Across Restarts")
    public void testDataPersistenceAcrossRestarts() {
        // Setup: Create various entities
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

        TestHelpers.createApplication("APP001", "U2310001A", "INT001");
        TestHelpers.createWithdrawalRequest("WR001", "U2310001A", "APP001", "Test reason");

        FilterSettings settings = filterManager.getFilterSettings("U2310001A");
        settings.setLevelFilter(InternshipLevel.BASIC);
        filterManager.updateFilterSettings(settings);

        // Simulate application restart by resetting singletons
        TestStateManager.resetSingletons();

        // Reinitialize managers
        userManager = UserManager.getInstance();
        internshipManager = InternshipManager.getInstance();
        applicationManager = ApplicationManager.getInstance();
        filterManager = FilterManager.getInstance();
        WithdrawalManager withdrawalManager = WithdrawalManager.getInstance();

        // Verify all data persisted
        // Users
        User student = userManager.getUserById("U2310001A");
        assertNotNull(student, "Student should persist");
        CompanyRepresentative persistedRep = (CompanyRepresentative) userManager.getUserById(rep.getUserId());
        assertNotNull(persistedRep, "Representative should persist");
        assertEquals(ApprovalStatus.APPROVED, persistedRep.getApprovalStatus());

        // Internships
        Internship persistedInternship = internshipManager.getInternshipById("INT001");
        assertNotNull(persistedInternship, "Internship should persist");
        assertEquals("Software Developer Intern", persistedInternship.getTitle());
        assertEquals(InternshipStatus.APPROVED, persistedInternship.getStatus());

        // Applications
        Application persistedApp = applicationManager.getApplicationById("APP001");
        assertNotNull(persistedApp, "Application should persist");
        assertEquals("U2310001A", persistedApp.getStudentId());
        assertEquals("INT001", persistedApp.getInternshipId());

        // Withdrawal requests
        WithdrawalRequest persistedWithdrawal = withdrawalManager.getWithdrawalById("WR001");
        assertNotNull(persistedWithdrawal, "Withdrawal request should persist");
        assertEquals("U2310001A", persistedWithdrawal.getStudentId());
        assertEquals("APP001", persistedWithdrawal.getApplicationId());

        // Filter settings
        FilterSettings persistedSettings = filterManager.getFilterSettings("U2310001A");
        assertNotNull(persistedSettings, "Filter settings should persist");
        assertEquals(InternshipLevel.BASIC, persistedSettings.getLevelFilter());
    }
}
