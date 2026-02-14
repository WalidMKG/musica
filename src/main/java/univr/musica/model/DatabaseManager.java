package univr.musica.model;

import univr.musica.config.AppConfig;

import java.io.File;
import java.sql.*;

public class DatabaseManager {
    private  String dbUrl;
    
    public DatabaseManager() {
        this(AppConfig.DATABASE_PATH);
    }
    
    public DatabaseManager(String dbPath) {
        // Create parent directories if they don't exist
        File dbFile = new File(dbPath);
        if (dbFile.getParentFile() != null && !dbFile.getParentFile().exists()) {
            dbFile.getParentFile().mkdirs();
        }
        
        this.dbUrl = "jdbc:sqlite:" + dbPath;
        initializeDatabase();
    }
    
    /**
     * Initialize the database and create tables if they don't exist
     */
    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create users table if it doesn't exist
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "username TEXT PRIMARY KEY, " +
                    "password TEXT, " +
                    "is_admin INTEGER DEFAULT 0, " +
                    "status INTEGER DEFAULT 0, " + // 0 = Pending, 1 = Approved
                    "last_song_id INTEGER" +
                    ")");

// 2. Tabella Comments con Foreign Keys
            stmt.execute("CREATE TABLE IF NOT EXISTS comments (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "text TEXT NOT NULL, " +
                    "username TEXT NOT NULL, " +
                    "song_id INTEGER, " +
                    "FOREIGN KEY(username) REFERENCES users(username) ON DELETE CASCADE, " +
                    "FOREIGN KEY(song_id) REFERENCES songs(id) ON DELETE CASCADE" +
                    ")");



            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS songs (
                        id      INTEGER PRIMARY KEY AUTOINCREMENT,
                        title  TEXT NOT NULL,
                        author TEXT NOT NULL,
                        genre  TEXT,
                        year   TEXT
                    )""");


            try (ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE username = 'admin'")) {
                if (!rs.next()) {
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO users (username, password, is_admin, status) VALUES (?, ?, ?, ?)")) {
                        pstmt.setString(1, "admin");
                        pstmt.setString(2, "admin");
                        pstmt.setInt(3, 1); // is_admin
                        pstmt.setInt(4, 1); // status = Approved
                        pstmt.executeUpdate();
                    }
                }
            }
            
            // Create other tables as needed
            // Check if admin user exists, create if it doesn't
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE username = 'admin'")) {
                if (!rs.next()) {
                    // Admin user doesn't exist, create it
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO users (username, password, is_admin) VALUES (?, ?, ?)")) {
                        pstmt.setString(1, "admin");
                        pstmt.setString(2, "admin");
                        pstmt.setInt(3, 1);
                        pstmt.executeUpdate();
                    }
                }
            }
            
            } catch (SQLException e) {
                System.err.println("Error initializing database: " + e.getMessage());
            }




    }
    
    /**
     * Get a database connection
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }
    
    /**
     * Execute an update query (INSERT, UPDATE, DELETE)
     */
    public int executeUpdate(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) return 0;

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }


            return affectedRows;

        } catch (SQLException e) {
            System.err.println("Errore executeUpdate: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Execute a query and process the results with a ResultSetProcessor
     */
    public <T> T executeQuery(String sql, ResultSetProcessor<T> processor, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            
            // Execute the query and process results
            try (ResultSet rs = pstmt.executeQuery()) {
                return processor.process(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Interface for processing a ResultSet into a specific type
     */
    public interface ResultSetProcessor<T> {
        T process(ResultSet rs) throws SQLException;
    }
}