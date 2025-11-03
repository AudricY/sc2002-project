package control;

import entity.*;
import util.FileManager;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;

public class ApplicationManager {
    private static final String APPLICATIONS_FILE = "applications.dat";
    private List<Application> applications;
    private static ApplicationManager instance;

    private ApplicationManager() {
        this.applications = FileManager.loadFromFile(APPLICATIONS_FILE);
    }

    public static ApplicationManager getInstance() {
        if (instance == null) {
            instance = new ApplicationManager();
        }
        return instance;
    }

    public void saveApplications() {
        FileManager.saveToFile(APPLICATIONS_FILE, applications);
    }

    public boolean addApplication(String applicationId, String studentId, String internshipId) {
        // Enforce date limits
        InternshipManager internshipManager = InternshipManager.getInstance();
        LocalDate closingDate = internshipManager.getInternshipById(internshipId).getClosingDate();
        LocalDate today = LocalDate.now();
        if (today.isAfter(closingDate)) return false;
        Application application = new Application(applicationId, studentId,
                internshipId);
        applications.add(application);
        saveApplications();
        return true;
    }

    public Application getApplicationById(String applicationId) {
        return applications.stream()
                .filter(a -> a.getApplicationId().equals(applicationId))
                .findFirst()
                .orElse(null);
    }

    public void updateApplication(Application application) {
        for (int i = 0; i < applications.size(); i++) {
            if (applications.get(i).getApplicationId().equals(application.getApplicationId())) {
                applications.set(i, application);
                saveApplications();
                return;
            }
        }
    }

    public void removeApplication(Application application) {
        applications.remove(application);
        saveApplications();
    }

    public List<Application> getApplicationsByStudent(String studentId) {
        return applications.stream()
                .filter(a -> a.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public List<Application> getApplicationsByInternship(String internshipId) {
        return applications.stream()
                .filter(a -> a.getInternshipId().equals(internshipId))
                .collect(Collectors.toList());
    }

    public int countPendingApplicationsByStudent(String studentId) {
        return (int) applications.stream()
                .filter(a -> a.getStudentId().equals(studentId))
                .filter(a -> a.getStatus() == ApplicationStatus.PENDING)
                .count();
    }

    public boolean hasAppliedToInternship(String studentId, String internshipId) {
        return applications.stream()
                .anyMatch(a -> a.getStudentId().equals(studentId) &&
                        a.getInternshipId().equals(internshipId));
    }

    public void reviewApplication(Application application, int decision) {
        switch (decision) {
            case 1:
                application.setStatus(ApplicationStatus.SUCCESSFUL);
                updateApplication(application);
                break;
            case 2:
                application.setStatus(ApplicationStatus.UNSUCCESSFUL);
                updateApplication(application);
            default:
                break;
        }

    }

    public List<Application> getApplicationsByInternshipandStatus(String internshipId, ApplicationStatus status) {
        return getApplicationsByInternship(
                        internshipId).stream()
                .filter(a -> a.getStatus() == status)
                .collect(java.util.stream.Collectors.toList());
    } 

    public List<Application> getApplicationsByStudentandStatus(String studentId, ApplicationStatus status) {
        return getApplicationsByStudent(studentId)
            .stream()
            .filter(a -> a.getStatus() == status)
            .collect(Collectors.toList());
    }

    public long getApplicationCount(List<Application> apps, ApplicationStatus status) {
        return apps.stream().filter(a -> a.getStatus() == status).count();
    }

    public void handleApplicationAcceptance(Student student, Application application) {
        application.setStatus(ApplicationStatus.CONFIRMED);
        updateApplication(application);

        student.setConfirmedPlacementId(application.getInternshipId());
        UserManager.getInstance().updateUser(student);

        InternshipManager internshipManager = InternshipManager.getInstance();
        Internship internship = internshipManager.getInternshipById(application.getInternshipId());
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
