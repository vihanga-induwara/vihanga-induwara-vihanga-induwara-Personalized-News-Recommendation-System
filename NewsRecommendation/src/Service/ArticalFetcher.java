package Service;

import DB.DatabaseHandler;
import Model.Article;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Service class for fetching articles from an API.
 */
public class ArticalFetcher {
    private static final String API_URL = "https://newsapi.org/v2/top-headlines";
    private static final String API_KEY = "1a5a2bbf4f4f4fd8a9a92a98f25c9515"; // Replace with your actual API key
    private final DatabaseHandler dbHandler;

    // Thread pool size for multithreading
    private static final int THREAD_POOL_SIZE = 5;

    /**
     * Constructor initializes the database handler.
     */
    public ArticalFetcher() {
        dbHandler = new DatabaseHandler();
    }

    /**
     * Fetch top news articles and save them to the database.
     *
     * @return List of Article objects
     * @throws Exception if fetching news fails
     */
    public List<Article> fetchTopNews() throws Exception {
        return fetchAndSaveNews("country=us");
    }

    /**
     * Fetch news articles by category and save them to the database.
     *
     * @param category the category to filter news
     * @return List of Article objects
     * @throws Exception if fetching news fails
     */
    public List<Article> fetchNewsByCategory(String category) throws Exception {
        return fetchAndSaveNews("category=" + category + "&country=us");
    }

    /**
     * Fetch news based on query parameters and save the articles to the database.
     *
     * @param queryParams the query parameters for the API call
     * @return List of Article objects
     * @throws Exception if fetching news fails
     */
    private List<Article> fetchAndSaveNews(String queryParams) throws Exception {
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
                String author = articleJson.optString("author", "Unknown");
                String content = articleJson.optString("content", "Content not available.");
                String publishedDate = articleJson.optString("publishedAt", "Unknown");

                // Create an Article object
                Article article = new Article(title, author, content, publishedDate);
                articles.add(article);
            }

            // Save fetched articles to the database using multithreading
            saveArticlesToDatabase(articles);
        } else {
            throw new Exception("Failed to fetch news. HTTP Response Code: " + responseCode);
        }

        return articles;
    }

    /**
     * Save a list of articles to the database using multithreading.
     *
     * @param articles the list of articles to save
     */
    private void saveArticlesToDatabase(List<Article> articles) {
        // Create a thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        for (Article article : articles) {
            // Submit a task for each article
            executorService.submit(() -> {
                try {
                    // Synchronize database access to prevent conflicts
                    synchronized (dbHandler) {
                        if (!dbHandler.articleExists(article.getTitle())) { // Check for duplicates
                            dbHandler.saveArticle(article);
                            System.out.println("Article saved: " + article.getTitle());
                        } else {
                            System.out.println("Article already exists: " + article.getTitle());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error saving article: " + article.getTitle() + " - " + e.getMessage());
                }
            });
        }

        // Shut down the executor service gracefully
        executorService.shutdown();
    }
}
