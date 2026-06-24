package univr.musica.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import univr.musica.model.Model;
import univr.musica.model.User;

/**
 * Classe che gestisce la registrazione dell'utente
 */

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

    /**
     * Controlla la validità del sign-in, l'user deve essere non preso e la password di conferma deve coincidere, genera errori
     * mostrati aall'utente in caso
     * @param actionEvent
     */
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

    /**
     * Controlla che i due campi password coincidano
     * @param password1
     * @param password2
     * @return
     */
    private boolean passwordsMatch(String password1, String password2) {
        return password1.equals(password2) && !password1.isEmpty();
    }

    /**
     * Button back che riporta alla login view
     * @param actionEvent
     */
    public void back_to_login(ActionEvent actionEvent) {
        model.getViewFactory().showLoginWindow();
    }
}
