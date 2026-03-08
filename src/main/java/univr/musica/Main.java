package univr.musica;

import javafx.application.Application;
import javafx.stage.Stage;
import univr.musica.model.*;

import java.io.IOException;

import static javafx.application.Application.launch;

/**
 * Main dell'applicazione
 */

public class Main extends Application {

    /**
     * Start del main, ottiene l'istanza del modello e mostra la scena del login
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        Model.getInstance().getViewFactory().showLoginWindow();
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * Metodo che viene chiamato alla chisura del programma,
     * salva per l'utente loggato l'ultima canzone in riproduzione,
     * questo serve a permettere il resume al next login.
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        String username = Model.getInstance().getAuthenticatedUser().getUsername();
        System.out.println(Model.getInstance().getPlaybackManager().currentSongProperty());
        Model.getInstance().getUserRepository().updateLastSong(username,1);
        System.out.println("chiudioooo");
    }
}