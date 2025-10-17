package entity;

public class CareerCenterStaff extends User {
    private static final long serialVersionUID = 1L;

    private String staffDepartment;

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
