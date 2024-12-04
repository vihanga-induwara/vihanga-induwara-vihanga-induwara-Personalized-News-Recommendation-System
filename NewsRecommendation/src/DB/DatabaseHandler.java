package DB;

import Model.Admin;
import Model.GeneralUser;
import java.sql.*;

public class DatabaseHandler {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/newsapp"; // Update with your database name
    private static final String DB_USER = "root"; // Replace with your database username
    private static final String DB_PASSWORD = ""; // Replace with your database password

    public static Connection connection; // Shared connection (static)

    // Establish a connection to the database
    public static void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
    }

    // Close the database connection
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Validate user login with username and password
    public GeneralUser validateUserLogin(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        connect();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // Use hashed passwords in production
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
        String query = "INSERT INTO users (UserId, Username, password, email, name) VALUES (?, ?, ?, ?, ?)";
        connect();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getUserId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getName());
            return stmt.executeUpdate() > 0; // Return true if insert was successful
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

    // Retrieve user preferences by user ID
    public ResultSet getUserPreferences(int userId) throws SQLException {
        String query = "SELECT category FROM preferences WHERE userId = ?";
        connect();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, userId);
        return statement.executeQuery();
    }

    // Retrieve user reading history
    public ResultSet getUserReadingHistory(int userId) throws SQLException {
        String query = """
                SELECT a.title, r.action, r.interactionTime
                FROM reading_history r
                JOIN articles a ON r.articleId = a.articleId
                WHERE r.userId = ?
                """;
        connect();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, userId);
        return statement.executeQuery();
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

    // Save reading history details
    public boolean saveReadingHistory(String userId, String articleId, String action, Timestamp interactionTime) throws SQLException {
        String query = "INSERT INTO reading_history (UserID, ArticleID, Action, InteractionTime) VALUES (?, ?, ?, ?)";
        connect();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userId); // UserID
            stmt.setString(2, articleId); // ArticleID
            stmt.setString(3, action); // Action (e.g., 'view', 'like', 'comment')
            stmt.setTimestamp(4, interactionTime); // InteractionTime (timestamp)
            return stmt.executeUpdate() > 0; // Return true if insert was successful
        } finally {
            closeConnection();
        }
    }

    // Retrieve user's reading history
    public ResultSet getUserReadingHistory(String userId) throws SQLException {
        String query = """
            SELECT a.title, r.action, r.interactionTime
            FROM reading_history r
            JOIN articles a ON r.ArticleID = a.ArticleID
            WHERE r.UserID = ?
            """;
        connect();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userId); // Set userId to fetch their reading history
            return statement.executeQuery();
        }
    }



}

