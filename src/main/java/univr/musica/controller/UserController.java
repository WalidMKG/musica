package univr.musica.controller;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import univr.musica.Main;
import univr.musica.model.Song;
import univr.musica.model.SongRepository;

public class UserController {
    public HBox home_icon;
    private SongRepository songRepository;

    public void initialize() {
        songRepository = Main.getSongRepository();
    }


    public void go_home(MouseEvent mouseEvent) {
        Song prova = new Song("Cazzo","Culo", "Rock", "1982");
        songRepository.saveSong(prova);
    }
}
