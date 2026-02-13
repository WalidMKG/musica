package univr.musica.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import univr.musica.Main;
import univr.musica.model.Model;
import univr.musica.model.Song;
import univr.musica.view.ViewFactory;


import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;


public class HomeController implements Initializable {

    public HBox song_container;
    public Label songName;
    public Label artist;
    public ImageView img;
    public ScrollPane scroll_view;
    @FXML
    private ListView<Song> songTest;

    private ObservableList<Song> songList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadMediaFromDatabase();

        songTest.setItems(songList);

        songTest.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                System.out.println("Selezionato: " + newVal.getTitle());
                System.out.println(newVal.getTitle());
            }
        });

        for (Song song : songList) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/univr/musica/fxml/User/song.fxml"));
                Node card = fxmlLoader.load();
                SongCardController cardController = fxmlLoader.getController();
                cardController.setData(song);
                song_container.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    @FXML
    private ScrollPane scrollPane;

    private final double scrollAmount = 0.2;

    @FXML
    private void scrollLeft() {
        double currentH = scrollPane.getHvalue();
        scrollPane.setHvalue(Math.max(currentH - scrollAmount, 0.0));
    }

    @FXML
    private void scrollRight() {
        double currentH = scrollPane.getHvalue();
        scrollPane.setHvalue(Math.min(currentH + scrollAmount, 1.0));
    }

    private void loadMediaFromDatabase() {
        songList.addAll(Model.getInstance().getSongRepository().getAllSongs());
    }


}
