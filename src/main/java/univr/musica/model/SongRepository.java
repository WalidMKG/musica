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
 * Repository che gestisce la tabella delle canzoni nel Database e la persistenza dei File su Disco.
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
        song.setUploader(uploader);
        return song;
    }

    /**
     * Partendo dall'id ritorna la canzone mappata nella cache
     */
    public Song getSong(int id) {
        return songCache.get(id);
    }

    /**
     * Salva la canzone nel DB e copia fisicamente i file associati (MP3, PDF, Copertina) nelle relative cartelle.
     */
    public boolean saveSongComplete(Song song, String uploaderUsername, File mp3File, File pdfFile, File coverFile) {
        // 0. Controllo Bloccante: Il file MP3 è OBBLIGATORIO
        if (mp3File == null || !mp3File.exists()) {
            System.err.println("ERRORE: Impossibile salvare la canzone senza un file MP3!");
            return false;
        }

        // 1. Inserimento nel Database
        boolean dbSuccess = saveSong(song, uploaderUsername);

        if (!dbSuccess) {
            System.err.println("Errore nell'inserimento della canzone a Database.");
            return false;
        }

        // 2. Recupera l'ID univoco generato dal DB
        int realId = getLastInsertedId();
        String idStr = String.valueOf(realId);

        // 3. Gestione salvataggio fisico dei file
        try {
            // Salva l'MP3 (sicuro che non sia null grazie al check iniziale)
            saveFileLocally(mp3File, "mp3", idStr);

            if (pdfFile != null) {
                saveFileLocally(pdfFile, "pdf", idStr);
            }

            // Se l'utente non seleziona una copertina, viene usata quella di default
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
     * Salva la riga nella tabella DB e aggiorna la cache locale.
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
     * Helper privato per copiare un file nella relativa sottocartella (mp3, pdf, jpg)
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
     * Cancella la canzone dal DB, dalla cache e rimuove i file fisici (.mp3, .jpg, .pdf) da disco.
     */
    public void deleteSong(int songID) {
        Song songToDelete = getSong(songID);

        if (songToDelete == null) {
            System.out.println("DEBUG: Errore! Canzone con ID " + songID + " non trovata nella cache.");
            return;
        }

        String title = songToDelete.getTitle();

        // Elimina dal Database
        int success = dbManager.executeUpdate("DELETE FROM songs WHERE id = ?", songID);

        if (success > 0) {
            // Rimuovi dalla cache
            songCache.remove(songID);

            // Rimuovi i file fisici su disco
            deleteSongFiles(songID);

            System.out.println("DEBUG: Song '" + title + "' (ID: " + songID + ") rimossa con successo dal DB, dalla cache e da Disco.");
        } else {
            System.out.println("DEBUG: Il database non ha eliminato alcuna riga per l'ID: " + songID);
        }
    }

    /**
     * Helper privato per eliminare i file fisici su disco legati a un'ID canzone
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
}