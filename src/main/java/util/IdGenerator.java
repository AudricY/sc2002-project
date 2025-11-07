package util;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates unique IDs for internships, applications, and withdrawal requests.
 */
public class IdGenerator {

    private static final String FILE_PATH = "id_counters.properties";

    private static AtomicInteger internshipCounter;
    private static AtomicInteger applicationCounter;
    private static AtomicInteger withdrawalCounter;

    static {
        Properties props = FileManager.loadIdProperties(FILE_PATH);

        internshipCounter = new AtomicInteger(
                Integer.parseInt(props.getProperty("internshipCounter", "1000")));
        applicationCounter = new AtomicInteger(
                Integer.parseInt(props.getProperty("applicationCounter", "1000")));
        withdrawalCounter = new AtomicInteger(
                Integer.parseInt(props.getProperty("withdrawalCounter", "1000")));
    }

    /**
     * Generates a unique internship ID.
     *
     * @return internship ID in format INT{number}
     */
    public static synchronized String generateInternshipId() {
        int id = internshipCounter.incrementAndGet();
        saveCounters();
        return "INT" + id;
    }

    /**
     * Generates a unique application ID.
     *
     * @return application ID in format APP{number}
     */
    public static synchronized String generateApplicationId() {
        int id = applicationCounter.incrementAndGet();
        saveCounters();
        return "APP" + id;
    }

    /**
     * Generates a unique withdrawal request ID.
     *
     * @return withdrawal ID in format WD{number}
     */
    public static synchronized String generateWithdrawalId() {
        int id = withdrawalCounter.incrementAndGet();
        saveCounters();
        return "WD" + id;
    }

    /**
     * Saves current counter values to properties file for persistence.
     */
    private static void saveCounters() {
        Properties props = new Properties();
        props.setProperty("internshipCounter", String.valueOf(internshipCounter.get()));
        props.setProperty("applicationCounter", String.valueOf(applicationCounter.get()));
        props.setProperty("withdrawalCounter", String.valueOf(withdrawalCounter.get()));

        FileManager.saveIdProperties(props, FILE_PATH, "ID counters for Internship, Application, Withdrawal");
    }
}
