package util;

/**
 * Result of a password change attempt.
 */
public enum PasswordChangeResult {
    /** Password changed successfully */
    SUCCESS,
    /** New password and confirmation don't match */
    MISMATCH,
    /** Password format is invalid */
    INVALID_FORMAT,
    /** New password is same as old password */
    DUPLICATE,
    /** Old password is incorrect */
    INCORRECT_OLD
}
