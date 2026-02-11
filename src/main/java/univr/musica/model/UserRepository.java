package univr.musica.model;

import univr.musica.Main;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final DatabaseManager dbManager;
    private Map<String, User> userCache = new HashMap<>();

    public UserRepository() {
        this.dbManager = Main.getDatabaseManager();
        refreshUserCache();
    }

    private void refreshUserCache() {
        userCache.clear();
        // AGGIUNTO last_song_id nella query
        dbManager.executeQuery(
                "SELECT username, password, is_admin, last_song_id FROM users",
                rs -> {
                    while (rs.next()) {
                        String username = rs.getString("username");
                        String password = rs.getString("password");
                        boolean isAdmin = rs.getInt("is_admin") == 1;
                        int lastSongId = rs.getInt("last_song_id");

                        // Verifica che il costruttore corrisponda ai tuoi parametri
                        User user = new User(username, password, isAdmin, true, lastSongId);
                        userCache.put(username, user);
                    }
                    return null;
                }
        );
    }

    public void saveUser(User user) {
        // AGGIUNTO il quarto parametro per non perdere il last_song_id durante il salvataggio
        int success = dbManager.executeUpdate(
                "INSERT OR REPLACE INTO users (username, password, is_admin, last_song_id) VALUES (?, ?, ?, ?)",
                user.getUsername(),
                user.getPassword(),
                user.isAdmin() ? 1 : 0,
                user.getLastSongId()
        );

        if (success!=0) {
            userCache.put(user.getUsername(), user);
        }
    }
    
    /**
     * Delete a user from the repository
     */
    public void deleteUser(String username) {
        User user = userCache.get(username);
        
        // Never delete admin users as a safety measure
        if (user != null && !user.isAdmin()) {
            int success = dbManager.executeUpdate(
                "DELETE FROM users WHERE username = ?",
                username
            );
            
            if (success!=0) {
                userCache.remove(username);
            }
        }
    }

    /**
     * Get a user by username
     */
    public User getUser(String username) {
        return userCache.get(username);
    }
    
    /**
     * Check if a username exists
     */
    public boolean usernameExists(String username) {
        return userCache.containsKey(username);
    }

    /**
     * Aggiorna nel database l'ID dell'ultima canzone ascoltata dall'utente
     */
    public void updateLastSong(String username, int songId) {
        int success = dbManager.executeUpdate(
                "UPDATE users SET last_song_id = ? WHERE username = ?",
                songId,
                username
        );

        if (success!=0) {
            User user = userCache.get(username);
            if (user != null) {
                user.setLastSongId(songId);
            }
            System.out.println("Ultima canzone salvata per " + username + ": " + songId);
        }
    }


    
    /**
     * Get all users
     */
    public Map<String, User> getAllUsers() {
        return new HashMap<>(userCache);
    }
}