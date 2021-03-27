package takeABreak.model.dto;

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
    private int parentId;
    private int postId;
    private String content;

    /*
    "user_id" : 3,
    "parent_id" : null,
    "content" : "My first comment - blablabla"
    */
}
