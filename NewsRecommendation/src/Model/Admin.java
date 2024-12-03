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
            System.out.println("\nAdmin Menu:");
            System.out.println("1. View Users");
            System.out.println("2. Manage Users");
            System.out.println("3. View Reports");
            System.out.println("4. Logout");
            System.out.print("Please select an option (1-4): ");

            int choice = input.nextInt();
            input.nextLine(); // Consume the newline character

            switch (choice) {
                case 1 -> viewUsers();
                case 2 -> manageUsers(input);
                case 3 -> viewReports();
                case 4 -> {
                    System.out.println("Logging out...");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void manageUsers(Scanner input) {
        DatabaseHandler dbHandler = new DatabaseHandler();

        while (true) {
            System.out.println("\n----- Manage Users -----");
            System.out.println("1. View All Users");
            System.out.println("2. Add New User");
            System.out.println("3. Update Existing User");
            System.out.println("4. Delete User");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = input.nextInt();
            input.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> viewAllUsers(dbHandler);
                case 2 -> addUser(dbHandler, input);
                case 3 -> updateUser(dbHandler, input);
                case 4 -> deleteUser(dbHandler, input);
                case 5 -> {
                    System.out.println("Exiting user management...");
                    return;
                }
                default -> System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private void viewAllUsers(DatabaseHandler dbHandler) {
        System.out.println("\nViewing all users...");
        String query = "SELECT * FROM users";

        try {
            dbHandler.connect();
            Statement stmt = dbHandler.connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String userId = rs.getString("userId");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String name = rs.getString("name");

                System.out.println("User ID: " + userId);
                System.out.println("Username: " + username);
                System.out.println("Email: " + email);
                System.out.println("Name: " + name);
                System.out.println("----------");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
        } finally {
            try {
                dbHandler.closeConnection();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    private void addUser(DatabaseHandler dbHandler, Scanner input) {
        System.out.println("\nAdding a new user...");
        System.out.print("Enter username: ");
        String username = input.nextLine();
        System.out.print("Enter email: ");
        String email = input.nextLine();
        System.out.print("Enter name: ");
        String name = input.nextLine();

        String query = "INSERT INTO users (username, email, name) VALUES (?, ?, ?)";

        try {
            dbHandler.connect();
            try (PreparedStatement stmt = dbHandler.connection.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, name);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("User added successfully.");
                } else {
                    System.out.println("Failed to add user.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        } finally {
            try {
                dbHandler.closeConnection();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    private void updateUser(DatabaseHandler dbHandler, Scanner input) {
        System.out.print("\nEnter the username of the user you want to update: ");
        String username = input.nextLine();
        System.out.print("Enter new email: ");
        String newEmail = input.nextLine();

        String query = "UPDATE users SET email = ? WHERE username = ?";

        try {
            dbHandler.connect();
            try (PreparedStatement stmt = dbHandler.connection.prepareStatement(query)) {
                stmt.setString(1, newEmail);
                stmt.setString(2, username);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("User updated successfully.");
                } else {
                    System.out.println("User not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        } finally {
            try {
                dbHandler.closeConnection();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    private void deleteUser(DatabaseHandler dbHandler, Scanner input) {
        System.out.print("\nEnter the username of the user you want to delete: ");
        String username = input.nextLine();

        String query = "DELETE FROM users WHERE username = ?";

        try {
            dbHandler.connect();
            try (PreparedStatement stmt = dbHandler.connection.prepareStatement(query)) {
                stmt.setString(1, username);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("User deleted successfully.");
                } else {
                    System.out.println("User not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        } finally {
            try {
                dbHandler.closeConnection();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    private void viewReports() {
        System.out.println("\n========== VIEW REPORTS ==========");
        try {
            DatabaseHandler.connect(); // Ensure the database is connected

            String query = """
                SELECT r.Action, COUNT(r.Action) as Count, a.Title
                FROM reading_history r
                JOIN Articles a ON r.ArticleID = a.ArticleID
                GROUP BY r.Action, a.Title
                ORDER BY a.Title, r.Action
                """;

            try (PreparedStatement stmt = DatabaseHandler.connection.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                System.out.printf("%-30s %-20s %-10s%n", "Article Title", "Action", "Count");
                System.out.println("---------------------------------------------------------");
                while (rs.next()) {
                    String title = rs.getString("Title");
                    String action = rs.getString("Action");
                    int count = rs.getInt("Count");
                    System.out.printf("%-30s %-20s %-10d%n", title, action, count);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reports: " + e.getMessage());
        } finally {
            try {
                DatabaseHandler.connection.close(); // Ensure the connection is closed
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        System.out.println("==================================\n");
    }

    private void viewUsers() {
        System.out.println("\n========== VIEW USERS ==========");
        try {
            DatabaseHandler.connect(); // Ensure the database is connected

            String query = """
                SELECT UserID, Username, Email, Name, CreatedAt
                FROM Users
                ORDER BY CreatedAt DESC
                """;

            try (PreparedStatement stmt = DatabaseHandler.connection.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                System.out.printf("%-10s %-20s %-30s %-20s %-20s%n", "UserID", "Username", "Email", "Name", "Created At");
                System.out.println("--------------------------------------------------------------------------------------------");
                while (rs.next()) {
                    int userId = rs.getInt("UserID");
                    String username = rs.getString("Username");
                    String email = rs.getString("Email");
                    String name = rs.getString("Name");
                    Timestamp createdAt = rs.getTimestamp("CreatedAt");
                    System.out.printf("%-10d %-20s %-30s %-20s %-20s%n", userId, username, email, name, createdAt);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        } finally {
            try {
                DatabaseHandler.connection.close(); // Ensure the connection is closed
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        System.out.println("=================================\n");
    }


}
