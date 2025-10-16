package entity;

public class CompanyRepresentative extends User {
    private static final long serialVersionUID = 1L;

    private String companyName;
    private String department;
    private String position;
    private ApprovalStatus approvalStatus;

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

    public boolean isApproved() {
        return approvalStatus == ApprovalStatus.APPROVED;
    }

    @Override
    public String getProfileInfo() {
        return String.format("Company: %s, Department: %s, Position: %s, Status: %s",
                companyName, department, position, approvalStatus);
    }
}
