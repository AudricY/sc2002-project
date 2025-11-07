package util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Utility class for validating user input.
 */
public class InputValidator {

    /**
     * Validates email format.
     *
     * @param email email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Validates password meets minimum length requirement.
     *
     * @param password password to validate
     * @return true if valid (at least 6 characters), false otherwise
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Validates date string format.
     *
     * @param date date string to validate
     * @return true if valid yyyy-MM-dd format, false otherwise
     */
    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateUtils.FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Validates if input is a positive integer.
     *
     * @param input string to validate
     * @return true if positive integer, false otherwise
     */
    public static boolean isPositiveInteger(String input) {
        try {
            int value = Integer.parseInt(input);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates if value is within range.
     *
     * @param value value to check
     * @param min minimum value
     * @param max maximum value
     * @return true if within range, false otherwise
     */
    public static boolean isValidRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
}
