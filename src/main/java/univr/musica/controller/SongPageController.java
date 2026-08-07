package univr.musica.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import univr.musica.config.AppConfig;
import univr.musica.model.Comments;
import univr.musica.model.Model;
import univr.musica.model.PlaybackManager;
import univr.musica.model.Song;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SongPageController implements Initializable {
    private final Model model;

    @FXML private Label song_title;
    @FXML private HBox close_popup;
    @FXML private Button play_cur_song;
    @FXML private Label artist_name;
    @FXML private Button play_cur_song1;
    @FXML private Button play_cur_song2;
    @FXML private ScrollPane comments_view;
    @FXML private TextArea comment_text;
    @FXML private Button post_comment;
    @FXML private VBox vbox1;
    @FXML private Button pdf_btn;
    @FXML private Button video_btn;
    @FXML private VBox comments_list;
    @FXML private ImageView song_Cover;
    @FXML private Button remove_btn;
    @FXML private Button rem_song;
    @FXML private Label char_counter;

    private Song song;
    private static final int MAX_COMMENT_LENGTH = 200;

    public SongPageController(Model model) {
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        char_counter.setText("0/" + MAX_COMMENT_LENGTH);
        comment_text.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > MAX_COMMENT_LENGTH) {
                comment_text.setText(oldVal);
                return;
            }
            char_counter.setText(newVal.length() + "/" + MAX_COMMENT_LENGTH);

            if (newVal.length() == MAX_COMMENT_LENGTH) {
                char_counter.setStyle("-fx-text-fill: red;");
            } else if (newVal.length() >= MAX_COMMENT_LENGTH * 0.9) {
                char_counter.setStyle("-fx-text-fill: orange;");
            } else {
                char_counter.setStyle("-fx-text-fill: white;");
            }
        });
    }

    public void setSongData(Song song) {
        if (song != null) {
            this.song = song;
            song_title.setText(song.getTitle());
            artist_name.setText(song.getAuthor());
            song_Cover.setImage(song.getCover());

            if (rem_song != null) {
                String currentUsername = model.getAuthenticatedUser() != null
                        ? model.getAuthenticatedUser().getUsername()
                        : "";

                boolean isAdmin = model.getAuthenticatedUser() != null
                        && model.getAuthenticatedUser().isAdmin();

                boolean isUploader = currentUsername.equalsIgnoreCase(song.getUploader());

                boolean canDelete = isAdmin || isUploader;

                rem_song.setVisible(canDelete);
                rem_song.setManaged(canDelete);
            }

            loadComments();

            String fullPath = AppConfig.DATA_DIR + "/pdf/" + song.getId() + ".pdf";
            File file = new File(fullPath);

            if (!file.exists()) {
                System.err.println("File pdf non trovato sul disco: " + file.getAbsolutePath());
                pdf_btn.setDisable(true);
            }
        }
    }

    private void loadComments() {
        System.out.println("entro nel metodo commenti");
        if (song != null) {
            comments_list.getChildren().clear();
            System.out.println("entro nel metodo IF");
            List<Comments> currentComments = model.getCommentsRepository().searchCommentsRep(song.getId());
            System.out.println(currentComments.size() + " " +  song.getTitle());
            for (Comments comment : currentComments) {
                System.out.println("entro nel metodo FOR");
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/univr/musica/fxml/Comment.fxml"));

                    loader.setControllerFactory(clazz -> new CommentController(this.model, this));

                    Node commentNode = loader.load();

                    CommentController commentController = loader.getController();
                    commentController.setCommentData(comment, this.song);

                    comments_list.getChildren().add(commentNode);
                    System.out.println("commento caricato con successo");
                } catch (Exception e) {
                    System.err.println("Errore nel caricamento del commento:");
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteComment(Comments comment) {
        if (comment == null) return;

        boolean deleted = model.getCommentsRepository().removeComment(comment);

        if (deleted) {
            System.out.println("Commento " + comment.getId() + " eliminato con successo.");
            loadComments();
        } else {
            System.err.println("Errore durante l'eliminazione del commento.");
        }
    }

    @FXML
    private void close_popup(MouseEvent mouseEvent) {
        Node pageToClose = (Node) mouseEvent.getSource();
        while (pageToClose.getParent() != null && !(pageToClose.getParent() instanceof StackPane)) {
            pageToClose = pageToClose.getParent();
        }
        if (pageToClose.getParent() instanceof StackPane container) {
            container.getChildren().remove(pageToClose);
        }
    }

    @FXML
    private void play_cur_song(ActionEvent actionEvent) {
        PlaybackManager.getInstance().play(song);
    }

    @FXML
    private void post_comment(ActionEvent actionEvent) {
        if (comment_text.textProperty().get().length() < 1 ) {
            return;
        }
        System.out.println("Commento postato : "+ comment_text.getText());

        Model.getInstance().getCommentsRepository().saveComment(
                new Comments(comment_text.getText(),
                        model.getAuthenticatedUser().getUsername(),
                        song.getId()));

        loadComments();
        comment_text.clear();
    }

    @FXML
    private void removeSong(ActionEvent actionEvent) {
        System.out.println("RIMUOVO");
        model.getSongRepository().deleteSong(song.getId());

        Node pageToClose = (Node) actionEvent.getSource();
        while (pageToClose.getParent() != null && !(pageToClose.getParent() instanceof StackPane)) {
            pageToClose = pageToClose.getParent();
        }
        if (pageToClose.getParent() instanceof StackPane container) {
            container.getChildren().remove(pageToClose);
        }
    }

    @FXML
    private void open_pdf(ActionEvent actionEvent) throws IOException {
        System.out.println("Apro file pdf");
        String fullPath = AppConfig.DATA_DIR + "/pdf/" + song.getId() + ".pdf";
        File file = new File(fullPath);

        Desktop desktop = Desktop.getDesktop();

        desktop.open(file);
    }
}