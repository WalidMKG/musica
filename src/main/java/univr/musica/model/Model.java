package univr.musica.model;

import univr.musica.view.ViewFactory;

public class Model {

    private static Model model;
    private final ViewFactory viewFactory;
    private static User authenticatedUser = null;


    private Model() {
        this.viewFactory = new ViewFactory();
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
