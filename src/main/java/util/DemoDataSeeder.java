package util;

import control.ApplicationManager;
import control.InternshipManager;
import control.UserManager;
import entity.*;
import java.time.LocalDate;

/**
 * Utility class for seeding demo data (company representative and internships)
 * for demonstration purposes. Creates pre-approved demo company rep and
 * 8 diverse internships with various levels, majors, and dates.
 */
public class DemoDataSeeder {
    private static final String DEMO_REP_EMAIL = "demo.rep@techcorp.com";
    private static final String DEMO_REP_USER_ID = "demo.rep@techcorp.com";
    private static final String DEMO_REP_PASSWORD = "password";
    private static final String DEMO_REP_NAME = "Demo Representative";
    private static final String DEMO_COMPANY_NAME = "TechCorp Solutions";
    private static final String DEMO_DEPARTMENT = "Human Resources";
    private static final String DEMO_POSITION = "Recruitment Manager";

    /**
     * Seeds demo data: creates a pre-approved company representative, 8 diverse internships,
     * and 3 demo applications for student U2310005E.
     * This method loads existing data, adds demo data, and saves to files.
     */
    public static void seedDemoData() {
        FileManager.ensureDataDirectory();
        
        System.out.println("Seeding demo data...");
        
        // Seed demo company representative
        seedDemoCompanyRepresentative();
        
        // Seed demo internships
        seedDemoInternships();
        
        // Seed demo applications
        seedDemoApplications();
        
        System.out.println("Demo data seeding completed successfully!");
        System.out.println("Demo Company Rep: " + DEMO_REP_EMAIL + " (password: " + DEMO_REP_PASSWORD + ")");
        System.out.println("Created 8 internships (3 BASIC, 3 INTERMEDIATE, 2 ADVANCED)");
        System.out.println("Created 3 demo applications for student U2310005E");
    }

    /**
     * Creates and saves a pre-approved demo company representative.
     */
    private static void seedDemoCompanyRepresentative() {
        UserManager userManager = UserManager.getInstance();
        
        // Check if demo rep already exists
        if (userManager.userExists(DEMO_REP_USER_ID)) {
            System.out.println("Demo company representative already exists. Skipping...");
            return;
        }
        
        // Create demo company representative with APPROVED status
        CompanyRepresentative demoRep = new CompanyRepresentative(
            DEMO_REP_USER_ID,
            DEMO_REP_PASSWORD,
            DEMO_REP_NAME,
            DEMO_REP_EMAIL,
            DEMO_COMPANY_NAME,
            DEMO_DEPARTMENT,
            DEMO_POSITION
        );
        
        // Set approval status to APPROVED
        demoRep.setApprovalStatus(ApprovalStatus.APPROVED);
        
        // Add to system
        userManager.addUser(demoRep);
        
        System.out.println("Created demo company representative: " + DEMO_REP_EMAIL);
    }

    /**
     * Creates and saves 8 diverse internships for demo purposes.
     * All internships are APPROVED, VISIBLE, and assigned to demo rep.
     */
    private static void seedDemoInternships() {
        InternshipManager internshipManager = InternshipManager.getInstance();
        
        // Check if internships already exist (simple check - if demo rep has any)
        if (internshipManager.countInternshipsByRepresentative(DEMO_REP_USER_ID) > 0) {
            System.out.println("Demo internships already exist. Skipping...");
            return;
        }
        
        LocalDate baseDate = LocalDate.of(2026, 1, 1);
        
        // 3 BASIC level internships
        createInternship(internshipManager, "INT001", "Frontend Development Intern", 
            "Learn React and modern web development practices", InternshipLevel.BASIC,
            "Computer Science", baseDate, baseDate.plusMonths(3), DEMO_COMPANY_NAME, DEMO_REP_USER_ID, 5);
        
        createInternship(internshipManager, "INT002", "Data Analytics Intern", 
            "Work with Python and data visualization tools", InternshipLevel.BASIC,
            "Data Science & AI", baseDate.plusDays(5), baseDate.plusMonths(4), DEMO_COMPANY_NAME, DEMO_REP_USER_ID, 5);
        
        createInternship(internshipManager, "INT003", "UI/UX Design Intern", 
            "Create user interfaces and design systems", InternshipLevel.BASIC,
            "Information Engineering & Media", baseDate.plusDays(10), baseDate.plusMonths(5), DEMO_COMPANY_NAME, DEMO_REP_USER_ID, 5);
        
        // 3 INTERMEDIATE level internships
        createInternship(internshipManager, "INT004", "Full-Stack Software Engineer Intern", 
            "Develop end-to-end web applications using modern frameworks", InternshipLevel.INTERMEDIATE,
            "Computer Science", baseDate.plusDays(15), baseDate.plusMonths(6), DEMO_COMPANY_NAME, DEMO_REP_USER_ID, 5);
        
        createInternship(internshipManager, "INT005", "Machine Learning Intern", 
            "Build and deploy ML models for real-world applications", InternshipLevel.INTERMEDIATE,
            "Data Science & AI", baseDate.plusDays(20), baseDate.plusMonths(7), DEMO_COMPANY_NAME, DEMO_REP_USER_ID, 5);
        
        createInternship(internshipManager, "INT006", "Embedded Systems Intern", 
            "Work on IoT devices and embedded software development", InternshipLevel.INTERMEDIATE,
            "Computer Engineering", baseDate.plusDays(25), baseDate.plusMonths(8), DEMO_COMPANY_NAME, DEMO_REP_USER_ID, 5);
        
        // 2 ADVANCED level internships
        createInternship(internshipManager, "INT007", "Senior Software Architect Intern", 
            "Design scalable systems and lead technical initiatives", InternshipLevel.ADVANCED,
            "Computer Science", baseDate.plusDays(30), baseDate.plusMonths(9), DEMO_COMPANY_NAME, DEMO_REP_USER_ID, 5);
        
        createInternship(internshipManager, "INT008", "Advanced AI Research Intern", 
            "Contribute to cutting-edge AI research projects", InternshipLevel.ADVANCED,
            "Computer Engineering", baseDate.plusDays(35), baseDate.plusMonths(10), DEMO_COMPANY_NAME, DEMO_REP_USER_ID, 5);
        
        System.out.println("Created 8 demo internships");
    }

    /**
     * Creates and saves 3 demo applications for student U2310005E.
     * Applications are for Computer Science internships across different levels.
     */
    private static void seedDemoApplications() {
        ApplicationManager applicationManager = ApplicationManager.getInstance();
        String studentId = "U2310005E";
        
        // Check if student already has applications
        if (applicationManager.getApplicationsByStudent(studentId).size() > 0) {
            System.out.println("Demo applications for " + studentId + " already exist. Skipping...");
            return;
        }
        
        // Create 3 applications for Computer Science internships (BASIC, INTERMEDIATE, ADVANCED)
        String[] internshipIds = {"INT001", "INT004", "INT007"};
        String[] applicationIds = {"APP001", "APP002", "APP003"};
        
        int successCount = 0;
        for (int i = 0; i < internshipIds.length; i++) {
            boolean added = applicationManager.addApplication(applicationIds[i], studentId, internshipIds[i]);
            if (added) {
                successCount++;
            }
        }
        
        System.out.println("Created " + successCount + " demo applications for student " + studentId);
    }

    /**
     * Helper method to create an internship with APPROVED status and VISIBLE flag.
     */
    private static void createInternship(InternshipManager manager, String internshipId, String title,
                                        String description, InternshipLevel level, String preferredMajor,
                                        LocalDate openingDate, LocalDate closingDate, String companyName,
                                        String representativeId, int totalSlots) {
        // Create internship (defaults to PENDING status)
        manager.addInternship(internshipId, title, description, level, preferredMajor,
                            openingDate, closingDate, companyName, representativeId, totalSlots);
        
        // Get the internship and update to APPROVED status
        Internship internship = manager.getInternshipById(internshipId);
        if (internship != null) {
            internship.setStatus(InternshipStatus.APPROVED);
            internship.setVisible(true);
            manager.updateInternship(internship);
        }
    }

    /**
     * Main method to run the seeder standalone.
     * Usage: mvn exec:java -Dexec.mainClass="util.DemoDataSeeder"
     */
    public static void main(String[] args) {
        seedDemoData();
    }
}

