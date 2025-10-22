package util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class InputValidator {

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateUtils.FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isPositiveInteger(String input) {
        try {
            int value = Integer.parseInt(input);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
}
