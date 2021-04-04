package takeABreak.model.dto.comments;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.pojo.Comment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Component
public class GetResponseCommentDTO {

    private int id;
    private String content;
    private LocalDate createdAt;
    private List<GetResponseCommentDTO> subComments;
    private int likers;
    private int dislikers;

    public GetResponseCommentDTO(Comment comment) {
        id = comment.getId();
        content = comment.getContent();
        createdAt = comment.getCreatedAt();
        subComments = new ArrayList<>();
        for (Comment c : comment.getSubComments()){
            subComments.add(new GetResponseCommentDTO(c));
        }
        likers = comment.getLikers().size();
        dislikers = comment.getDislikers().size();
    }
}
