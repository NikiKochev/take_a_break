package takeABreak.model.dto.comments;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.Comment;

import java.time.LocalDate;

@NoArgsConstructor
@Setter
@Getter
@Component
public class AddingResponseCommentsDTO {

    private int ownerId;
    private int parentId;
    private int postId;
    private String content;
    private LocalDate createdAt;

    public AddingResponseCommentsDTO(Comment comment) {
        ownerId = comment.getUser().getId();
        if(comment.getParent() !=null) {
            parentId = comment.getParent().getId();
        }
        postId = comment.getPost().getId();
        content = comment.getContent();
        createdAt = comment.getCreatedAt();
    }
    /*{
    owner_id:5,
    parent_id:2,
    post_id:6
    content: "tralala"
    created_at: 25.11.2020
    }*/
}
