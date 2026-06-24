package univr.musica.controller.User;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import univr.musica.model.Comments;
import univr.musica.model.Model;

import java.net.URL;
import java.util.ResourceBundle;

public class CommentController implements Initializable {
    private final Model model;
    public Label comment_username;
    public Text comment_text;

    public CommentController(Model model) {
        this.model = model;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
    public void setCommentData(Comments comment) {
        if (comment != null) {
            comment_username.setText(comment.getUsername());
            comment_text.setText(comment.getText());
        }
    }
}
