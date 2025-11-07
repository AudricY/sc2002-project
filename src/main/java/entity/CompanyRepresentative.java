package entity;

/**
 * Represents a company representative user.
 * Requires approval from career center staff before accessing the system.
 */
public class CompanyRepresentative extends User {
    private static final long serialVersionUID = 1L;

    private String companyName;
    private String department;
    private String position;
    /** Approval status for system access */
    private ApprovalStatus approvalStatus;

    /**
     * Creates a new company representative user.
     *
     * @param userId unique user identifier
     * @param password user password
     * @param name representative's full name
     * @param email representative's email address
     * @param companyName name of the company
     * @param department department within the company
     * @param position job position/title
     */
    public CompanyRepresentative(String userId, String password, String name, String email,
                                 String companyName, String department, String position) {
        super(userId, password, name, email, UserRole.COMPANY_REPRESENTATIVE);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.approvalStatus = ApprovalStatus.PENDING;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    /**
     * Checks if the representative has been approved.
     *
     * @return true if approved, false otherwise
     */
    public boolean isApproved() {
        return approvalStatus == ApprovalStatus.APPROVED;
    }

    @Override
    public String getProfileInfo() {
        return String.format("Company: %s, Department: %s, Position: %s, Status: %s",
                companyName, department, position, approvalStatus);
    }
}
