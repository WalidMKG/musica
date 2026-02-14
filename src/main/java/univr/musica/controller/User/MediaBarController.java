package univr.musica.controller.User;

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
    public ImageView play_music;
    public Label currentArtName;
    public Label currentSongTitle;
    public ImageView currentSongCover;
    MediaBarController mediaBarController;
    Image image = new Image(getClass().getResource("/univr/musica/img/ic_home.png").toExternalForm());

    public MediaBarController(Model model) {
        this.model = model;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PlaybackManager.getInstance().setCurrentSong(Model.getInstance().getSongRepository().getSong(model.getAuthenticatedUser().getLastSongId()));
        System.out.println("Ultima canzone "+ model.getAuthenticatedUser().getLastSongId());
        PlaybackManager.getInstance().currentSongProperty().addListener((obs, oldSong, newSong) -> {
            if (newSong != null) {

            }
        });

        PlaybackManager.getInstance().playerStatusProperty().addListener((obs, oldStatus, newStatus) -> {
            if (newStatus == MediaPlayer.Status.PLAYING) {
                System.out.println("cambiato");
                play_music.setImage(new Image(getClass().getResource("/univr/musica/img/ic_pause.png").toExternalForm()));
                Song curr = PlaybackManager.getInstance().currentSongProperty().get();
                currentArtName.setText(curr.getAuthor());
                currentSongTitle.setText(curr.getTitle());
                System.out.printf("PATH COVER "+ curr.getCover());
                currentSongCover.setImage(curr.getCover());


            } else {
                play_music.setImage(new Image(getClass().getResource("/univr/musica/img/ic_play.png").toExternalForm()));
            }
        });

    }


    @FXML
    public void play_music() {
        MediaPlayer player = PlaybackManager.getInstance().getMediaPlayer();
        System.out.println("Ultima canzone "+ model.getAuthenticatedUser().getLastSongId());
        if (player == null) {
            System.out.println("Nessuna canzone caricata. Selezionane una prima di premere Play!");
            return;
        }

        if (player.getStatus() == MediaPlayer.Status.PLAYING) {
            player.pause();
        } else {
            player.play();
        }
    }
}
