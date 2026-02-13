package univr.musica.model;

import univr.musica.view.ViewFactory;

public class Model {

    private  static Model model;
    private  ViewFactory viewFactory;
    private  User authenticatedUser = null;
    private  DatabaseManager dbManager;
    private  UserRepository userRepository;
    private  CommentsRepository commentsRepository;
    private  PlaybackManager playbackManager;
    private  SongRepository songRepository;



    private Model() {
        this.viewFactory = new ViewFactory();
        this.dbManager = new DatabaseManager();

        this.userRepository = new UserRepository(dbManager);
        this.commentsRepository = new CommentsRepository(dbManager);
        this.songRepository = new  SongRepository(dbManager);

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
