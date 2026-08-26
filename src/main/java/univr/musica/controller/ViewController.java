package univr.musica.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import univr.musica.model.Model;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewController implements Initializable {

    private final Model model;

    @FXML
    private BorderPane mainView;

    public ViewController(Model model) {
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Registra il mainView dentro la ViewFactory
        model.getViewFactory().setMainView(mainView);

        //  Carica la vista iniziale di default
        model.getViewFactory().updateMainView("/univr/musica/fxml/HomeView.fxml");
    }
}