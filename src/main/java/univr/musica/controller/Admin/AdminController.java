package univr.musica.controller.Admin;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import univr.musica.model.Model;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    private final Model model;
    public HBox home_icon;
    public HBox go_to_profile;
    public HBox go_to_search;
    public HBox go_to_search1;
    public HBox upload_btn;
    public HBox go_to_users;
    public Circle Users_notification_shape;
    public Label USers_not_label;
    public HBox logout_request;

    public AdminController(Model model) {
        this.model = model;
    }

    public void go_home(MouseEvent mouseEvent) {
        model.getViewFactory().updateMainView("/univr/musica/fxml/Admin/AdminDash.fxml");
    }

    public void go_to_profile(MouseEvent mouseEvent) {
    }

    public void go_to_search(MouseEvent mouseEvent) {
        model.getViewFactory().updateMainView("/univr/musica/fxml/Admin/AdminSearchPage.fxml");
    }

    public void open_load_page(MouseEvent mouseEvent) {
    }

    public void go_to_users(MouseEvent mouseEvent) {
        model.getViewFactory().updateMainView("/univr/musica/fxml/Admin/AdminUsersView.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Users_notification_shape.setVisible(false);
        int pendingUsers = model.getUserRepository().getPendingUsers().size();
        if(pendingUsers>0){
            Users_notification_shape.setVisible(false);
            USers_not_label.setVisible(true);
            Users_notification_shape.setVisible(true);
            USers_not_label.setText(String.valueOf(pendingUsers));
        }
    }

    public void logout_request(MouseEvent mouseEvent) {
        model.getViewFactory().logout();
    }
}
