package univr.musica.model;

import univr.musica.view.ViewFactory;

public class Model {

    private  static Model model;
    private  ViewFactory viewFactory;
    private  User authenticatedUser ;
    private  DatabaseManager dbManager;
    private  UserRepository userRepository;
    private  CommentsRepository commentsRepository;
    private  PlaybackManager playbackManager;
    private  SongRepository songRepository;



    private Model() {
        this.viewFactory = new ViewFactory(this);
        this.dbManager = new DatabaseManager();


        this.userRepository = new UserRepository(this, dbManager);
        this.songRepository = new SongRepository(this, dbManager);
        this.commentsRepository = new CommentsRepository(this, dbManager);

        this.playbackManager = new PlaybackManager();
    }


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

    public static synchronized Model getInstance() {
        if(model == null) {
            model = new Model();
        }
        return model;
    }

}
