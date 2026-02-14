package univr.musica.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final Model model;
    private final DatabaseManager dbManager;
    private Map<String, User> userCache = new HashMap<>();

    public UserRepository(Model model, DatabaseManager dbManager) {
        this.model = model;
        this.dbManager = dbManager;
        refreshUserCache();
    }

    private void refreshUserCache() {
        userCache.clear();
        dbManager.executeQuery(
                "SELECT username, password, is_admin, status, last_song_id FROM users",
                rs -> {
                    while (rs.next()) {
                        String username = rs.getString("username");
                        String password = rs.getString("password");
                        boolean isAdmin = rs.getInt("is_admin") == 1;
                        boolean isApproved = rs.getInt("status") == 1;
                        int lastSongId = rs.getInt("last_song_id");

                        User user = new User(username, password, isAdmin, isApproved, lastSongId);
                        userCache.put(username, user);
                    }
                    return null;
                }
        );
    }

    public void saveUser(User user) {
        int success = dbManager.executeUpdate(
                "INSERT OR REPLACE INTO users (username, password, is_admin, status, last_song_id) VALUES (?, ?, ?, ?, ?)",
                user.getUsername(),
                user.getPassword(),
                user.isAdmin() ? 1 : 0,
                user.getStatus() ? 1 : 0, // Parametro aggiunto
                user.getLastSongId()
        );

        if (success != 0) {
            userCache.put(user.getUsername(), user);
        }
    }
    

    public void deleteUser(String username) {
        int success = dbManager.executeUpdate("DELETE FROM users WHERE username = ?", username);
        if (success > 0) {
            userCache.remove(username);
            System.out.println("DEBUG: Utente " + username + " rimosso con successo dalla cache.");
        } else {
            System.out.println("DEBUG: Errore! Utente " + username + " non trovato nel database.");
        }
    }


    public User getUser(String username) {
        return userCache.get(username);
    }
    

    public boolean usernameExists(String username) {
        return userCache.containsKey(username);
    }


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

    public ObservableList<User> getAllUsers() {
        ObservableList<User> activeList = FXCollections.observableArrayList();
        for (User u : userCache.values()) {
            if (u.getStatus() && !u.isAdmin()) { // Filtra solo approvati non admin
                activeList.add(u);
            }
        }
        return activeList;
    }

    public ObservableList<User> getPendingUsers() {
        // DEVE essere una nuova lista ad ogni chiamata
        ObservableList<User> list = FXCollections.observableArrayList();
        for (User u : userCache.values()) {
            if (!u.getStatus() && !u.isAdmin()) {
                list.add(u);
            }
        }
        return list;
    }

    public void approveUser(String username) {
        String sql = "UPDATE users SET status = 1 WHERE username = ?";
        if (dbManager.executeUpdate(sql, username) > 0) {
            User u = userCache.get(username);
            if (u != null) {
                u.setStatus(true);
                System.out.println("DEBUG: Stato aggiornato in cache per " + username);
            }
        }
        // refreshUserCache();
    }




}
