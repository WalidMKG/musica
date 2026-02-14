package univr.musica.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import univr.musica.model.Model;
import univr.musica.model.User;

public class RegistrationController {
    private final Model model;
    public TextField reg_user;
    public PasswordField reg_pwd;
    public PasswordField reg_pwd_confirm;
    public Button register_btn;
    public Label register_label;
    public Button back_to_login;

    public RegistrationController(Model model) {
        this.model = model;
    }


    public void initialize() {
        register_label.setVisible(false);
    }

    public void register(ActionEvent actionEvent) {
        String username = reg_user.getText();
        String password = reg_pwd.getText();
        String confirmPassword = reg_pwd_confirm.getText();
        if(!username.isEmpty() && passwordsMatch(password,reg_pwd_confirm.getText())  && !model.getUserRepository().usernameExists(username)) {
            User newUser = new User(reg_user.getText(), reg_pwd.getText(), false, false,0);
            model.getUserRepository().saveUser(newUser);
            register_label.setText("Utente registrato correttamente!");
            register_label.setTextFill(Color.GREEN);
            register_label.setVisible(true);
        }
        else if(!passwordsMatch(password,reg_pwd_confirm.getText())){
            register_label.setText("Errore, Password non valida!");
            register_label.setTextFill(Color.RED);
            register_label.setVisible(true);
        }
        else {
            register_label.setText("Errore, Username non valido!");
            register_label.setTextFill(Color.RED);
            register_label.setVisible(true);
        }
        reg_user.clear();
        reg_pwd.clear();
        reg_pwd_confirm.clear();
    }

    private boolean passwordsMatch(String password1, String password2) {
        return password1.equals(password2) && !password1.isEmpty();
    }

    public void back_to_login(ActionEvent actionEvent) {
        model.getViewFactory().showLoginWindow();
    }
}
