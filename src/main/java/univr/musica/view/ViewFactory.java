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

// Caricamento del font (es. Montserrat Regular)


public class ViewFactory {
    private static ViewFactory instance;
    private BorderPane mainView;
    private User user;

    Stage stage;

    public static ViewFactory getInstance() {
        if (instance == null) {
            instance = new ViewFactory();
        }
        return instance;
    }

    public void showLoginWindow() {
        if (stage != null) {
            stage.close();
        }
        createStage("/univr/musica/fxml/LoginView.fxml", AppConfig.APP_TITLE);
    }

    public void showRegisterWindow() {
        if (stage != null) {
            stage.close();
        }
        createStage("/univr/musica/fxml/RegisterView.fxml", "Registrazione");
    }

    public void showMainWindow(Stage currentStage) {
        if (stage != null) {
            stage.close();
        }
        if (this.user != null && this.user.getLastSongId() > 0) {
            Song s = Model.getInstance().getSongRepository().getSong(this.user.getLastSongId());
            if (s != null) {
                PlaybackManager.getInstance().setCurrentSong(s);
                System.out.println("Canzone caricata nel Manager: " + s.getTitle());
            }
        }
        createStage("/univr/musica/fxml/User/UserView.fxml", AppConfig.APP_TITLE);
    }


    public void setMainView(BorderPane mainView) {
        this.mainView = mainView;
    }

    public void updateMainView(String path) {
        try {
            Node node = createNode(path);


            if (this.mainView != null && node != null) {
                this.mainView.setCenter(node);
                System.out.println("DEBUG: Vista aggiornata con successo!");
            } else {
                System.out.println("DEBUG: Errore! mainView è " + (this.mainView == null ? "NULL" : "OK")
                        + " e node è " + (node == null ? "NULL" : "OK"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Node createNode(String path) throws IOException {
        try {
            return FXMLLoader.load(Objects.requireNonNull(getClass().getResource(path)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void createStage(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            this.stage = stage;
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
            stage.setResizable(false);
        } catch (IOException e) {
            System.err.println("Errore caricamento vista: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public void createScene(String fxmlPath, String title) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Errore caricamento vista: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public void setUser(User user) {
        this.user = user;

    }

    public User getUser() {
        return user;
    }

    public void loadLastUserSession() {
        int lastSongId = user.getLastSongId();
        if (lastSongId > 0) {
            Song lastSong = Model.getInstance().getSongRepository().getSong(lastSongId);
            if (lastSong != null) {
                PlaybackManager.getInstance().setCurrentSong(lastSong);
                System.out.println("Sessione ripristinata: " + lastSong.getTitle());
            }
        }
    }
}