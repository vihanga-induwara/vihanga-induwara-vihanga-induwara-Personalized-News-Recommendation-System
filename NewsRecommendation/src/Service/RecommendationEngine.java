package Service;

import java.util.*;

public class RecommendationEngine {
    private final Map<String, List<String>> userPreferences;  // Changed from Integer to String (username)
    private final Map<String, String> newsData;
    private final Map<String, List<String>> userReadHistory;  // Changed from Integer to String (username)

    public RecommendationEngine() {
        // Initialize the maps that will hold user preferences, news data, and user reading history
        userPreferences = new HashMap<>();
        newsData = new HashMap<>();
        userReadHistory = new HashMap<>();

        try {
            // Sample data for user preferences
            userPreferences.put("john_doe", Arrays.asList("Technology", "AI", "Health"));
            userPreferences.put("jane_doe", Arrays.asList("Sports", "Business"));

            // Sample data for news categories
            newsData.put("N1", "Technology");
            newsData.put("N2", "AI");
            newsData.put("N3", "Health");
            newsData.put("N4", "Sports");
            newsData.put("N5", "Business");
            newsData.put("N6", "Entertainment");

            // Sample data for user read history
            userReadHistory.put("john_doe", Arrays.asList("N1", "N3"));
            userReadHistory.put("jane_doe", Arrays.asList("N4"));

            // Inform the user that the recommendation engine has been initialized successfully
            System.out.println("🟢 Recommendation Engine Initialized Successfully! Ready to make recommendations!");

        } catch (Exception e) {
            // Handle any potential exceptions that may occur during data setup
            System.err.println("❌ Error initializing Recommendation Engine: " + e.getMessage());
            e.printStackTrace();  // Print the full stack trace for debugging
        }
    }

    public List<String> getRecommendations(String username) {
        // List to store recommendations
        List<String> recommendations = new ArrayList<>();

        try {
            // Check if user exists
            if (!userPreferences.containsKey(username)) {
                System.out.println("❌ User not found! Please make sure the username is correct.");
                return recommendations; // Return empty list if user doesn't exist
            }

            // Get user preferences
            List<String> preferences = userPreferences.get(username);

            // Get already read news
            List<String> readNews = userReadHistory.getOrDefault(username, Collections.emptyList());

            // Check if there are any preferences set for the user
            if (preferences.isEmpty()) {
                System.out.println("⚠️ No preferences found for the user. Please update preferences to get recommendations.");
                return recommendations; // Return empty list if no preferences are set
            }

            // Loop through the available news data to make recommendations
            for (Map.Entry<String, String> entry : newsData.entrySet()) {
                String newsId = entry.getKey();
                String category = entry.getValue();

                // Recommend news based on preferences and unread status
                if (preferences.contains(category) && !readNews.contains(newsId)) {
                    recommendations.add(newsId + " (" + category + ")");
                }
            }

            // Check if no recommendations were found
            if (recommendations.isEmpty()) {
                System.out.println("🔍 No new recommendations based on your preferences.");
            }

        } catch (Exception e) {
            // Handle unexpected errors and provide feedback to the user
            System.err.println("❌ Error fetching recommendations: " + e.getMessage());
            e.printStackTrace();  // Print the full stack trace for debugging
        }

        // Return the list of recommendations (empty if none found)
        return recommendations;
    }


}
