package takeABreak.model.dto.comments;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.dto.user.LoginUserResponseDTO;
import takeABreak.model.pojo.Comment;

@NoArgsConstructor
@Getter
@Setter
@Component
public class GetByIdResponseCommentDTO {

    private LoginUserResponseDTO user;
    private int count;
    public GetByIdResponseCommentDTO(Comment comment, Boolean isItLike) {
        user = new LoginUserResponseDTO(comment.getUser());
        if(isItLike) {
            count = comment.getLikers().size();
        }
        else {
            count = comment.getDislikers().size();
        }
    }
}
