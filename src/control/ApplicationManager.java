package control;

import entity.*;
import util.FileManager;
import java.util.*;
import java.util.stream.Collectors;

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

    public void addApplication(Application application) {
        applications.add(application);
        saveApplications();
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

    public Application getConfirmedApplication(String studentId) {
        return applications.stream()
                .filter(a -> a.getStudentId().equals(studentId))
                .filter(Application::isConfirmed)
                .findFirst()
                .orElse(null);
    }
}
