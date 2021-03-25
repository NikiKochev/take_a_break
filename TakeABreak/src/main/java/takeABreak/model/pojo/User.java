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
public class User {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private int age;
    private String password;
    private LocalDate createdAt;
    private LocalDate deletedAt;

    private List<Post> posts;
    private List<Comment> comments;

    //todo posts
    //todo comments
}
