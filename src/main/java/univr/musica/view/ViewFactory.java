package univr.musica.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import univr.musica.config.AppConfig;

import java.io.IOException;

// Caricamento del font (es. Montserrat Regular)


public class ViewFactory {
    private static ViewFactory instance;


    private ViewFactory() {}

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