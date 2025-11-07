package control;

import entity.*;
import util.InputValidator;
import util.PasswordChangeResult;

/**
 * Handles user authentication, login, logout, and password changes.
 */
public class AuthenticationController {
    private UserManager userManager;
    private User currentUser;

    /**
     * Creates a new authentication controller.
     */
    public AuthenticationController() {
        this.userManager = UserManager.getInstance();
        this.currentUser = null;
    }

    /**
     * Attempts to log in a user.
     * Company representatives must be approved.
     *
     * @param userId user identifier
     * @param password user password
     * @return true if login successful, false otherwise
     */
    public boolean login(String userId, String password) {
        User user = userManager.authenticateUser(userId, password);
        if (user == null) {
            return false;
        }

        if (user instanceof CompanyRepresentative) {
            CompanyRepresentative rep = (CompanyRepresentative) user;
            if (!rep.isApproved()) {
                return false;
            }
        }

        this.currentUser = user;
        return true;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Gets the currently logged in user.
     *
     * @return current user, or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Changes the current user's password if old password is correct.
     *
     * @param oldPassword current password
     * @param newPassword new password to set
     * @return true if password changed successfully, false otherwise
     */
    private boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null || !currentUser.getPassword().equals(oldPassword)) {
            return false;   
        }

        currentUser.setPassword(newPassword);
        if (currentUser.isFirstLogin()) {
            currentUser.setFirstLogin(false);
        }
        userManager.updateUser(currentUser);
        return true;
    }

    /**
     * Attempts to change the current user's password.
     *
     * @param oldPassword current password
     * @param newPassword new password
     * @param confirmPassword confirmation of new password
     * @return result of password change attempt
     */
    public PasswordChangeResult attemptPasswordChange(String oldPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) return PasswordChangeResult.MISMATCH;
        if (!InputValidator.isValidPassword(newPassword)) return PasswordChangeResult.INVALID_FORMAT;
        if (oldPassword.equals(newPassword)) return PasswordChangeResult.DUPLICATE;
        if (!changePassword(oldPassword, newPassword)) return PasswordChangeResult.INCORRECT_OLD;
        return PasswordChangeResult.SUCCESS;
    }

    /**
     * Registers a new company representative.
     *
     * @param name representative name
     * @param email email address (used as userId)
     * @param password password
     * @param companyName company name
     * @param department department
     * @param position position/title
     * @return true if registration successful, false if user already exists
     */
    public boolean registerCompanyRepresentative(String name, String email, String password,
                                                  String companyName, String department,
                                                  String position) {
        if (userExists(email)) return false;
        CompanyRepresentative rep = new CompanyRepresentative(email, password, name, email,
                companyName, department, position);
        userManager.addUser(rep);
        return true;
    }

    /**
     * Checks if a user exists.
     *
     * @param userId user identifier
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String userId) {
        return userManager.userExists(userId);
    }
}
