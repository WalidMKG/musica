package univr.musica;

import javafx.application.Application;
import javafx.stage.Stage;
import univr.musica.model.*;

import java.io.IOException;

import static javafx.application.Application.launch;

public class Main extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        Model.getInstance().getViewFactory().showLoginWindow();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        String username = Model.getInstance().getViewFactory().getUser().getUsername();
        System.out.println(Model.getInstance().getPlaybackManager().currentSongProperty());
        Model.getInstance().getUserRepository().updateLastSong(username,1);
        System.out.println("chiudioooo");
    }
}