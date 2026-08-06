package univr.musica.controller;

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
import univr.musica.model.Model;
import univr.musica.model.Song;

import java.io.File;
import java.net.URL;
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

        load_song_btn.setDisable(true);
    }

    @FXML
    public void load_pdf(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona il file PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documenti PDF (*.pdf)", "*.pdf"));

        File selectedFile = fileChooser.showOpenDialog(load_pdf_btn.getScene().getWindow());
        if (selectedFile != null) {
            System.out.println("PDF selezionato: " + selectedFile.getAbsolutePath());
            tempPdfFile = selectedFile;
        }
    }

    @FXML
    public void load_mp3(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona file MP3");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File MP3 (*.mp3)", "*.mp3"));

        File selectedFile = fileChooser.showOpenDialog(Load_mp3_btn.getScene().getWindow());
        if (selectedFile != null) {
            System.out.println("MP3 selezionato: " + selectedFile.getAbsolutePath());
            tempMp3File = selectedFile;
            load_song_btn.setDisable(false);
        }
    }

    @FXML
    public void load_cover(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona copertina");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Immagini (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg"
        ));

        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            System.out.println("Immagine selezionata: " + selectedFile.getAbsolutePath());
            loaded_cover.setImage(new Image(selectedFile.toURI().toString()));
            tempCoverFile = selectedFile;
        }
    }

    @FXML
    public void load_song(ActionEvent actionEvent) {
        System.out.println("Tentativo di caricamento canzone...");

        // 1. Validazione Campi Obbligatori (Titolo e Autore)
        String title = load_song_title.getText() != null ? load_song_title.getText().trim() : "";
        String author = load_song_Art.getText() != null ? load_song_Art.getText().trim() : "";
        String genre = load_song_Genre.getText() != null ? load_song_Genre.getText().trim() : "";
        String yearInput = load_song_Year.getText() != null ? load_song_Year.getText().trim() : "";

        if (title.isEmpty() || author.isEmpty()) {
            showError("Titolo e Autore sono obbligatori!");
            return;
        }

        // 2. Validation Bloccante File MP3
        if (tempMp3File == null || !tempMp3File.exists()) {
            showError("Seleziona prima un file MP3!");
            return;
        }

        // 3. Parsing Anno Sicuro
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

            // 4. Username dell'Uploader
            String uploaderUsername = (model != null && model.getAuthenticatedUser() != null)
                    ? model.getAuthenticatedUser().getUsername()
                    : "Unknown";

            // 5. Delegamento Completo al SongRepository (DB + File System)
            boolean success = model.getSongRepository().saveSongComplete(
                    song, uploaderUsername, tempMp3File, tempPdfFile, tempCoverFile
            );

            if (success) {
                System.out.println("Caricamento completato con successo!");
                resetFields();
            } else {
                showError("Errore durante il salvataggio della canzone!");
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
            error_lbl.setVisible(true);
            error_lbl.setStyle("-fx-text-fill: red;");
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

        if (error_lbl != null) {
            error_lbl.setStyle("-fx-text-fill: green;");
            error_lbl.setText("Brano caricato con successo!");
        }
    }
}