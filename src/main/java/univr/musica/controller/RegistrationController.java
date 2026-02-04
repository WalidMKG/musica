package univr.musica.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import univr.musica.Main;
import univr.musica.model.User;
import univr.musica.model.UserRepository;
import univr.musica.view.ViewFactory;

public class RegistrationController {
    public TextField reg_user;
    public TextField reg_pwd;
    public TextField reg_pwd_confirm;
    public Button register_btn;
    public Label register_label;
    public Button back_to_login;
    private UserRepository userRepository;


    public void initialize() {
        userRepository = Main.getUserRepository();
        register_label.setVisible(false);
    }

    public void register(ActionEvent actionEvent) {
        String username = reg_user.getText();
        String password = reg_pwd.getText();
        String confirmPassword = reg_pwd_confirm.getText();
        if(!username.isEmpty() && reg_pwd.getText().equals( reg_pwd_confirm.getText())) {
            User newUser = new User(reg_user.getText(), reg_pwd.getText(), false, false);
            userRepository.saveUser(newUser);

            register_label.setText("Bella");
            register_label.setTextFill(Color.GREEN);
            register_label.setVisible(true);
        }
        else{
            register_label.setText("Bella");
            register_label.setTextFill(Color.RED);
            register_label.setVisible(true);
        }
    }

    public void back_to_login(ActionEvent actionEvent) {
        Stage currentScene = (Stage) back_to_login.getScene().getWindow();
        ViewFactory.getInstance().showLoginWindow(currentScene);
    }
}
