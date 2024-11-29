package Model;

public class GeneralUser {
    private String userId;     // Optional field for unique user identification
    private String username;
    private String password;
    private String name;       // Optional field for full name
    private String email;

    // Constructor including all fields
    public GeneralUser(String userId, String username, String password, String name, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    // Constructor for simpler use cases (e.g., during registration)
    public GeneralUser(String username, String password, String email) {
        this(null, username, password, null, email);
    }

    // Constructor with optional name parameter (if name is provided)
    public GeneralUser(String username, String password, String email, String name) {
        this(username, password, email); // Call the main constructor to initialize username, password, and email
        this.name = name;
    }

    // Getters and Setters for all fields
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
