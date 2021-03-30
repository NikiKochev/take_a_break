package takeABreak.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import takeABreak.model.pojo.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
