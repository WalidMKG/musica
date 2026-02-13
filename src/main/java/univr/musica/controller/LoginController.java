package univr.musica.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import univr.musica.Main;
import univr.musica.model.Model;
import univr.musica.model.Song;
import univr.musica.model.User;
import univr.musica.model.UserRepository;
import univr.musica.view.ViewFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public Button login_btn;
    public TextField username_txt;
    public ChoiceBox<String> login_choice;
    public PasswordField pwd_fld;
    public Button register_btn;
    public Label login_lbl;
    @FXML
    private Label welcomeText;
    private UserRepository userRepository;


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
            Model.getInstance().getViewFactory().setUser(user);

            int lastId = user.getLastSongId();
            if (lastId > 0) {
                Song s = Model.getInstance().getSongRepository().getSong(lastId);
                if (s != null) {
                    Model.getInstance().getPlaybackManager().setCurrentSong(s);
                    System.out.println("DEBUG: Sessione ripristinata per " + s.getTitle());
                }
            }

            // 3. Ora apriamo la finestra
            Model.getInstance().getViewFactory().showMainWindow((Stage)login_lbl.getScene().getWindow());
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
        ViewFactory.getInstance().showRegisterWindow();
    }
}
