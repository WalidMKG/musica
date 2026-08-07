package univr.musica.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import univr.musica.model.Model;
import univr.musica.model.Song;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    private final Model model;

    @FXML public HBox song_container;
    @FXML public Label songName;
    @FXML public Label artist;
    @FXML public ImageView img;
    @FXML public ScrollPane scroll_view;
    @FXML public StackPane homeStack;
    @FXML public GridPane songGrid;

    @FXML public HBox upload_btn;
    @FXML public HBox logout_btn;
    @FXML public HBox go_to_users;
    @FXML public HBox go_to_search;
    @FXML public HBox home_icon;

    private ObservableList<Song> songList = FXCollections.observableArrayList();

    public HomeController(Model model) {
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Carico la home...");
        loadMediaFromDatabase();

        if (songGrid == null) {
            System.err.println("ERRORE: songGrid è null! Verifica che l'fx:id='songGrid' sia presente nel file FXML associato a HomeController.");
            return;
        }

        int column = 0;
        int row = 1;

        for (Song song : songList) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/univr/musica/fxml/song.fxml"));

                fxmlLoader.setControllerFactory(clazz -> new SongCardController(model));

                Node card = fxmlLoader.load();

                SongCardController cardController = fxmlLoader.getController();
                cardController.setData(song);

                if (homeStack != null) {
                    cardController.setParentContainer(homeStack);
                }

                if (column == 5) {
                    column = 0;
                    row++;
                }

                songGrid.add(card, column++, row);

            } catch (IOException e) {
                System.err.println("Errore nel caricamento della card per la canzone: " + song.getTitle());
                e.printStackTrace();
            }
        }
    }

    private void loadMediaFromDatabase() {
        songList.clear();
        songList.addAll(model.getSongRepository().getLatestSongs(10));
    }

    @FXML
    public void go_home(MouseEvent mouseEvent) {
        model.getViewFactory().updateMainView("/univr/musica/fxml/HomeView.fxml");
    }

    @FXML
    public void go_to_search(MouseEvent mouseEvent) {
        model.getViewFactory().updateMainView("/univr/musica/fxml/SearchPage.fxml");
    }

    @FXML
    public void go_to_users(MouseEvent mouseEvent) {
        model.getViewFactory().updateMainView("/univr/musica/fxml/UsersxView.fxml");
    }

    @FXML
    public void logout_request(MouseEvent mouseEvent) {
        model.getViewFactory().logout();
    }

    @FXML
    public void open_load_page(MouseEvent mouseEvent) {
        model.getViewFactory().updateMainView("/univr/musica/fxml/LoadPage.fxml");
    }
}