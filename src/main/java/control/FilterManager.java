package control;

import entity.FilterSettings;
import util.FileManager;
import java.util.*;

/**
 * Manages user filter settings persistence and retrieval.
 * Uses singleton pattern to ensure single instance.
 */
public class FilterManager {
    private static final String FILTERS_FILE = "filters.dat";
    private Map<String, FilterSettings> userFilters;
    private static FilterManager instance;

    /**
     * Private constructor for singleton pattern.
     * Initializes filter settings map from file.
     */
    private FilterManager() {
        List<FilterSettings> filterList = FileManager.loadFromFile(FILTERS_FILE);
        this.userFilters = new HashMap<>();
        for (FilterSettings fs : filterList) {
            userFilters.put(fs.getUserId(), fs);
        }
    }

    /**
     * Returns the singleton instance of FilterManager.
     *
     * @return FilterManager instance
     */
    public static FilterManager getInstance() {
        if (instance == null) {
            instance = new FilterManager();
        }
        return instance;
    }

    /**
     * Saves all filter settings to file.
     */
    public void saveFilters() {
        List<FilterSettings> filterList = new ArrayList<>(userFilters.values());
        FileManager.saveToFile(FILTERS_FILE, filterList);
    }

    /**
     * Gets filter settings for a user, creating defaults if needed.
     * Default settings have no filters applied (only alphabetical sorting).
     *
     * @param userId user identifier
     * @return filter settings for the user
     */
    public FilterSettings getFilterSettings(String userId) {
        if (!userFilters.containsKey(userId)) {
            FilterSettings settings = new FilterSettings(userId);
            userFilters.put(userId, settings);
            saveFilters();
        }
        return userFilters.get(userId);
    }

    /**
     * Updates filter settings for a user and saves to file.
     *
     * @param settings updated filter settings
     */
    public void updateFilterSettings(FilterSettings settings) {
        userFilters.put(settings.getUserId(), settings);
        saveFilters();
    }
}
