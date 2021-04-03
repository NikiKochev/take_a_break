package takeABreak.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import takeABreak.model.pojo.Content;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Integer> {

    List<Content> findAllBySession(String session);
}
