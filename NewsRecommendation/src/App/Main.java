package App;

import DB.DatabaseHandler;
import Model.Admin;
import Model.Article;
import Model.GeneralUser;
import Service.ArticalFetcher;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;



public class Main {
    private static final Scanner input = new Scanner(System.in);
    private final DatabaseHandler dbHandler = new DatabaseHandler(); // Assuming DatabaseHandler handles DB operations
    private boolean isUserLoggedIn = false;


    public static void main(String[] args) {
        Main app = new Main();
        app.displayMainMenu();
    }

    public void displayMainMenu() {
        while (true) {
            try {
                // Main Menu UI
                System.out.println("\n==============================");
                System.out.println("         🌟 MAIN MENU 🌟       ");
                System.out.println("==============================");
                System.out.println("1️⃣  Login");
                System.out.println("2️⃣  Registration");
                System.out.println("3️⃣  Admin Login");
                System.out.println("4️⃣  🚪 Exit");
                System.out.println("==============================");
                System.out.print("🔹 Choose an option (1-4): ");

                // Validate user input
                if (!input.hasNextInt()) {
                    System.out.println("❌ Invalid input! Please enter a number between 1 and 4.");
                    input.nextLine(); // Clear invalid input
                    continue;
                }

                int choice = input.nextInt();
                input.nextLine(); // Clear buffer

                // Process the user's choice
                switch (choice) {
                    case 1:
                        System.out.println("\n🔐 Login Selected");
                        handleUserLogin();
                        if (isUserLoggedIn) {
                            displayUserDashboard();
                        }
                        break;

                    case 2:
                        System.out.println("\n📝 Registration Selected");
                        handleUserRegistration();
                        break;

                    case 3:
                        System.out.println("\n👩‍💼 Admin Login Selected");
                        handleAdminLogin();
                        break;

                    case 4:
                        System.out.println("\n👋 Thank you for using our system! Goodbye!");
                        return; // Exit the application

                    default:
                        System.out.println("❌ Invalid choice! Please select a number between 1 and 4.");
                        break;
                }
            } catch (InputMismatchException ime) {
                // Handle cases where input is not an integer
                System.out.println("❌ Error: Please enter a valid number! Try again.");
                input.nextLine(); // Clear the invalid input
            } catch (Exception e) {
                // General exception handler
                System.out.println("⚠️ An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    private void displayUserDashboard() {
        while (true) {
            try {
                // User Dashboard UI
                System.out.println("\n======================================");
                System.out.println("        📰 USER DASHBOARD 📊          ");
                System.out.println("======================================");
                System.out.println("1️⃣  View Today's Top News");
                System.out.println("2️⃣  Categorical News");
                System.out.println("3️⃣  Recommended News");
                System.out.println("4️⃣  My Profile");
                System.out.println("5️⃣  🔙 Back to Main Menu");
                System.out.println("6️⃣  🚪 Logout");
                System.out.println("======================================");
                System.out.print("🔹 Choose an option (1-6): ");

                // Validate user input
                if (!input.hasNextInt()) {
                    System.out.println("❌ Invalid input! Please enter a number between 1 and 6.");
                    input.nextLine(); // Clear invalid input
                    continue;
                }

                int choice = input.nextInt();
                input.nextLine(); // Clear buffer

                // Process the user's choice
                switch (choice) {
                    case 1:
                        System.out.println("\n📅 Today's Top News:");
                        viewTodaysTopNews();
                        break;

                    case 2:
                        System.out.println("\n📂 Categorical News:");
                        viewCategoricalNews();
                        break;

                    case 3:
                        System.out.println("\n💡 Recommended News for You:");
                        viewRecommendedNews();
                        break;

                    case 4:
                        System.out.println("\n👤 Your Profile:");
                        viewUserProfile();
                        break;

                    case 5:
                        System.out.println("\n🔙 Returning to Main Menu...");
                        return; // Return to Main Menu

                    case 6:
                        System.out.println("\n🚪 Logging out...");
                        isUserLoggedIn = false;
                        System.out.println("✅ You have logged out successfully.");
                        return;

                    default:
                        System.out.println("❌ Invalid choice! Please select a number between 1 and 6.");
                        break;
                }

                // Automatically exit dashboard if logged out
                if (!isUserLoggedIn) {
                    return;
                }

            } catch (InputMismatchException ime) {
                // Handle cases where input is not an integer
                System.out.println("❌ Error: Please enter a valid number! Try again.");
                input.nextLine(); // Clear the invalid input
            } catch (Exception e) {
                // General exception handler
                System.out.println("⚠️ An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    private String loggedInUsername = null; // Store the logged-in username

    private void handleUserLogin() {
        System.out.println("\n======================================");
        System.out.println("           🔐 USER LOGIN             ");
        System.out.println("======================================");

        try {
            // Prompt user for login details
            System.out.print("👤 Enter username: ");
            String username = input.nextLine().trim();

            // Check if username is empty
            if (username.isEmpty()) {
                System.out.println("❌ Username cannot be empty. Please try again.");
                return;
            }

            System.out.print("🔑 Enter password: ");
            String password = input.nextLine();

            // Check if password is empty
            if (password.isEmpty()) {
                System.out.println("❌ Password cannot be empty. Please try again.");
                return;
            }

            // Validate user credentials
            System.out.println("\n🔄 Validating credentials, please wait...");
            GeneralUser user = dbHandler.validateUserLogin(username, password);

            // Handle validation result
            if (user != null) {
                loggedInUsername = username; // Store the logged-in username
                System.out.println("✅ Login successful! Welcome, " + user.getName() + " 🎉");
                displayUserDashboard(); // Display the user dashboard
            } else {
                System.out.println("❌ Invalid username or password. Please try again.");
            }

        } catch (SQLException sqlEx) {
            // Handle database-related errors
            System.out.println("❌ Database error: Unable to connect to the database. Please try again later.");
            System.out.println("Details: " + sqlEx.getMessage());

        } catch (NullPointerException npe) {
            // Handle null reference issues
            System.out.println("⚠️ Unexpected error: Required data is missing. Please contact support.");

        } catch (Exception e) {
            // General exception handling
            System.out.println("⚠️ An unexpected error occurred: " + e.getMessage());

        } finally {
            // Optionally provide feedback after execution
            System.out.println("📝 Note: Ensure your credentials are correct. Contact support if you face issues.");
        }

        System.out.println("======================================");
    }

    private void handleUserRegistration() {
        System.out.println("\n======================================");
        System.out.println("        📝 USER REGISTRATION          ");
        System.out.println("======================================");

        try {
            // Prompt for username
            System.out.print("👤 Enter username: ");
            String username = input.nextLine().trim();

            // Check if username is empty
            if (username.isEmpty()) {
                System.out.println("❌ Username cannot be empty. Please try again.");
                return;
            }

            // Check if username is unique
            if (!dbHandler.isUsernameUnique(username)) {
                System.out.println("❌ Username already exists. Please choose a different username.");
                return;
            }

            // Prompt for password
            System.out.print("🔑 Enter password: ");
            String password = input.nextLine();

            // Ensure password is not empty
            if (password.isEmpty()) {
                System.out.println("❌ Password cannot be empty. Please try again.");
                return;
            }

            // Prompt for email
            System.out.print("📧 Enter email: ");
            String email = input.nextLine().trim();

            // Basic email validation
            if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                System.out.println("❌ Invalid email format. Please try again.");
                return;
            }

            // Prompt for full name
            System.out.print("📝 Enter your full name: ");
            String name = input.nextLine().trim();

            // Ensure name is not empty or null
            if (name.isEmpty()) {
                System.out.println("❌ Name cannot be empty or null. Please try again.");
                return;
            }

            // Create a new GeneralUser object
            GeneralUser user = new GeneralUser(username, password, email, name);

            // Save user to the database
            System.out.println("\n🔄 Registering user, please wait...");
            if (dbHandler.saveUserToDB(user)) {
                System.out.println("✅ User registered successfully! 🎉");
            } else {
                System.out.println("❌ Failed to register user. Please try again.");
            }

        } catch (SQLException sqlEx) {
            // Handle database errors
            System.out.println("❌ Database error: Unable to save user. Please try again later.");
            System.out.println("Details: " + sqlEx.getMessage());

        } catch (IllegalArgumentException iae) {
            // Handle invalid arguments
            System.out.println("❌ Invalid input: " + iae.getMessage());

        } catch (Exception e) {
            // General exception handling
            System.out.println("⚠️ An unexpected error occurred: " + e.getMessage());

        } finally {
            // Optionally provide feedback after execution
            System.out.println("======================================");
        }
    }

    private void viewTodaysTopNews() {
        ArticalFetcher fetcher = new ArticalFetcher();
        Scanner scanner = new Scanner(System.in);

        try {
            // Fetch today's top news
            List<Article> topNews = fetcher.fetchTopNews();

            if (topNews.isEmpty()) {
                System.out.println("📭 No top news available today. Please check back later!");
                return;
            }

            while (true) {
                // Display the top news list
                System.out.println("\n" + "=".repeat(40));
                System.out.println("🌟 TODAY'S TOP NEWS 🌟");
                System.out.println("=".repeat(40));
                for (int i = 0; i < topNews.size(); i++) {
                    System.out.printf("%2d. %s\n", (i + 1), topNews.get(i).getTitle());
                }
                System.out.println("=".repeat(40));
                System.out.println("Options:");
                System.out.println("➡️ [1-N] Select an article to read.");
                System.out.println("↩️ [0] Return to the main menu.");
                System.out.print("Your choice: ");

                // Validate user input
                if (!scanner.hasNextInt()) {
                    System.out.println("❌ Invalid input. Please enter a number.");
                    scanner.next(); // Clear invalid input
                    continue;
                }

                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear the buffer

                // Handle user choice
                if (choice == 0) {
                    System.out.println("↩️ Returning to the main menu...");
                    return; // Exit to the main menu
                } else if (choice > 0 && choice <= topNews.size()) {
                    Article selectedArticle = topNews.get(choice - 1);

                    // Display the selected article details
                    System.out.println("\n" + "=".repeat(40));
                    System.out.println("📰 ARTICLE DETAILS 📰");
                    System.out.println("=".repeat(40));
                    System.out.printf("📌 Title: %s\n", selectedArticle.getTitle());
                    System.out.printf("✍️ Author: %s\n",
                            selectedArticle.getAuthor() != null ? selectedArticle.getAuthor() : "Unknown");
                    System.out.printf("📅 Published Date: %s\n",
                            selectedArticle.getPublishedDate() != null ? selectedArticle.getPublishedDate() : "Unknown");
                    System.out.println("-".repeat(40));
                    System.out.println(selectedArticle.getContent());
                    System.out.println("=".repeat(40));

                    // Ask user for their action
                    System.out.println("Would you like to:");
                    System.out.println("🟢 [1] Read this article.");
                    System.out.println("🔵 [2] Skip this article.");
                    System.out.print("Your choice: ");

                    // Validate the action choice
                    if (!scanner.hasNextInt()) {
                        System.out.println("❌ Invalid input. Please enter 1 or 2.");
                        scanner.next(); // Clear invalid input
                        continue;
                    }

                    int actionChoice = scanner.nextInt();
                    scanner.nextLine(); // Clear the buffer

                    // Record the user's action
                    String action = (actionChoice == 1) ? "read" : "skip";
                    dbHandler.recordUserAction(
                            loggedInUsername,
                            selectedArticle.getTitle(),
                            selectedArticle.getContent(),
                            selectedArticle.getAuthor() != null ? selectedArticle.getAuthor() : "Unknown",
                            selectedArticle.getPublishedDate() != null ? selectedArticle.getPublishedDate() : "Unknown",
                            action
                    );

                    System.out.printf("✅ Action recorded: You chose to %s this article.\n", action);
                    System.out.println("Press Enter to return to the news list.");
                    scanner.nextLine(); // Wait for user input
                } else {
                    System.out.println("❌ Invalid choice. Please select a valid article number.");
                }
            }
        } catch (NullPointerException npe) {
            // Handle null pointer exceptions (e.g., if fetcher or articles are null)
            System.err.println("⚠️ Error: Unable to fetch news data. Please try again later.");
        } catch (SQLException sqlEx) {
            // Handle database errors
            System.err.println("❌ Database error: Unable to record your action. Details: " + sqlEx.getMessage());
        } catch (Exception e) {
            // General exception handling
            System.err.println("⚠️ An unexpected error occurred: " + e.getMessage());
        } finally {
            System.out.println("\nThank you for using the News Viewer. 📖");
        }
    }

    private void viewCategoricalNews() {
        ArticalFetcher fetcher = new ArticalFetcher();
        System.out.print("🔍 Enter a category (e.g., business, entertainment, health, science, sports, technology): ");
        String category = input.nextLine().toLowerCase();

        try {
            // Fetch news for the given category
            List<Article> news = fetcher.fetchNewsByCategory(category);

            if (news.isEmpty()) {
                System.out.println("❌ No news articles found for the category: " + category);
                return;
            }

            while (true) {
                // Display the news list for the selected category
                System.out.println("\n📚 News in category '" + category + "':");
                for (int i = 0; i < news.size(); i++) {
                    System.out.printf("%2d. %s\n", (i + 1), news.get(i).getTitle());
                }

                // Prompt user to select an article
                System.out.println("\nEnter the number of the article you want to read in full:");
                System.out.println("↩️ [0] Go back to the User Dashboard");
                System.out.print("Your choice: ");
                int choice = input.nextInt();
                input.nextLine(); // Consume the newline character

                if (choice == 0) {
                    // Go back to the user dashboard
                    System.out.println("🔙 Returning to the User Dashboard...");
                    displayUserDashboard();
                    return;
                } else if (choice > 0 && choice <= news.size()) {
                    Article selectedArticle = news.get(choice - 1);

                    // Display the full article details
                    System.out.println("\n📰 Full Article:");
                    System.out.println("📌 Title: " + selectedArticle.getTitle());
                    System.out.println("✍️ Author: " + (selectedArticle.getAuthor() != null ? selectedArticle.getAuthor() : "Unknown"));
                    System.out.println("📅 Date: " + (selectedArticle.getPublishedDate() != null ? selectedArticle.getPublishedDate() : "Unknown"));
                    System.out.println("-".repeat(40));
                    System.out.println(selectedArticle.getContent());
                    System.out.println("=".repeat(40));

                    // Ask user for their action: Read or Skip
                    System.out.println("\nWould you like to:");
                    System.out.println("🟢 [1] Read this article.");
                    System.out.println("🔵 [2] Skip this article.");
                    System.out.print("Your choice: ");

                    // Validate the action choice
                    if (!input.hasNextInt()) {
                        System.out.println("❌ Invalid input. Please enter 1 or 2.");
                        input.next(); // Clear invalid input
                        continue;
                    }

                    int actionChoice = input.nextInt();
                    input.nextLine(); // Consume the newline character

                    // Record the user's action
                    String action = (actionChoice == 1) ? "read" : "skip";
                    dbHandler.recordUserAction(
                            loggedInUsername,
                            selectedArticle.getTitle(),
                            selectedArticle.getContent(),
                            selectedArticle.getAuthor() != null ? selectedArticle.getAuthor() : "Unknown",
                            selectedArticle.getPublishedDate() != null ? selectedArticle.getPublishedDate() : "Unknown",
                            action
                    );

                    System.out.println("✅ Action recorded: You chose to " + action + " this article.");

                    // Ask for next action
                    System.out.println("\nWhat would you like to do next?");
                    System.out.println("1. Go back to the news list for this category");
                    System.out.println("2. Go back to the User Dashboard");
                    System.out.print("Your choice: ");

                    // Validate the next choice
                    if (!input.hasNextInt()) {
                        System.out.println("❌ Invalid input. Please select 1 or 2.");
                        input.next(); // Clear invalid input
                        continue;
                    }

                    int nextChoice = input.nextInt();
                    input.nextLine(); // Consume the newline character

                    if (nextChoice == 2) {
                        // Exit to the user dashboard
                        System.out.println("🔙 Returning to the User Dashboard...");
                        displayUserDashboard();
                        return;
                    } else if (nextChoice != 1) {
                        System.out.println("❌ Invalid choice. Returning to the news list.");
                    }
                } else {
                    System.out.println("❌ Invalid selection. Please choose a valid article number.");
                }
            }
        } catch (NullPointerException npe) {
            // Handle null pointer exceptions (e.g., if articles are null)
            System.err.println("⚠️ Error: Unable to fetch news for category '" + category + "'. Please try again later.");
        } catch (SQLException sqlEx) {
            // Handle database errors
            System.err.println("❌ Database error: Unable to record your action. Details: " + sqlEx.getMessage());
        } catch (Exception e) {
            // General exception handling
            System.err.println("⚠️ An unexpected error occurred: " + e.getMessage());
        } finally {
            // Ensure the dashboard is always available after an error
            System.out.println("\nThank you for using the News Viewer. 📖");
        }
    }

    public void viewRecommendedNews() {
        if (loggedInUsername == null) { // Ensure a user is logged in
            System.out.println("❌ Error: No user is logged in. Please log in first.");
            return;
        }

        try {
            DatabaseHandler dbHandler = new DatabaseHandler();

            // Retrieve the user's preferred category from the database
            String preferredCategory = dbHandler.getUserPreferredCategory(loggedInUsername);

            if (preferredCategory == null || preferredCategory.isEmpty()) {
                System.out.println("⚠️ No preferred category found for the user. Please update your preferences.");
                return;
            }

            ArticalFetcher fetcher = new ArticalFetcher();
            // Fetch 5 articles from the user's preferred category
            List<Article> articles = fetcher.fetchNewsByCategory(preferredCategory);

            if (articles.isEmpty()) {
                System.out.println("❌ No articles found in your preferred category: " + preferredCategory);
                return;
            }

            // Display the recommended news articles
            System.out.println("\n🔖 Recommended News Articles in '" + preferredCategory + "' for User " + loggedInUsername + ":");
            for (int i = 0; i < Math.min(5, articles.size()); i++) {
                System.out.printf("📄 %d. %s\n", (i + 1), articles.get(i).getTitle());
            }

            // Allow the user to choose an article to read
            System.out.println("\nEnter the number of the article you want to read in full:");
            System.out.println("↩️ [0] Go back to the User Dashboard");
            System.out.print("Your choice: ");

            // Validate user input for article selection
            if (!input.hasNextInt()) {
                System.out.println("❌ Invalid input. Please enter a valid article number.");
                input.next(); // Clear invalid input
                return;
            }

            int choice = input.nextInt();
            input.nextLine(); // Consume the newline character

            if (choice == 0) {
                // Go back to the user dashboard
                System.out.println("🔙 Returning to the User Dashboard...");
                displayUserDashboard();
                return;
            } else if (choice > 0 && choice <= Math.min(5, articles.size())) {
                // Display the full article
                Article selectedArticle = articles.get(choice - 1);
                System.out.println("\n📰 Full Article:");
                System.out.println("📌 Title: " + selectedArticle.getTitle());
                System.out.println("✍️ Author: " + (selectedArticle.getAuthor() != null ? selectedArticle.getAuthor() : "Unknown"));
                System.out.println("📅 Date: " + (selectedArticle.getPublishedDate() != null ? selectedArticle.getPublishedDate() : "Unknown"));
                System.out.println("-".repeat(40));
                System.out.println(selectedArticle.getContent());
                System.out.println("=".repeat(40));
            } else {
                System.out.println("❌ Invalid selection. Returning to the User Dashboard.");
                displayUserDashboard();
            }
        } catch (SQLException sqlEx) {
            // Handle database-related errors
            System.err.println("❌ Database error: Unable to fetch user preferences or articles. Details: " + sqlEx.getMessage());
            displayUserDashboard();
        } catch (Exception e) {
            // General exception handling for other errors
            System.err.println("⚠️ An unexpected error occurred while retrieving recommended news: " + e.getMessage());
            displayUserDashboard(); // Ensure fallback to dashboard on error
        }
    }

    private void viewUserProfile() {
        DatabaseHandler dbManager = new DatabaseHandler();
        Scanner scanner = new Scanner(System.in);

        // Prompt user for username input
        System.out.println("🔑 Enter your username to view profile: ");
        String username = scanner.nextLine().trim();

        // Validate the username input
        if (username.isEmpty()) {
            System.out.println("❌ Username cannot be empty. Please try again.");
            return;
        }

        try {
            // Query user details from the database
            ResultSet userResult = dbManager.getUserProfile(username);
            if (userResult.next()) {
                System.out.println("\n----- User Profile -----");
                System.out.println("👤 Username: " + userResult.getString("username")); // Display username
                System.out.println("📧 Email: " + userResult.getString("email"));
                System.out.println("📅 Account Created At: " + userResult.getString("createdAt"));

                // Query and display user preferences
                ResultSet preferencesResult = dbManager.getUserPreferences(username);
                System.out.println("\n🌟 Preferences:");
                if (!preferencesResult.isBeforeFirst()) {
                    System.out.println("- No preferences set.");
                }
                while (preferencesResult.next()) {
                    System.out.println("🔹 " + preferencesResult.getString("category"));
                }

                // Query and display reading history
                ResultSet historyResult = dbManager.getUserReadingHistory(String.valueOf(userResult.getInt("userId")));
                System.out.println("\n📚 Reading History:");
                if (!historyResult.isBeforeFirst()) {
                    System.out.println("- No reading history available.");
                }
                while (historyResult.next()) {
                    System.out.println("📰 Article: " + historyResult.getString("title"));
                    System.out.println("  ⏱️ Action: " + historyResult.getString("action"));
                    System.out.println("  🕒 Interaction Time: " + historyResult.getString("interactionTime"));
                }
            } else {
                System.out.println("❌ User not found!");
            }
        } catch (SQLException e) {
            // Handle SQL exceptions (e.g., issues with the database)
            System.err.println("⚠️ Error retrieving user profile: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            System.err.println("⚠️ Unexpected error: " + e.getMessage());
        } finally {
            // Ensure that the user is returned to the dashboard even after an error
            System.out.println("\n🔙 Returning to User Dashboard...");
            displayUserDashboard();
        }

    }

    private static void handleAdminLogin() {
        System.out.println("\n🔒 Admin Login");
        System.out.println("=".repeat(40));

        // Prompt for admin credentials
        System.out.print("👤 Enter Admin Username: ");
        String adminUsername = input.nextLine();
        System.out.print("🔑 Enter Admin Password: ");
        String adminPassword = input.nextLine();

        try {
            // Validate the admin credentials using the DatabaseHandler
            Admin admin = DatabaseHandler.validateAdminLogin(adminUsername, adminPassword);

            if (admin != null) {
                // Successful login message
                System.out.println("\n✅ Admin login successful! Welcome, " + admin.getName() + " 🎉");
                System.out.println("-".repeat(40));

                // Redirect to the admin menu
                admin.adminMenu(input); // Passing input for handling admin-specific actions
            } else {
                // Invalid credentials message
                System.out.println("\n❌ Invalid admin username or password. Please try again.");
            }
        } catch (NullPointerException npe) {
            // Handle null pointer exceptions (e.g., admin object being null)
            System.out.println("\n⚠️ Unexpected Error: Admin credentials might be missing or incomplete.");
            System.err.println("📜 Details: " + npe.getMessage());
        } catch (InputMismatchException ime) {
            // Handle invalid input types
            System.out.println("\n❌ Input Error: Please provide valid username and password values.");
            input.nextLine(); // Clear the invalid input buffer
        } catch (Exception e) {
            // Catch all other exceptions
            System.out.println("\n⚠️ An unexpected error occurred during admin login.");
            System.err.println("📜 Details: " + e.getMessage());
        } finally {
            // Always show this message regardless of success or failure
            System.out.println("\n🔙 Returning to Main Menu...");
            System.out.println("=".repeat(40));
        }
    }

}
