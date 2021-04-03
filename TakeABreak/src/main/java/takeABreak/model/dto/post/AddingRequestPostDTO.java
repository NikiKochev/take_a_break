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
    private boolean isAdultContent;
    private int contentId;

}
