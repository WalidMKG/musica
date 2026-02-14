package univr.musica.controller.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import univr.musica.model.Model;
import univr.musica.model.Song;

import java.io.IOException;
import java.util.Objects;

public class SongCardController {

    private final Model model;

    @FXML
    private ImageView img;

    @FXML
    private Label songName;

    @FXML
    private Label artist;
    private StackPane parentContainer;
    private Song song;

    public SongCardController(Model model) {
        this.model = model;
    }

    public void setData(Song song) {
        this.song = song;
        songName.setText(song.getTitle());
        artist.setText(song.getAuthor());

        String path = "file:" + univr.musica.config.AppConfig.DATA_DIR + "/jpg/" + song.getId() + ".jpg";
        String defaultPath = "file:" + univr.musica.config.AppConfig.DATA_DIR + "/jpg/default.jpg";

        try {

            Image image = new Image(path, true);

            image.errorProperty().addListener((obs, old, hasError) -> {
                if (hasError) {
                    img.setImage(new Image(defaultPath));
                }
            });

            img.setImage(image);
        } catch (Exception e) {
            img.setImage(new Image(defaultPath));
            System.err.println("Errore nel caricamento della cover per ID: " + song.getId());
        }
    }

    public void setParentContainer(StackPane homeStack) {
        this.parentContainer = homeStack;
    }

    @FXML
    private void onCardClicked() {
        if (song != null && parentContainer != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/univr/musica/fxml/User/SongPage.fxml"));
                loader.setControllerFactory(clazz -> new SongPageController(model));

                Node songPage = loader.load();
                SongPageController controller = loader.getController();
                controller.setSongData(song);


                parentContainer.getChildren().add(songPage);

            } catch ( IOException e) {
                e.printStackTrace();
            }
        }
    }
}