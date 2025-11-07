package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date parsing and formatting.
 */
public class DateUtils {
    /** Date formatter for yyyy-MM-dd format */
    public static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Parses a date string to LocalDate.
     *
     * @param date date string in yyyy-MM-dd format
     * @return parsed LocalDate
     */
    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, FORMATTER);
    }

    /**
     * Formats a LocalDate to string.
     *
     * @param date date to format
     * @return formatted date string, empty string if date is null
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(FORMATTER) : "";
    }
}