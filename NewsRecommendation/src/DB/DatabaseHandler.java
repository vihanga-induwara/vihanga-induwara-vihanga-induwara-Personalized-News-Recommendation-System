package DB;

import Model.Admin;
import Model.Article;
import Model.GeneralUser;
import java.sql.*;
import java.util.List;

public class DatabaseHandler {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/newsapp"; // database name
    private static final String DB_USER = "root"; // database username
    private static final String DB_PASSWORD = ""; // database password

    public static Connection connection; // Shared connection (static)

    // Establish a connection to the database
    public static void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
    }

    // Method to validate admin login
    public static Admin validateAdminLogin(String adminUsername, String adminPassword) {
        String query = "SELECT * FROM Admins WHERE Username = ? AND Password = ?";
        try {
            connect(); // Ensure the connection is established
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, adminUsername);
                statement.setString(2, adminPassword);

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    int adminID = resultSet.getInt("AdminID");
                    String username = resultSet.getString("Username");
                    String password = resultSet.getString("Password");
                    Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
                    return new Admin(adminID, username, password, createdAt);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during admin login validation: " + e.getMessage());
        }
        return null;
    }


    // Close the database connection
    public void closeConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
    }

    // Validate user login with username and password
    public GeneralUser validateUserLogin(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        connect();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String userId = rs.getString("userId");
                String name = rs.getString("name");
                String email = rs.getString("email");
                return new GeneralUser(userId, username, password, name, email);
            }
        } finally {
            closeConnection();
        }
        return null;
    }

    // Check if a username is unique in the database
    public boolean isUsernameUnique(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        connect();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) == 0; // Username is unique if count is 0
            }
        } finally {
            closeConnection();
        }
        return false;
    }

    // Save a user to the database
    public boolean saveUserToDB(GeneralUser user) throws SQLException {
        String query = "INSERT INTO users (UserId, Username, Password, Email, Name) VALUES (?, ?, ?, ?, ?)";
        connect();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getUserId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getName());
            return stmt.executeUpdate() > 0;
        } finally {
            closeConnection();
        }
    }

    // Retrieve user profile by username
    public ResultSet getUserProfile(String username) throws SQLException {
        String query = "SELECT userId, username, email, createdAt FROM users WHERE username = ?";
        connect();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        return statement.executeQuery();
    }

    // Retrieve user preferences by username
    public ResultSet getUserPreferences(String username) throws SQLException {
        String query = "SELECT category FROM preferences WHERE username = ?";
        connect();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        return statement.executeQuery();
    }

    // Save reading history details
    public boolean saveReadingHistory(String userId, String articleId, String action, Timestamp interactionTime) throws SQLException {
        String query = "INSERT INTO reading_history (UserID, ArticleID, Action, InteractionTime) VALUES (?, ?, ?, ?)";
        connect();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.setString(2, articleId);
            stmt.setString(3, action);
            stmt.setTimestamp(4, interactionTime);
            return stmt.executeUpdate() > 0;
        } finally {
            closeConnection();
        }
    }

    // Retrieve user's reading history
    public ResultSet getUserReadingHistory(String username) throws SQLException {
        String query = """
            SELECT a.title, r.action, r.interactionTime
            FROM reading_history r
            JOIN articles a ON r.articleId = a.articleId
            WHERE r.username = ?
            """;
        connect();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        return statement.executeQuery();
    }

    // Retrieve user preferred category
    public String getUserPreferredCategory(String loggedInUsername) {
        String query = """
            SELECT category FROM preferences p
            JOIN users u ON p.username = u.username
            WHERE u.username = ?
        """;
        try {
            connect();
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, loggedInUsername);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getString("category");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching preferred category: " + e.getMessage());
        } finally {
            try {
                closeConnection();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
        return null;
    }

    // Method to save an article to the database
    public boolean saveArticleToDB(String title, String author, String content, String publishedDate) throws SQLException {
        connect();
        String checkQuery = "SELECT COUNT(*) FROM articles WHERE title = ?";
        String insertQuery = "INSERT INTO articles (Title, Author, Content, PublishedDate) VALUES (?, ?, ?, ?)";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, title);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Article already exists
            }
        }

        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setString(1, title);
            insertStmt.setString(2, author);
            insertStmt.setString(3, content);
            insertStmt.setString(4, publishedDate);
            return insertStmt.executeUpdate() > 0;
        } finally {
            closeConnection();
        }
    }

    public void recordUserAction(String username, String articleTitle, String articleContent, String articleAuthor, String publishedDate, String action) throws SQLException {
        connect(); // Establish database connection

        // SQL queries for finding existing articles, inserting new articles, and recording user actions
        String findArticleQuery = "SELECT ArticleID FROM articles WHERE title = ?";
        String insertArticleQuery = "INSERT INTO articles (Title, Content, Author, PublishedDate, created_at) VALUES (?, ?, ?, ?, NOW())";
        String insertHistoryQuery = "INSERT INTO reading_history (username, articleId, action, interactionTime) VALUES (?, ?, ?, NOW())";

        try {
            // Check if the article exists in the database
            String articleId = null;
            try (PreparedStatement findStmt = connection.prepareStatement(findArticleQuery)) {
                findStmt.setString(1, articleTitle); // Set the article title as the parameter
                ResultSet rs = findStmt.executeQuery();

                if (rs.next()) {
                    // If the article exists, get its ID
                    articleId = rs.getString("ArticleID");
                } else {
                    // If the article doesn't exist, insert a new article into the database
                    try (PreparedStatement insertArticleStmt = connection.prepareStatement(insertArticleQuery, Statement.RETURN_GENERATED_KEYS)) {
                        insertArticleStmt.setString(1, articleTitle);      // Set the article title
                        insertArticleStmt.setString(2, articleContent);    // Set the article content
                        insertArticleStmt.setString(3, articleAuthor);     // Set the article author
                        insertArticleStmt.setString(4, publishedDate);     // Set the article published date
                        insertArticleStmt.executeUpdate();

                        // Get the generated article ID after insertion
                        ResultSet generatedKeys = insertArticleStmt.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            articleId = generatedKeys.getString(1);
                        } else {
                            throw new SQLException("Failed to insert new article, no ID obtained.");
                        }
                    }
                }
            }

            // Record the user's action on the article (read or skip)
            try (PreparedStatement insertStmt = connection.prepareStatement(insertHistoryQuery)) {
                insertStmt.setString(1, username);    // Set the username
                insertStmt.setString(2, articleId);   // Set the article ID
                insertStmt.setString(3, action);      // Set the action (read or skip)
                insertStmt.executeUpdate();
            }
        } finally {
            closeConnection(); // Ensure the database connection is closed
        }
    }

    public boolean articleExists(String title) throws SQLException {
        String query = "SELECT COUNT(*) FROM articles WHERE title = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // DatabaseHandler - Save article method
    public void saveArticle(Article article) {
        String query = "INSERT INTO articles (title, author, content, publishedDate) VALUES (?, ?, ?, ?)";
        try {
            connect();  // Ensure connection is established before the operation

            // Prepare and execute the statement
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, article.getTitle());
                stmt.setString(2, article.getAuthor());
                stmt.setString(3, article.getContent());
                stmt.setString(4, article.getPublishedDate());
                stmt.executeUpdate();
                System.out.println("Article saved successfully.");
            } catch (SQLException e) {
                System.err.println("Error during the article save process: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error establishing the connection: " + e.getMessage());
        }
        // Connection is not closed here, will be closed after all operations
    }


}
