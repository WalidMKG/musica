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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class LoadController implements Initializable {
    @FXML public TextField load_song_title;
    @FXML public Button load_song_btn;
    @FXML public Button load_pdf_btn;
    @FXML public Button Load_mp3_btn;
    @FXML public ImageView loaded_cover;
    @FXML public TextField load_song_Year;
    @FXML public TextField load_song_Genre;
    @FXML public TextField load_song_Art;
    @FXML public Label error_lbl;

    private File tempMp3File;
    private File tempPdfFile;
    private File tempCoverFile;

    private Model model;

    public LoadController() {
        this.model = Model.getInstance();
    }

    public LoadController(Model model) {
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (this.model == null) {
            this.model = Model.getInstance();
        }
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
            System.err.println("Errore salvataggio file locale: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void load_song(ActionEvent actionEvent) {
        System.out.println("Tentativo di caricamento canzone...");

        // 1. Validazione Campi Vuoti
        String title = load_song_title.getText() != null ? load_song_title.getText().trim() : "";
        String author = load_song_Art.getText() != null ? load_song_Art.getText().trim() : "";
        String genre = load_song_Genre.getText() != null ? load_song_Genre.getText().trim() : "";
        String yearInput = load_song_Year.getText() != null ? load_song_Year.getText().trim() : "";

        if (title.isEmpty() || author.isEmpty()) {
            showError("Titolo e Autore sono obbligatori!");
            return;
        }

        // 2. Parsing Anno Sicuro
        String yearStr;
        try {
            int yearValue = Integer.parseInt(yearInput);
            int currentYear = LocalDate.now().getYear();
            yearStr = (yearValue > 0 && yearValue <= currentYear) ? String.valueOf(yearValue) : String.valueOf(currentYear);
        } catch (NumberFormatException e) {
            yearStr = String.valueOf(LocalDate.now().getYear());
        }

        try {
            Song song = new Song(title, author, genre.isEmpty() ? "Sconosciuto" : genre, yearStr);

            // 3. Verifica Utente Loggato
            String uploaderUsername = "Unknown";
            if (model != null && model.getAuthenticatedUser() != null) {
                uploaderUsername = model.getAuthenticatedUser().getUsername();
            }

            // 4. Salvataggio su DB
            boolean success = model.getSongRepository().saveSong(song, uploaderUsername);

            if (success) {
                int realId = model.getSongRepository().getLastInsertedId();
                String id = String.valueOf(realId);

                System.out.println("Canzone salvata nel DB con ID: " + id + " dall'utente: " + uploaderUsername);

                // Salvataggio File
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

                System.out.println("Salvataggio completato con successo!");
                resetFields();
            } else {
                showError("Errore durante l'inserimento nel DB!");
            }

        } catch (Exception e) {
            System.err.println("Eccezione durante il caricamento della canzone:");
            e.printStackTrace();
            showError("Errore imprevisto durante il salvataggio!");
        }
    }

    private void showError(String message) {
        System.err.println("ERRORE FORM: " + message);
        if (error_lbl != null) {
            error_lbl.setText(message);
        }
    }

    private void resetFields() {
        load_song_title.clear();
        load_song_Art.clear();
        load_song_Genre.clear();
        load_song_Year.clear();

        var stream = getClass().getResourceAsStream("/univr/musica/data/img/ic_upload.png");
        if (loaded_cover != null && stream != null) {
            loaded_cover.setImage(new Image(stream));
        }

        tempMp3File = null;
        tempPdfFile = null;
        tempCoverFile = null;
        if (error_lbl != null) error_lbl.setText("Canzone caricata con successo!");
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
        fileChooser.setTitle("Seleziona copertina");

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