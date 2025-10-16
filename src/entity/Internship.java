package entity;

import java.io.Serializable;
import java.time.LocalDate;

public class Internship implements Serializable {
    private static final long serialVersionUID = 1L;

    private String internshipId;
    private String title;
    private String description;
    private InternshipLevel level;
    private String preferredMajor;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private InternshipStatus status;
    private String companyName;
    private String representativeId;
    private int totalSlots;
    private int confirmedSlots;
    private boolean visible;

    public Internship(String internshipId, String title, String description,
                      InternshipLevel level, String preferredMajor,
                      LocalDate openingDate, LocalDate closingDate,
                      String companyName, String representativeId, int totalSlots) {
        this.internshipId = internshipId;
        this.title = title;
        this.description = description;
        this.level = level;
        this.preferredMajor = preferredMajor;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.status = InternshipStatus.PENDING;
        this.companyName = companyName;
        this.representativeId = representativeId;
        this.totalSlots = totalSlots;
        this.confirmedSlots = 0;
        this.visible = true;
    }

    public String getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(String internshipId) {
        this.internshipId = internshipId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InternshipLevel getLevel() {
        return level;
    }

    public void setLevel(InternshipLevel level) {
        this.level = level;
    }

    public String getPreferredMajor() {
        return preferredMajor;
    }

    public void setPreferredMajor(String preferredMajor) {
        this.preferredMajor = preferredMajor;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(LocalDate openingDate) {
        this.openingDate = openingDate;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public InternshipStatus getStatus() {
        return status;
    }

    public void setStatus(InternshipStatus status) {
        this.status = status;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRepresentativeId() {
        return representativeId;
    }

    public void setRepresentativeId(String representativeId) {
        this.representativeId = representativeId;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
    }

    public int getConfirmedSlots() {
        return confirmedSlots;
    }

    public void setConfirmedSlots(int confirmedSlots) {
        this.confirmedSlots = confirmedSlots;
    }

    public void incrementConfirmedSlots() {
        this.confirmedSlots++;
        if (this.confirmedSlots >= this.totalSlots) {
            this.status = InternshipStatus.FILLED;
        }
    }

    public void decrementConfirmedSlots() {
        if (this.confirmedSlots > 0) {
            this.confirmedSlots--;
            if (this.status == InternshipStatus.FILLED) {
                this.status = InternshipStatus.APPROVED;
            }
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isApproved() {
        return status == InternshipStatus.APPROVED;
    }

    public boolean isFilled() {
        return status == InternshipStatus.FILLED;
    }

    public boolean hasAvailableSlots() {
        return confirmedSlots < totalSlots;
    }
}
