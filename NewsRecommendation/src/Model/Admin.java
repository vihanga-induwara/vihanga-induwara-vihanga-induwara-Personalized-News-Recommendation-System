package Model;

import DB.DatabaseHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
        this.createdAt = createdAt;
        this.name = name;
    }

    // Getter for adminID
    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter for createdAt
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
            System.out.println("2. Manage Articles");
            System.out.println("3. View Reports");
            System.out.println("4. Logout");
            System.out.print("Please select an option (1-4): ");

            int choice = input.nextInt();
            input.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    viewUsers();
                    break;
                case 2:
                    manageArticles();
                    break;
                case 3:
                    viewReports();
                    break;
                case 4:
                    System.out.println("Logging out...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    // Method to view users (could fetch from database or other data source)
    private void viewUsers() {
        System.out.println("\nViewing users...");

        String query = "SELECT * FROM users"; // Query to retrieve all users from the users table
        DatabaseHandler dbHandler = new DatabaseHandler();

        try {
            dbHandler.connect(); // Establish the connection to the database
            Statement stmt = dbHandler.connection.createStatement();
            ResultSet rs = stmt.executeQuery(query); // Execute the query

            while (rs.next()) {
                // Retrieving user details from the result set
                String userId = rs.getString("userId");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String name = rs.getString("name");

                // Displaying the user details
                System.out.println("User ID: " + userId);
                System.out.println("Username: " + username);
                System.out.println("Password: " + password); // For security, it's better not to print password
                System.out.println("Email: " + email);
                System.out.println("Name: " + name);
                System.out.println("----------");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
        } finally {
            try {
                dbHandler.closeConnection(); // Ensure the database connection is closed
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }


    // Method to manage articles (add/edit/delete articles)
    private void manageArticles() {
        System.out.println("\nManaging articles...");
        // Add logic to manage articles (add/edit/delete)
    }

    // Method to view reports (e.g., user activity, popular articles)
    private void viewReports() {
        System.out.println("\nViewing reports...");
        // Add logic to generate and display reports
    }
}
