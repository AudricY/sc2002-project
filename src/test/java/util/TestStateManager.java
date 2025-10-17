package util;

import control.*;
import java.io.File;
import java.lang.reflect.Field;

/**
 * Utility class for managing test state and resetting singleton instances.
 * This class ensures each test starts with a clean state by:
 * - Deleting all .dat files
 * - Resetting singleton manager instances
 */
public class TestStateManager {

    private static final String DATA_DIR = "data/";
    private static final String[] DATA_FILES = {
        "users.dat",
        "filters.dat",
        "internships.dat",
        "withdrawals.dat",
        "applications.dat"
    };

    /**
     * Resets the entire system state for testing.
     * Deletes all data files and resets singleton instances.
     */
    public static void resetSystemState() {
        deleteAllDataFiles();
        resetSingletons();
    }

    /**
     * Deletes all .dat files in the data directory.
     */
    private static void deleteAllDataFiles() {
        for (String filename : DATA_FILES) {
            File file = new File(DATA_DIR + filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * Resets all singleton manager instances using reflection.
     * This forces managers to reload data from files (or start fresh).
     */
    public static void resetSingletons() {
        resetSingleton(UserManager.class, "instance");
        resetSingleton(InternshipManager.class, "instance");
        resetSingleton(ApplicationManager.class, "instance");
        resetSingleton(WithdrawalManager.class, "instance");
        resetSingleton(FilterManager.class, "instance");
    }

    /**
     * Resets a singleton instance using reflection.
     *
     * @param clazz The singleton class
     * @param fieldName The name of the static instance field
     */
    private static void resetSingleton(Class<?> clazz, String fieldName) {
        try {
            Field instance = clazz.getDeclaredField(fieldName);
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Warning: Could not reset singleton for " + clazz.getName());
        }
    }

    /**
     * Ensures the data directory exists.
     */
    public static void ensureDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
