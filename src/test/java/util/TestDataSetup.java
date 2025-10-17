package util;

import control.UserManager;
import entity.UserRole;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility class for setting up test data files.
 * Creates CSV files with test data for students and staff.
 */
public class TestDataSetup {

    private static final String DATA_DIR = "data/";

    /**
     * Creates all necessary test CSV files with sample data.
     */
    public static void createTestCSVFiles() {
        createTestStudentsCSV();
        createTestStaffCSV();
    }

    /**
     * Creates a students.csv file with test student data.
     */
    public static void createTestStudentsCSV() {
        String[] studentData = {
            "S001,Alice Tan,alice.tan@university.edu,2,Computer Science",
            "S002,Bob Lee,bob.lee@university.edu,3,Business Administration",
            "S003,Charlie Wong,charlie.wong@university.edu,1,Engineering",
            "S004,Diana Chen,diana.chen@university.edu,4,Computer Science",
            "S005,Eric Lim,eric.lim@university.edu,2,Information Systems",
            "S006,Fiona Ng,fiona.ng@university.edu,3,Engineering",
            "S007,George Koh,george.koh@university.edu,1,Business Administration",
            "S008,Hannah Teo,hannah.teo@university.edu,4,Information Systems",
            "S009,Ivan Ong,ivan.ong@university.edu,2,Computer Science",
            "S010,Julia Sim,julia.sim@university.edu,3,Engineering"
        };

        writeCSVFile("students.csv", studentData);
    }

    /**
     * Creates a staff.csv file with test staff data.
     */
    public static void createTestStaffCSV() {
        String[] staffData = {
            "STAFF001,Michael Brown,michael.brown@university.edu,Career Services",
            "STAFF002,Sarah Johnson,sarah.johnson@university.edu,Student Affairs",
            "STAFF003,David Williams,david.williams@university.edu,Career Services"
        };

        writeCSVFile("staff.csv", staffData);
    }

    /**
     * Writes data to a CSV file.
     *
     * @param filename The name of the CSV file
     * @param data Array of CSV rows to write
     */
    private static void writeCSVFile(String filename, String[] data) {
        File file = new File(DATA_DIR + filename);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing test CSV file: " + filename);
            e.printStackTrace();
        }
    }

    /**
     * Loads test users from CSV files into the system.
     * Should be called after resetting the system state.
     */
    public static void loadTestUsers() {
        UserManager userManager = UserManager.getInstance();
        userManager.loadUsersFromCSV("students.csv", UserRole.STUDENT);
        userManager.loadUsersFromCSV("staff.csv", UserRole.CAREER_CENTER_STAFF);
    }

    /**
     * Performs complete test data initialization:
     * 1. Ensures data directory exists
     * 2. Creates test CSV files
     * 3. Loads users from CSV files
     */
    public static void initializeTestData() {
        TestStateManager.ensureDataDirectory();
        createTestCSVFiles();
        loadTestUsers();
    }
}
