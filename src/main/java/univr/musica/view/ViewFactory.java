package univr.musica.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import univr.musica.Main;
import univr.musica.config.AppConfig;
import univr.musica.model.Model;
import univr.musica.model.PlaybackManager;
import univr.musica.model.Song;
import univr.musica.model.User;

import java.io.IOException;
import java.util.Objects;

/**
 * Classe viewfactory.
 * Questa gestisce tutto ciò che riguarda la gestione delle viste, caricamtno stage e scene
 */

public class ViewFactory {
    private final Model model;
    private BorderPane mainView;
    private Stage stage;

    public ViewFactory(Model model) {
        this.model = model;
    }

    /**
     * Funzione che carica una scene.
     * Riceve il path del file fxml e lo carica.
     * Inoltre prepara il controller della nuova view passando il model al costruttore.
     * @param fxmlPath
     * @return
     */
    private Scene loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(controllerClass -> {
                try {
                    return controllerClass.getConstructor(Model.class)
                            .newInstance(Model.getInstance());
                } catch (Exception e) {
                    try { return controllerClass.getDeclaredConstructor().newInstance(); }
                    catch (Exception ex) { throw new RuntimeException(ex); }
                }
            });
            return new Scene(loader.load());
        } catch (IOException e) {
            System.err.println("Errore caricamento FXML: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Setta la mainView della viewfactory
     * @param mainView
     */
    public void setMainView(BorderPane mainView) {

        this.mainView = mainView;

    }

    /**
     * Aggiorna la main view con un nuovo fxml ricevuto
     * @param fxmlPath
     */
    public void updateMainView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(controllerClass -> {
                try {
                    return controllerClass.getConstructor(Model.class)
                            .newInstance(Model.getInstance());
                } catch (Exception e) {
                    try { return controllerClass.getDeclaredConstructor().newInstance(); }
                    catch (Exception ex) { throw new RuntimeException(ex); }
                }
            });

            Node node = loader.load();

            if (this.mainView != null) {
                this.mainView.setCenter(node);
                System.out.println(" Vista centrale aggiornata con: " + fxmlPath);
            } else {
                System.err.println("ERRORE: mainView è null! Non posso aggiornare il centro.");
            }
        } catch (IOException e) {
            System.err.println("Errore durante l'update della MainView: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * metodo eseguito per ritornare alla login view
     */
    public void showLoginWindow() {
        Scene scene = loadScene("/univr/musica/fxml/LoginView.fxml");
        if (stage == null) {
            stage = new Stage();
            stage.setResizable(false);
        }
        stage.setScene(scene);
        stage.setTitle(AppConfig.APP_TITLE);
        stage.show();
    }
    /**
     * metodo eseguito per ritornare alla register view
     */

    public void showRegisterWindow() {
        Scene scene = loadScene("/univr/musica/fxml/RegisterView.fxml");
        stage.setScene(scene);
        stage.setTitle("Registrazione");
    }

    /**
     * metodo eseguito per caricare la main view dell'USER
     */
    public void showMainWindow() {
        loadLastUserSession();
        Scene scene = loadScene("/univr/musica/fxml/User/UserView.fxml");
        stage.setScene(scene);
        stage.setTitle(AppConfig.APP_TITLE);
        stage.setResizable(true);
        stage.centerOnScreen();
    }

    /**
     * Metodo per caricare la main view dell'ADMIN
     */
    public void showAdminWindow() {
        loadLastUserSession();
        Scene scene = loadScene("/univr/musica/fxml/Admin/AdminView.fxml");
        stage.setScene(scene);
        stage.setTitle(AppConfig.APP_TITLE);
        stage.setResizable(true);
        stage.centerOnScreen();
    }



    public void loadLastUserSession() {
        int lastSongId = model.getAuthenticatedUser().getLastSongId();
        if (lastSongId > 0) {
            Song lastSong = Model.getInstance().getSongRepository().getSong(lastSongId);
            if (lastSong != null) {
                PlaybackManager.getInstance().setCurrentSong(lastSong);
                System.out.println("Sessione ripristinata: " + lastSong.getTitle());
            }
        }
    }

    /**
     * Metodo che gestisce la richiesta di logout
     * Ferma il playbackmanager, chiude lo stage e torna al login
     */
    public void logout() {
        PlaybackManager.getInstance().stop();

        if (this.stage != null) {
            this.stage.close();
        }


        this.mainView = null;


        showLoginWindow();
    }

}