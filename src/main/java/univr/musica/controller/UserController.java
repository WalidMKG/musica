package univr.musica.controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import univr.musica.Main;
import univr.musica.model.Model;
import univr.musica.model.Song;
import univr.musica.model.SongRepository;
import univr.musica.view.ViewFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    public HBox home_icon;
    public HBox go_to_profile;
    public BorderPane mainView;
    public HBox upload_btn;
    public HBox go_to_search;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialize chiamato! mainView è: " + (mainView != null ? "PRESENTE" : "NULL"));
        if(mainView != null){
            ViewFactory.getInstance().setMainView(mainView);
        }
    }


    public void go_home(MouseEvent mouseEvent) {
        ViewFactory.getInstance().updateMainView("/univr/musica/fxml/User/UserDashboard.fxml");

        Song prova = new Song("Cazzo","Culo", "Rock", "1982");
        Main.getSongRepository() .saveSong(prova);
    }

    public void go_to_profile(MouseEvent mouseEvent) {
        System.out.println("click ricevuto");
        ViewFactory.getInstance().updateMainView("/univr/musica/fxml/User/UserProfile.fxml");
    }

    public void open_load_page(MouseEvent mouseEvent) {
        ViewFactory.getInstance().updateMainView("/univr/musica/fxml/User/LoadPage.fxml");
    }

    public void go_to_search(MouseEvent mouseEvent) {
        System.out.println("Going to search");
        ViewFactory.getInstance().updateMainView("/univr/musica/fxml/User/SearchPage.fxml");
    }
}



