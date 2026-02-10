package univr.musica.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class PlaybackManager {
    private static PlaybackManager instance;
    private MediaPlayer mediaPlayer;
    private final ObjectProperty<Song> currentSong = new SimpleObjectProperty<>();

    private PlaybackManager() {}

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
        this.currentSong.set(song); // Questo aggiorna i nomi sulla barra

        if (song != null) {
            try {
                String path = "/univr/musica/mp3/" + song.getId() + ".mp3";
                URL resource = getClass().getResource(path);

                if (resource != null) {
                    if (mediaPlayer != null) mediaPlayer.dispose();

                    mediaPlayer = new MediaPlayer(new Media(resource.toExternalForm()));


                    mediaPlayer.statusProperty().addListener((obs, old, newStatus) -> {
                        playerStatusProperty().set(newStatus);
                    });
                }
            } catch (Exception e) {
                System.err.println("Errore caricamento file audio: " + e.getMessage());
            }
        }
    }


    public void play(Song song) {
        if (song == null) return;

        try {
            String cleanPath = "/univr/musica/mp3/" + song.getId() + ".mp3";
            URL resource = getClass().getResource(cleanPath);

            if (resource == null) {
                System.err.println("File audio non trovato: " + cleanPath);
                return;
            }

            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            Media hit = new Media(resource.toExternalForm());
            mediaPlayer = new MediaPlayer(hit);

            mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                playerStatus.set(newStatus);
            });

            currentSong.set(song);
            mediaPlayer.play();

        } catch (Exception e) {
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
}