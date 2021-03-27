package takeABreak.model.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import takeABreak.model.pojo.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
