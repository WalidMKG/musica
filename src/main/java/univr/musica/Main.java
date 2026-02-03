package univr.musica;

import javafx.application.Application;
import javafx.stage.Stage;
import univr.musica.model.DatabaseManager;
import univr.musica.model.UserRepository;
import univr.musica.view.ViewFactory;

import java.io.IOException;

import static javafx.application.Application.launch;

public class Main extends Application {
    // 1. Rendi queste variabili statiche in modo che getDatabaseManager() possa restituirle
    private static DatabaseManager dbManager;
    private static UserRepository userRepository;

    public static DatabaseManager getDatabaseManager() {
        return dbManager;
    }

    public static UserRepository getUserRepository() {
        return userRepository;
    }

    @Override
    public void start(Stage stage) throws IOException {
        // 2. Inizializza PRIMA il database, poi la cache, poi la finestra
        dbManager = new DatabaseManager();
        userRepository = new UserRepository();

        // 3. Ora che i dati sono pronti, mostra la finestra
        ViewFactory.getInstance().showLoginWindow(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}