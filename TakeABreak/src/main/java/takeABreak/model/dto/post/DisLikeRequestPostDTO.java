package takeABreak.model.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
public class DisLikeRequestPostDTO {

    private int postId;
    /*{
    "userId" : 3,
    "postId" : 5
    }
    */
}
