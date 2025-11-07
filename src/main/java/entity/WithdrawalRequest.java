package entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a student's request to withdraw from an application or placement.
 * Requires approval from career center staff.
 */
public class WithdrawalRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;
    private String studentId;
    private String applicationId;
    private String reason;
    private ApprovalStatus status;
    private LocalDateTime requestDate;
    private String reviewedByStaffId;
    private LocalDateTime reviewDate;

    /**
     * Creates a new withdrawal request.
     *
     * @param requestId unique request identifier
     * @param studentId ID of the student making the request
     * @param applicationId ID of the application to withdraw from
     * @param reason reason for withdrawal
     */
    public WithdrawalRequest(String requestId, String studentId, String applicationId, String reason) {
        this.requestId = requestId;
        this.studentId = studentId;
        this.applicationId = applicationId;
        this.reason = reason;
        this.status = ApprovalStatus.PENDING;
        this.requestDate = LocalDateTime.now();
        this.reviewedByStaffId = null;
        this.reviewDate = null;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public String getReviewedByStaffId() {
        return reviewedByStaffId;
    }

    public void setReviewedByStaffId(String reviewedByStaffId) {
        this.reviewedByStaffId = reviewedByStaffId;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }
}
