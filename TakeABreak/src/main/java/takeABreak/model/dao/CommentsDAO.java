package takeABreak.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.pojo.Comment;
import takeABreak.model.repository.CommentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
@Setter
@Getter
public class CommentsDAO {

    @Autowired
    private CommentRepository cRepository;

    public List<Comment> getCommentsByUser(int userId, int page, int perpage) {
        List<Comment> comments = new ArrayList<>();
        String query ="SELECT id FROM take_a_break.comments WHERE owner_id = ? LIMIT ? , ?;";
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/take_a_break", "root", "root");
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1,userId);
            statement.setInt(2,perpage*page);
            statement.setInt(3,perpage);
            ResultSet result = statement.executeQuery();
            result.next();
            while (result.next()){
                comments.add(cRepository.findById(result.getInt("id")).get());
            }
        } catch (SQLException e) {
            throw new NotFoundException("no connection");
        }
        return comments;
    }

    public List<Comment> getCommentsByPost(int id, int page, int perpage) {
        List<Comment> comments = new ArrayList<>();
        String query ="SELECT id FROM take_a_break.comments WHERE post_id = ? LIMIT ? , ?;";
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/take_a_break", "root", "root");
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1,id);
            statement.setInt(2,perpage*page);
            statement.setInt(3,perpage);
            ResultSet result = statement.executeQuery();
            result.next();
            while (result.next()){
                comments.add(cRepository.findById(result.getInt("id")).get());
            }
        } catch (SQLException e) {
            throw new NotFoundException("no connection");
        }
        return comments;
    }
}
