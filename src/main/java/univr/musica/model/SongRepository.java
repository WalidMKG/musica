package univr.musica.model;

import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository che gestisce la tabella delle canzoni
 */
public class SongRepository {
    private final DatabaseManager dbManager;
    private Map<Integer, Song> songCache = new HashMap<>();

    public SongRepository(Model model, DatabaseManager dbManager) {
        this.dbManager = dbManager;
        refreshSongCache();
    }

    /**
     * Aggiornamento delal cache delle canzoni
     */
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

    /**
     * Partendo dall'id ritorna la canzone mappata nella cache
     * @param id
     * @return
     */
    public Song getSong(int id) {
        return songCache.get(id);
    }

    /**
     * Salva la canzone nella base di dati e aggiorna la cache
     * @param song
     * @return
     */
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

    /**
     * Ritorna l'ultima canzone inserita
     * @return
     */
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

    /**
     * Ritorna le ultime n canzoni inserite, dove n = limit.
     * @param limit
     * @return
     */
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

    /**
     * Ricerca la base di dati delle canzoni a partire dalla stringa SearchTerm, che può essere titolo e autore.
     * @param searchTerm
     * @return
     */
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

    /**
     * Cancella la canzone dal db
     * @param songID
     */
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
