package Service;

import java.util.*;

public class RecommendationEngine {
    // Sample data
    private final Map<Integer, List<String>> userPreferences; // User ID -> Liked categories
    private final Map<String, String> newsData; // News ID -> Category
    private final Map<Integer, List<String>> userReadHistory; // User ID -> List of read news

    public RecommendationEngine() {
        // Initialize sample data
        userPreferences = new HashMap<>();
        newsData = new HashMap<>();
        userReadHistory = new HashMap<>();

        // Sample user preferences
        userPreferences.put(1, Arrays.asList("Technology", "AI", "Health"));
        userPreferences.put(2, Arrays.asList("Sports", "Entertainment", "Business"));

        // Sample news articles
        newsData.put("N1", "Technology");
        newsData.put("N2", "AI");
        newsData.put("N3", "Health");
        newsData.put("N4", "Sports");
        newsData.put("N5", "Business");
        newsData.put("N6", "Entertainment");

        // Sample read history
        userReadHistory.put(1, Arrays.asList("N1", "N3")); // User 1 has read N1, N3
        userReadHistory.put(2, Arrays.asList("N4"));       // User 2 has read N4
    }

    public List<String> getRecommendations(int userId) {
        // Check if user exists
        if (!userPreferences.containsKey(userId)) {
            return Collections.emptyList();
        }

        // Get user preferences
        List<String> preferences = userPreferences.get(userId);

        // Get already read news
        List<String> readNews = userReadHistory.getOrDefault(userId, Collections.emptyList());

        // Recommend news articles based on preferences
        List<String> recommendations = new ArrayList<>();
        for (Map.Entry<String, String> entry : newsData.entrySet()) {
            String newsId = entry.getKey();
            String category = entry.getValue();

            // Recommend if the category matches preferences and news is unread
            if (preferences.contains(category) && !readNews.contains(newsId)) {
                recommendations.add(newsId + " (" + category + ")");
            }
        }

        return recommendations;
    }
}
