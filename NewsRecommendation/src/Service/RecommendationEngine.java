package Service;

import java.util.*;

public class RecommendationEngine {
    private final Map<String, List<String>> userPreferences;  // Changed from Integer to String (username)
    private final Map<String, String> newsData;
    private final Map<String, List<String>> userReadHistory;  // Changed from Integer to String (username)

    public RecommendationEngine() {
        userPreferences = new HashMap<>();
        newsData = new HashMap<>();
        userReadHistory = new HashMap<>();

        // Sample data
        userPreferences.put("john_doe", Arrays.asList("Technology", "AI", "Health"));
        userPreferences.put("jane_doe", Arrays.asList("Sports", "Business"));

        newsData.put("N1", "Technology");
        newsData.put("N2", "AI");
        newsData.put("N3", "Health");
        newsData.put("N4", "Sports");
        newsData.put("N5", "Business");
        newsData.put("N6", "Entertainment");

        userReadHistory.put("john_doe", Arrays.asList("N1", "N3"));
        userReadHistory.put("jane_doe", Arrays.asList("N4"));
    }

    public List<String> getRecommendations(String username) {
        // Check if user exists
        if (!userPreferences.containsKey(username)) {
            return Collections.emptyList();
        }

        // Get user preferences
        List<String> preferences = userPreferences.get(username);

        // Get already read news
        List<String> readNews = userReadHistory.getOrDefault(username, Collections.emptyList());

        List<String> recommendations = new ArrayList<>();
        for (Map.Entry<String, String> entry : newsData.entrySet()) {
            String newsId = entry.getKey();
            String category = entry.getValue();

            // Recommend news based on preferences and unread status
            if (preferences.contains(category) && !readNews.contains(newsId)) {
                recommendations.add(newsId + " (" + category + ")");
            }
        }

        return recommendations;
    }
}
