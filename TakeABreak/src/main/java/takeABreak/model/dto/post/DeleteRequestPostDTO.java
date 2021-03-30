package takeABreak.model.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Getter
@Setter
@Component
public class DeleteRequestPostDTO {

    private int postId;
    private int userId;
    /*
    {
    "post_id" : 3
    }
    */
}
