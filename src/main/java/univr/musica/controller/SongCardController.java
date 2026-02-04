package univr.musica.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import univr.musica.model.Song;

import java.util.Objects;

public class SongCardController {

    @FXML
    private ImageView img;

    @FXML
    private Label songName;

    @FXML
    private Label artist;

    public void setData(Song song) {
        System.out.printf("cerco in "+"/univr/musica/data/img/" + song.getId() + ".jpg");
        try {
            img.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/univr/musica/data/img/" + song.getId() + ".jpg"))));
        }
        catch (Exception e) {

            img.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/univr/musica/data/img/" + 1 + ".jpg"))));
        }

        songName.setText(song.getTitle());
        artist.setText(song.getAuthor());
    }
}