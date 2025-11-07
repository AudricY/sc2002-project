package util;

import java.io.*;
import java.util.*;

/**
 * Utility class for file operations including serialization and CSV reading.
 */
public class FileManager {
    private static final String DATA_DIR = "data/";

    /**
     * Saves a list of objects to a file using serialization.
     *
     * @param filename filename to save to
     * @param data list of objects to save
     * @param <T> type of objects in the list
     */
    public static <T> void saveToFile(String filename, List<T> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_DIR + filename))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("Error saving to file: " + filename);
            e.printStackTrace();
        }
    }

    /**
     * Loads a list of objects from a file using deserialization.
     *
     * @param filename filename to load from
     * @param <T> type of objects in the list
     * @return list of loaded objects, empty list if file doesn't exist
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> loadFromFile(String filename) {
        File file = new File(DATA_DIR + filename);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading from file: " + filename);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Ensures the data directory exists, creating it if necessary.
     */
    public static void ensureDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Reads records from a CSV file.
     *
     * @param filename CSV filename
     * @param skipfirst true to skip the first line (header)
     * @return list of string arrays representing CSV rows
     */
    public static List<String[]> readCSV(String filename, boolean skipfirst) {
        List<String[]> records = new ArrayList<>();
        File file = new File(DATA_DIR + filename);
        
        if (!file.exists()) {
            return records;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (skipfirst) { // skip header
                    skipfirst = false;
                    continue;
                }
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] fields = line.split(",");
                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].trim();
                }
                records.add(fields);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + filename);
            e.printStackTrace();
        }

        return records;
    }

    /**
     * Loads properties from a file.
     *
     * @param filePath path to properties file
     * @return properties object, empty if file doesn't exist
     */
    public static Properties loadIdProperties(String filePath) {
        ensureDataDirectory();
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(DATA_DIR + filePath)) {
            props.load(fis);
        } catch (FileNotFoundException e) {
            // File not found → return empty properties
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    /**
     * Saves properties to a file.
     *
     * @param props properties to save
     * @param filePath path to save to
     * @param comment comment to include in file
     */
    public static void saveIdProperties(Properties props, String filePath, String comment) {
        ensureDataDirectory();
        try (FileOutputStream fos = new FileOutputStream(DATA_DIR + filePath)) {
            props.store(fos, comment);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
