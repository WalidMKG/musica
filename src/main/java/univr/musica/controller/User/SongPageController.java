package univr.musica.controller.User;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import univr.musica.model.Comments;
import univr.musica.model.Model;
import univr.musica.model.PlaybackManager;
import univr.musica.model.Song;

import java.net.URL;
import java.util.ResourceBundle;

public class SongPageController implements Initializable {
    private final Model model;
    public Label song_title;
    public HBox close_popup;
    public Button play_cur_song;
    public Label artist_name;
    public Button play_cur_song1;
    public Button play_cur_song2;
    public ScrollPane comments_view;
    public TextField comment_text;
    public Button post_comment;
    public VBox vbox1;
    public Button pdf_btn;
    public Button video_btn;
    private Song song;

    public SongPageController(Model model) {
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //VUOTO PER ORA
    }

    public void setSongData(Song song) {
        if (song != null) {
            this.song = song;
            song_title.setText(song.getTitle());
            artist_name.setText(song.getAuthor());
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

    public void post_comment(ActionEvent actionEvent) {
        System.out.println("Commento postato : "+ comment_text.getText());
        Model.getInstance().getCommentsRepository().saveComment(
                new Comments(comment_text.getText(),
                model.getAuthenticatedUser().getUsername(),
                song.getId()));
    }
}
