package control;

import entity.*;
import util.FileManager;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Function;

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

    public void addInternship(String internshipId, String title, String description, InternshipLevel level, String preferredMajor, LocalDate openingDate, LocalDate closingDate, String companyName, String representativeId, int totalSlots) {
        internships.add(new Internship(internshipId, title, description, level, preferredMajor, openingDate, closingDate, companyName, representativeId, totalSlots));
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

    public void reviewInternship(Internship internship, int decision) {
        switch(decision){
            case 1:
                internship.setStatus(InternshipStatus.APPROVED);
                updateInternship(internship);
                break;
            case 2:
                internship.setStatus(InternshipStatus.REJECTED);
                updateInternship(internship);
                break;
            default:
                break;
        }
    }

    public void editInternshipField(Internship internship, int field, String newValue) {
        switch (field) {
            case 1:
                internship.setTitle(newValue);
                break;
            case 2:
                internship.setDescription(newValue);
                break;
            case 3:
                internship.setPreferredMajor(newValue);
                break;
            default:
                break;
        }
        updateInternship(internship);
    }

    public void toggleInternshipVisibility(Internship internship) {
        internship.setVisible(!internship.isVisible());
        updateInternship(internship);
    }

    public <K> Map<K, List<Internship>> getAllInternships(Function<Internship, K> groupByField) {
        return internships.stream()
            .collect(Collectors.groupingBy(groupByField));

    }
}
