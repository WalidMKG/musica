package univr.musica.controller.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import univr.musica.model.Comments;
import univr.musica.model.Model;
import univr.musica.model.PlaybackManager;
import univr.musica.model.Song;

import java.net.URL;
import java.util.List;
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
    public TextArea comment_text;
    public Button post_comment;
    public VBox vbox1;
    public Button pdf_btn;
    public Button video_btn;
    public VBox comments_list;
    public ImageView song_Cover;
    private Song song;

    private static final int MAX_COMMENT_LENGTH = 200;
    public Label char_counter;

    public SongPageController(Model model) {
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Inizializzo commenti e dettagli

        char_counter.setText("0/" + MAX_COMMENT_LENGTH);
        comment_text.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > MAX_COMMENT_LENGTH) {
                comment_text.setText(oldVal); // blocca oltre il limite
                return;
            }
            char_counter.setText(newVal.length() + "/" + MAX_COMMENT_LENGTH);

            // colore
            if (newVal.length() == MAX_COMMENT_LENGTH) {
                char_counter.setStyle("-fx-text-fill: red;");
            } else if (newVal.length() >= MAX_COMMENT_LENGTH * 0.9) {
                char_counter.setStyle("-fx-text-fill: orange;");
            } else {
                char_counter.setStyle("-fx-text-fill: white;"); // Ritorna bianco se l'utente cancella i caratteri
            }
        });

    }

    public void setSongData(Song song) {
        if (song != null) {
            this.song = song;
            song_title.setText(song.getTitle());
            artist_name.setText(song.getAuthor());
            song_Cover.setImage(song.getCover());
            loadComments();
        }


    }

    /**
     * Metodo che carica i commenti data un crto id canzone
     */
    private void loadComments() {
        System.out.println("entro nel metodo commenti");
        if (song != null) {
            comments_list.getChildren().clear();
            System.out.println("entro nel metodo IF");
            List<Comments> currentComments = model.getCommentsRepository().searchCommentsRep(song.getId());

            /*if (currentComments.isEmpty()) {
                Label placeholder = new Label("Nessun commento ancora.\nSii il primo a commentare!");
                placeholder.setStyle(
                        "-fx-text-fill: #535353;" +
                                "-fx-font-size: 14px;" +
                                "-fx-text-alignment: center;"
                );
                placeholder.setWrapText(true);
                // GIUSTO: aggiungi alla VBox dentro la ScrollPane, non alla ScrollPane
                comments_list.getChildren().add(placeholder);
                return; // esci subito
            }*/


            System.out.println(currentComments.size() + " " +  song.getTitle());
            for (Comments comment : currentComments) {
                System.out.println("entro nel metodo FOR");
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/univr/musica/fxml/User/Comment.fxml"));

                    loader.setControllerFactory(clazz -> new CommentController(this.model));

                    Node commentNode = loader.load();

                    CommentController commentController = loader.getController();
                    commentController.setCommentData(comment);

                    comments_list.getChildren().add(commentNode);
                    System.out.println("commento caricato con successo");
                } catch (Exception e) {
                    System.err.println("Errore nel caricamento del commento:");
                    e.printStackTrace();
                }
            }
        }


    }


    // Se non ci sono commenti mostra un placeholder



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
        //Controllo che il testo non sia vuoto
        if (comment_text.textProperty().get().length() < 1 ) {
            return;
        }
        System.out.println("Commento postato : "+ comment_text.getText());

        //carico nel db
        Model.getInstance().getCommentsRepository().saveComment(
                new Comments(comment_text.getText(),
                model.getAuthenticatedUser().getUsername(),
                song.getId()));

        /**
         * Aggiorna elenco commenti
         */
        loadComments();
    }
}
