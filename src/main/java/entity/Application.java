package entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Application implements Serializable {
    private static final long serialVersionUID = 1L;

    private String applicationId;
    private String studentId;
    private String internshipId;
    private ApplicationStatus status;
    private LocalDateTime applicationDate;
    private boolean confirmed;

    public Application(String applicationId, String studentId, String internshipId) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.internshipId = internshipId;
        this.status = ApplicationStatus.PENDING;
        this.applicationDate = LocalDateTime.now();
        this.confirmed = false;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(String internshipId) {
        this.internshipId = internshipId;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
