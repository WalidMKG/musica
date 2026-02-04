package univr.musica.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import univr.musica.Main;
import univr.musica.config.AppConfig;
import univr.musica.model.Song;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

public class LoadController implements Initializable {
    public TextField load_song_title;
    public Button load_song_btn;
    public Button load_pdf_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    public void load_pdf(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona el archivo");

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Documenti PDF (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(filter);

        File selectedFile = fileChooser.showOpenDialog(load_pdf_btn.getScene().getWindow());

        if(selectedFile != null){
            System.out.println("PDF selezionato:" +  selectedFile.getAbsolutePath());
            //gestisciFilePDF(selectedFile);
            saveFileLocally(selectedFile,"pdf");
        }
    }

    private void gestisciFilePDF(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                System.err.println("Apertura file non supportata su questo sistema.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String saveFileLocally(File selectedFile, String type) {
        try {
            // Determiniamo la cartella di destinazione in base al tipo
            String folder = /*type.equalsIgnoreCase("pdf") ? "@../../" : */AppConfig.DATA_DIR+"/pdf/";
            File destFolder = new File(folder);
            if (!destFolder.exists())
                destFolder.mkdirs();

            // Creiamo il percorso di destinazione (mantenendo il nome originale o rinominandolo)
            File destination = new File(folder + selectedFile.getName());

            // Copia fisica del file (sovrascrive se esiste già con lo stesso nome)
            Files.copy(selectedFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("File copiato in: " + destination.getAbsolutePath());

            // Restituiamo il percorso relativo da salvare nel database SQLite
            return destination.getPath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public void load_song(ActionEvent actionEvent) {
        Song song = new Song(load_song_title.getText(),"Drake","Rock", "1999");
        Main.getSongRepository().saveSong(song);
    }
}
