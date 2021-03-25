package takeABreak.model.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Component
public class Post {

    private int id;
    private String title;
    private String description;
    private Content contentId;
    private Category category;
    private int userId;
    private LocalDate createdAt;

    //many to one !!!!
    private List<Comment> commentList;

    //todo list  of comments
}
