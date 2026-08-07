package univr.musica.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import univr.musica.model.Model;
import univr.musica.model.Song;
import univr.musica.model.User;
import univr.musica.model.UserRepository;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller della login view
 */
public class LoginController implements Initializable {
    private final Model model;

    @FXML public Button login_btn;
    @FXML public TextField username_txt;
    @FXML public ChoiceBox<String> login_choice;
    @FXML public PasswordField pwd_fld;
    @FXML public Button register_btn;
    @FXML public Label login_lbl;
    @FXML private Label welcomeText;

    private UserRepository userRepository;

    public LoginController(Model model) {
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userRepository = Model.getInstance().getUserRepository();
    }

    public void login(ActionEvent actionEvent) {
        String username = username_txt.getText().trim();
        String password = pwd_fld.getText();

        if (username.isEmpty() || password.isEmpty()) {
            login_lbl.setVisible(true);
            login_lbl.setText("Inserire username e password validi");
            login_lbl.setTextFill(Color.RED);
            return;
        }

        User user = userRepository.getUser(username);
        checkLogin(password, user);
    }

    /**
     * Metodo che controlla la validità del login.
     * Gestisce: controllo password, controllo attivazione account e reindirizzamento
     */
    private void checkLogin(String password, User user) {
        if (user != null && user.checkPassword(password)) {

            // Se non è admin ed è disattivato/in attesa
            if (!user.isAdmin() && !user.getStatus()) {
                login_lbl.setVisible(true);
                login_lbl.setText("Account in attesa di approvazione admin.");
                login_lbl.setTextFill(Color.ORANGE);
                return;
            }

            // Imposta l'utente autenticato nel Model
            model.setAuthenticatedUser(user);

            // Ripristina l'ultima sessione di riproduzione se presente
            int lastId = user.getLastSongId();
            if (lastId > 0) {
                Song s = model.getSongRepository().getSong(lastId);
                if (s != null) {
                    model.getPlaybackManager().currentSongProperty().set(s);
                    System.out.println("DEBUG: Sessione ripristinata per " + s.getTitle());
                }
            }


            model.getViewFactory().showMainWindow();

        } else {
            login_lbl.setVisible(true);
            login_lbl.setText("Login Error: Credenziali errate");
            login_lbl.setTextFill(Color.DARKRED);
        }
    }

    public void register(ActionEvent actionEvent) {
        model.getViewFactory().showRegisterWindow();
    }
}