package univr.musica.controller.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import univr.musica.config.AppConfig;
import univr.musica.model.Model;
import univr.musica.model.Song;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class LoadController implements Initializable {
    public TextField load_song_title;
    public Button load_song_btn;
    public Button load_pdf_btn;
    public Button Load_mp3_btn;
    public ImageView loaded_cover;
    public TextField load_song_Year;
    public TextField load_song_Genre;
    public TextField load_song_Art;
    public Label error_lbl;
    private File tempMp3File;
    private File tempPdfFile;
    private File tempCoverFile;


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
            tempPdfFile = selectedFile;
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


    public String saveFileLocally(File selectedFile, String type, String name) {
        try {
            // Determiniamo la cartella di destinazione in base al tipo
            String folder = /*type.equalsIgnoreCase("pdf") ? "@../../" : */AppConfig.DATA_DIR+"/"+type+"/";
            File destFolder = new File(folder);
            if (!destFolder.exists())
                destFolder.mkdirs();

            // Creiamo il percorso di destinazione (mantenendo il nome originale o rinominandolo)
            File destination = new File(folder + name  + "."+ type);

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


    @FXML
    public void load_song(ActionEvent actionEvent) {
        System.out.println("Loading song "+ load_song_title.getText());
        try {
            int yearValue = Integer.parseInt(load_song_Year.getText());
            String yearStr = (yearValue > 0 && yearValue <= LocalDate.now().getYear())
                    ? load_song_Year.getText()
                    : String.valueOf(LocalDate.now().getYear());

            Song song = new Song(load_song_title.getText(), load_song_Art.getText(), load_song_Genre.getText(), yearStr);

            // 1. Salva e controlla il boolean
            boolean success = Model.getInstance().getSongRepository().saveSong(song);

            if (success) {

                int realId = Model.getInstance().getSongRepository().getLastInsertedId();
                String id = String.valueOf(realId);

                System.out.println("Canzone salvata nel DB con ID: " + id);


                if (tempMp3File != null) saveFileLocally(tempMp3File, "mp3", id);
                if (tempPdfFile != null) saveFileLocally(tempPdfFile, "pdf", id);

                if (tempCoverFile == null) {
                    System.out.println("Cover nulla metto la DEFAULT");
                    File defaultFile = new File(AppConfig.DATA_DIR + "/jpg/default.jpg");
                    System.out.println(defaultFile.getAbsolutePath());
                    if (defaultFile.exists()) {
                        System.out.println("Default file esiste: " + defaultFile.getAbsolutePath());
                        saveFileLocally(defaultFile, "jpg", id); // Forziamo .jpg per coerenza
                    }
                    else {
                        System.out.println("Default file not esiste: " + defaultFile.getAbsolutePath());
                    }
                } else {
                    saveFileLocally(tempCoverFile, "jpg", id);
                }

                System.out.println("Salvataggio file completato per ID: " + id);
                resetFields();
            } else {
                error_lbl.setText("Errore nel salvataggio Database");
            }

        } catch (NumberFormatException e) {
            error_lbl.setText("Anno non valido!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void resetFields(){
        load_song_title.clear();
        load_song_Art.clear();
        load_song_Genre.clear();
        load_song_Year.clear();
        loaded_cover.setImage(new Image(getClass().getResourceAsStream("/univr/musica/data/img/ic_upload.png")));
        tempMp3File = null;
        tempPdfFile = null;
        tempCoverFile = null;
    }

    public void load_mp3(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select mp3 file");

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("File mp3  (*.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().add(filter);

        File selectedFile = fileChooser.showOpenDialog(load_pdf_btn.getScene().getWindow());

        if(selectedFile != null){
            System.out.println("PDF selezionato:" +  selectedFile.getAbsolutePath());
            tempMp3File = selectedFile;
        }
    }

    @FXML
    public void load_cover(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona el archivo");
        System.out.println("carica immagine");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
                "Immagini (*.png, *.jpg, *.jpeg)",
                "*.png", "*.jpg", "*.jpeg"
        );
        fileChooser.getExtensionFilters().add(filter);

        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if(selectedFile != null){
            System.out.println("Immagine selezionata:" +  selectedFile.getAbsolutePath());
            loaded_cover.setImage(new Image(selectedFile.toURI().toString()));
            tempCoverFile = selectedFile;
        }
    }
}
