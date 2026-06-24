package univr.musica.model;

/**
 * Classe che rappresenta un commento
 * mantiene id commento, testo, username del autore e id della canzone che commenta
 */
public class Comments {
        private int id;
        private String text;
        private String username;
        private int song_id;

        /**
         * Costruttore dell'oggetto comment conosciuto l'id
         * Questo viene usato dal database manager quando ESTRAGGO il commento dalla base di dati
         * @param id
         * @param text
         * @param username
         * @param song_id
         */
        public Comments(int id, String text, String username, int song_id) {
            this.id = id;
            this.text = text;
            this.username = username;
            this.song_id = song_id;
        }

    /**
     * Costruttore che crea l'oggetto commento senza l'id.
     * Questo verrà usato quando viene creato nella vista per poi venir messo nella base di dati
     * @param text
     * @param username
     * @param song_id
     */
        public Comments( String text, String username, int song_id) {
            this.text = text;
            this.username = username;
            this.song_id = song_id;
        }

    /**
     * Metodo che ritorna il testo del commento
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * Metodo che ritorna l'id
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Metodo che ritorna l'user dell'autore
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Metodo che ritorna l'id della canzone commentata
     * @return id canzone
     */
    public int getSong_id() {
        return song_id;
    }
}