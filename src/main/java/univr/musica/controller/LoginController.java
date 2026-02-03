package univr.musica.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import univr.musica.view.ViewFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public Button login_btn;
    public TextField username_txt;
    public ChoiceBox<String> login_choice;
    public PasswordField pwd_fld;
    public Button register_btn;
    @FXML
    private Label welcomeText;




    public void login(ActionEvent actionEvent) {
        System.out.println("ciaone");
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_choice.getItems().addAll("Admin","User");
        login_choice.setValue("User");
    }

    public void register(ActionEvent actionEvent) {
        Stage currentStage = (Stage) register_btn.getScene().getWindow();

        ViewFactory.getInstance().showRegisterWindow(currentStage);
    }
}
