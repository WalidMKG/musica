package univr.musica;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import univr.musica.model.DatabaseManager;
import univr.musica.model.Model;
import univr.musica.model.SongRepository;
import univr.musica.model.UserRepository;
import univr.musica.view.ViewFactory;

import java.io.IOException;

import static javafx.application.Application.launch;

public class Main extends Application {
    private static DatabaseManager dbManager;
    private static UserRepository userRepository;
    private static SongRepository songRepository;


    public static DatabaseManager getDatabaseManager() {
        return dbManager;
    }
    public static UserRepository getUserRepository() {
        return userRepository;
    }

    public static SongRepository getSongRepository() {
        return songRepository;
    }


    @Override
    public void start(Stage stage) throws IOException {
        dbManager = new DatabaseManager();
        userRepository = new UserRepository();
        songRepository = new SongRepository();


        //mostra la finestra
        Model.getInstance().getViewFactory().showLoginWindow(stage);

    }

    public static void main(String[] args) {
        launch();
    }
}