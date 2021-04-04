package takeABreak.model.dto.comments;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.Comment;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Component
public class FindResponseCommentDTO {

    private List<Comment> comments;

    public FindResponseCommentDTO(List<Comment> commentsByUser) {
        this.comments = new ArrayList<>();
        for (Comment c :commentsByUser){
            if(c.getParent() == null){
                comments.add(c);
            }
        }
    }

}
