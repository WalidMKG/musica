package univr.musica.model;

public class Comments {
        private int id;
        private String text;
        private String username;
        private int song_id;

        public Comments(int id, String text, String username, int song_id) {
            this.id = id;
            this.text = text;
            this.username = username;
            this.song_id = song_id;
        }

        public Comments( String text, String username, int song_id) {
            this.text = text;
            this.username = username;
            this.song_id = song_id;
        }

    public String getText() {
        return text;
    }
    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }

    public int getSong_id() {
        return song_id;
    }
}