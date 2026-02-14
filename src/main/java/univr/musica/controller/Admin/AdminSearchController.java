package univr.musica.controller.Admin;

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
import univr.musica.model.CommentsRepository;
import univr.musica.model.Model;
import univr.musica.model.Song;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminSearchController implements Initializable {
    private final Model model;
    public TextField search_field;
    public ListView<Song> search_results;
    public Button search_button;
    public StackPane SearchUI;
    CommentsRepository songRepository;

    private ObservableList<Song> songList = FXCollections.observableArrayList();

    public AdminSearchController(Model model) {
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        search_results.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/univr/musica/fxml/Admin/AdminSongPage.fxml"));

                    // 1. Devi impostare la Factory per passare il Model al costruttore
                    loader.setControllerFactory(clazz -> new AdminSongPageController(model));

                    Parent overlayNode = loader.load();

                    // 2. Assicurati di usare il tipo di controller corretto (AdminSongPageController)
                    AdminSongPageController detailController = loader.getController();
                    detailController.setSongData(newVal);

                    // 3. Pulisci SearchUI prima di aggiungere per evitare sovrapposizioni
                    SearchUI.getChildren().clear();
                    SearchUI.getChildren().add(overlayNode);

                } catch (IOException e) {
                    System.err.println("Errore nel caricamento della pagina canzone Admin");
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
        songList.addAll(model.getSongRepository().searchSongRep(searchTerm));
        search_results.setItems(songList);
    }
}
