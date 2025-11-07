package entity;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Stores user-specific filter and sorting preferences for internship listings.
 */
public class FilterSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private InternshipLevel levelFilter;
    private String majorFilter;
    private InternshipStatus statusFilter;
    private SortCriteria sortBy;

    /**
     * Available sorting criteria for internship listings.
     */
    public enum SortCriteria {
        /** Sort by title alphabetically */
        ALPHABETICAL,
        /** Sort by opening date */
        OPENING_DATE,
        /** Sort by closing date */
        CLOSING_DATE,
        /** Sort by internship level */
        LEVEL
    }

    /**
     * Creates filter settings for a user.
     *
     * @param userId user identifier
     */
    public FilterSettings(String userId) {
        this.userId = userId;
        this.levelFilter = null;
        this.majorFilter = null;
        this.statusFilter = null;
        this.sortBy = SortCriteria.ALPHABETICAL;
    }

    public String getUserId() {
        return userId;
    }

    public InternshipLevel getLevelFilter() {
        return levelFilter;
    }

    public void setLevelFilter(InternshipLevel levelFilter) {
        this.levelFilter = levelFilter;
    }

    public String getMajorFilter() {
        return majorFilter;
    }

    public void setMajorFilter(String majorFilter) {
        this.majorFilter = majorFilter;
    }

    public InternshipStatus getStatusFilter() {
        return statusFilter;
    }

    public void setStatusFilter(InternshipStatus statusFilter) {
        this.statusFilter = statusFilter;
    }

    public SortCriteria getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortCriteria sortBy) {
        this.sortBy = sortBy;
    }

    /**
     * Returns a comparator based on the current sort criteria.
     *
     * @return comparator for sorting internships
     */
    public Comparator<Internship> getComparator() {
        switch (sortBy) {
            case OPENING_DATE:
                return Comparator.comparing(Internship::getOpeningDate);
            case CLOSING_DATE:
                return Comparator.comparing(Internship::getClosingDate);
            case LEVEL:
                return Comparator.comparing(Internship::getLevel);
            case ALPHABETICAL:
            default:
                return Comparator.comparing(Internship::getTitle);
        }
    }
}
