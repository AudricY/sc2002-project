package entity;

public class Student extends User {
    private static final long serialVersionUID = 1L;

    private int yearOfStudy;
    private String major;
    private String confirmedPlacementId;

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

    public boolean hasConfirmedPlacement() {
        return confirmedPlacementId != null;
    }

    @Override
    public String getProfileInfo() {
        return String.format("Year of Study: %d, Major: %s", yearOfStudy, major);
    }
}
