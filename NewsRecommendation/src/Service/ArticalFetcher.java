package Service;

import Model.Article;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ArticalFetcher {
    private static final String API_URL = "https://newsapi.org/v2/top-headlines";
    private static final String API_KEY = "1a5a2bbf4f4f4fd8a9a92a98f25c9515"; // Replace with your actual API key

    /**
     * Fetch top news articles.
     *
     * @return List of Article objects
     * @throws Exception if fetching news fails
     */
    public List<Article> fetchTopNews() throws Exception {
        return fetchNews("country=us");
    }

    /**
     * Fetch news articles by category.
     *
     * @param category the category to filter news
     * @return List of Article objects
     * @throws Exception if fetching news fails
     */
    public List<Article> fetchNewsByCategory(String category) throws Exception {
        return fetchNews("category=" + category + "&country=us");
    }

    /**
     * Generic method to fetch news based on query parameters.
     *
     * @param queryParams the query parameters for the API call
     * @return List of Article objects
     * @throws Exception if fetching news fails
     */
    private List<Article> fetchNews(String queryParams) throws Exception {
        List<Article> articles = new ArrayList<>();

        // Construct the API request URL
        String endpoint = API_URL + "?" + queryParams + "&apiKey=" + API_KEY;

        // Make an HTTP GET request
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) { // HTTP OK
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray articlesArray = jsonResponse.getJSONArray("articles");

            // Extract details of each article
            for (int i = 0; i < articlesArray.length(); i++) {
                JSONObject articleJson = articlesArray.getJSONObject(i);
                String title = articleJson.getString("title");
                String author = articleJson.optString("author", "Unknown"); // Optional field
                String content = articleJson.optString("content", "Content not available.");
                String publishedDate = articleJson.optString("publishedAt", "Unknown");

                // Create an Article object
                Article article = new Article(title, author, content, publishedDate);
                articles.add(article);
            }
        } else {
            throw new Exception("Failed to fetch news. HTTP Response Code: " + responseCode);
        }

        return articles;
    }
}
