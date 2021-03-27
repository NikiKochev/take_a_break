package takeABreak.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import takeABreak.model.pojo.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
}
