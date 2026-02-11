package univr.musica.model;
import javafx.scene.image.Image;
import univr.musica.config.AppConfig;

import java.awt.Desktop;
import java.io.File;
import java.util.Objects;

public class Song {
    private int id;
    private String title;
    private String author;
    private String genre;
    private String year;
    private String path = "/1.jpg";


    public Song(int id,String title, String author, String genre, String year){
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.year = year;
        this.id = id;
        path = "/"+ id + ".jpg";
    }


    public Song(String title, String author, String genre, String year){
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.year = year;
    }

    public Song(String title, String author, String genre){
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.year = "unknown";
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }
    public String getGenre() {
        return genre;
    }
    public String getYear() {
        return year;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return title+ "-"+author+"-"+year;
    }

    public String getPath(String pathType) {
        return id + pathType;
    }

    public Image getCover() {
        File file = new File(AppConfig.DATA_DIR + "/jpg/" + id + ".jpg");

        if (file.exists()) {
            return new Image(file.toURI().toString());
        } else {
            System.err.println("Cover non trovata in: " + file.getAbsolutePath());
            var stream = getClass().getResourceAsStream("/univr/musica/data/jpg/default.png");

            if (stream != null) {
                return new Image(stream);
            } else {
                System.err.println("ATTENZIONE: Immagine di default non trovata nelle risorse!");
                return new Image("https://via.placeholder.com/150");
            }
        }
    }
}
