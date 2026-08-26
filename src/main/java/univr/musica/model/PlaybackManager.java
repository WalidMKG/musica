package univr.musica.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import univr.musica.config.AppConfig;

import java.io.File;

/**
 * Singleton che gestisce la riproduzione audio
 */
public class PlaybackManager {
    private static PlaybackManager instance;
    private MediaPlayer mediaPlayer;

    private final ObjectProperty<Song> currentSong = new SimpleObjectProperty<>();
    private final ObjectProperty<MediaPlayer.Status> playerStatus = new SimpleObjectProperty<>();

    private PlaybackManager() {}

    public static PlaybackManager getInstance() {
        if (instance == null) {
            instance = new PlaybackManager();
        }
        return instance;
    }

    public ObjectProperty<Song> currentSongProperty() {
        return currentSong;
    }

    public Song getCurrentSong() {
        return currentSong.get();
    }

    public ObjectProperty<MediaPlayer.Status> playerStatusProperty() {
        return playerStatus;
    }

    /**
     * Imposta e avvia la riproduzione di una canzone
     */
    public void play(Song song) {
        if (song == null) return;

        // Aggiorna la canzone corrente
        this.currentSong.set(song);

        try {
            String fullPath = AppConfig.DATA_DIR + "/mp3/" + song.getId() + ".mp3";
            File file = new File(fullPath);

            if (!file.exists()) {
                System.err.println("File audio non trovato: " + file.getAbsolutePath());
                return;
            }

            // Libera il vecchio MediaPlayer se esisteva
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }

            // Inizializza il nuovo player
            String mediaUri = file.toURI().toString();
            mediaPlayer = new MediaPlayer(new Media(mediaUri));

            mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                playerStatus.set(newStatus);
            });

            mediaPlayer.play();
            System.out.println("Riproduzione avviata: " + song.getTitle() + " (ID: " + song.getId() + ")");

        } catch (Exception e) {
            System.err.println("Errore durante il play: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Play e Pause
     */
    public void togglePlayPause() {
        if (mediaPlayer == null) return;

        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * Ferma la riproduzione e libera la memoria
     */
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
            playerStatus.set(MediaPlayer.Status.STOPPED);
            System.out.println("Riproduzione interrotta e risorse liberate.");
        }
    }
}