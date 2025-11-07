package entity;

/**
 * Represents a career center staff member.
 * Has administrative privileges to approve representatives and internships.
 */
public class CareerCenterStaff extends User {
    private static final long serialVersionUID = 1L;

    private String staffDepartment;

    /**
     * Creates a new career center staff member.
     *
     * @param userId unique user identifier
     * @param password user password
     * @param name staff member's full name
     * @param email staff member's email address
     * @param staffDepartment department the staff member belongs to
     */
    public CareerCenterStaff(String userId, String password, String name, String email,
                             String staffDepartment) {
        super(userId, password, name, email, UserRole.CAREER_CENTER_STAFF);
        this.staffDepartment = staffDepartment;
    }

    public String getStaffDepartment() {
        return staffDepartment;
    }

    public void setStaffDepartment(String staffDepartment) {
        this.staffDepartment = staffDepartment;
    }

    @Override
    public String getProfileInfo() {
        return String.format("Staff Department: %s", staffDepartment);
    }
}
