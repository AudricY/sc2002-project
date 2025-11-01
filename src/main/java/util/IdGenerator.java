package util;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

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

    public static synchronized String generateInternshipId() {
        int id = internshipCounter.incrementAndGet();
        saveCounters();
        return "INT" + id;
    }

    public static synchronized String generateApplicationId() {
        int id = applicationCounter.incrementAndGet();
        saveCounters();
        return "APP" + id;
    }

    public static synchronized String generateWithdrawalId() {
        int id = withdrawalCounter.incrementAndGet();
        saveCounters();
        return "WD" + id;
    }

    private static void saveCounters() {
        Properties props = new Properties();
        props.setProperty("internshipCounter", String.valueOf(internshipCounter.get()));
        props.setProperty("applicationCounter", String.valueOf(applicationCounter.get()));
        props.setProperty("withdrawalCounter", String.valueOf(withdrawalCounter.get()));

        FileManager.saveIdProperties(props, FILE_PATH, "ID counters for Internship, Application, Withdrawal");
    }
}
