package univr.musica;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import univr.musica.model.*;
import univr.musica.view.ViewFactory;

import java.io.IOException;

import static javafx.application.Application.launch;

public class Main extends Application {
    private static DatabaseManager dbManager;
    private static UserRepository userRepository;
    private static SongRepository songRepository;
    private static PlaybackManager playbackManager;


    public static DatabaseManager getDatabaseManager() {
        return dbManager;
    }
    public static UserRepository getUserRepository() {
        return userRepository;
    }

    public static SongRepository getSongRepository() {
        return songRepository;
    }

    public static PlaybackManager getPlaybackManager() {
        return playbackManager;
    }

    @Override
    public void start(Stage stage) throws IOException {
        dbManager = new DatabaseManager();
        userRepository = new UserRepository();
        songRepository = new SongRepository();
        playbackManager = PlaybackManager.getInstance();


        //mostra la finestra
        Model.getInstance().getViewFactory().showLoginWindow();

    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        String username = Model.getInstance().getViewFactory().getUser().getUsername();
        System.out.println(playbackManager.currentSongProperty());
        userRepository.updateLastSong(username,1);
        System.out.println("chiudioooo");
    }
}