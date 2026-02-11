package univr.musica.model;

import javafx.event.ActionEvent;
import univr.musica.Main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongRepository {
    private final DatabaseManager dbManager;
    private Map<Integer, Song> songCache = new HashMap<>();

    public SongRepository() {
        this.dbManager = Main.getDatabaseManager();
        refreshSongCache();
    }

    private void refreshSongCache() {
        songCache.clear();

        dbManager.executeQuery(
                "SELECT id, title, author, genre, year FROM songs",
                rs -> {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String title = rs.getString("title");
                        String author = rs.getString("author");
                        String genre = rs.getString("genre");
                        String year = rs.getString("year");


                        Song song = new Song(id,title, author, genre, year);
                        songCache.put(id, song);
                    }
                    return null;
                }
        );
    }

    public Song getSong(int id) {
        return songCache.get(id);
    }

    public boolean saveSong(Song song) {
        int rowsAffected = dbManager.executeUpdate(
                "INSERT INTO songs (title, author, genre, year) VALUES (?, ?, ?, ?)",
                song.getTitle(), song.getAuthor(), song.getGenre(), song.getYear()
        );

        if (rowsAffected > 0) {
            refreshSongCache(); // Aggiorna la cache per includere la nuova canzone
            return true;
        }
        return false;
    }

    // Metodo extra per recuperare l'ID appena creato
    public int getLastInsertedId() {
        return dbManager.executeQuery("SELECT id FROM songs ORDER BY id DESC LIMIT 1", rs -> {
            if (rs.next()) {
                return rs.getInt("id");
            }
            return 0;
        });
    }


    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT id, title, author, genre, year FROM songs";

        dbManager.executeQuery(sql, rs -> {
            while (rs.next()) {
                Song song = new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getString("year")
                );
                songs.add(song);
            }
            return null;
        });

        return songs;
    }


    public List<Song> searchSongRep(String searchTerm) {
        List<Song> songs = new ArrayList<>();

        String sql = "SELECT id, title, author, genre, year FROM songs " +
                "WHERE title LIKE ? OR author LIKE ?";

        dbManager.executeQuery(sql, rs -> {
            while (rs.next()) {
                songs.add(new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getString("year")
                ));
            }
            return null;
        }, "%" + searchTerm + "%", "%" + searchTerm + "%");

        return songs;
    }

}
