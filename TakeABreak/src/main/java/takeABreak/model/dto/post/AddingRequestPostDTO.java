package takeABreak.model.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Getter
@Setter
@Component
public class AddingRequestPostDTO {

    private String title;
    private String description;
    private int categoryId;
    private int contentId;
    private int userId;

    /*{
    "title" : "My first funny post with a picture",
    "description" : "Testing take a break platform",
    "categoryId" : 5,
    "userId" : 1
}*/
}
