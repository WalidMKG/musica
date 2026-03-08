package univr.musica.config;

import java.io.File;


/**
 * Config del progetto, mi semplifica la chiamata a titolo app e directories path
 */
public class AppConfig {
    // Application settings
    public static final String APP_TITLE = "Spartiti";
    
    // Data storage settings
    public static final String DATA_DIR = "data";
    public static final String DATABASE_PATH = DATA_DIR + "/database/musica.db";
    
    // Create data directory if it doesn't exist
    static {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }
}