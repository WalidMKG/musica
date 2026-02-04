package univr.musica.model;

public class User {
    private String id;
    private String username;
    private String password;
    private boolean isAdmin;
    private boolean isActive;
    
    /**
     * Constructor for creating a user with username and password
     * 
     * @param username The user's username
     * @param password The user's password (stored in plain text for educational purposes)
     */
    public User(String username, String password, boolean isAdmin, boolean isActive) {
        this.id = username; // Using username as ID for simplicity
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.isActive = false;
    }
    
    /**
     * Factory method to create a new user
     * 
     * @param username The user's username
     * @param password The user's password
     * @return A new User object
     */
    public static User create(String username, String password, boolean isAdmin) {
        return new User(username, password, isAdmin, false);
    }

    public static User create(String username, String password) {
        return new User(username, password, false, false);
    }
    
    /**
     * Verify if the provided password matches the stored password
     * 
     * @param password The password to check
     * @return true if the password matches, false otherwise
     */
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
    
    /**
     * Get the user's ID
     * 
     * @return The user's ID (same as username in this implementation)
     */
    public String getId() {
        return id;
    }
    
    /**
     * Get the user's username
     * 
     * @return The user's username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Get the user's password
     * 
     * @return The user's password
     */
    public String getPassword() {
        return password;
    }

    public boolean getStatus(){
        return isActive;
    }


    public boolean isAdmin() {
        return isAdmin;
    }


}