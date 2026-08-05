package univr.musica.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository che gestisce la tabella delle canzoni
 */
public class SongRepository {
    private final DatabaseManager dbManager;
    private final Map<Integer, Song> songCache = new HashMap<>();

    public SongRepository(Model model, DatabaseManager dbManager) {
        this.dbManager = dbManager;
        refreshSongCache();
    }

    /**
     * Aggiornamento della cache delle canzoni
     */
    private void refreshSongCache() {
        songCache.clear();

        dbManager.executeQuery(
                "SELECT id, title, author, genre, year, uploader FROM songs",
                rs -> {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String title = rs.getString("title");
                        String author = rs.getString("author");
                        String genre = rs.getString("genre");
                        String year = rs.getString("year");
                        String uploader = rs.getString("uploader");

                        Song song = createSongInstance(id, title, author, genre, year, uploader);
                        songCache.put(id, song);
                    }
                    return null;
                }
        );
    }

    /**
     * Helper per istanziare l'oggetto Song impostando anche l'uploader
     */
    private Song createSongInstance(int id, String title, String author, String genre, String year, String uploader) {
        Song song = new Song(id, title, author, genre, year);
        song.setUploader(uploader); // Presuppone che la classe Song abbia setUploader()
        return song;
    }

    /**
     * Partendo dall'id ritorna la canzone mappata nella cache
     */
    public Song getSong(int id) {
        return songCache.get(id);
    }

    /**
     * Salva la canzone nella base di dati e aggiorna la cache includendo l'uploader
     */
    public boolean saveSong(Song song, String uploaderUsername) {
        int rowsAffected = dbManager.executeUpdate(
                "INSERT INTO songs (title, author, genre, year, uploader) VALUES (?, ?, ?, ?, ?)",
                song.getTitle(),
                song.getAuthor(),
                song.getGenre(),
                song.getYear(),
                uploaderUsername
        );

        if (rowsAffected > 0) {
            int newId = getLastInsertedId();
            Song savedSong = createSongInstance(
                    newId,
                    song.getTitle(),
                    song.getAuthor(),
                    song.getGenre(),
                    song.getYear(),
                    uploaderUsername
            );
            songCache.put(newId, savedSong);
            return true;
        }
        return false;
    }

    /**
     * Ritorna l'id dell'ultima canzone inserita
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
        String sql = "SELECT id, title, author, genre, year, uploader FROM songs";

        dbManager.executeQuery(sql, rs -> {
            while (rs.next()) {
                songs.add(createSongInstance(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getString("year"),
                        rs.getString("uploader")
                ));
            }
            return null;
        });

        return songs;
    }

    /**
     * Ritorna le ultime n canzoni inserite, dove n = limit.
     */
    public List<Song> getLatestSongs(int limit) {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT id, title, author, genre, year, uploader FROM songs ORDER BY id DESC LIMIT ?";

        dbManager.executeQuery(sql, rs -> {
            while (rs.next()) {
                songs.add(createSongInstance(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getString("year"),
                        rs.getString("uploader")
                ));
            }
            return null;
        }, limit);

        return songs;
    }

    /**
     * Ricerca la base di dati delle canzoni a partire da searchTerm (titolo o autore).
     */
    public List<Song> searchSongRep(String searchTerm) {
        List<Song> songs = new ArrayList<>();

        String sql = "SELECT id, title, author, genre, year, uploader FROM songs " +
                "WHERE title LIKE ? OR author LIKE ?";

        dbManager.executeQuery(sql, rs -> {
            while (rs.next()) {
                songs.add(createSongInstance(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getString("year"),
                        rs.getString("uploader")
                ));
            }
            return null;
        }, "%" + searchTerm + "%", "%" + searchTerm + "%");

        return songs;
    }

    /**
     * Cancella la canzone dal db e dalla cache
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