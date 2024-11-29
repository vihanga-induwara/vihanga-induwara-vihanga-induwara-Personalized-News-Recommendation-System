package Service;

import org.json.JSONArray;
import org.json.JSONObject;

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
     * @return List of article titles
     * @throws Exception if fetching news fails
     */
    public List<String> fetchTopNews() throws Exception {
        return fetchNews("country=us");
    }

    /**
     * Fetch news articles by category.
     *
     * @param category the category to filter news
     * @return List of article titles
     * @throws Exception if fetching news fails
     */
    public List<String> fetchNewsByCategory(String category) throws Exception {
        return fetchNews("category=" + category + "&country=us");
    }

    /**
     * Generic method to fetch news based on query parameters.
     *
     * @param queryParams the query parameters for the API call
     * @return List of article titles
     * @throws Exception if fetching news fails
     */
    private List<String> fetchNews(String queryParams) throws Exception {
        List<String> articles = new ArrayList<>();

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

            // Extract titles of the articles
            for (int i = 0; i < articlesArray.length(); i++) {
                JSONObject article = articlesArray.getJSONObject(i);
                String title = article.getString("title");
                articles.add(title);
            }
        } else {
            throw new Exception("Failed to fetch news. HTTP Response Code: " + responseCode);
        }

        return articles;
    }
}
