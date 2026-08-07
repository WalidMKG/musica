package univr.musica.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Text;
import univr.musica.model.Comments;
import univr.musica.model.Model;
import univr.musica.model.Song;

import java.net.URL;
import java.util.ResourceBundle;

public class CommentController implements Initializable {
    private final Model model;
    private final SongPageController songPageController;

    private Comments comment;

    @FXML private Label comment_username;
    @FXML private Text comment_text;
    @FXML private MenuButton menu_options;
    @FXML private MenuItem reply_item;
    @FXML private MenuItem delete_item;

    public CommentController(Model model, SongPageController songPageController) {
        this.model = model;
        this.songPageController = songPageController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (menu_options != null) {
            menu_options.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: white; -fx-cursor: hand;");
        }
    }

    public void setCommentData(Comments comment, Song song) {
        if (comment == null || song == null) return;

        this.comment = comment;

        comment_username.setText(comment.getUsername());
        comment_text.setText(comment.getText());

        String currentUser = model.getAuthenticatedUser().getUsername();

        boolean isAuthorOrPerformer = song.getAuthor() != null && comment.getUsername().equalsIgnoreCase(song.getUploader());
        if (isAuthorOrPerformer) {
            comment_username.getStyleClass().add("label-author");
        }

        boolean isAdmin = model.getAuthenticatedUser().isAdmin();
        boolean isMyComment = comment.getUsername().equalsIgnoreCase(currentUser);
        boolean isSongUploader = song.getUploader() != null && song.getUploader().equalsIgnoreCase(currentUser);

        boolean canManageComment = isAdmin || isMyComment || isSongUploader;

        if (menu_options != null) {
            menu_options.setVisible(canManageComment);
        }
    }

    @FXML
    private void onReply(ActionEvent actionEvent) {
        if (comment != null) {
            System.out.println("Risposta al commento di: " + comment.getUsername());
        }
    }

    @FXML
    private void onDelete(ActionEvent actionEvent) {
        if (songPageController != null && comment != null) {
            songPageController.deleteComment(this.comment);
        } else {
            System.err.println("Errore: songPageController o comment risultano null!");
        }
    }
}