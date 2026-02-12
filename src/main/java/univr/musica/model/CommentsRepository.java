package univr.musica.model;

import univr.musica.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsRepository {
    private final DatabaseManager dbManager;
    private Map<Integer, Comments> CommentsCache = new HashMap<>();

    public CommentsRepository() {
        this.dbManager = Main.getDatabaseManager();
        refreshCommentsCache();
    }

    private void refreshCommentsCache() {
        CommentsCache.clear();

        dbManager.executeQuery(
                "SELECT id, text, username, song_id FROM comments",
                rs -> {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String title = rs.getString("text");
                        String username = rs.getString("username");
                        int song_id = rs.getInt("song_id");

                        Comments comment = new Comments(id,title, username, song_id);
                        CommentsCache.put(id, comment);
                    }
                    return null;
                }
        );
    }

    public Comments getComment(int id) {
        return CommentsCache.get(id);
    }

    public boolean saveComment(Comments comment) {
        int rowsAffected = dbManager.executeUpdate(
                "INSERT INTO comments (text, username, song_id) VALUES (?, ?, ?)",
                comment.getText(), comment.getUsername(), comment.getSong_id()
        );

        if (rowsAffected > 0) {
            int newId = getLastInsertedId();
            Comments savedComment = new Comments(newId, comment.getText(), comment.getUsername(), comment.getSong_id());
            CommentsCache.put(newId, savedComment);
            return true;
        }
        return false;
    }

    public int getLastInsertedId() {
        return dbManager.executeQuery("SELECT id FROM comments ORDER BY id DESC LIMIT 1", rs -> {
            if (rs.next()) {
                return rs.getInt("id");
            }
            return 0;
        });
    }



    public List<Comments> searchCommentsRep(int songID) {
        List<Comments> comments = new ArrayList<>();

        String sql = "SELECT text, username, song_id FROM comments " +
                "WHERE song_id LIKE ? ";

        dbManager.executeQuery(sql, rs -> {
            while (rs.next()) {
                comments.add(new Comments(
                        rs.getInt("id"),
                        rs.getString("text"),
                        rs.getString("username"),
                        rs.getInt("song_id")
                ));
            }
            return null;
        }, "%" + songID + "%");

        return comments;
    }

}
