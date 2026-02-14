package univr.musica.model;

import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongRepository {
    private final DatabaseManager dbManager;
    private Map<Integer, Song> songCache = new HashMap<>();

    public SongRepository(Model model, DatabaseManager dbManager) {
        this.dbManager = dbManager;
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
                song.getTitle(),
                song.getAuthor(),
                song.getGenre(),
                song.getYear()
        );

        if (rowsAffected > 0) {
            int newId = getLastInsertedId();
            Song savedSong = new Song(newId, song.getTitle(), song.getAuthor(), song.getGenre(), song.getYear());
            songCache.put(newId, savedSong);
            return true;
        }
        return false;
    }

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

    public List<Song> getLatestSongs(int limit) {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT id, title, author, genre, year FROM songs ORDER BY id DESC LIMIT ?";

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
        }, limit);

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


    public void deleteSong(int songID) {
        Song songToDelete = getSong(songID);

        if (songToDelete == null) {
            System.out.println("DEBUG: Errore! Canzone con ID " + songID + " non trovata nella cache.");
            return;
        }

        String title = songToDelete.getTitle();

        int success = dbManager.executeUpdate("DELETE FROM songs WHERE id = ?", songID);

        if (success > 0) {
            songCache.remove(songID);
            System.out.println("DEBUG: Song '" + title + "' rimossa con successo dal DB e dalla cache.");
        } else {
            System.out.println("DEBUG: Il database non ha eliminato alcuna riga per l'ID: " + songID);
        }
    }

}
