package takeABreak.model.dto.comments;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Getter
@Setter
@Component
public class AddingRequestCommentsDTO {

    private int userId;
    private int postId;
    private int parentId;
    private String content;

    /*
    "user_id" : 3,
    "parent_id" : null,
    "content" : "My first comment - blablabla"
    */
}
