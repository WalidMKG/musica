package univr.musica.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import univr.musica.model.PlaybackManager;
import univr.musica.model.Song;

import java.net.URL;
import java.util.ResourceBundle;

public class SongPageController implements Initializable {

    public Label song_title;
    public HBox close_popup;
    public Button play_cur_song;
    private Song song;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //VUOTO PER ORA
    }

    public void setSongData(Song song) {
        if (song != null) {
            this.song = song;
            song_title.setText(song.getTitle());
        }
    }



    public void close_popup(MouseEvent mouseEvent) {
        Node pageToClose = (Node) mouseEvent.getSource();
        while (pageToClose.getParent() != null && !(pageToClose.getParent() instanceof StackPane)) {
            pageToClose = pageToClose.getParent();
        }
        if (pageToClose.getParent() instanceof StackPane container) {
            container.getChildren().remove(pageToClose);
        }
    }

    public void play_cur_song(ActionEvent actionEvent) {
        PlaybackManager.getInstance().play(song);
    }
}
