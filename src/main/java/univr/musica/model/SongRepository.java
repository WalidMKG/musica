package univr.musica.model;

import univr.musica.config.AppConfig;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository per gestire le canzoni su database e il salvataggio dei file su disco
 */
public class SongRepository {
    private final DatabaseManager dbManager;
    private final Map<Integer, Song> songCache = new HashMap<>();

    public SongRepository(Model model, DatabaseManager dbManager) {
        this.dbManager = dbManager;
        refreshSongCache();
    }

    /**
     * Ricarica la cache locale con tutte le canzoni presenti nel db
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
     * Crea l'oggetto Song impostando anche chi l'ha caricato
     */
    private Song createSongInstance(int id, String title, String author, String genre, String year, String uploader) {
        Song song = new Song(id, title, author, genre, year);
        song.setUploader(uploader);
        return song;
    }

    /**
     * Prende la canzone dalla cache tramite id
     */
    public Song getSong(int id) {
        return songCache.get(id);
    }

    /**
     * Salva la canzone nel db e copia mp3, pdf e copertina nelle cartelle
     */
    public boolean saveSongComplete(Song song, String uploaderUsername, File mp3File, File pdfFile, File coverFile) {
        // mp3 obbligatorio, se non c'e' blocca tutto
        if (mp3File == null || !mp3File.exists()) {
            System.err.println("ERRORE: Impossibile salvare la canzone senza un file MP3!");
            return false;
        }

        // Inserimento a DB
        boolean dbSuccess = saveSong(song, uploaderUsername);

        if (!dbSuccess) {
            System.err.println("Errore nell'inserimento della canzone a Database.");
            return false;
        }

        // Prendo l'id generato da sqlite
        int realId = getLastInsertedId();
        String idStr = String.valueOf(realId);

        // Copio i file fisici rinominandoli con l'id
        try {
            // Salvo l'mp3
            saveFileLocally(mp3File, "mp3", idStr);

            // Salvo il pdf se l'ha messo
            if (pdfFile != null) {
                saveFileLocally(pdfFile, "pdf", idStr);
            }

            // Se non c'e' la copertina metto quella di default
            if (coverFile == null) {
                File defaultFile = new File(AppConfig.DATA_DIR + "/jpg/default.jpg");
                if (defaultFile.exists()) {
                    saveFileLocally(defaultFile, "jpg", idStr);
                }
            } else {
                saveFileLocally(coverFile, "jpg", idStr);
            }

            System.out.println("DEBUG: Canzone salvata con successo nel DB e su Disco con ID: " + idStr);
            return true;

        } catch (Exception e) {
            System.err.println("Errore durante la copia dei file per ID: " + idStr);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Esegue la query di insert e aggiorna la cache
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
     * Copia il file selezionato dentro data/(mp3|pdf|jpg)
     */
    private String saveFileLocally(File selectedFile, String type, String name) {
        try {
            String folder = AppConfig.DATA_DIR + "/" + type + "/";
            File destFolder = new File(folder);
            if (!destFolder.exists()) {
                destFolder.mkdirs();
            }

            File destination = new File(folder + name + "." + type);
            Files.copy(selectedFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("DEBUG: File salvato in: " + destination.getAbsolutePath());
            return destination.getPath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Elimina la canzone da db, cache e cancella i file collegati su disco
     */
    public void deleteSong(int songID) {
        Song songToDelete = getSong(songID);

        if (songToDelete == null) {
            System.out.println("DEBUG: Errore! Canzone con ID " + songID + " non trovata nella cache.");
            return;
        }

        String title = songToDelete.getTitle();

        // Elimino la riga dal DB
        int success = dbManager.executeUpdate("DELETE FROM songs WHERE id = ?", songID);

        if (success > 0) {
            // Rimuovo da cache
            songCache.remove(songID);

            // Cancello i file fisici
            deleteSongFiles(songID);

            System.out.println("DEBUG: Song '" + title + "' (ID: " + songID + ") rimossa con successo dal DB, dalla cache e da Disco.");
        } else {
            System.out.println("DEBUG: Il database non ha eliminato alcuna riga per l'ID: " + songID);
        }
    }

    /**
     * Elimina mp3, jpg e pdf associati all'id della canzone
     */
    private void deleteSongFiles(int songId) {
        String[] extensions = {".mp3", ".jpg", ".pdf"};
        String[] folders = {"/mp3/", "/jpg/", "/pdf/"};

        for (int i = 0; i < extensions.length; i++) {
            File file = new File(AppConfig.DATA_DIR + folders[i] + songId + extensions[i]);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("DEBUG: File rimosso da disco: " + file.getPath());
                } else {
                    System.err.println("Impossibile eliminare il file: " + file.getPath());
                }
            }
        }
    }

    /**
     * Prende l'ultimo id inserito nella tabella
     */
    public int getLastInsertedId() {
        return dbManager.executeQuery("SELECT id FROM songs ORDER BY id DESC LIMIT 1", rs -> {
            if (rs.next()) {
                return rs.getInt("id");
            }
            return 0;
        });
    }

    /**
     * Ritorna la lista di tutte le canzoni nel db
     */
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
     * Ritorna le ultime canzoni caricate in base al limite passato
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
     * Cerca le canzoni per titolo o autore con LIKE
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
}