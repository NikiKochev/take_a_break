package takeABreak.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import takeABreak.model.pojo.FileType;
import takeABreak.model.pojo.FormatType;
import takeABreak.model.pojo.FormatTypeId;

@Repository
public interface FormatTypeRepository extends JpaRepository<FormatType, FormatTypeId> {
}
