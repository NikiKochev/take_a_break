package takeABreak.model.dto.comments;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Setter
@Getter
public class DeleteResponseCommentDTO {

    private String msg;
    public DeleteResponseCommentDTO(String msg) {
        this.msg = msg;
    }

}
