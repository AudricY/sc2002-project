package entity;

/**
 * Represents a student user in the system.
 * Contains academic information and placement status.
 */
public class Student extends User {
    private static final long serialVersionUID = 1L;

    private int yearOfStudy;
    private String major;
    /** ID of the internship placement the student has confirmed */
    private String confirmedPlacementId;

    /**
     * Creates a new student user.
     *
     * @param userId unique user identifier
     * @param password user password
     * @param name student's full name
     * @param email student's email address
     * @param yearOfStudy year of study (1-4)
     * @param major student's major field of study
     */
    public Student(String userId, String password, String name, String email,
                   int yearOfStudy, String major) {
        super(userId, password, name, email, UserRole.STUDENT);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
        this.confirmedPlacementId = null;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getConfirmedPlacementId() {
        return confirmedPlacementId;
    }

    public void setConfirmedPlacementId(String confirmedPlacementId) {
        this.confirmedPlacementId = confirmedPlacementId;
    }

    /**
     * Checks if the student has confirmed a placement.
     *
     * @return true if a placement has been confirmed, false otherwise
     */
    public boolean hasConfirmedPlacement() {
        return confirmedPlacementId != null;
    }

    @Override
    public String getProfileInfo() {
        return String.format("Year of Study: %d, Major: %s", yearOfStudy, major);
    }
}
