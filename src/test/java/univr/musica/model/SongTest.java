package univr.musica.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SongTest {

    private Song song;

    @BeforeEach
    public void setUp() {
        song = new Song(1, "Titolo Prova", "Autore Prova", "Rock", "2024", "walid");
    }

    @Test
    public void testSongProperties() {
        assertEquals(1, song.getId());
        assertEquals("Titolo Prova", song.getTitle());
        assertEquals("Autore Prova", song.getAuthor());
        assertEquals("Rock", song.getGenre());
        assertEquals("2024", song.getYear());
        assertEquals("walid", song.getUploader());
    }

    @Test
    public void testSongFormatting() {
        assertNotNull(song.toString(), "La rappresentazione stringa del brano non deve essere null");
    }
}