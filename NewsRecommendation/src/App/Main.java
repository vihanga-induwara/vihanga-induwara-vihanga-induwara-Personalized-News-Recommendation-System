package App;

import DB.DatabaseHandler;
import Model.Admin;
import Model.Article;
import Model.GeneralUser;
import Service.ArticalFetcher;
import Service.RecommendationEngine;

import java.security.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            List<Article> topNews = fetcher.fetchTopNews();

            while (true) {
                // Displaying the top news list
                System.out.println("\n" + "=".repeat(40));
                System.out.println("🌟 Today's Top News 🌟");
                System.out.println("=".repeat(40));
                for (int i = 0; i < topNews.size(); i++) {
                    System.out.printf("%2d. %s\n", (i + 1), topNews.get(i).getTitle());
                }
                System.out.println("=".repeat(40));
                System.out.println("Options:");
                System.out.println("[1-N] Select an article to read.");
                System.out.println("[0] Return to the main menu.");
                System.out.print("Your choice: ");

                // Validating user input
                if (!scanner.hasNextInt()) {
                    System.out.println("❌ Invalid input. Please enter a number.");
                    scanner.next(); // Clear invalid input
                    continue;
                }

                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear the buffer

                // Handling user choice
                if (choice == 0) {
                    System.out.println("Returning to the main menu...");
                    displayUserDashboard();
                    break;
                } else if (choice > 0 && choice <= topNews.size()) {
                    Article selectedArticle = topNews.get(choice - 1);
                    System.out.println("\n" + "=".repeat(40));
                    System.out.println("📰 Article Details 📰");
                    System.out.println("=".repeat(40));
                    System.out.printf("Title: %s\n", selectedArticle.getTitle());
                    System.out.printf("Author: %s\n", selectedArticle.getAuthor() != null ? selectedArticle.getAuthor() : "Unknown");
                    System.out.printf("Published Date: %s\n", selectedArticle.getPublishedDate() != null ? selectedArticle.getPublishedDate() : "Unknown");
                    System.out.println("-".repeat(40));
                    System.out.println(selectedArticle.getContent());
                    System.out.println("=".repeat(40));

                    // Ask user for their action: Read or Skip
                    System.out.println("Would you like to:");
                    System.out.println("[1] Read this article.");
                    System.out.println("[2] Skip this article.");
                    System.out.print("Your choice: ");
                    int actionChoice = scanner.nextInt();
                    scanner.nextLine(); // Clear the buffer

                    // Record the user's action
                    String action = actionChoice == 1 ? "read" : "skip";
                    dbHandler.recordUserAction(
                            loggedInUsername,
                            selectedArticle.getTitle(),
                            selectedArticle.getContent(),
                            selectedArticle.getAuthor() != null ? selectedArticle.getAuthor() : "Unknown",
                            selectedArticle.getPublishedDate() != null ? selectedArticle.getPublishedDate() : "Unknown",
                            action
                    );


                    System.out.println("Action recorded: " + action);
                    System.out.println("Press Enter to return to the news list.");
                    scanner.nextLine(); // Wait for user input
                } else {
                    System.out.println("❌ Invalid choice. Please select a valid article number.");
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error fetching the news: " + e.getMessage());
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

                        // Ask user for their action: Read or Skip
                        System.out.println("\nWould you like to:");
                        System.out.println("[1] Read this article.");
                        System.out.println("[2] Skip this article.");
                        System.out.print("Your choice: ");
                        int actionChoice = input.nextInt();
                        input.nextLine(); // Consume the newline character

                        // Record the user's action
                        String action = actionChoice == 1 ? "read" : "skip";
                        dbHandler.recordUserAction(
                                loggedInUsername,
                                selectedArticle.getTitle(),
                                selectedArticle.getContent(),
                                selectedArticle.getAuthor() != null ? selectedArticle.getAuthor() : "Unknown",
                                selectedArticle.getPublishedDate() != null ? selectedArticle.getPublishedDate() : "Unknown",
                                action
                        );


                        System.out.println("Action recorded: " + action);
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

        try {
            DatabaseHandler dbHandler = new DatabaseHandler();
            // Retrieve the user's preferred category from the database
            String preferredCategory = dbHandler.getUserPreferredCategory(loggedInUsername);

            if (preferredCategory == null || preferredCategory.isEmpty()) {
                System.out.println("No preferred category found for the user. Please update your preferences.");
                return;
            }

            ArticalFetcher fetcher = new ArticalFetcher();
            // Fetch 5 articles from the user's preferred category
            List<Article> articles = fetcher.fetchNewsByCategory(preferredCategory);

            if (articles.isEmpty()) {
                System.out.println("No articles found in your preferred category: " + preferredCategory);
                return;
            }

            System.out.println("\nRecommended News Articles in '" + preferredCategory + "' for User " + loggedInUsername + ":");
            for (int i = 0; i < Math.min(5, articles.size()); i++) {
                System.out.println((i + 1) + ". " + articles.get(i).getTitle());
            }

            // Allow the user to choose an article to read
            System.out.println("\nEnter the number of the article you want to read in full:");
            System.out.println("0. Go back to the User Dashboard");
            int choice = input.nextInt();
            input.nextLine(); // Consume the newline character

            if (choice == 0) {
                // Go back to the user dashboard
                displayUserDashboard();
                return;
            } else if (choice > 0 && choice <= Math.min(5, articles.size())) {
                Article selectedArticle = articles.get(choice - 1);
                System.out.println("\nFull Article:");
                System.out.println("Title: " + selectedArticle.getTitle());
                System.out.println("Author: " + selectedArticle.getAuthor());
                System.out.println("Date: " + selectedArticle.getPublishedDate());
                System.out.println("Content: \n" + selectedArticle.getContent());
            } else {
                System.out.println("Invalid selection. Returning to the User Dashboard.");
                displayUserDashboard();
            }
        } catch (Exception e) {
            System.err.println("Error while retrieving recommended news: " + e.getMessage());
            displayUserDashboard(); // Ensure fallback to dashboard on error
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
                System.out.println("Username: " + userResult.getString("username")); // Match database column name
                System.out.println("Email: " + userResult.getString("email"));
                System.out.println("Account Created At: " + userResult.getString("createdAt")); // Match case

                // Query preferences
                ResultSet preferencesResult = dbManager.getUserPreferences(username);
                System.out.println("\nPreferences:");
                if (!preferencesResult.isBeforeFirst()) {
                    System.out.println("- No preferences set.");
                }
                while (preferencesResult.next()) {
                    System.out.println("- " + preferencesResult.getString("category"));
                }

                // Query reading history
                ResultSet historyResult = dbManager.getUserReadingHistory(String.valueOf(userResult.getInt("userId"))); // Match database column
                System.out.println("\nReading History:");
                if (!historyResult.isBeforeFirst()) {
                    System.out.println("- No reading history available.");
                }
                while (historyResult.next()) {
                    System.out.println("- Article: " + historyResult.getString("title"));
                    System.out.println("  Action: " + historyResult.getString("action"));
                    System.out.println("  Interaction Time: " + historyResult.getString("interactionTime"));
                }
            } else {
                System.out.println("User not found!");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user profile: " + e.getMessage());
        } finally {
            System.out.println("\nReturning to User Dashboard...");
            displayUserDashboard();
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
