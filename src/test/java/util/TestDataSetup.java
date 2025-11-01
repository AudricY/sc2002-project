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
    private static final String ID_PROPERTIES_FILE = "id_counters.properties";

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
            "StudentID,Name,Major,Year,Email",
            "U2310001A,Tan Wei Ling,Computer Science,2,tan001@e.ntu.edu.sg",
            "U2310002B,Ng Jia Hao,Data Science & AI,3,ng002@e.ntu.edu.sg",
            "U2310003C,Lim Yi Xuan,Computer Engineering,4,lim003@e.ntu.edu.sg",
            "U2310004D,Chong Zhi Hao,Information Engineering & Media,1,chong004@e.ntu.edu.sg",
            "U2310005E,Wong Shu Hui,Computer Science,3,wong005@e.ntu.edu.sg"
        };

        writeCSVFile("students.csv", studentData);
    }

    /**
     * Creates a staff.csv file with test staff data.
     */
    public static void createTestStaffCSV() {
        String[] staffData = {
            "StaffID,Name,Role,Department,Email",
            "sng001,Dr. Sng Hui Lin,Career Center Staff,CCDS,sng001@ntu.edu.sg",
            "tan002,Mr. Tan Boon Kiat,Career Center Staff,CCDS,tan002@ntu.edu.sg",
            "lee003,Ms. Lee Mei Ling,Career Center Staff,CCDS,lee003@ntu.edu.sg"
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
        File propsFile = new File(DATA_DIR + ID_PROPERTIES_FILE);
        if (propsFile.exists()) {
            propsFile.delete();
            System.out.println("Deleted old properties file: " + ID_PROPERTIES_FILE);
        }
        createTestCSVFiles();
        loadTestUsers();
    }
}
