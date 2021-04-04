package takeABreak.model.dto.post;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;


@NoArgsConstructor
@Getter
@Setter
@Component
@Data
@SuperBuilder
public class AddingRequestPostDTO {

    private String title;
    private String description;
    private int categoryId;
    private boolean isAdultContent;
    private int contentId;

}
