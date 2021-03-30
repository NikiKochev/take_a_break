package takeABreak.model.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import takeABreak.model.dto.user.LoginUserResponseDTO;
import takeABreak.model.pojo.Post;
import takeABreak.model.pojo.User;

@Component
@Getter
@Setter
@NoArgsConstructor
public class DisLikeResponsePostDTO {
    private LoginUserResponseDTO user;
    private int count;
    public DisLikeResponsePostDTO(Post post, User u, Boolean isItLike) {
        user = new LoginUserResponseDTO(u);
        if(isItLike) {
            count = post.getLikers().size();
        }
        else {
            count = post.getDislikers().size();
        }
    }
}
