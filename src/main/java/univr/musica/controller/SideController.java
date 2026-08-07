package univr.musica.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import univr.musica.model.Model;

import java.net.URL;
import java.util.ResourceBundle;

public class SideController implements Initializable {
    private final Model model;

    @FXML public HBox home_icon;
    @FXML public HBox go_to_search;
    @FXML public HBox go_to_users;
    @FXML public HBox upload_btn;
    @FXML public HBox logout_btn;

    @FXML public Circle Users_notification_shape;
    @FXML public Label USers_not_label;

    public SideController(Model model) {
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Mostra il pulsante gestione utenti solo se l'utente corrente è Admin
        if (go_to_users != null) {
            boolean isAdmin = model.getAuthenticatedUser() != null && model.getAuthenticatedUser().isAdmin();
            go_to_users.setVisible(isAdmin);
            go_to_users.setManaged(isAdmin);
        }
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
        model.getViewFactory().updateMainView("/univr/musica/fxml/UsersView.fxml");
    }

    @FXML
    public void open_load_page(MouseEvent mouseEvent) {
        model.getViewFactory().updateMainView("/univr/musica/fxml/LoadPage.fxml");
    }

    @FXML
    public void logout_request(MouseEvent mouseEvent) {
        model.getViewFactory().logout();
    }
}