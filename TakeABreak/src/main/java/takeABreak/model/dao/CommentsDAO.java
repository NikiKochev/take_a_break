package takeABreak.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.pojo.Comment;
import takeABreak.service.CommentService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
@Component
@NoArgsConstructor
@Setter
@Getter
public class CommentsDAO {
    @Autowired
    private DBCredentials credentials;
    @Autowired
    private CommentService commentService;

    public List<Comment> find(String query) {
        List<Comment> comments = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(credentials.getUrl(), credentials.getUsername(), credentials.getPassword());
            PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet result = statement.executeQuery();
            while (result.next()){
                comments.add(commentService.getCommentById(result.getInt(1)));
            }
        } catch (SQLException e) {
            throw new NotFoundException("no connection"+ e.getMessage());
        }
        return comments;
    }

    public List<Comment> findByUser(int id, int page, int perpage) {
        String query = "SELECT id FROM comments WHERE owner_id = "+id+" ORDER BY created_at DESC LIMIT "+
                (page*perpage - perpage)+", "+perpage+";";
        return find(query);
    }

    public List<Comment> findByPost(int id, int page, int perpage) {
        String query = "SELECT id FROM comments WHERE post_id = "+id+" ORDER BY created_at DESC LIMIT "+
                (page*perpage - perpage)+", "+perpage+";";
        return find(query);
    }
}
