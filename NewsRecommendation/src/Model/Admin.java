package Model;

import DB.DatabaseHandler;

import java.sql.*;
import java.util.Scanner;

public class Admin {
    private int adminID;
    private String username;
    private String password;
    private String name;
    private Timestamp createdAt;

    // Constructor
    public Admin(int adminID, String username, String password, Timestamp createdAt) {
        this.adminID = adminID;
        this.username = username;
        this.password = password;
        this.name = name;
        this.createdAt = createdAt;
    }

    // Getter and Setter methods
    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Admin [AdminID=" + adminID + ", Username=" + username + ", Name=" + name + ", CreatedAt=" + createdAt + "]";
    }

    // Admin menu to manage admin-specific tasks
    public void adminMenu(Scanner input) {
        boolean running = true;
        while (running) {
            try {
                // Display Admin Menu with clear options and proper alignment using emojis
                System.out.println("\n🌟 Admin Menu 🌟");
                System.out.println("1️⃣. View Users");
                System.out.println("2️⃣. Manage Users");
                System.out.println("3️⃣. View Reports");
                System.out.println("4️⃣. Logout");
                System.out.print("💡 Please select an option (1-4): ");

                // Validate input for a valid integer choice
                if (!input.hasNextInt()) {
                    System.out.println("❌ Invalid input. Please enter a number between 1 and 4.");
                    input.nextLine(); // Clear invalid input
                    continue;
                }

                int choice = input.nextInt();
                input.nextLine(); // Consume the newline character after integer input

                // Process the user's choice
                switch (choice) {
                    case 1:
                        System.out.println("👥 Viewing users...");
                        viewUsers();
                        break;
                    case 2:
                        System.out.println("🔧 Managing users...");
                        manageUsers(input);
                        break;
                    case 3:
                        System.out.println("📊 Viewing reports...");
                        viewReports();
                        break;
                    case 4:
                        System.out.println("🔒 Logging out... Goodbye!");
                        running = false; // Exit the menu
                        break;
                    default:
                        // Handle invalid choices outside 1-4 range
                        System.out.println("❌ Invalid choice. Please select a number between 1 and 4.");
                }
            } catch (Exception e) {
                // General exception handling to catch any unexpected issues
                System.err.println("⚠️ An error occurred: " + e.getMessage());
                input.nextLine(); // Clear the scanner buffer
            }
        }
    }

    // User Manger menu to manage admin-specific tasks
    private void manageUsers(Scanner input) {
        DatabaseHandler dbHandler = new DatabaseHandler();

        while (true) {
            try {
                // Display User Management Menu with clear options and emojis for better UX
                System.out.println("\n🔧 ----- Manage Users ----- 🔧");
                System.out.println("1️⃣. View All Users");
                System.out.println("2️⃣. Add New User");
                System.out.println("3️⃣. Update Existing User");
                System.out.println("4️⃣. Delete User");
                System.out.println("5️⃣. Exit");
                System.out.print("💡 Enter your choice (1-5): ");

                // Validate input for a valid integer choice
                if (!input.hasNextInt()) {
                    System.out.println("❌ Invalid input. Please enter a valid number (1-5).");
                    input.nextLine(); // Clear invalid input
                    continue; // Ask the user for input again
                }

                int choice = input.nextInt();
                input.nextLine(); // Consume the newline character

                // Handle the user's choice with a switch-case
                switch (choice) {
                    case 1:
                        System.out.println("👥 Viewing all users...");
                        viewAllUsers(dbHandler);
                        break;
                    case 2:
                        System.out.println("➕ Adding a new user...");
                        addUser(dbHandler, input);
                        break;
                    case 3:
                        System.out.println("✏️ Updating an existing user...");
                        updateUser(dbHandler, input);
                        break;
                    case 4:
                        System.out.println("🗑️ Deleting a user...");
                        deleteUser(dbHandler, input);
                        break;
                    case 5:
                        System.out.println("🔒 Exiting user management... Goodbye!");
                        return; // Exit the user management menu
                    default:
                        // Handle invalid choices outside the 1-5 range
                        System.out.println("❌ Invalid choice! Please enter a number between 1 and 5.");
                }
            } catch (Exception e) {
                // General exception handling for any unforeseen errors
                System.err.println("⚠️ An error occurred while managing users: " + e.getMessage());
                input.nextLine(); // Clear the scanner buffer to prevent infinite loop on invalid input
            }
        }
    }

    private void viewAllUsers(DatabaseHandler dbHandler) {
        // Display a welcoming message with an emoji for better UX
        System.out.println("\n👀 Viewing All Users... Please wait while we fetch the details.");

        String query = "SELECT * FROM users"; // SQL query to fetch all users

        try {
            // Establish connection to the database
            dbHandler.connect();
            Statement stmt = dbHandler.connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Check if any users exist
            if (!rs.isBeforeFirst()) {
                System.out.println("❌ No users found in the database.");
                return;
            }

            // Loop through each result (user) and display details
            while (rs.next()) {
                // Fetch user details from the result set
                String userId = rs.getString("userId");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String name = rs.getString("name");

                // Display the user information with clear alignment and formatting
                System.out.println("\n🆔 User ID: " + userId);
                System.out.println("👤 Username: " + username);
                System.out.println("📧 Email: " + email);
                System.out.println("📝 Name: " + name);
                System.out.println("----------");
            }
        } catch (SQLException e) {
            // Handle SQLException with a user-friendly error message
            System.err.println("⚠️ Error retrieving users: " + e.getMessage());
        } finally {
            // Ensure the connection is closed, even in case of an error
            try {
                dbHandler.closeConnection();
                System.out.println("🔌 Database connection closed.");
            } catch (SQLException e) {
                System.err.println("⚠️ Error closing connection: " + e.getMessage());
            }
        }
    }

    private void addUser(DatabaseHandler dbHandler, Scanner input) {
        // Display a welcoming message with an emoji for better UX
        System.out.println("\n👤 Adding a New User... Please provide the required details.");

        // Prompt the admin to enter user details
        System.out.print("🔑 Enter username: ");
        String username = input.nextLine().trim(); // Trim input to remove leading/trailing spaces
        System.out.print("📧 Enter email: ");
        String email = input.nextLine().trim();
        System.out.print("📝 Enter name: ");
        String name = input.nextLine().trim();

        // Validate the inputs to ensure they are not empty
        if (username.isEmpty() || email.isEmpty() || name.isEmpty()) {
            System.out.println("⚠️ All fields are required. Please try again.");
            return; // Return early if validation fails
        }

        // Prepare the SQL query for inserting the new user
        String query = "INSERT INTO users (username, email, name) VALUES (?, ?, ?)";

        try {
            // Connect to the database
            dbHandler.connect();

            // Prepare the statement and set parameters
            try (PreparedStatement stmt = dbHandler.connection.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, name);

                // Execute the update and check if the user was added successfully
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    // Successful user addition message
                    System.out.println("✅ User added successfully.");
                } else {
                    // Failure message if no rows were affected
                    System.out.println("❌ Failed to add user. Please try again.");
                }
            }
        } catch (SQLException e) {
            // Handle SQL exceptions with a user-friendly message
            System.err.println("⚠️ Error adding user: " + e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            System.err.println("⚠️ Unexpected error: " + e.getMessage());
        } finally {
            // Ensure the database connection is closed in all cases
            try {
                dbHandler.closeConnection();
                System.out.println("🔌 Database connection closed.");
            } catch (SQLException e) {
                // Handle any errors that occur while closing the connection
                System.err.println("⚠️ Error closing connection: " + e.getMessage());
            }
        }
    }

    private void updateUser(DatabaseHandler dbHandler, Scanner input) {
        // Display a welcoming message with an emoji for better UX
        System.out.println("\n✏️ Updating a User's Information...");

        // Prompt the admin to enter the username of the user they wish to update
        System.out.print("🔑 Enter the username of the user you want to update: ");
        String username = input.nextLine().trim(); // Trim input to remove leading/trailing spaces

        // Validate the username input
        if (username.isEmpty()) {
            System.out.println("⚠️ Username cannot be empty. Please try again.");
            return; // Return early if validation fails
        }

        // Prompt for the new email address
        System.out.print("📧 Enter new email: ");
        String newEmail = input.nextLine().trim();

        // Validate the email input
        if (newEmail.isEmpty()) {
            System.out.println("⚠️ Email cannot be empty. Please try again.");
            return; // Return early if validation fails
        }

        // SQL query for updating the user's email
        String query = "UPDATE users SET email = ? WHERE username = ?";

        try {
            // Connect to the database
            dbHandler.connect();

            // Prepare the statement and set parameters for the SQL query
            try (PreparedStatement stmt = dbHandler.connection.prepareStatement(query)) {
                stmt.setString(1, newEmail);
                stmt.setString(2, username);

                // Execute the update and check if any rows were affected
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    // Successful update message
                    System.out.println("✅ User updated successfully.");
                } else {
                    // Failure message if no rows were affected
                    System.out.println("❌ User not found. Please check the username.");
                }
            }
        } catch (SQLException e) {
            // Handle SQL exceptions with a user-friendly message
            System.err.println("⚠️ Error updating user: " + e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            System.err.println("⚠️ Unexpected error: " + e.getMessage());
        } finally {
            // Ensure the database connection is closed in all cases
            try {
                dbHandler.closeConnection();
                System.out.println("🔌 Database connection closed.");
            } catch (SQLException e) {
                // Handle any errors that occur while closing the connection
                System.err.println("⚠️ Error closing connection: " + e.getMessage());
            }
        }
    }

    private void deleteUser(DatabaseHandler dbHandler, Scanner input) {
        // Display a welcoming message with an emoji for better UX
        System.out.println("\n🗑️ Deleting a User...");

        // Prompt the admin to enter the username of the user they wish to delete
        System.out.print("🔑 Enter the username of the user you want to delete: ");
        String username = input.nextLine().trim(); // Trim input to remove leading/trailing spaces

        // Validate the username input
        if (username.isEmpty()) {
            System.out.println("⚠️ Username cannot be empty. Please try again.");
            return; // Return early if validation fails
        }

        // SQL query for deleting a user by username
        String query = "DELETE FROM users WHERE username = ?";

        try {
            // Connect to the database
            dbHandler.connect();

            // Prepare the statement and set the username parameter for the SQL query
            try (PreparedStatement stmt = dbHandler.connection.prepareStatement(query)) {
                stmt.setString(1, username);

                // Execute the delete and check if any rows were affected
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    // Successful deletion message
                    System.out.println("✅ User deleted successfully.");
                } else {
                    // Failure message if no rows were affected
                    System.out.println("❌ User not found. Please check the username.");
                }
            }
        } catch (SQLException e) {
            // Handle SQL exceptions with a user-friendly message
            System.err.println("⚠️ Error deleting user: " + e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            System.err.println("⚠️ Unexpected error: " + e.getMessage());
        } finally {
            // Ensure the database connection is closed in all cases
            try {
                dbHandler.closeConnection();
                System.out.println("🔌 Database connection closed.");
            } catch (SQLException e) {
                // Handle any errors that occur while closing the connection
                System.err.println("⚠️ Error closing connection: " + e.getMessage());
            }
        }
    }

    private void viewReports() {
        // Display a welcoming message with an emoji for better UX
        System.out.println("\n📊 ========== VIEW REPORTS ==========");

        // Try to connect to the database
        try {
            DatabaseHandler.connect(); // Ensure the database is connected

            // Define the SQL query to fetch the report data
            String query = """
            SELECT r.Action, COUNT(r.Action) as Count, a.Title
            FROM reading_history r
            JOIN Articles a ON r.ArticleID = a.ArticleID
            GROUP BY r.Action, a.Title
            ORDER BY a.Title, r.Action
            """;

            // Prepare and execute the SQL query
            try (PreparedStatement stmt = DatabaseHandler.connection.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();

                // Print header for the report with proper alignment
                System.out.printf("%-30s %-20s %-10s%n", "Article Title", "Action", "Count");
                System.out.println("---------------------------------------------------------");

                // Iterate through the result set and display each record
                while (rs.next()) {
                    String title = rs.getString("Title");
                    String action = rs.getString("Action");
                    int count = rs.getInt("Count");

                    // Print the data in a formatted manner for better readability
                    System.out.printf("%-30s %-20s %-10d%n", title, action, count);
                }
            }
        } catch (SQLException e) {
            // Catch SQL-related errors and print user-friendly error message
            System.err.println("⚠️ Error fetching reports: " + e.getMessage());
        } catch (Exception e) {
            // Catch any unexpected exceptions
            System.err.println("⚠️ Unexpected error: " + e.getMessage());
        } finally {
            // Ensure the connection is closed after the operation
            try {
                DatabaseHandler.connection.close();
                System.out.println("🔌 Database connection closed.");
            } catch (SQLException e) {
                // Handle any errors that occur while closing the connection
                System.err.println("⚠️ Error closing connection: " + e.getMessage());
            }
        }

        // Print a footer message for the report view
        System.out.println("==================================");
    }

    private void viewUsers() {
        // Display a welcoming message with an emoji for better UX
        System.out.println("\n👥 ========== VIEW USERS ==========");

        // Try to connect to the database
        try {
            DatabaseHandler.connect(); // Ensure the database is connected

            // Define the SQL query to fetch user data
            String query = """
            SELECT UserID, Username, Email, Name, CreatedAt
            FROM Users
            ORDER BY CreatedAt DESC
            """;

            // Prepare and execute the SQL query
            try (PreparedStatement stmt = DatabaseHandler.connection.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();

                // Print the header of the report with proper alignment
                System.out.printf("%-10s %-20s %-30s %-20s %-20s%n", "UserID", "Username", "Email", "Name", "Created At");
                System.out.println("--------------------------------------------------------------------------------------------");

                // Iterate through the result set and display each user's data
                while (rs.next()) {
                    int userId = rs.getInt("UserID");
                    String username = rs.getString("Username");
                    String email = rs.getString("Email");
                    String name = rs.getString("Name");
                    Timestamp createdAt = rs.getTimestamp("CreatedAt");

                    // Print user data in a formatted manner for better readability
                    System.out.printf("%-10d %-20s %-30s %-20s %-20s%n", userId, username, email, name, createdAt);
                }
            }
        } catch (SQLException e) {
            // Catch SQL-related errors and print a user-friendly error message
            System.err.println("⚠️ Error fetching users: " + e.getMessage());
        } catch (Exception e) {
            // Catch any unexpected exceptions
            System.err.println("⚠️ Unexpected error: " + e.getMessage());
        } finally {
            // Ensure the connection is closed after the operation
            try {
                DatabaseHandler.connection.close();
                System.out.println("🔌 Database connection closed.");
            } catch (SQLException e) {
                // Handle any errors that occur while closing the connection
                System.err.println("⚠️ Error closing connection: " + e.getMessage());
            }
        }

        // Print a footer message for the user view
        System.out.println("=================================\n");
    }

}
