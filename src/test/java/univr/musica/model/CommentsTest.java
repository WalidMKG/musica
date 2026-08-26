package univr.musica.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommentsTest {

    private Comments comment;

    @BeforeEach
    public void setUp() {
        comment = new Comments(1, "Ottimo brano!", "walid", 10);
    }

    @Test
    public void testCommentProperties() {
        assertEquals(1, comment.getId());
        assertEquals("Ottimo brano!", comment.getText());
        assertEquals("walid", comment.getUsername());
        assertEquals(10, comment.getSong_id());
    }

    @Test
    public void testCommentLengthLimit() {
        String longText = "a".repeat(200);
        Comments maxComment = new Comments(2, longText, "walid", 10);
        assertTrue(maxComment.getText().length() <= 200, "Il commento non deve superare i 200 caratteri");
    }
}