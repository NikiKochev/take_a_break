package takeABreak.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import takeABreak.model.pojo.Category;
import takeABreak.model.pojo.Post;
import takeABreak.model.pojo.User;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findAllByUser(User user);

    List<Post> findAllByCategory(Category category);
}
