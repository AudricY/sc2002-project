import util.InputValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for InputValidator utility class.
 * Tests I/O handling and validation logic for various input types.
 */
@DisplayName("Input Validator Tests")
public class InputValidatorTests {

    @Test
    @DisplayName("Valid Date Format Tests")
    public void testValidDateFormats() {
        // Valid yyyy-MM-dd format
        assertTrue(InputValidator.isValidDate("2025-01-15"), "Valid date should pass");
        assertTrue(InputValidator.isValidDate("2025-12-31"), "Valid date at year end should pass");
        assertTrue(InputValidator.isValidDate("2024-02-29"), "Valid leap year date should pass");
        assertTrue(InputValidator.isValidDate("2025-06-01"), "Valid mid-year date should pass");
    }

    @Test
    @DisplayName("Invalid Date Format Tests")
    public void testInvalidDateFormats() {
        // Invalid formats
        assertFalse(InputValidator.isValidDate("2025/01/15"), "Slash format should fail");
        assertFalse(InputValidator.isValidDate("15-01-2025"), "dd-MM-yyyy format should fail");
        assertFalse(InputValidator.isValidDate("01/15/2025"), "MM/dd/yyyy format should fail");
        assertFalse(InputValidator.isValidDate("2025-1-15"), "Single digit month should fail");
        assertFalse(InputValidator.isValidDate("2025-01-5"), "Single digit day should fail");
        assertFalse(InputValidator.isValidDate("25-01-15"), "Two digit year should fail");
    }

    @Test
    @DisplayName("Invalid Date Values Tests")
    public void testInvalidDateValues() {
        // Invalid dates
        assertFalse(InputValidator.isValidDate("2025-13-01"), "Invalid month should fail");
        assertFalse(InputValidator.isValidDate("2025-00-01"), "Zero month should fail");
        assertFalse(InputValidator.isValidDate("2025-01-32"), "Invalid day should fail");
        assertFalse(InputValidator.isValidDate("2025-02-30"), "Invalid day for February should fail");
        assertFalse(InputValidator.isValidDate("2025-04-31"), "Invalid day for April should fail");
        assertFalse(InputValidator.isValidDate("2024-02-30"), "Invalid leap year day should fail");
    }

    @Test
    @DisplayName("Null and Empty Date Tests")
    public void testNullAndEmptyDateInputs() {
        // Null and empty inputs
        assertFalse(InputValidator.isValidDate(null), "Null date should fail");
        assertFalse(InputValidator.isValidDate(""), "Empty string should fail");
        assertFalse(InputValidator.isValidDate("   "), "Whitespace only should fail");
    }

    @Test
    @DisplayName("Malformed Date String Tests")
    public void testMalformedDateStrings() {
        // Malformed strings
        assertFalse(InputValidator.isValidDate("abc"), "Non-numeric string should fail");
        assertFalse(InputValidator.isValidDate("2025-abc-15"), "Non-numeric month should fail");
        assertFalse(InputValidator.isValidDate("2025-01-abc"), "Non-numeric day should fail");
        assertFalse(InputValidator.isValidDate("not-a-date"), "Completely invalid string should fail");
        assertFalse(InputValidator.isValidDate("2025-01"), "Incomplete date should fail");
        assertFalse(InputValidator.isValidDate("2025-01-15-extra"), "Extra characters should fail");
    }

    @Test
    @DisplayName("Valid Email Format Tests")
    public void testValidEmailFormats() {
        assertTrue(InputValidator.isValidEmail("user@example.com"), "Standard email should pass");
        assertTrue(InputValidator.isValidEmail("test.user@domain.co.uk"), "Email with subdomain should pass");
        assertTrue(InputValidator.isValidEmail("user+tag@example.com"), "Email with plus should pass");
        assertTrue(InputValidator.isValidEmail("user_name@example.com"), "Email with underscore should pass");
        assertTrue(InputValidator.isValidEmail("user123@example123.com"), "Email with numbers should pass");
    }

    @Test
    @DisplayName("Invalid Email Format Tests")
    public void testInvalidEmailFormats() {
        assertFalse(InputValidator.isValidEmail(null), "Null email should fail");
        assertFalse(InputValidator.isValidEmail(""), "Empty email should fail");
        assertFalse(InputValidator.isValidEmail("invalid"), "Email without @ should fail");
        assertFalse(InputValidator.isValidEmail("@example.com"), "Email without local part should fail");
        assertFalse(InputValidator.isValidEmail("user@"), "Email without domain should fail");
        assertFalse(InputValidator.isValidEmail("user @example.com"), "Email with space should fail");
    }

    @Test
    @DisplayName("Valid Password Length Tests")
    public void testValidPasswordLengths() {
        assertTrue(InputValidator.isValidPassword("123456"), "6 character password should pass");
        assertTrue(InputValidator.isValidPassword("password123"), "Longer password should pass");
        assertTrue(InputValidator.isValidPassword("abcdefghijklmnop"), "Very long password should pass");
    }

    @Test
    @DisplayName("Invalid Password Length Tests")
    public void testInvalidPasswordLengths() {
        assertFalse(InputValidator.isValidPassword(null), "Null password should fail");
        assertFalse(InputValidator.isValidPassword(""), "Empty password should fail");
        assertFalse(InputValidator.isValidPassword("12345"), "5 character password should fail");
        assertFalse(InputValidator.isValidPassword("1234"), "4 character password should fail");
        assertFalse(InputValidator.isValidPassword("1"), "Single character password should fail");
    }

    @Test
    @DisplayName("Positive Integer Validation Tests")
    public void testPositiveIntegerValidation() {
        assertTrue(InputValidator.isPositiveInteger("1"), "Single digit positive integer should pass");
        assertTrue(InputValidator.isPositiveInteger("123"), "Multi-digit positive integer should pass");
        assertTrue(InputValidator.isPositiveInteger("999999"), "Large positive integer should pass");
        
        assertFalse(InputValidator.isPositiveInteger("0"), "Zero should fail");
        assertFalse(InputValidator.isPositiveInteger("-1"), "Negative number should fail");
        assertFalse(InputValidator.isPositiveInteger(null), "Null should fail");
        assertFalse(InputValidator.isPositiveInteger(""), "Empty string should fail");
        assertFalse(InputValidator.isPositiveInteger("abc"), "Non-numeric string should fail");
        assertFalse(InputValidator.isPositiveInteger("12.5"), "Decimal should fail");
    }

    @Test
    @DisplayName("Range Validation Tests")
    public void testRangeValidation() {
        assertTrue(InputValidator.isValidRange(5, 1, 10), "Value in range should pass");
        assertTrue(InputValidator.isValidRange(1, 1, 10), "Value at minimum should pass");
        assertTrue(InputValidator.isValidRange(10, 1, 10), "Value at maximum should pass");
        
        assertFalse(InputValidator.isValidRange(0, 1, 10), "Value below minimum should fail");
        assertFalse(InputValidator.isValidRange(11, 1, 10), "Value above maximum should fail");
        assertFalse(InputValidator.isValidRange(-5, 1, 10), "Negative value should fail");
    }
}

