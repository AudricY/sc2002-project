package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, FORMATTER);
    }

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(FORMATTER) : "";
    }
}