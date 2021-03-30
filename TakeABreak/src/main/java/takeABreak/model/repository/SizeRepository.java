package takeABreak.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import takeABreak.model.pojo.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer> {

}
