package univr.musica.controller.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import univr.musica.controller.User.SongCardController;
import univr.musica.model.Model;
import univr.musica.model.Song;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class AdminHomeController implements Initializable {

    private final Model model;
    public HBox song_container;
    public Label songName;
    public Label artist;
    public ImageView img;
    public ScrollPane scroll_view;
    public StackPane homeStack;
    public GridPane songGrid;
    @FXML
    //private ListView<Song> songTest;

    private ObservableList<Song> songList = FXCollections.observableArrayList();

    public AdminHomeController(Model model) {
        this.model = model;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadMediaFromDatabase();

        //songTest.setItems(songList);

        /*songTest.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                System.out.println("Selezionato: " + newVal.getTitle());
                System.out.println(newVal.getTitle());
            }
        });*/

        int column = 0;
        int row = 1;

        for (Song song : songList) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/univr/musica/fxml/User/song.fxml"));
                fxmlLoader.setControllerFactory(clazz -> new SongCardController(model));
                Node card = fxmlLoader.load();

                SongCardController cardController = fxmlLoader.getController();
                cardController.setData(song);
                cardController.setParentContainer(homeStack);

                // Gestione della griglia: 5 colonne per riga
                if (column == 5) {
                    column = 0;
                    row++;
                }

                songGrid.add(card, column++, row);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }







    /*private void scrollLeft() {
        double currentH = scrollPane.getHvalue();
        scrollPane.setHvalue(Math.max(currentH - scrollAmount, 0.0));
    }*/


    /*private void scrollRight() {
        double currentH = scrollPane.getHvalue();
        scrollPane.setHvalue(Math.min(currentH + scrollAmount, 1.0));
    }*/

    private void loadMediaFromDatabase() {
        songList.addAll(model.getSongRepository().getLatestSongs(10));
    }


}
