package App;

import DB.DatabaseHandler;
import Model.Admin;
import Model.Article;
import Model.GeneralUser;
import Service.ArticalFetcher;
import Service.RecommendationEngine;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;


public class Main {
    private static final Scanner input = new Scanner(System.in);
    private final DatabaseHandler dbHandler = new DatabaseHandler(); // Assuming DatabaseHandler handles DB operations
    private boolean isUserLoggedIn = false;
    private GeneralUser loggedInUser;

    public static void main(String[] args) {
        Main app = new Main();
        app.displayMainMenu();
    }

    public void displayMainMenu() {
        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Login");
            System.out.println("2. Registration");
            System.out.println("3. Admin Login");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice = input.nextInt();
            input.nextLine(); // Clear buffer

            switch (choice) {
                case 1:
                    handleUserLogin();
                    if (isUserLoggedIn) {
                        displayUserDashboard();
                    }
                    break;
                case 2:
                    handleUserRegistration();
                    break;
                case 3:
                    handleAdminLogin();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return; // Exit the application
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    private void displayUserDashboard() {
        while (true) {
            System.out.println("\nUser Dashboard:");
            System.out.println("1. View Today's Top News");
            System.out.println("2. Categorical News");
            System.out.println("3. View Recommended News");
            System.out.println("4. View My User Profile");
            System.out.println("5. Back to Main Menu");
            System.out.println("6. Logout");
            System.out.print("Choose an option: ");

            int choice = input.nextInt();
            input.nextLine(); // Clear buffer

            switch (choice) {
                case 1:
                    viewTodaysTopNews();
                    break;
                case 2:
                    viewCategoricalNews();
                    break;
                case 3:
                    viewRecommendedNews();
                    break;
                case 4:
                    viewUserProfile();
                    break;
                case 5:
                    return; // Return to Main Menu
                case 6:
                    isUserLoggedIn = false;
                    System.out.println("Logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }

            if (!isUserLoggedIn) {
                return; // Exit dashboard if logged out
            }
        }
    }

    private void viewAllUsers(DatabaseHandler dbManager) {
        try {
            dbManager.connect();
            String query = "SELECT * FROM users";
            Statement stmt = dbManager.connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n----- User List -----");
            while (rs.next()) {
                System.out.println("User ID: " + rs.getString("userId"));
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("---------------------");
            }
            dbManager.closeConnection();
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
    }

    private void addUser(DatabaseHandler dbManager, Scanner scanner) {
        System.out.println("Enter Username: ");
        String username = scanner.nextLine();
        System.out.println("Enter Password: ");
        String password = scanner.nextLine();
        System.out.println("Enter Email: ");
        String email = scanner.nextLine();
        System.out.println("Enter Name: ");
        String name = scanner.nextLine();

        GeneralUser newUser = new GeneralUser(username, password, email, name);
        try {
            boolean success = dbManager.saveUserToDB(newUser);
            if (success) {
                System.out.println("User added successfully!");
            } else {
                System.out.println("Failed to add user.");
            }
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }

    private void updateUser(DatabaseHandler dbManager, Scanner scanner) {
        System.out.println("Enter the User ID of the user to update: ");
        String userId = scanner.nextLine();

        System.out.println("Enter new Username (leave blank to keep unchanged): ");
        String username = scanner.nextLine();
        System.out.println("Enter new Password (leave blank to keep unchanged): ");
        String password = scanner.nextLine();
        System.out.println("Enter new Email (leave blank to keep unchanged): ");
        String email = scanner.nextLine();
        System.out.println("Enter new Name (leave blank to keep unchanged): ");
        String name = scanner.nextLine();

        try {
            dbManager.connect();
            String query = """
                UPDATE users 
                SET username = COALESCE(NULLIF(?, ''), username),
                    password = COALESCE(NULLIF(?, ''), password),
                    email = COALESCE(NULLIF(?, ''), email),
                    name = COALESCE(NULLIF(?, ''), name)
                WHERE userId = ?""";
            PreparedStatement stmt = dbManager.connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, name);
            stmt.setString(5, userId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("User updated successfully!");
            } else {
                System.out.println("User ID not found.");
            }
            dbManager.closeConnection();
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    private void deleteUser(DatabaseHandler dbManager, Scanner scanner) {
        System.out.println("Enter the User ID of the user to delete: ");
        String userId = scanner.nextLine();

        try {
            dbManager.connect();
            String query = "DELETE FROM users WHERE userId = ?";
            PreparedStatement stmt = dbManager.connection.prepareStatement(query);
            stmt.setString(1, userId);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("User deleted successfully!");
            } else {
                System.out.println("User ID not found.");
            }
            dbManager.closeConnection();
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

    private String loggedInUsername = null; // Store the logged-in username

    private void handleUserLogin() {
        System.out.println("\nUser Login...");
        System.out.print("Enter username: ");
        String username = input.nextLine();
        System.out.print("Enter password: ");
        String password = input.nextLine();

        try {
            GeneralUser user = dbHandler.validateUserLogin(username, password);
            if (user != null) {
                loggedInUsername = username; // Store the logged-in username
                System.out.println("Login successful! Welcome, " + user.getName());
                displayUserDashboard(); // Display the user dashboard
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (Exception e) {
            System.out.println("Error occurred while logging in: " + e.getMessage());
        }
    }

    private void handleUserRegistration() {
        System.out.println("\nUser Registration...");
        try {
            System.out.print("Enter username: ");
            String username = input.nextLine();

            // Check if username is unique
            if (dbHandler.isUsernameUnique(username)) {
                System.out.print("Enter password: ");
                String password = input.nextLine();
                System.out.print("Enter email: ");
                String email = input.nextLine();
                System.out.print("Enter your full name: ");  // Prompt for name
                String name = input.nextLine();

                // Ensure name is not empty or null
                if (name == null || name.trim().isEmpty()) {
                    System.out.println("Name cannot be empty or null. Please try again.");
                    return;
                }

                GeneralUser user = new GeneralUser(username, password, email, name); // Pass the name to the constructor

                if (dbHandler.saveUserToDB(user)) {
                    System.out.println("User registered successfully!");
                } else {
                    System.out.println("Failed to register user. Please try again.");
                }
            } else {
                System.out.println("Username already exists. Please choose a different username.");
            }
        } catch (Exception e) {
            System.err.println("An error occurred during registration: " + e.getMessage());
        }
    }

    private void viewTodaysTopNews() {
        ArticalFetcher fetcher = new ArticalFetcher();
        Scanner scanner = new Scanner(System.in);

        try {
            // Fetch today's top news
            List<Article> topNews = fetcher.fetchTopNews(); // Fetch Article objects

            while (true) {
                System.out.println("\n========== Today's Top News ==========");
                for (int i = 0; i < topNews.size(); i++) {
                    System.out.println((i + 1) + ". " + topNews.get(i).getTitle());
                }
                System.out.println("======================================");
                System.out.println("Options:");
                System.out.println("[1-N] Enter the number of an article to read the full details.");
                System.out.println("[0] Return to the main menu.");
                System.out.println("======================================");
                System.out.print("Your choice: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a valid number.");
                    scanner.next(); // Clear invalid input
                    continue;
                }

                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear the buffer

                if (choice == 0) {
                    System.out.println("\nReturning to the User Main menu...");
                    displayUserDashboard();
                    break;
                } else if (choice > 0 && choice <= topNews.size()) {
                    Article selectedArticle = topNews.get(choice - 1);
                    System.out.println("\n========== Article Details ==========");
                    System.out.println("Title: " + selectedArticle.getTitle());
                    System.out.println("Author: " + selectedArticle.getAuthor());
                    System.out.println("Published Date: " + selectedArticle.getPublishedDate());
                    System.out.println("\nContent:\n" + selectedArticle.getContent());
                    System.out.println("======================================");
                    System.out.println("Press Enter to return to the news list.");
                    scanner.nextLine();
                } else {
                    System.out.println("Invalid choice. Please select a valid article number.");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while fetching the news: " + e.getMessage());
        }
    }

    private void viewCategoricalNews() {
        ArticalFetcher fetcher = new ArticalFetcher();
        System.out.print("Enter a category (e.g., business, entertainment, health, science, sports, technology): ");
        String category = input.nextLine().toLowerCase();

        try {
            // Fetch news for the given category
            List<Article> news = fetcher.fetchNewsByCategory(category);
            if (news.isEmpty()) {
                System.out.println("No news articles found for the category: " + category);
            } else {
                boolean keepBrowsing = true;
                while (true) {
                    System.out.println("\nNews in category '" + category + "':");
                    for (int i = 0; i < news.size(); i++) {
                        System.out.println((i + 1) + ". " + news.get(i).getTitle());
                    }

                    // Prompt user to select an article
                    System.out.println("\nEnter the number of the article you want to read in full:");
                    System.out.println("0. Go back to the User Dashboard");
                    int choice = input.nextInt();
                    input.nextLine(); // Consume the newline character

                    if (choice == 0) {
                        // Go back to the user dashboard
                        displayUserDashboard();
                        return;
                    } else if (choice > 0 && choice <= news.size()) {
                        Article selectedArticle = news.get(choice - 1);
                        System.out.println("\nFull Article:");
                        System.out.println("Title: " + selectedArticle.getTitle());
                        System.out.println("Author: " + selectedArticle.getAuthor());
                        System.out.println("Date: " + selectedArticle.getPublishedDate());
                        System.out.println("Content: \n" + selectedArticle.getContent());

                        // Ask the user if they want to continue
                        System.out.println("\nWhat would you like to do next?");
                        System.out.println("1. Go back to the news list for this category");
                        System.out.println("2. Go back to the User Dashboard");
                        int nextChoice = input.nextInt();
                        input.nextLine(); // Consume the newline character

                        if (nextChoice == 2) {
                            // Exit to the user dashboard
                            displayUserDashboard();
                            return;
                        } else if (nextChoice != 1) {
                            System.out.println("Invalid choice. Returning to the news list.");
                        }
                    } else {
                        System.out.println("Invalid selection. Please try again.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch categorical news: " + e.getMessage());
            displayUserDashboard(); // Ensure fallback to dashboard on error
        }
    }

    public void viewRecommendedNews() {
        if (loggedInUsername == null) { // Ensure a user is logged in
            System.out.println("Error: No user is logged in. Please log in first.");
            return;
        }

        RecommendationEngine recommendationEngine = new RecommendationEngine();

        // Get recommendations for the user based on their username
        List<String> recommendedNews = recommendationEngine.getRecommendations(loggedInUsername);

        // Display the recommended news
        System.out.println("Recommended News Articles for User " + loggedInUsername + ":");
        if (recommendedNews.isEmpty()) {
            System.out.println("No recommendations available.");
        } else {
            for (String news : recommendedNews) {
                System.out.println("- " + news);
            }
        }
    }

    private void viewUserProfile() {
        DatabaseHandler dbManager = new DatabaseHandler();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your username to view profile: ");
        String username = scanner.nextLine();

        try {
            // Query user details
            ResultSet userResult = dbManager.getUserProfile(username);
            if (userResult.next()) {
                System.out.println("----- User Profile -----");
                System.out.println("Username: " + userResult.getString("Username"));
                System.out.println("Email: " + userResult.getString("Email"));
                System.out.println("Account Created At: " + userResult.getString("CreatedAt"));

                // Query preferences
                ResultSet preferencesResult = dbManager.getUserPreferences(userResult.getInt("UserID"));
                System.out.println("\nPreferences:");
                while (preferencesResult.next()) {
                    System.out.println("- " + preferencesResult.getString("Category"));
                }

                // Query reading history
                ResultSet historyResult = dbManager.getUserReadingHistory(userResult.getInt("UserID"));
                System.out.println("\nReading History:");
                while (historyResult.next()) {
                    System.out.println("- Article: " + historyResult.getString("Title"));
                    System.out.println("  Action: " + historyResult.getString("Action"));
                    System.out.println("  Interaction Time: " + historyResult.getString("InteractionTime"));
                }
            } else {
                System.out.println("User not found!");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user profile: " + e.getMessage());
        }

        // After viewing profile, go back to the user dashboard
        System.out.println("\nReturning to User Dashboard...");
        displayUserDashboard();
    }

    private static void handleAdminLogin() {
        System.out.println("\nAdmin Login...");
        System.out.print("Enter admin username: ");
        String adminUsername = input.nextLine();
        System.out.print("Enter admin password: ");
        String adminPassword = input.nextLine();

        try {
            // Validate the admin credentials using DBHandler
            Admin admin = DatabaseHandler.validateAdminLogin(adminUsername, adminPassword);
            if (admin != null) {
                System.out.println("Admin login successful! Welcome, " + admin.getName());
                // Admin-specific functionality is moved to Admin class
                admin.adminMenu(input); // Passing input for menu handling
            } else {
                System.out.println("Invalid admin username or password.");
            }
        } catch (Exception e) {
            System.out.println("Error occurred while logging in as admin: " + e.getMessage());
        }
    }

}
