package univr.musica.controller.Admin;

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

import java.awt.Desktop;
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

    private Model model;

    // Costruttore vuoto per FXML standard
    public LoadController() {
        this.model = Model.getInstance();
    }

    // Costruttore per Dependency Injection se usi ControllerFactory
    public LoadController(Model model) {
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void load_pdf(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona il file PDF");

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Documenti PDF (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(filter);

        File selectedFile = fileChooser.showOpenDialog(load_pdf_btn.getScene().getWindow());

        if (selectedFile != null) {
            System.out.println("PDF selezionato: " + selectedFile.getAbsolutePath());
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
            String folder = AppConfig.DATA_DIR + "/" + type + "/";
            File destFolder = new File(folder);
            if (!destFolder.exists()) {
                destFolder.mkdirs();
            }

            File destination = new File(folder + name + "." + type);

            Files.copy(selectedFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("File copiato in: " + destination.getAbsolutePath());
            return destination.getPath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void load_song(ActionEvent actionEvent) {
        System.out.println("Loading song " + load_song_title.getText());
        try {
            int yearValue = Integer.parseInt(load_song_Year.getText());
            String yearStr = (yearValue > 0 && yearValue <= LocalDate.now().getYear())
                    ? load_song_Year.getText()
                    : String.valueOf(LocalDate.now().getYear());

            Song song = new Song(load_song_title.getText(), load_song_Art.getText(), load_song_Genre.getText(), yearStr);

            // Recupera lo username dell'utente loggato
            String uploaderUsername = model.getAuthenticatedUser() != null
                    ? model.getAuthenticatedUser().getUsername()
                    : "Unknown";

            // Passa l'uploader a saveSong
            boolean success = model.getSongRepository().saveSong(song, uploaderUsername);

            if (success) {
                int realId = model.getSongRepository().getLastInsertedId();
                String id = String.valueOf(realId);

                System.out.println("Canzone salvata nel DB con ID: " + id + " da: " + uploaderUsername);

                if (tempMp3File != null) saveFileLocally(tempMp3File, "mp3", id);
                if (tempPdfFile != null) saveFileLocally(tempPdfFile, "pdf", id);

                if (tempCoverFile == null) {
                    File defaultFile = new File(AppConfig.DATA_DIR + "/jpg/default.jpg");
                    if (defaultFile.exists()) {
                        saveFileLocally(defaultFile, "jpg", id);
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

    private void resetFields() {
        load_song_title.clear();
        load_song_Art.clear();
        load_song_Genre.clear();
        load_song_Year.clear();
        if (loaded_cover != null && getClass().getResource("/univr/musica/data/img/ic_upload.png") != null) {
            loaded_cover.setImage(new Image(getClass().getResourceAsStream("/univr/musica/data/img/ic_upload.png")));
        }
        tempMp3File = null;
        tempPdfFile = null;
        tempCoverFile = null;
        if (error_lbl != null) error_lbl.setText("");
    }

    public void load_mp3(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona file MP3");

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("File MP3 (*.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().add(filter);

        File selectedFile = fileChooser.showOpenDialog(Load_mp3_btn.getScene().getWindow());

        if (selectedFile != null) {
            System.out.println("MP3 selezionato: " + selectedFile.getAbsolutePath());
            tempMp3File = selectedFile;
        }
    }

    @FXML
    public void load_cover(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona la copertina");

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
                "Immagini (*.png, *.jpg, *.jpeg)",
                "*.png", "*.jpg", "*.jpeg"
        );
        fileChooser.getExtensionFilters().add(filter);

        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            System.out.println("Immagine selezionata: " + selectedFile.getAbsolutePath());
            loaded_cover.setImage(new Image(selectedFile.toURI().toString()));
            tempCoverFile = selectedFile;
        }
    }
}