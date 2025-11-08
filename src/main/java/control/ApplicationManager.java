package control;

import entity.*;
import util.FileManager;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;

/**
 * Manages application operations including creation, status updates, and business rules.
 * Enforces 3 concurrent application limit and date validation.
 * Uses singleton pattern to ensure single instance.
 */
public class ApplicationManager {
    private static final String APPLICATIONS_FILE = "applications.dat";
    private List<Application> applications;
    private static ApplicationManager instance;

    /**
     * Private constructor for singleton pattern.
     * Initializes applications list from file.
     */
    private ApplicationManager() {
        this.applications = FileManager.loadFromFile(APPLICATIONS_FILE);
    }

    /**
     * Returns the singleton instance of ApplicationManager.
     *
     * @return ApplicationManager instance
     */
    public static ApplicationManager getInstance() {
        if (instance == null) {
            instance = new ApplicationManager();
        }
        return instance;
    }

    /**
     * Saves all applications to file.
     */
    public void saveApplications() {
        FileManager.saveToFile(APPLICATIONS_FILE, applications);
    }

    /**
     * Adds a new application if valid (before closing date).
     *
     * @param applicationId unique application identifier
     * @param studentId student identifier
     * @param internshipId internship identifier
     * @return true if added successfully, false otherwise
     */
    public boolean addApplication(String applicationId, String studentId, String internshipId) {
        // Enforce date limits
        InternshipManager internshipManager = InternshipManager.getInstance();
        Internship internship = internshipManager.getInternshipById(internshipId);
        if (internship == null) {
            return false;
        }
        LocalDate closingDate = internship.getClosingDate();
        if (closingDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        if (today.isAfter(closingDate)) return false;
        Application application = new Application(applicationId, studentId,
                internshipId);
        applications.add(application);
        saveApplications();
        return true;
    }

    /**
     * Gets an application by ID.
     *
     * @param applicationId application identifier
     * @return application if found, null otherwise
     */
    public Application getApplicationById(String applicationId) {
        return applications.stream()
                .filter(a -> a.getApplicationId().equals(applicationId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Updates an existing application and saves to file.
     *
     * @param application updated application object
     */
    public void updateApplication(Application application) {
        for (int i = 0; i < applications.size(); i++) {
            if (applications.get(i).getApplicationId().equals(application.getApplicationId())) {
                applications.set(i, application);
                saveApplications();
                return;
            }
        }
    }

    /**
     * Removes an application and saves to file.
     *
     * @param application application to remove
     */
    public void removeApplication(Application application) {
        applications.remove(application);
        saveApplications();
    }

    /**
     * Gets all applications by a student.
     *
     * @param studentId student identifier
     * @return list of applications
     */
    public List<Application> getApplicationsByStudent(String studentId) {
        return applications.stream()
                .filter(a -> a.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    /**
     * Gets all applications for an internship.
     *
     * @param internshipId internship identifier
     * @return list of applications
     */
    public List<Application> getApplicationsByInternship(String internshipId) {
        return applications.stream()
                .filter(a -> a.getInternshipId().equals(internshipId))
                .collect(Collectors.toList());
    }

    /**
     * Counts pending applications for a student.
     *
     * @param studentId student identifier
     * @return count of pending applications
     */
    public int countPendingApplicationsByStudent(String studentId) {
        return (int) applications.stream()
                .filter(a -> a.getStudentId().equals(studentId))
                .filter(a -> a.getStatus() == ApplicationStatus.PENDING)
                .count();
    }

    /**
     * Checks if a student has already applied to an internship.
     *
     * @param studentId student identifier
     * @param internshipId internship identifier
     * @return true if already applied, false otherwise
     */
    public boolean hasAppliedToInternship(String studentId, String internshipId) {
        return applications.stream()
                .anyMatch(a -> a.getStudentId().equals(studentId) &&
                        a.getInternshipId().equals(internshipId));
    }

    /**
     * Reviews and updates an application's status.
     * Enforces slot availability when approving applications.
     *
     * @param application application to review
     * @param decision 1 for approve, 2 for reject
     */
    public void reviewApplication(Application application, int decision) {
        switch (decision) {
            case 1:
                // Check slot availability before approving
                InternshipManager internshipManager = InternshipManager.getInstance();
                Internship internship = internshipManager.getInternshipById(application.getInternshipId());
                
                if (internship != null) {
                    // Count confirmed slots (already accepted by students)
                    int confirmedSlots = internship.getConfirmedSlots();
                    
                    // Count successful applications (approved but not yet accepted)
                    // Exclude the current application to avoid double counting
                    List<Application> successfulApps = getApplicationsByInternship(application.getInternshipId())
                        .stream()
                        .filter(a -> a.getStatus() == ApplicationStatus.SUCCESSFUL)
                        .filter(a -> !a.getApplicationId().equals(application.getApplicationId()))
                        .collect(Collectors.toList());
                    int successfulCount = successfulApps.size();
                    
                    // Calculate available slots
                    int totalSlots = internship.getTotalSlots();
                    int availableSlots = totalSlots - confirmedSlots - successfulCount;
                    
                    // Only approve if slots are available
                    if (availableSlots > 0) {
                        application.setStatus(ApplicationStatus.SUCCESSFUL);
                        updateApplication(application);
                    }
                    // If no slots available, application remains in current status (typically PENDING)
                } else {
                    // If internship not found, still allow approval (edge case)
                    application.setStatus(ApplicationStatus.SUCCESSFUL);
                    updateApplication(application);
                }
                break;
            case 2:
                application.setStatus(ApplicationStatus.UNSUCCESSFUL);
                updateApplication(application);
                break;
            default:
                break;
        }

    }

    /**
     * Gets applications for an internship filtered by status.
     *
     * @param internshipId internship identifier
     * @param status status to filter by
     * @return list of applications
     */
    public List<Application> getApplicationsByInternshipandStatus(String internshipId, ApplicationStatus status) {
        return getApplicationsByInternship(
                        internshipId).stream()
                .filter(a -> a.getStatus() == status)
                .collect(java.util.stream.Collectors.toList());
    } 

    /**
     * Gets applications for a student filtered by status.
     *
     * @param studentId student identifier
     * @param status status to filter by
     * @return list of applications
     */
    public List<Application> getApplicationsByStudentandStatus(String studentId, ApplicationStatus status) {
        return getApplicationsByStudent(studentId)
            .stream()
            .filter(a -> a.getStatus() == status)
            .collect(Collectors.toList());
    }

    /**
     * Counts applications with a specific status.
     *
     * @param apps list of applications
     * @param status status to count
     * @return count of applications
     */
    public long getApplicationCount(List<Application> apps, ApplicationStatus status) {
        return apps.stream().filter(a -> a.getStatus() == status).count();
    }

    /**
     * Handles student acceptance of a placement offer.
     * Updates application status, student placement, and withdraws other successful applications.
     *
     * @param student student accepting the offer
     * @param application application being accepted
     */
    public void handleApplicationAcceptance(Student student, Application application) {
        application.setStatus(ApplicationStatus.CONFIRMED);
        updateApplication(application);

        student.setConfirmedPlacementId(application.getInternshipId());
        UserManager.getInstance().updateUser(student);

        InternshipManager internshipManager = InternshipManager.getInstance();
        Internship internship = internshipManager.getInternshipById(application.getInternshipId());
        if (internship == null) {
            return;
        }
        internship.incrementConfirmedSlots();
        internshipManager.updateInternship(internship);
        List<Application> otherApps = getApplicationsByStudent(student.getUserId())
                .stream()
                .filter(a -> !a.getApplicationId().equals(application.getApplicationId()))
                .filter(a -> a.getStatus() == ApplicationStatus.SUCCESSFUL)
                .collect(Collectors.toList());

        for (Application app : otherApps) {
            app.setStatus(ApplicationStatus.UNSUCCESSFUL);
            updateApplication(app);
        }
    }
}
