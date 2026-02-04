package univr.musica.model;
import java.awt.Desktop;
import java.io.File;

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
}
