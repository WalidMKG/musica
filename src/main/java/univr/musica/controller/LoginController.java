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

public class LoginController implements Initializable {
    private final Model model;
    public Button login_btn;
    public TextField username_txt;
    public ChoiceBox<String> login_choice;
    public PasswordField pwd_fld;
    public Button register_btn;
    public Label login_lbl;
    @FXML
    private Label welcomeText;
    private UserRepository userRepository;

    public LoginController(Model model) {
        this.model = model;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userRepository = Model.getInstance().getUserRepository();
        login_choice.getItems().addAll("Admin","User");
        login_choice.setValue("User");
    }


    public void login(ActionEvent actionEvent) {
        String username = username_txt.getText();
        String password = pwd_fld.getText();

        //Se al login i campi sono vuoti da errore
        if(username.isEmpty() || password.isEmpty()) {
            login_lbl.setVisible(true);
            login_lbl.setText("Inserire username e password validi");
            login_lbl.setTextFill(Color.RED);
            return;
        }
        User user = userRepository.getUser(username);

        if(login_choice.getValue().equals("User")) {
            checkLogin(password, user);
        }
        else{
            if(!user.isAdmin()) {
                login_lbl.setVisible(true);
                login_lbl.setText("Login Error");
                login_lbl.setTextFill(Color.DARKRED);
            }else
                checkLogin(password, user);
        }
    }

    private void checkLogin(String password, User user) {
        if (user != null && user.checkPassword(password)) {

            if (!user.isAdmin() && !user.getStatus()) {
                login_lbl.setVisible(true);
                login_lbl.setText("Account in attesa di approvazione admin.");
                login_lbl.setTextFill(Color.ORANGE);
                return;
            }

            model.setAuthenticatedUser(user);

            int lastId = user.getLastSongId();
            if (lastId > 0) {
                Song s = model.getSongRepository().getSong(lastId);
                if (s != null) {
                    model.getPlaybackManager().setCurrentSong(s);
                    System.out.println("DEBUG: Sessione ripristinata per " + s.getTitle());
                }
            }
            if (user.isAdmin()) {
                model.getViewFactory().showAdminWindow();
            } else {
                model.getViewFactory().showMainWindow();
            }
        } else {
            login_lbl.setVisible(true);
            login_lbl.setText("Login Error");
            login_lbl.setTextFill(Color.DARKRED);
        }
        System.out.println("ciaone");
    }

    //Caso in cui l'utente clicca il tasto per registrarsi
    public void register(ActionEvent actionEvent) {
        Stage currentStage = (Stage) register_btn.getScene().getWindow();
        model.getViewFactory().showRegisterWindow();
    }
}
