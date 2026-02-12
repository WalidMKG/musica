package univr.musica;

import javafx.application.Application;
import javafx.stage.Stage;
import univr.musica.model.*;

import java.io.IOException;

import static javafx.application.Application.launch;

public class Main extends Application {
    private static DatabaseManager dbManager;
    private static UserRepository userRepository;
    private static CommentsRepository commentsRepository;
    private static PlaybackManager playbackManager;
    private static SongRepository songRepository;



    public static DatabaseManager getDatabaseManager() {
        return dbManager;
    }
    public static UserRepository getUserRepository() {
        return userRepository;
    }

    public static CommentsRepository getCommentsRepository() {
        return commentsRepository;
    }

    public static PlaybackManager getPlaybackManager() {
        return playbackManager;
    }

    public static SongRepository getSongRepository() {
        return songRepository;
    }

    @Override
    public void start(Stage stage) throws IOException {
        dbManager = new DatabaseManager();
        userRepository = new UserRepository();
        commentsRepository = new CommentsRepository();
        playbackManager = PlaybackManager.getInstance();
        songRepository = new SongRepository();


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