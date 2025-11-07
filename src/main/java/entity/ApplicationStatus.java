package entity;

/**
 * Status of a student's internship application.
 */
public enum ApplicationStatus {
    /** Application is pending review */
    PENDING,
    /** Application was approved by company */
    SUCCESSFUL,
    /** Application was rejected */
    UNSUCCESSFUL,
    /** Student confirmed the placement */
    CONFIRMED
}
