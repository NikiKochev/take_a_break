package takeABreak.model.dto.post;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@SuperBuilder
@NoArgsConstructor
public class EditingRequestPostDTO extends AddingRequestPostDTO{

    private int postId;


}