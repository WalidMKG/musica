package univr.musica.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import univr.musica.config.AppConfig;
import univr.musica.model.Model;
import univr.musica.model.Song;

import java.io.IOException;
import java.net.URL;

public class ViewFactory {
    private final Model model;
    private BorderPane mainView;
    private Stage stage;

    public ViewFactory(Model model) {
        this.model = model;
    }

    private void applyGlobalStyle(Scene scene) {
        if (scene != null) {
            URL cssUrl = getClass().getResource("/univr/musica/css/global.css");
            if (cssUrl == null) {
                cssUrl = getClass().getResource("/univr.musica/css/global.css");
            }
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("WARNING: Impossibile trovare global.css");
            }
        }
    }

    private Scene loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(controllerClass -> {
                try {
                    return controllerClass.getConstructor(Model.class)
                            .newInstance(Model.getInstance());
                } catch (Exception e) {
                    try {
                        return controllerClass.getDeclaredConstructor().newInstance();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            Scene scene = new Scene(loader.load());
            applyGlobalStyle(scene);
            return scene;

        } catch (IOException e) {
            System.err.println("Errore caricamento FXML: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }

    public void setMainView(BorderPane mainView) {
        this.mainView = mainView;
    }

    public void updateMainView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(controllerClass -> {
                try {
                    return controllerClass.getConstructor(Model.class)
                            .newInstance(Model.getInstance());
                } catch (Exception e) {
                    try {
                        return controllerClass.getDeclaredConstructor().newInstance();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            Node node = loader.load();

            if (this.mainView != null) {
                this.mainView.setCenter(node);
                System.out.println("Vista centrale aggiornata con: " + fxmlPath);
            } else {
                System.err.println("ERRORE: mainView è null! Non posso aggiornare il centro.");
            }
        } catch (IOException e) {
            System.err.println("Errore durante l'update della MainView: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public void showLoginWindow() {
        Scene scene = loadScene("/univr/musica/fxml/LoginView.fxml");
        if (stage == null) {
            stage = new Stage();
        }
        stage.setScene(scene);
        stage.setTitle(AppConfig.APP_TITLE);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

    public void showRegisterWindow() {
        Scene scene = loadScene("/univr/musica/fxml/RegisterView.fxml");
        if (stage == null) {
            stage = new Stage();
        }
        stage.setScene(scene);
        stage.setTitle("Registrazione");
        stage.setResizable(false);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Unica finestra principale per tutti gli utenti
     */
    public void showMainWindow() {
        loadLastUserSession();
        Scene scene = loadScene("/univr/musica/fxml/MainView.fxml");
        if (stage != null) {
            stage.setScene(scene);
            stage.setTitle(AppConfig.APP_TITLE);
            stage.setResizable(false);
            stage.sizeToScene();
            stage.centerOnScreen();
            if (!stage.isShowing()) {
                stage.show();
            }
        }
    }

    public void loadLastUserSession() {
        if (model.getAuthenticatedUser() != null) {
            int lastSongId = model.getAuthenticatedUser().getLastSongId();
            if (lastSongId > 0) {
                Song lastSong = model.getSongRepository().getSong(lastSongId);
                if (lastSong != null) {
                    model.getPlaybackManager().currentSongProperty().set(lastSong);
                    System.out.println("DEBUG ViewFactory: Sessione ripristinata per " + lastSong.getTitle());
                }
            }
        }
    }

    public void logout() {
        Model.getInstance().getUserRepository().saveLastSongSession();
        model.getPlaybackManager().stop();
        model.setAuthenticatedUser(null);

        if (this.stage != null) {
            this.stage.close();
            this.stage = null;
        }

        this.mainView = null;
        showLoginWindow();
    }
}