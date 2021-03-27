package takeABreak.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.Comment;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Component
public class FindResponseCommentDTO {

    private List<Comment> comments;

    public FindResponseCommentDTO(List<Comment> commentsByUser) {
        comments.addAll(commentsByUser);
    }

}
