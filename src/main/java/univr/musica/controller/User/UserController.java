package univr.musica.controller.User;

import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import univr.musica.model.Model;
import univr.musica.model.Song;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    private final Model model;
    public HBox home_icon;
    public HBox go_to_profile;
    public BorderPane mainView;
    public HBox upload_btn;
    public HBox go_to_search;
    public HBox logout_btn;

    public UserController(Model model) {
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialize chiamato! mainView è: " + (mainView != null ? "PRESENTE" : "NULL"));
        if(mainView != null){
            model.getViewFactory().setMainView(mainView);
        }
    }


    public void go_home(MouseEvent mouseEvent) throws IOException {
        model.getViewFactory().updateMainView("/univr/musica/fxml/User/UserDashboard.fxml");
    }

    public void go_to_profile(MouseEvent mouseEvent) throws IOException {
        System.out.println("click ricevuto");
        model.getViewFactory().updateMainView("/univr/musica/fxml/User/UserProfile.fxml");
    }

    public void open_load_page(MouseEvent mouseEvent) throws IOException {
        model.getViewFactory().updateMainView("/univr/musica/fxml/User/LoadPage.fxml");
    }

    public void go_to_search(MouseEvent mouseEvent) throws IOException {
        System.out.println("Going to search");
        model.getViewFactory().updateMainView("/univr/musica/fxml/User/SearchPage.fxml");
    }

    public void logout_request(MouseEvent mouseEvent) throws IOException {
        System.out.println("logout request");
    }
}



