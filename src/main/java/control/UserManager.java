package control;

import entity.*;
import util.FileManager;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages user operations including authentication, CRUD, and CSV loading.
 * Uses singleton pattern to ensure single instance.
 */
public class UserManager {
    private static final String USERS_FILE = "users.dat";
    private List<User> users;
    private static UserManager instance;

    /**
     * Private constructor for singleton pattern.
     * Initializes users list from file.
     */
    private UserManager() {
        this.users = FileManager.loadFromFile(USERS_FILE);
    }

    /**
     * Returns the singleton instance of UserManager.
     *
     * @return UserManager instance
     */
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * Saves all users to file.
     */
    public void saveUsers() {
        FileManager.saveToFile(USERS_FILE, users);
    }

    /**
     * Authenticates a user with provided credentials.
     *
     * @param userId user identifier
     * @param password user password
     * @return authenticated user, or null if authentication fails
     */
    public User authenticateUser(String userId, String password) {
        return users.stream()
                .filter(u -> u.getUserId().equals(userId) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if a user exists.
     *
     * @param userId user identifier
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String userId) {
        return users.stream().anyMatch(u -> u.getUserId().equals(userId));
    }

    /**
     * Adds a new user and saves to file.
     *
     * @param user user to add
     */
    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    /**
     * Gets a user by ID.
     *
     * @param userId user identifier
     * @return user if found, null otherwise
     */
    public User getUserById(String userId) {
        return users.stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Updates an existing user and saves to file.
     *
     * @param user updated user object
     */
    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(user.getUserId())) {
                users.set(i, user);
                saveUsers();
                return;
            }
        }
    }

    /**
     * Gets all company representatives pending approval.
     *
     * @return list of pending representatives
     */
    public List<CompanyRepresentative> getPendingRepresentatives() {
        return users.stream()
                .filter(u -> u instanceof CompanyRepresentative)
                .map(u -> (CompanyRepresentative) u)
                .filter(cr -> cr.getApprovalStatus() == ApprovalStatus.PENDING)
                .collect(Collectors.toList());
    }

    /**
     * Gets all students in the system.
     *
     * @return list of all students
     */
    public List<Student> getAllStudents() {
        return users.stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .collect(Collectors.toList());
    }

    /**
     * Reviews and updates a company representative's approval status.
     *
     * @param rep company representative to review
     * @param decision 1 for approve, 2 for reject
     */
    public void reviewRepresentative(CompanyRepresentative rep, int decision) {
        switch (decision) {
            case 1:
                rep.setApprovalStatus(ApprovalStatus.APPROVED);
                updateUser(rep);
                break;
            case 2:
                rep.setApprovalStatus(ApprovalStatus.REJECTED);
                updateUser(rep);
                break;
            default:
                break;
        }
    }

    /**
     * Loads users from a CSV file.
     *
     * @param filename CSV filename
     * @param role role of users to load
     */
    public void loadUsersFromCSV(String filename, UserRole role) {
        List<String[]> records = FileManager.readCSV(filename, true);

        for (String[] record : records) {

            String defaultPassword = "password";

            User user = null;
            try {
                switch (role) {
                    case STUDENT:
                        // must have at least StudentID, Name, Major, Year, Email
                        if (record.length >= 5) {
                            String userId = record[0];
                            if (userExists(userId)) continue;
                            String name = record[1];
                            String major = record[2];
                            int year;
                            try {
                                year = Integer.parseInt(record[3]);
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid year format in CSV record: " + Arrays.toString(record));
                                continue;
                            }
                            String email = record[4];
                            user = new Student(userId, defaultPassword, name, email, year, major);
                        }
                        break;
                    case CAREER_CENTER_STAFF:
                        // must have at least StaffID, Name, Role, Department, Email
                        if (record.length >= 5) {
                            String userId = record[0];
                            if (userExists(userId)) continue;
                            String name = record[1];
                            // Skip role field since it's guaranteed to be a career center staff
                            String dept = record[3];
                            String email = record[4];
                            user = new CareerCenterStaff(userId, defaultPassword, name, email, dept);
                        }
                        break;
                    default:
                        continue;
                }
            } catch (Exception e) {
                System.err.println("Error processing CSV record: " + Arrays.toString(record));
                System.err.println("Error: " + e.getMessage());
                continue;
            }

            if (user != null) {
                users.add(user);
            }
        }
        saveUsers();
    }
}
