package control;

import entity.*;
import util.FileManager;
import java.util.*;
import java.util.stream.Collectors;

public class InternshipManager {
    private static final String INTERNSHIPS_FILE = "internships.dat";
    private List<Internship> internships;
    private static InternshipManager instance;

    private InternshipManager() {
        this.internships = FileManager.loadFromFile(INTERNSHIPS_FILE);
    }

    public static InternshipManager getInstance() {
        if (instance == null) {
            instance = new InternshipManager();
        }
        return instance;
    }

    public void saveInternships() {
        FileManager.saveToFile(INTERNSHIPS_FILE, internships);
    }

    public void addInternship(Internship internship) {
        internships.add(internship);
        saveInternships();
    }

    public Internship getInternshipById(String internshipId) {
        return internships.stream()
                .filter(i -> i.getInternshipId().equals(internshipId))
                .findFirst()
                .orElse(null);
    }

    public void updateInternship(Internship internship) {
        for (int i = 0; i < internships.size(); i++) {
            if (internships.get(i).getInternshipId().equals(internship.getInternshipId())) {
                internships.set(i, internship);
                saveInternships();
                return;
            }
        }
    }

    public List<Internship> getInternshipsByRepresentative(String representativeId) {
        return internships.stream()
                .filter(i -> i.getRepresentativeId().equals(representativeId))
                .collect(Collectors.toList());
    }

    public List<Internship> getVisibleInternshipsForStudent(Student student) {
        return internships.stream()
                .filter(i -> i.isVisible() && i.getStatus() == InternshipStatus.APPROVED)
                .filter(i -> isEligibleForInternship(student, i))
                .collect(Collectors.toList());
    }

    private boolean isEligibleForInternship(Student student, Internship internship) {
        int year = student.getYearOfStudy();
        InternshipLevel level = internship.getLevel();

        if (year <= 2 && level != InternshipLevel.BASIC) {
            return false;
        }

        return true;
    }

    public List<Internship> getPendingInternships() {
        return internships.stream()
                .filter(i -> i.getStatus() == InternshipStatus.PENDING)
                .collect(Collectors.toList());
    }

    public List<Internship> filterInternships(List<Internship> list, FilterSettings settings) {
        List<Internship> filtered = new ArrayList<>(list);

        if (settings.getLevelFilter() != null) {
            filtered = filtered.stream()
                    .filter(i -> i.getLevel() == settings.getLevelFilter())
                    .collect(Collectors.toList());
        }

        if (settings.getMajorFilter() != null && !settings.getMajorFilter().isEmpty()) {
            filtered = filtered.stream()
                    .filter(i -> i.getPreferredMajor().equalsIgnoreCase(settings.getMajorFilter()))
                    .collect(Collectors.toList());
        }

        if (settings.getStatusFilter() != null) {
            filtered = filtered.stream()
                    .filter(i -> i.getStatus() == settings.getStatusFilter())
                    .collect(Collectors.toList());
        }

        filtered.sort(settings.getComparator());
        return filtered;
    }

    public int countInternshipsByRepresentative(String representativeId) {
        return (int) internships.stream()
                .filter(i -> i.getRepresentativeId().equals(representativeId))
                .count();
    }
}
