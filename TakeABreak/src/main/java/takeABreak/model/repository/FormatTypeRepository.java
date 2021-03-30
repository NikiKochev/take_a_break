package takeABreak.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import takeABreak.model.pojo.FileType;

@Repository
public interface FormatTypeRepository extends JpaRepository<FileType, Integer> {
}
