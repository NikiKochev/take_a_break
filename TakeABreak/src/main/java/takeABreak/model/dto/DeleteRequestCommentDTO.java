package takeABreak.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Setter
@Getter
public class DeleteRequestCommentDTO {

    private int userId;
    private int commentId;
    /*{
    "user_id" : 3,
    "comment_id" : 1
}*/
}
