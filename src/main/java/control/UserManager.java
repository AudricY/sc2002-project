package control;

import entity.*;
import util.FileManager;
import java.util.*;
import java.util.stream.Collectors;

public class UserManager {
    private static final String USERS_FILE = "users.dat";
    private List<User> users;
    private static UserManager instance;

    private UserManager() {
        this.users = FileManager.loadFromFile(USERS_FILE);
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void saveUsers() {
        FileManager.saveToFile(USERS_FILE, users);
    }

    public User authenticateUser(String userId, String password) {
        return users.stream()
                .filter(u -> u.getUserId().equals(userId) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public boolean userExists(String userId) {
        return users.stream().anyMatch(u -> u.getUserId().equals(userId));
    }

    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    public User getUserById(String userId) {
        return users.stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(user.getUserId())) {
                users.set(i, user);
                saveUsers();
                return;
            }
        }
    }

    public List<CompanyRepresentative> getPendingRepresentatives() {
        return users.stream()
                .filter(u -> u instanceof CompanyRepresentative)
                .map(u -> (CompanyRepresentative) u)
                .filter(cr -> cr.getApprovalStatus() == ApprovalStatus.PENDING)
                .collect(Collectors.toList());
    }

    public List<Student> getAllStudents() {
        return users.stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .collect(Collectors.toList());
    }

    public void loadUsersFromCSV(String filename, UserRole role) {
        List<String[]> records = FileManager.readCSV(filename);

        for (String[] record : records) {
            if (record.length < 4) continue;

            String userId = record[0];
            if (userExists(userId)) continue;

            String name = record[1];
            String email = record[2];
            String defaultPassword = "password";

            User user = null;
            switch (role) {
                case STUDENT:
                    if (record.length >= 5) {
                        int year = Integer.parseInt(record[3]);
                        String major = record[4];
                        user = new Student(userId, defaultPassword, name, email, year, major);
                    }
                    break;
                case CAREER_CENTER_STAFF:
                    if (record.length >= 4) {
                        String dept = record[3];
                        user = new CareerCenterStaff(userId, defaultPassword, name, email, dept);
                    }
                    break;
            }

            if (user != null) {
                users.add(user);
            }
        }
        saveUsers();
    }
}
