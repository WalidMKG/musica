package univr.musica.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import univr.musica.config.AppConfig;

import java.io.File;
import java.net.URL;

public class PlaybackManager {
    private static PlaybackManager instance;
    private MediaPlayer mediaPlayer;
    private final ObjectProperty<Song> currentSong = new SimpleObjectProperty<>();
    private final ObjectProperty<Song> nextSong = new SimpleObjectProperty<>();
    private final ObjectProperty<Song> prevSong = new SimpleObjectProperty<>();
    public PlaybackManager() {}

    private final ObjectProperty<MediaPlayer.Status> playerStatus = new SimpleObjectProperty<>();

    public ObjectProperty<MediaPlayer.Status> playerStatusProperty() {
        return playerStatus;
    }

    public static PlaybackManager getInstance() {
        if (instance == null) instance = new PlaybackManager();
        return instance;
    }




    public ObjectProperty<Song> currentSongProperty() {
        return currentSong;
    }


    public void setCurrentSong(Song song) {
        this.currentSong.set(song); // Notifica la barra (titolo, autore, ecc.)

        if (song != null) {
            try {
                String fullPath = AppConfig.DATA_DIR + "/mp3/" + song.getId() + ".mp3";
                File file = new File(fullPath);

                if (file.exists()) {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.dispose();
                    }

                    String mediaUri = file.toURI().toString();
                    mediaPlayer = new MediaPlayer(new Media(mediaUri));

                    mediaPlayer.statusProperty().addListener((obs, old, newStatus) -> {
                        playerStatusProperty().set(newStatus);
                    });

                    System.out.println("Player pronto per la canzone: " + song.getTitle());
                } else {
                    System.err.println("Impossibile caricare: file non trovato in " + fullPath);
                }
            } catch (Exception e) {
                System.err.println("Errore caricamento file audio: " + e.getMessage());
            }
        }
    }


    public void play(Song song) {
        if (song == null) return;

        try {
            String fullPath = AppConfig.DATA_DIR + "/mp3/" + song.getId() + ".mp3";
            File file = new File(fullPath);

            if (!file.exists()) {
                System.err.println("File audio non trovato sul disco: " + file.getAbsolutePath());
                return;
            }

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }

            String mediaUri = file.toURI().toString();
            Media hit = new Media(mediaUri);

            mediaPlayer = new MediaPlayer(hit);

            mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                playerStatus.set(newStatus);
            });

            currentSong.set(song);
            mediaPlayer.play();

        } catch (Exception e) {
            System.err.println("Errore durante il play: " + e.getMessage());
            e.printStackTrace();
        }
    }

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