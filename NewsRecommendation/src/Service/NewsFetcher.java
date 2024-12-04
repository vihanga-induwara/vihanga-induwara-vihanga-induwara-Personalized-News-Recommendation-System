package Service;

import DB.DatabaseHandler;
import Model.Article;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Base class for fetching news from an API and saving articles to the database.
 */
public class NewsFetcher {
    protected static final String API_URL = "https://newsapi.org/v2/top-headlines";
    protected static final String API_KEY = "1a5a2bbf4f4f4fd8a9a92a98f25c9515"; // Replace with your actual API key
    protected final DatabaseHandler dbHandler;

    // Thread pool size for multithreading
    protected static final int THREAD_POOL_SIZE = 5;

    /**
     * Constructor initializes the database handler.
     */
    public NewsFetcher() {
        dbHandler = new DatabaseHandler();
    }

    /**
     * Fetch news articles based on query parameters and save them to the database.
     *
     * @param queryParams the query parameters for the API call
     * @return List of Article objects
     * @throws Exception if fetching news fails
     */
    protected List<Article> fetchAndSaveNews(String queryParams) throws Exception {
        List<Article> articles = new ArrayList<>();

        // Display a loading message with an emoji for better UX
        System.out.println("🔄 Fetching news articles, please wait...");

        // Construct the API request URL with proper query parameters
        String endpoint = API_URL + "?" + queryParams + "&apiKey=" + API_KEY;

        HttpURLConnection connection = null;

        try {
            // Make an HTTP GET request to fetch the news data
            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Check the response code to ensure the request was successful
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) { // HTTP OK (Success)
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                // Read the response line by line
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray articlesArray = jsonResponse.getJSONArray("articles");

                // Extract article details and create Article objects
                for (int i = 0; i < articlesArray.length(); i++) {
                    JSONObject articleJson = articlesArray.getJSONObject(i);
                    String title = articleJson.getString("title");
                    String author = articleJson.optString("author", "Unknown");
                    String content = articleJson.optString("content", "Content not available.");
                    String publishedDate = articleJson.optString("publishedAt", "Unknown");

                    // Create an Article object and add it to the list
                    Article article = new Article(title, author, content, publishedDate);
                    articles.add(article);
                }

                // Notify the user about the successful fetch with an emoji
                System.out.println("✅ Articles fetched successfully.");

                // Save fetched articles to the database using multithreading
                saveArticlesToDatabase(articles);
            } else {
                // If the response code is not 200, throw an exception
                throw new Exception("⚠️ Failed to fetch news. HTTP Response Code: " + responseCode);
            }
        } catch (MalformedURLException e) {
            // Handle the case where the URL is malformed
            System.err.println("⚠️ Invalid URL: " + e.getMessage());
            throw e; // Rethrow the exception after logging
        } catch (IOException e) {
            // Handle IO-related exceptions
            System.err.println("⚠️ Error during HTTP request: " + e.getMessage());
            throw e; // Rethrow the exception after logging
        } catch (JSONException e) {
            // Handle JSON parsing errors
            System.err.println("⚠️ Error parsing JSON response: " + e.getMessage());
            throw e; // Rethrow the exception after logging
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            System.err.println("⚠️ Unexpected error: " + e.getMessage());
            throw e; // Rethrow the exception after logging
        } finally {
            // Ensure that the HTTP connection is closed properly
            if (connection != null) {
                connection.disconnect();
                System.out.println("🔌 Connection closed.");
            }
        }

        // Return the list of articles fetched
        return articles;
    }

    /**
     * Save a list of articles to the database using multithreading.
     *
     * @param articles the list of articles to save
     */
    protected void saveArticlesToDatabase(List<Article> articles) {
        // Create a thread pool with fixed size for parallel execution
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // Display a message indicating that articles are being saved
        System.out.println("💾 Saving articles to the database...");

        for (Article article : articles) {
            // Submit a task for each article to be processed in parallel
            executorService.submit(() -> {
                try {
                    // Synchronize database access to prevent conflicts when saving articles
                    synchronized (dbHandler) {
                        // Check if the article already exists in the database to avoid duplicates
                        if (!dbHandler.articleExists(article.getTitle())) {
                            // Save the article if it doesn't already exist
                            dbHandler.saveArticle(article);
                            System.out.println("✅ Article saved: " + article.getTitle());
                        } else {
                            // Inform the user if the article already exists in the database
                            System.out.println("⚠️ Article already exists: " + article.getTitle());
                        }
                    }
                } catch (SQLException e) {
                    // Handle database-related exceptions
                    System.err.println("❌ Database error while saving article: " + article.getTitle() + " - " + e.getMessage());
                } catch (Exception e) {
                    // Handle general exceptions that may occur during the article saving process
                    System.err.println("❌ Error saving article: " + article.getTitle() + " - " + e.getMessage());
                }
            });
        }

        // Gracefully shut down the executor service after all tasks are completed
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                System.err.println("❌ Timeout reached while waiting for tasks to finish.");
            } else {
                System.out.println("🟢 All tasks completed successfully.");
            }
        } catch (InterruptedException e) {
            // Handle interruption if the thread pool is interrupted while waiting for task completion
            System.err.println("❌ Executor service interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Preserve the interrupt status
        } finally {
            // Always ensure the executor service is shut down gracefully
            if (!executorService.isTerminated()) {
                System.out.println("❗ Executor service did not terminate in time, forcing shutdown.");
                executorService.shutdownNow();
            }
        }
    }

}
