package control;

import entity.*;
import util.IdGenerator;

public class AuthenticationController {
    private UserManager userManager;
    private User currentUser;

    public AuthenticationController() {
        this.userManager = UserManager.getInstance();
        this.currentUser = null;
    }

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

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean changePassword(String oldPassword, String newPassword) {
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

    public boolean registerCompanyRepresentative(String name, String email, String password,
                                                  String companyName, String department,
                                                  String position) {
        CompanyRepresentative rep = new CompanyRepresentative(email, password, name, email,
                companyName, department, position);
        userManager.addUser(rep);
        return true;
    }
}
