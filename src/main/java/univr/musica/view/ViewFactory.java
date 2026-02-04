package univr.musica.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import univr.musica.config.AppConfig;

import java.io.IOException;
import java.util.Objects;

// Caricamento del font (es. Montserrat Regular)


public class ViewFactory {
    private static ViewFactory instance;
    private BorderPane mainView;


    public static ViewFactory getInstance() {
        if (instance == null) {
            instance = new ViewFactory();
        }
        return instance;
    }

    public void showLoginWindow(Stage currentStage) {
        if (currentStage != null) {
            currentStage.close();
        }
        createStage("/univr/musica/fxml/LoginView.fxml", AppConfig.APP_TITLE);
    }

    public void showRegisterWindow(Stage currentStage) {
        if (currentStage != null) {
            currentStage.close();
        }
        createStage("/univr/musica/fxml/RegisterView.fxml", "Registrazione");
    }

    public void showMainWindow(Stage currentStage) {
        if (currentStage != null) {
            currentStage.close();
        }
        createStage("/univr/musica/fxml/User/UserView.fxml", AppConfig.APP_TITLE);
    }


    public void setMainView(BorderPane mainView) {
        this.mainView = mainView;
    }

    public void updateMainView(String path) {
        try {
            Node node = createNode(path);

            // USIAMO "this.mainView" che abbiamo settato nel setMainView
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


            //scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
            stage.setResizable(false);
        } catch (IOException e) {
            System.err.println("Errore caricamento vista: " + fxmlPath);
            e.printStackTrace();
        }
    }
}