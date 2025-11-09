package control;

import entity.*;
import util.FileManager;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Function;

/**
 * Manages internship operations including CRUD, filtering, and eligibility checks.
 * Uses singleton pattern to ensure single instance.
 */
public class InternshipManager {
    private static final String INTERNSHIPS_FILE = "internships.dat";
    private List<Internship> internships;
    private static InternshipManager instance;

    /**
     * Private constructor for singleton pattern.
     * Initializes internships list from file.
     */
    private InternshipManager() {
        this.internships = FileManager.loadFromFile(INTERNSHIPS_FILE);
    }

    /**
     * Returns the singleton instance of InternshipManager.
     *
     * @return InternshipManager instance
     */
    public static InternshipManager getInstance() {
        if (instance == null) {
            instance = new InternshipManager();
        }
        return instance;
    }

    /**
     * Saves all internships to file.
     */
    public void saveInternships() {
        FileManager.saveToFile(INTERNSHIPS_FILE, internships);
    }

    /**
     * Adds a new internship and saves to file.
     *
     * @param internshipId unique internship identifier
     * @param title internship title
     * @param description internship description
     * @param level required level
     * @param preferredMajor preferred major
     * @param openingDate opening date
     * @param closingDate closing date
     * @param companyName company name
     * @param representativeId representative ID
     * @param totalSlots total available slots
     */
    public void addInternship(String internshipId, String title, String description, InternshipLevel level, String preferredMajor, LocalDate openingDate, LocalDate closingDate, String companyName, String representativeId, int totalSlots) {
        internships.add(new Internship(internshipId, title, description, level, preferredMajor, openingDate, closingDate, companyName, representativeId, totalSlots));
        saveInternships();
    }

    /**
     * Gets an internship by ID.
     *
     * @param internshipId internship identifier
     * @return internship if found, null otherwise
     */
    public Internship getInternshipById(String internshipId) {
        return internships.stream()
                .filter(i -> i.getInternshipId().equals(internshipId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Updates an existing internship and saves to file.
     *
     * @param internship updated internship object
     */
    public void updateInternship(Internship internship) {
        for (int i = 0; i < internships.size(); i++) {
            if (internships.get(i).getInternshipId().equals(internship.getInternshipId())) {
                internships.set(i, internship);
                saveInternships();
                return;
            }
        }
    }

    /**
     * Gets all internships posted by a representative.
     *
     * @param representativeId representative identifier
     * @return list of internships
     */
    public List<Internship> getInternshipsByRepresentative(String representativeId) {
        return internships.stream()
                .filter(i -> i.getRepresentativeId().equals(representativeId))
                .collect(Collectors.toList());
    }

    /**
     * Gets visible and eligible internships for a student.
     *
     * @param student student to check eligibility for
     * @return list of eligible internships
     */
    public List<Internship> getVisibleInternshipsForStudent(Student student) {
        return internships.stream()
                .filter(i -> i.isVisible() && i.getStatus() == InternshipStatus.APPROVED)
                .filter(i -> isEligibleForInternship(student, i))
                .collect(Collectors.toList());
    }

    /**
     * Checks if a student is eligible for an internship based on year, level, and major.
     *
     * @param student student to check
     * @param internship internship to check eligibility for
     * @return true if eligible, false otherwise
     */
    private boolean isEligibleForInternship(Student student, Internship internship) {
        int year = student.getYearOfStudy();
        InternshipLevel level = internship.getLevel();

        // Year 1-2 students can only see BASIC level
        if (year <= 2 && level != InternshipLevel.BASIC) {
            return false;
        }

        // Students can only see internships matching their major
        if (!student.getMajor().equalsIgnoreCase(internship.getPreferredMajor())) {
            return false;
        }

        return true;
    }

    /**
     * Gets all internships pending staff approval.
     *
     * @return list of pending internships
     */
    public List<Internship> getPendingInternships() {
        return internships.stream()
                .filter(i -> i.getStatus() == InternshipStatus.PENDING)
                .collect(Collectors.toList());
    }

    /**
     * Filters and sorts internships based on filter settings.
     *
     * @param list list of internships to filter
     * @param settings filter settings to apply
     * @return filtered and sorted list
     */
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

    /**
     * Counts internships posted by a representative.
     *
     * @param representativeId representative identifier
     * @return count of internships
     */
    public int countInternshipsByRepresentative(String representativeId) {
        return (int) internships.stream()
                .filter(i -> i.getRepresentativeId().equals(representativeId))
                .count();
    }

    /**
     * Reviews and updates an internship's approval status.
     *
     * @param internship internship to review
     * @param decision 1 for approve, 2 for reject
     */
    public void reviewInternship(Internship internship, int decision) {
        switch(decision){
            case 1:
                internship.setStatus(InternshipStatus.APPROVED);
                internship.setVisible(true); // Ensure approved internships are visible to students
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

    /**
     * Edits a specific field of an internship.
     *
     * @param internship internship to edit
     * @param field field number (1=title, 2=description, 3=major)
     * @param newValue new value for the field
     */
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
        // Reset to pending to account for rejected
        internship.setStatus(InternshipStatus.PENDING);
        updateInternship(internship);
    }

    /**
     * Toggles the visibility of an internship.
     *
     * @param internship internship to toggle
     */
    public void toggleInternshipVisibility(Internship internship) {
        internship.setVisible(!internship.isVisible());
        updateInternship(internship);
    }

    /**
     * Groups all internships by a specified field.
     *
     * @param groupByField function to extract grouping key
     * @param <K> type of grouping key
     * @return map of grouped internships
     */
    public <K> Map<K, List<Internship>> getAllInternships(Function<Internship, K> groupByField) {
        return internships.stream()
            .collect(Collectors.groupingBy(groupByField));

    }

    /**
     * Gets all internships in the system.
     *
     * @return list of all internships
     */
    public List<Internship> getAllInternships() {
        return new ArrayList<>(internships);
    }
}
