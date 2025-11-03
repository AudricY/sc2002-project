package control;

import entity.FilterSettings;
import entity.InternshipLevel;
import entity.Student;
import entity.User;
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
            UserManager userManager = UserManager.getInstance();
            User user = userManager.getUserById(userId);
            if (user instanceof Student) {
                Student student = (Student) user;
                int year = student.getYearOfStudy();
                InternshipLevel levelFilter;
                if (year <= 2) {
                    levelFilter = InternshipLevel.BASIC;
                } else {
                    levelFilter = InternshipLevel.ADVANCED;
                }
                settings.setLevelFilter(levelFilter);
                settings.setMajorFilter(student.getMajor());
            }
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
