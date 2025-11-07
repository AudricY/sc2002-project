package entity;

/**
 * Status of an internship opportunity.
 */
public enum InternshipStatus {
    /** Awaiting staff approval */
    PENDING,
    /** Approved and open for applications */
    APPROVED,
    /** Rejected by staff */
    REJECTED,
    /** All slots are filled */
    FILLED
}
