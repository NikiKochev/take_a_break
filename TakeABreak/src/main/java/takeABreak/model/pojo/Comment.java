package takeABreak.model.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.time.LocalDate;


@NoArgsConstructor
@Setter
@Getter
@Component
public class Comment {

    private int id;
    private String content;
    private Post post;
    private Comment parent;
    private User user;
    private LocalDate deletedAt;
}
