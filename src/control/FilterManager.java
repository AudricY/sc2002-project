package control;

import entity.FilterSettings;
import util.FileManager;
import java.util.*;

public class FilterManager {
    private static final String FILTERS_FILE = "filters.dat";
    private Map<String, FilterSettings> userFilters;
    private static FilterManager instance;

    private FilterManager() {
        List<FilterSettings> filterList = FileManager.loadFromFile(FILTERS_FILE);
        this.userFilters = new HashMap<>();
        for (FilterSettings fs : filterList) {
            userFilters.put(fs.getUserId(), fs);
        }
    }

    public static FilterManager getInstance() {
        if (instance == null) {
            instance = new FilterManager();
        }
        return instance;
    }

    public void saveFilters() {
        List<FilterSettings> filterList = new ArrayList<>(userFilters.values());
        FileManager.saveToFile(FILTERS_FILE, filterList);
    }

    public FilterSettings getFilterSettings(String userId) {
        if (!userFilters.containsKey(userId)) {
            FilterSettings settings = new FilterSettings(userId);
            userFilters.put(userId, settings);
            saveFilters();
        }
        return userFilters.get(userId);
    }

    public void updateFilterSettings(FilterSettings settings) {
        userFilters.put(settings.getUserId(), settings);
        saveFilters();
    }
}
