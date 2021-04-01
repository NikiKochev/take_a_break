package takeABreak.model.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Getter
@Setter
@Component
public class AddImageToPostResponseDTO {
    private int userId;
    private int fileType = 1;//image in db table file_type
    private String pathSize1;
    private String pathSize2;
    private String pathSize3;
    private String pathSize4;
}
