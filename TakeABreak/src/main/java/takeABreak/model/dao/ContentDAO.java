package takeABreak.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.pojo.Content;
import takeABreak.service.ContentService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
@Setter
@Getter
public class ContentDAO {

    @Autowired
    private CategoryDAO categoryDAO;
    @Autowired
    private DBCredentials credentials;
    @Autowired
    private ContentService contentService;

    public List<Content> find(String find) {
        List<Content> contents = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(credentials.getUrl(), credentials.getUsername(), credentials.getPassword());
            PreparedStatement statement = connection.prepareStatement(find)) {
            ResultSet result = statement.executeQuery();
            while (result.next()){
                contents.add(contentService.findById(result.getInt(1)));
            }
        } catch (SQLException e) {
            throw new NotFoundException("no connection"+ e.getMessage());
        }
        return contents;
    }

    public List<Content> findAllContentWithoutPostOlderThan1Hour(){
        String q = "SELECT c.* FROM takeabreak.posts p\n" +
                "RIGHT JOIN content c\n" +
                "ON (p.content_id = c.id)\n" +
                "WHERE p.id IS NULL AND  c.created_at < DATE_SUB(NOW(), INTERVAL 1 HOUR)";
        return find(q);
    }
}
