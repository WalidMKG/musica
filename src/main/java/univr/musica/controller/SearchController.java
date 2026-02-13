package univr.musica.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import univr.musica.Main;
import univr.musica.model.Model;
import univr.musica.model.Song;
import univr.musica.model.CommentsRepository;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SearchController implements Initializable {
    public TextField search_field;
    public ListView<Song> search_results;
    public Button search_button;
    public StackPane SearchUI;
    CommentsRepository songRepository;

    private ObservableList<Song> songList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        search_results.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                System.out.println("Selezionato: " + newVal.getTitle());
                System.out.println(newVal.getTitle());

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/univr/musica/fxml/User/SongPage.fxml"));
                    Parent overlayNode = loader.load();
                    SongPageController detailController = loader.getController();
                    detailController.setSongData(newVal);
                    SearchUI.getChildren().add(overlayNode);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }







    public void searchSongRep(ActionEvent actionEvent) {
        String searchTerm = search_field.getText();
        if (searchTerm.isEmpty()) {
            return;
        }
        songList.clear();
        songList.addAll(Model.getInstance().getSongRepository().searchSongRep(searchTerm));
        search_results.setItems(songList);
    }
}
