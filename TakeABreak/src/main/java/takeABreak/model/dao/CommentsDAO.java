package takeABreak.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import takeABreak.model.repository.CommentRepository;

@Component
@NoArgsConstructor
@Setter
@Getter
public class CommentsDAO {

    @Autowired
    private CommentRepository cRepository;

}
