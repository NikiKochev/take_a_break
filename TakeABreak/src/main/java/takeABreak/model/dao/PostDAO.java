package takeABreak.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import takeABreak.exceptions.NotFoundException;
import takeABreak.model.pojo.Post;
import takeABreak.model.repository.PostRepository;
import takeABreak.service.PostService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
@Setter
@Getter
public class PostDAO {

    @Autowired
    private CategoryDAO categoryDAO;
    @Autowired
    private DBCredentials credentials;
    @Autowired
    private PostService postService;

    public List<Post> find(String find) {
        List<Post> posts = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(credentials.getUrl(), credentials.getUsername(), credentials.getPassword());
            PreparedStatement statement = connection.prepareStatement(find)) {
            ResultSet result = statement.executeQuery();
            while (result.next()){
                posts.add(postService.findById(result.getInt(1)));
            }
        } catch (SQLException e) {
            throw new NotFoundException("no connection"+ e.getMessage());
        }
        return posts;
    }

    public List<Post> findLast(int page, int perpage) {
        String query = "SELECT id FROM posts ORDER BY created_at DESC LIMIT "+
                (page*perpage - perpage)+", "+perpage+";";
        return find(query);
    }

    public List<Post> findBy(String text, int page, int perpage) {
        List<String> words = new ArrayList<>();
        String[] word = text.split(" ");
        for (String w : word){
            w.trim();
            words.add(w);
        }
        List<Post> posts = (categoryDAO.getPostByCategory(words,page,perpage));
        if(posts.size() < page){
            posts.addAll(findByCol(words,page,perpage, "title"));
        }
        if(posts.size()<page){
            posts.addAll(findByCol(words,page,perpage, "description"));
        }
        return posts;
    }

    public List<Post> findByCategory(int categoryId, int page, int perpage) {
        String q = "SELECT id FROM posts WHERE categories_id = "+categoryId+" ORDER BY created_at DESC LIMIT "+
                (page*perpage - perpage)+", "+perpage+";";
        return find(q);
    }

    public List<Post> findByUser(int userId, int page, int perpage) {
        String q = "SELECT id FROM posts WHERE owner_id = "+userId+" ORDER BY created_at DESC LIMIT "+
                (page*perpage - perpage)+", "+perpage+";";
        return find(q);
    }

    private List<Post> findByCol(List<String> text, int page, int perpage, String colName) {
        StringBuilder query = new StringBuilder("SELECT id FROM posts ");
        for (String word : text){
            query = add(query, word, colName);
        }
        query.append(" LIMIT "+ ((page*perpage - perpage)+ ", "+ perpage));
        return find(query.toString());
    }

    private StringBuilder add(StringBuilder query, String word, String colName) {
        if(query.compareTo(new StringBuilder("SELECT id FROM posts")) != 0 ){
            query.append(" AND ");
        }
        else {
            query.append("WHERE ");
        }
        query.append(" "+colName+" LIKE \"%" + word + "%\"");
        return query;
    }
}
