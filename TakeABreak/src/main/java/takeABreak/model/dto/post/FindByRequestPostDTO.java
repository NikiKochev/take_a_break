package takeABreak.model.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
public class FindByRequestPostDTO {

    private String text;
    private int page;
    private int perpage;
}
