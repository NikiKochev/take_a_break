package takeABreak.model.dto.comments;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.Comment;

@Component
@NoArgsConstructor
@Setter
@Getter
public class EditRequestCommentDTO {

    private int userId;
    private int commentId;
    private String content;

    public EditRequestCommentDTO(Comment c) {
    }

    /*{
    "user_id" : 3,
    "comment_id" : 1,
    "content" : "I have just edited my first comment - tralala"
}*/
}
