package takeABreak.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import takeABreak.model.pojo.FileType;
import takeABreak.model.pojo.FormatType;

@Repository
public interface FileTypeRepository extends JpaRepository<FileType, Integer> {
}
