package takeABreak.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import takeABreak.model.pojo.FormatType;
import takeABreak.model.pojo.FormatTypeId;

import java.util.List;

@Repository
public interface FormatTypeRepository extends JpaRepository<FormatType, FormatTypeId> {

    List<FormatType> findAllByContentId(int contentId);
}
