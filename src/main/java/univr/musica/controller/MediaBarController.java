package univr.musica.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import univr.musica.model.Model;
import univr.musica.model.PlaybackManager;
import univr.musica.model.Song;

import java.net.URL;
import java.util.ResourceBundle;

public class MediaBarController implements Initializable {
    private final Model model;

    @FXML public ImageView play_music;
    @FXML public Label currentArtName;
    @FXML public Label currentSongTitle;
    @FXML public ImageView currentSongCover;

    public MediaBarController(Model model) {
        this.model = model;
    }

    public MediaBarController() {
        this.model = Model.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentSongTitle.setVisible(false);
        currentArtName.setVisible(false);
        currentSongCover.setVisible(false);
        PlaybackManager playbackManager = model.getPlaybackManager();

        // Ascolta i cambiamenti della canzone corrente
        playbackManager.currentSongProperty().addListener((obs, oldSong, newSong) -> {
            updateSongUI(newSong);
        });

        // getsione icona Play/Pause
        playbackManager.playerStatusProperty().addListener((obs, oldStatus, newStatus) -> {
            if (newStatus == MediaPlayer.Status.PLAYING) {
                setPlayIcon("/univr/musica/img/ic_pause.png");
            } else {
                setPlayIcon("/univr/musica/img/ic_play.png");
            }
        });

        // Caricamento iniziale dell'interfaccia se c'è già una canzone presente
        Song initialSong = playbackManager.getCurrentSong();
        if (initialSong != null) {
            updateSongUI(initialSong);
        }
    }

    /**
     * Aggiorna titolo, autore e copertina nella MediaBar
     */
    private void updateSongUI(Song song) {
        if (song != null) {
            currentSongTitle.setVisible(true);
            currentArtName.setVisible(true);
            currentSongCover.setVisible(true);
            if (currentSongTitle != null) currentSongTitle.setText(song.getTitle());
            if (currentArtName != null) currentArtName.setText(song.getAuthor());

            if (currentSongCover != null && song.getCover() != null) {
                currentSongCover.setImage(song.getCover());
            }
        } else {
            if (currentSongTitle != null) currentSongTitle.setText("-");
            if (currentArtName != null) currentArtName.setText("-");
        }
    }

    /**
     * cambio icona play
     */
    private void setPlayIcon(String resourcePath) {
        if (play_music != null) {
            try {
                URL iconUrl = getClass().getResource(resourcePath);
                if (iconUrl != null) {
                    play_music.setImage(new Image(iconUrl.toExternalForm()));
                }
            } catch (Exception e) {
                System.err.println("Errore caricamento icona player: " + e.getMessage());
            }
        }
    }

    /**
     * Evento al click dell'icona Play/Pause
     */
    @FXML
    public void play_music() {
        PlaybackManager playbackManager = model.getPlaybackManager();
        MediaPlayer player = playbackManager.getMediaPlayer();
        Song currentSong = playbackManager.getCurrentSong();

        if (player == null && currentSong != null) {
            playbackManager.play(currentSong);
            return;
        }

        // Se il player esiste già, alterna tra Play e Pausa
        if (player != null) {
            playbackManager.togglePlayPause();
        } else {
            System.out.println("Nessuna canzone caricata. Selezionane una prima di premere Play!");
        }
    }
}