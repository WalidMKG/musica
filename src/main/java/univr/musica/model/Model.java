package univr.musica.model;

import univr.musica.view.ViewFactory;

/**
 * Classe che definisce il Model.
 * Questo viene istanziato come un singleton e fa da parte centrale della logica del programma
 */
public class Model {

    private  static Model model;
    private  ViewFactory viewFactory;
    private  User authenticatedUser ;
    private  DatabaseManager dbManager;
    private  UserRepository userRepository;
    private  CommentsRepository commentsRepository;
    private  PlaybackManager playbackManager;
    private  SongRepository songRepository;


    /**
     * Istanzia viewfactory, dbmanager e le varie repository del database
     * Questo riimarrano nel model in caso di necessità saranno chiamate da qui
     */
    private Model() {
        this.viewFactory = new ViewFactory(this);
        this.dbManager = new DatabaseManager();


        this.userRepository = new UserRepository(this, dbManager);
        this.songRepository = new SongRepository(this, dbManager);
        this.commentsRepository = new CommentsRepository(this, dbManager);

        this.playbackManager = new PlaybackManager();
    }

    /**
     * ritorna l'oggetto richiesto
     * @return
     */

    public DatabaseManager getDatabaseManager() {
        return dbManager;
    }
    public UserRepository getUserRepository() {
        return userRepository;
    }

    public CommentsRepository getCommentsRepository() {
        return commentsRepository;
    }

    public PlaybackManager getPlaybackManager() {
        return playbackManager;
    }

    public SongRepository getSongRepository() {
        return songRepository;
    }

    public User getAuthenticatedUser() { return authenticatedUser; }
    public void setAuthenticatedUser(User user) { this.authenticatedUser = user; }


    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    /**
     * Ritorna un'istanza sincronizzata del model e lo crea solo se non + già istanziato
     * @return
     */
    public static synchronized Model getInstance() {
        if(model == null) {
            model = new Model();
        }
        return model;
    }

}
