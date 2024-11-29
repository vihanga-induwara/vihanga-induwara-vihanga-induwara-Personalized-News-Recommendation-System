package App;

import DB.DatabaseHandler;
import Model.Admin;
import Model.Article;
import Model.GeneralUser;
import Service.ArticalFetcher;
import Service.RecommendationEngine;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;


public class Main {
    private static final Scanner input = new Scanner(System.in);
    private final DatabaseHandler dbHandler = new DatabaseHandler(); // Assuming DatabaseHandler handles DB operations
    private boolean isAdminLoggedIn = false;
    private boolean isUserLoggedIn = false;

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
                    if (isAdminLoggedIn) {
                        displayAdminDashboard();
                    }
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

    private void displayAdminDashboard() {
        while (true) {
            System.out.println("\nAdmin Dashboard:");
            System.out.println("1. Manage Users");
            System.out.println("2. Manage News");
            System.out.println("3. View System Logs");
            System.out.println("4. Back to Main Menu");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");

            int choice = input.nextInt();
            input.nextLine(); // Clear buffer

            switch (choice) {
                case 1:
                    manageUsers();
                    break;
                case 2:
                    manageNews();
                    break;
                case 3:
                    viewSystemLogs();
                    break;
                case 4:
                    return; // Return to Main Menu
                case 5:
                    isAdminLoggedIn = false;
                    System.out.println("Logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }

            if (!isAdminLoggedIn) {
                return; // Exit dashboard if logged out
            }
        }
    }

    private void manageUsers() {
        System.out.println("Managing users...");
        // Add logic to manage users
    }

    private void manageNews() {
        System.out.println("Managing news...");
        // Add logic to manage news
    }

    private void viewSystemLogs() {
        System.out.println("Viewing system logs...");
        // Add logic to display system logs
    }

    private void handleUserLogin() {
        System.out.println("\nUser Login...");
        System.out.print("Enter username: ");
        String username = input.nextLine();
        System.out.print("Enter password: ");
        String password = input.nextLine();

        try {
            GeneralUser user = dbHandler.validateUserLogin(username, password);
            if (user != null) {
                System.out.println("Login successful! Welcome, " + user.getName());
                displayUserDashboard();
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
                System.out.println("Enter the number of an article to read the full details.");
                System.out.println("Enter 0 to go back to the main menu.");
                System.out.print("Your choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear the buffer

                if (choice == 0) {
                    System.out.println("Returning to the User Main menu...");
                    displayUserDashboard();
                    break;
                } else if (choice > 0 && choice <= topNews.size()) {
                    // Display full article details
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
                    System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch top news: " + e.getMessage());
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
                System.out.println("News in category '" + category + "':");
                for (Article article : news) {
                    System.out.println("- " + article);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch categorical news: " + e.getMessage());
        }
    }

    private void viewRecommendedNews() {
        RecommendationEngine recommendationEngine = new RecommendationEngine();

        // Get recommendations for a specific user (e.g., userId: 1)
        List<String> recommendedNews = recommendationEngine.getRecommendations(1);

        // Display the recommended news
        System.out.println("Recommended News Articles:");
        for (String news : recommendedNews) {
            System.out.println("- " + news);
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
