package univr.musica.model;

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

    public void saveSong(Song song) {
        boolean success = dbManager.executeUpdate(
                "INSERT OR REPLACE INTO songs (title, author, genre, year) VALUES (?, ?, ?, ?)",
                song.getTitle(), song.getAuthor(), song.getGenre(), song.getYear()
        );

        if (success) {
            songCache.put(song.getId(), song);
        }
    }


    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT id, title, author, genre, year FROM songs";

        dbManager.executeQuery(sql, rs -> {
            while (rs.next()) {
                //ID aggiunto dal costruttore
                Song song = new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getString("year")
                );
                // Se hai aggiunto il campo path, ricordati di caricarlo:
                // song.setPath(rs.getString("path"));
                songs.add(song);
            }
            return null;
        });

        return songs;
    }
}
