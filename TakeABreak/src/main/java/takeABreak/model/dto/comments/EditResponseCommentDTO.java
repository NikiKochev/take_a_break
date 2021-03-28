package takeABreak.model.dto.comments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.Comment;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class EditResponseCommentDTO {
    private int ownerId;
    private int parentId;
    private int postId;
    private String content;

    public EditResponseCommentDTO(Comment comment) {
        ownerId = comment.getUser().getId();
        if(comment.getParent() != null) {
            parentId = comment.getParent().getId();
        }
        postId = comment.getPost().getId();
        content= comment.getContent();
    }
}
