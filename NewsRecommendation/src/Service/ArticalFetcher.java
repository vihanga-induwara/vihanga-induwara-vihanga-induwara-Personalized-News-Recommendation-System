package Service;

import Model.Article;
import java.util.List;

/**
 * Service class for fetching articles specifically and saving them to the database.
 */
public class ArticalFetcher extends NewsFetcher {

    /**
     * Constructor initializes the parent class (NewsFetcher).
     */
    public ArticalFetcher() {
        super();
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
}
