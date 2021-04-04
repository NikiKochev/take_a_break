package takeABreak.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.Post;

import java.util.List;

@Component
@NoArgsConstructor
@Setter
@Getter
public class CategoryDAO {
    @Autowired
    private PostDAO postDAO;

    public List<Post> getPostByCategory(List<String> text, int page, int perpage) {
        StringBuilder query = new StringBuilder("SELECT posts.id FROM posts JOIN categories ON (categories_id = categories.id) GROUP BY categories.name ");
        for (String word : text){
            query = add(query, word);
        }
        query.append(" LIMIT "+ ((page*perpage - perpage)+ ", "+ perpage));
        return postDAO.find(query.toString());
    }

    private StringBuilder add(StringBuilder query, String value) {
        if(query.compareTo(new StringBuilder("SELECT id FROM posts JOIN categories ON (categories_id = id) GROUP BY name ")) != 0){
            query.append(" AND ");
        }
        else {
            query.append("HAVING ");
        }
        query.append(" name LIKE \"%" + value + "%\"");
        return query;
    }
}
